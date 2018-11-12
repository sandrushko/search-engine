package searchengine.server

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRefFactory, ActorSelection}
import searchengine.server.actor.TextServiceRequestActor
import searchengine.server.rest._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

class TextService(shardConnector: ShardConnector, timeout: FiniteDuration = FiniteDuration(10, TimeUnit.SECONDS))
                 (implicit actorRefFactory: ActorRefFactory) {

  private implicit val ec = actorRefFactory.dispatcher

  def create(key: String, text: String): Future[CreateResult] = {
    val promise = Promise[Any]
    val worker = actorRefFactory.actorOf(TextServiceRequestActor.props(promise, chooseShard(key), timeout))
    worker ! Create(key, text)
    promise.future.mapTo[CreateResult]
  }

  def read(key: String): Future[ReadResult] = {
    val promise = Promise[Any]
    val worker = actorRefFactory.actorOf(TextServiceRequestActor.props(promise, chooseShard(key), timeout))
    worker ! Read(key)
    promise.future.mapTo[ReadResult]
  }

  def search(text: String): Future[SearchResult] = {

    val workersAndResults = shardConnector.allShards.map(shard => {
      val prom = Promise[Any]
      val worker = actorRefFactory.actorOf(TextServiceRequestActor.props(prom, shard, timeout))
      worker ! Search(text)
      worker -> prom.future
    })
    val resultsFromAllShards = workersAndResults.map(_._2.mapTo[SearchResult])
    val result = Future.sequence(resultsFromAllShards).map(_.foldLeft(Set.empty[String])((a, e) => a ++ e.keys)).map(SearchResult.apply)

    result
  }

  protected def chooseShard(key: String): ActorSelection = {
    val index = math.abs(key.hashCode) % shardConnector.shardCount
    shardConnector.shard(index)
  }
}
