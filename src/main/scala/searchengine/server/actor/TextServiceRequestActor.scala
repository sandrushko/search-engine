package searchengine.server.actor

import java.util.concurrent.TimeoutException
import akka.actor.{Actor, ActorLogging, ActorSelection, Cancellable, Props}
import scala.concurrent.Promise
import searchengine.server.rest._
import searchengine.sharding._
import scala.concurrent.duration.FiniteDuration
import scala.util._

class TextServiceRequestActor(promise: Promise[Any],
                              shard: ActorSelection,
                              timeout: FiniteDuration) extends Actor with ActorLogging {

  def receive: Receive = waitingRequest

  var cancellable: Cancellable = _

  val waitingResponse: Receive = {
    case StoreWriteResult(key) =>
      promise.complete(Success(CreateResult(key)))
      Option(cancellable).foreach(_.cancel())
      context.stop(self)
    case StoreReadResult(key, text) =>
      promise.complete(Success(ReadResult(key, text)))
      Option(cancellable).foreach(_.cancel())
      context.stop(self)
    case StoreSearchResult(keys) =>
      promise.complete(Success(SearchResult(keys)))
      Option(cancellable).foreach(_.cancel())
      context.stop(self)
    case akka.actor.Status.Failure(cause) =>
      promise.complete(Failure(cause))
      Option(cancellable).foreach(_.cancel())
      context.stop(self)
  }

  val waitingRequest: Receive = {
    case Create(key, text) =>
      shard ! StoreWrite(key, text)
      context.become(waitingResponse)
      setResponseTimeout(timeout)
    case Read(key) =>
      shard ! StoreRead(key)
      context.become(waitingResponse)
      setResponseTimeout(timeout)
    case Search(text) =>
      shard ! StoreSearch(text)
      context.become(waitingResponse)
      setResponseTimeout(timeout)
  }

  def setResponseTimeout(duration: FiniteDuration): Unit = {
    cancellable = context.system.getScheduler.scheduleOnce(duration){
      promise.tryFailure(new TimeoutException())
      context.stop(self)
    }(context.dispatcher)
  }
}

object TextServiceRequestActor {

  def props(promise: Promise[Any], shard: ActorSelection, timeout: FiniteDuration) =
    Props(classOf[TextServiceRequestActor], promise, shard, timeout)
}
