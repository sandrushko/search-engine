package searchengine.server

import searchengine._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import searchengine.server.rest.RestApi

object ServerApp extends App {

  implicit val actorSystem = ActorSystem(SearchServerActorSystemName)
  implicit val actorMaterializer = ActorMaterializer(ActorMaterializerSettings(actorSystem))(actorSystem)

  import scala.collection.JavaConverters._

  val shards = actorSystem.settings.config.getStringList("sharding.nodes").asScala.toSet.toIndexedSeq

  val shardConnector = new ShardConnector(shards)

  val textService = new TextService(shardConnector)

  val restApi = new RestApi(textService)

  val host = actorSystem.settings.config.getString("rest.host")
  val port = actorSystem.settings.config.getInt("rest.port")
  Http().bindAndHandle(RouteResult.route2HandlerFlow(restApi.route), host, port)
}
