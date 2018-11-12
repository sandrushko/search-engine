package searchengine.sharding.actor

import akka.actor.{Actor, ActorLogging, Props, Status}
import searchengine.sharding._
import searchengine.sharding.storage.{Index, Storage}
import scala.concurrent.Future
import scala.util._

class ShardActor(val storage: Storage, val index: Index) extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = {
    case StoreWrite(key, text) =>
      log.debug(s"Store write '$key' = '$text'")
      val sendTo = sender
      Future.sequence(Seq(storage.write(key, text), index.index(key, text))).onComplete({
        case Success(_) => sendTo ! StoreWriteResult(key)
        case Failure(cause) => sendTo ! Status.Failure(cause)
      })
    case StoreRead(key) =>
      log.debug(s"Store read '$key'")
      val sendTo = sender
      storage.read(key).onComplete({
        case Success(text) => sendTo ! StoreReadResult(key, text)
        case Failure(cause) => sendTo ! Status.Failure(cause)
      })
    case StoreSearch(text) =>
      log.debug(s"Store search '$text'")
      val sendTo = sender
      index.search(text).onComplete({
        case Success(keys) => sendTo ! StoreSearchResult(keys)
        case Failure(cause) => sendTo ! Status.Failure(cause)
      })
  }
}

object ShardActor {

  def props(storage: Storage, index: Index) = Props(classOf[ShardActor], storage, index)
}
