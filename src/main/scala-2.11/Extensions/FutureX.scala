package Extensions

import java.util.{Timer, TimerTask}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

object FutureX {

  def delay[T](wait: scala.concurrent.duration.Duration)(block: => T): Future[T] = delay(wait.toMillis)(block)

  def delay[T](waitMillis: Long)(block: => T): Future[T] = {
    val promise = Promise[T]
    val t = new Timer()
    t.schedule(new TimerTask {
      override def run(): Unit = {
        promise.complete(Try(block))
      }
    }, waitMillis)
    promise.future
  }

  implicit class FlattenFutures[T](val future: Future[Future[T]]) extends AnyVal {
    def flatten(implicit ec: ExecutionContext): Future[T] = future.flatMap(x => x)
  }

}
