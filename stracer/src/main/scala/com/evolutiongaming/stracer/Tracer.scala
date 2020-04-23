package com.evolutiongaming.stracer

import cats.{Applicative, Monad}
import cats.implicits._
import com.evolutiongaming.stracer.util.FromConfigReaderResult
import com.evolutiongaming.stracer.util.PureConfigHelper._
import com.typesafe.config.Config
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

trait Tracer[F[_]] {

  def trace(sampling: Option[Sampling] = None): F[Option[Trace]]
}

object Tracer {

  def apply[F[_]](implicit F: Tracer[F]): Tracer[F] = F

  def empty[F[_]: Applicative]: Tracer[F] = const(none[Trace].pure[F])

  def const[F[_]](trace: F[Option[Trace]]): Tracer[F] = {
    val trace1  = trace
    new Tracer[F] {

      def trace(sampling: Option[Sampling]): F[Option[Trace]] = trace1
    }
  }


  def of[F[_]: Monad: FromConfigReaderResult: TraceGen](
    runtimeConf: F[RuntimeConf],
    config: Config
  ): F[Tracer[F]] = {
    def startupConf = ConfigSource
      .fromConfig(config)
      .load[StartupConf]
    for {
      tracerConfig <- startupConf.liftTo[F]
      tracer       <- of(tracerConfig, runtimeConf)
    } yield tracer
  }


  def of[F[_]: Monad: TraceGen](
    startupConf: StartupConf,
    runtimeConf: F[RuntimeConf]
  ): F[Tracer[F]] = {

    val tracer = if (startupConf.enabled) apply[F](runtimeConf) else empty[F]
    tracer.pure[F]
  }


  def apply[F[_]: Monad: TraceGen](conf: F[RuntimeConf]): Tracer[F] = {
    new Tracer[F] {

      def trace(sampling: Option[Sampling]): F[Option[Trace]] =
        for {
          conf    <- conf
          enabled  = conf.enabled
          trace   <- if (enabled) TraceGen.summon[F].root.map(_.some) else none[Trace].pure[F]
        } yield trace

    }
  }


  final case class StartupConf(enabled: Boolean = false)

  object StartupConf {

    val default: StartupConf = StartupConf()

    implicit val configReaderStartupConf: ConfigReader[StartupConf] = deriveReader
  }


  final case class RuntimeConf(enabled: Boolean = true)

  object RuntimeConf {
    val default: RuntimeConf = RuntimeConf()
  }
}
