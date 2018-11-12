package searchengine.server.rest

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import searchengine.server.TextService
import JsonProtocol._
import scala.util._

class RestApi(textEngine: TextService) {

  val route: Route =
    pathPrefix("text") {
      path(Segment) {
        key =>
          post {
            entity(as[String]) {
              text =>
                onComplete(textEngine.create(key, text)) {
                  case Success(result) => complete(result)
                  case Failure(exception) => failWith(exception)
                }
            }
          } ~ get {
            onComplete(textEngine.read(key)) {
              case Success(result) => complete(result)
              case Failure(exception) => failWith(exception)
            }
          }
      } ~
      parameter('search.as[String]) {
        search => {
          onComplete(textEngine.search(search)) {
            case Success(result) => complete(result)
            case Failure(exception) => failWith(exception)
          }
        }
      }
    }
}