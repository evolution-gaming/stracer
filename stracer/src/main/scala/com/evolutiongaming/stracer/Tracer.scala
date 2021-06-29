package com.evolutiongaming.stracer

import cats.{Applicative, Monad}
import cats.syntax.all._
import com.evolutiongaming.random.Random
import com.evolutiongaming.stracer.Sampling.{Accept, Debug}
import com.evolutiongaming.stracer.util.FromConfigReaderResult
import com.evolutiongaming.stracer.util.PureConfigHelper._
import com.typesafe.config.Config
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

trait Tracer[F[_]] {

  def trace(sampling: Option[Sampling] = None): F[Option[Trace]]

  def childOf(traceCtx: Option[Trace]): F[Option[Trace]]
}

object Tracer {

  def apply[F[_]](implicit F: Tracer[F]): Tracer[F] = F

  def empty[F[_]: Applicative]: Tracer[F] = const(none[Trace].pure[F])

  def const[F[_]](trace: F[Option[Trace]]): Tracer[F] = {
    val trace1  = trace
    new Tracer[F] {

      def trace(sampling: Option[Sampling]): F[Option[Trace]] = trace1

      def childOf(traceCtx: Option[Trace]): F[Option[Trace]] = trace1
    }
  }


  def of[F[_]: Monad: FromConfigReaderResult: TraceGen: Random](
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


  def of[F[_]: Monad: TraceGen: Random](
    startupConf: StartupConf,
    runtimeConf: F[RuntimeConf]
  ): F[Tracer[F]] = {

    val tracer = if (startupConf.enabled) apply[F](runtimeConf) else empty[F]
    tracer.pure[F]
  }


  def apply[F[_]: Monad: TraceGen: Random](conf: F[RuntimeConf]): Tracer[F] = {
    new Tracer[F] {

      def trace(sampling: Option[Sampling]): F[Option[Trace]] =
        whenEnabled {
          Monad[F].ifM(shouldTrace(sampling))(TraceGen.summon[F].root.map(_.some), none[Trace].pure[F])
        }

      def childOf(traceCtx: Option[Trace]): F[Option[Trace]] =
        whenEnabled {
          traceCtx.traverse(_.child)
        }

      private def whenEnabled(ctx: => F[Option[Trace]]): F[Option[Trace]] =
        for {
          conf    <- conf
          enabled  = conf.enabled
          ctx     <- if (enabled) ctx else none[Trace].pure[F]
        } yield ctx

      private def shouldTrace(sampling: Option[Sampling]): F[Boolean] = for {
        probabilityConfig <- conf.map(_.probability)
        probability = probabilityConfig(sampling)
        randomValue <- Random[F].double
      } yield probability > randomValue
    }
  }


  final case class StartupConf(enabled: Boolean = false)

  object StartupConf {

    val default: StartupConf = StartupConf()

    implicit val configReaderStartupConf: ConfigReader[StartupConf] = deriveReader
  }

  def defaultProbability: Option[Sampling] => Double = {
    case Some(Accept) | Some(Debug) => 1.0
    case _ => 0.01
  }

  val alwaysProbability: Option[Sampling] => Double = _ => 1.0

  val neverProbability: Option[Sampling] => Double = _ => -1.0

  final case class RuntimeConf(enabled: Boolean = true, probability: Option[Sampling] => Double = defaultProbability)

  object RuntimeConf {
    val default: RuntimeConf = RuntimeConf()
  }

}
