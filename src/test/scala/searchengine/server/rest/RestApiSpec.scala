package searchengine.server.rest

import java.net.URLEncoder
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.Specs2RouteTest
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import searchengine.server.TextService
import scala.concurrent.Future

class RestApiSpec extends Specification with Specs2RouteTest with Mockito {

  val BaseUrl = "/text"

  "Rest api" should {

    "support adding text via POST" in {
      val TestKey = "testKey"
      val TestData = "test data"

      val textService = mock[TextService]

      textService.create(anyString, anyString) answers
        {args: Array[AnyRef] => Future.successful(CreateResult(args(0).toString))}

      val restApi = new RestApi(textService)

      Post(BaseUrl + s"/$TestKey", HttpEntity(TestData)) ~> restApi.route ~> check {
        response.status must_== StatusCodes.OK
        val responseString = responseAs[String]
        responseString must contain(TestKey)
      }
    }

    "support reading text via GET" in {
      val TestKey = "testKey"
      val TestData = "test data"

      val textService = mock[TextService]

      textService.read(anyString) answers
        {args: Array[AnyRef] => Future.successful(ReadResult(args(0).toString, TestData))}

      val restApi = new RestApi(textService)

      Get(BaseUrl + s"/$TestKey") ~> restApi.route ~> check {
        response.status must_== StatusCodes.OK
        val responseString = responseAs[String]
        responseString must contain(TestKey) and contain(TestData)
      }
    }

    "support searching text via GET" in {
      val TestKey = "testKey"
      val TestQuery = URLEncoder.encode("test query", "utf-8")

      val textService = mock[TextService]

      textService.search(anyString) answers
        {args: Array[AnyRef] => Future.successful(SearchResult(Set(TestKey)))}

      val restApi = new RestApi(textService)

      Get(BaseUrl + s"?search=$TestQuery") ~> restApi.route ~> check {
        response.status must_== StatusCodes.OK
        val responseString = responseAs[String]
        responseString must contain(TestKey)
      }
    }

    "return http error 500 in cause of request processing error" in {
      val TestQuery = URLEncoder.encode("test query", "utf-8")

      val textService = mock[TextService]

      textService.search(anyString) answers
        {args: Array[AnyRef] => Future.failed(new Exception())}

      val restApi = new RestApi(textService)

      Get(BaseUrl + s"?search=$TestQuery") ~> restApi.route ~> check {
        response.status must_== StatusCodes.InternalServerError
      }
    }
  }

}
