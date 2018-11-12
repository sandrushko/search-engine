package searchengine.sharding

import searchengine._
import akka.actor.ActorSystem
import searchengine.sharding.actor.ShardActor
import searchengine.sharding.storage.{Index, Storage}

object ShardApp extends App {

  val actorSystem = ActorSystem(SearchServerShardActorSystemName)

  val storage = new Storage()

  val index = new Index()

  actorSystem.actorOf(ShardActor.props(storage, index), ShardActorName)
}
