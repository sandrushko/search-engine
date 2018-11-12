package searchengine.server.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val createResultFormat: RootJsonFormat[CreateResult] = jsonFormat1(CreateResult)

  implicit val ReadResultFormat: RootJsonFormat[ReadResult] = jsonFormat2(ReadResult)

  implicit val searchResultFormat: RootJsonFormat[SearchResult] = jsonFormat1(SearchResult)
}
