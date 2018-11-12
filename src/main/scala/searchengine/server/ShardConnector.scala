package searchengine.server

import akka.actor.{ActorRefFactory, ActorSelection}
import searchengine._

class ShardConnector(shards: IndexedSeq[String])(implicit actorRefFactory: ActorRefFactory) {

  def shard(index: Int): ActorSelection = actorRefFactory.actorSelection(s"akka.tcp://$SearchServerShardActorSystemName@${shards(index)}/user/$ShardActorName")

  def shardCount: Int = shards.length

  def allShards: Seq[ActorSelection] = for(i <- 0 until shardCount) yield shard(i)
}
