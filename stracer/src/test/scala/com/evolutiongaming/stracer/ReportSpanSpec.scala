package com.evolutiongaming.stracer

import java.time.Instant
import com.evolutiongaming.stracer.implicits._

import scala.concurrent.duration._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ReportSpanSpec extends AnyFunSuite with Matchers {

  import ReportSpanSpec._

  test("") {

    val localEndpoint = Endpoint(serviceName = "serviceName".some)

    val tags = ("name1" -> "value1") & ("name2" -> "value2")

    val reportSpanRecord = new ReportSpanRecord[StateT] {
      def apply(span: SpanRecord) = StateT { state =>
        val state1 = state.copy(records = span :: state.records)
        (state1, ())
      }
    }

    val timestamp = Instant.ofEpochMilli(1551824013554L)

    val traceId = TraceId(timestamp, randomInt = 12345, randomLong = 123456789L)

    val spanId = SpanId(1551818273913L)

    val duration = 1.second

    val span = Span(
      traceId,
      spanId,
      "name",
      timestamp,
      duration = duration.some,
      tags = ("name2" -> "value") & ("name3" -> "value3")
    )

    val reportSpan = ReportSpan[StateT](localEndpoint, tags, reportSpanRecord, Kind.producer)

    val (state, _) = reportSpan(span).run(State.Empty)
    val record = SpanRecord(
      traceId = traceId,
      spanId = spanId,
      parentId = none,
      kind = Kind.producer.some,
      name = "name".some,
      timestamp = timestamp.some,
      duration = duration.some,
      localEndpoint = localEndpoint.some,
      remoteEndpoint = none,
      annotations = List.empty,
      tags = Map(("name1", "value1"), ("name2", "value"), ("name3", "value3")),
      debug = None,
      shared = None
    )
    state shouldEqual State(List(record))
  }

}

object ReportSpanSpec {

  case class State(records: List[SpanRecord])

  object State {
    val Empty: State = State(List.empty)
  }

  type StateT[A] = cats.data.StateT[cats.Id, State, A]

  object StateT {

    def apply[A](f: State => (State, A)): StateT[A] = cats.data.StateT[cats.Id, State, A](f)
  }
}
