package searchengine.sharding.storage

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import org.specs2.mock.Mockito
import searchengine.sharding._
import searchengine.sharding.actor.ShardActor
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class ShardActorSpec extends TestKit(ActorSystem("Test")) with SpecificationLike with Mockito {

  "Shard Actor" should {

    "Use storage and index when receives write request" in {
      val storage = mock[Storage]
      storage.write(anyString, anyString)(any[ExecutionContext]()) returns Future.successful(())

      val index = mock[Index]
      index.index(anyString, anyString)(any[ExecutionContext]()) returns Future.successful(())

      val testProbe = TestProbe()
      val actor = system.actorOf(ShardActor.props(storage, index))

      testProbe.send(actor, StoreWrite("key", "value"))
      testProbe.expectMsgType[StoreWriteResult](100 millis) must_== StoreWriteResult("key")

      there was one(storage).write(anyString, anyString)(any[ExecutionContext]())
      there was one(index).index(anyString, anyString)(any[ExecutionContext]())
    }

    "Use storage only when receives read request" in {
      val storage = mock[Storage]
      storage.read(anyString)(any[ExecutionContext]()) returns Future.successful("value")

      val index = mock[Index]

      val testProbe = TestProbe()
      val actor = system.actorOf(ShardActor.props(storage, index))

      testProbe.send(actor, StoreRead("key"))
      testProbe.expectMsgType[StoreReadResult](100 millis) must_== StoreReadResult("key", "value")

      there was noMoreCallsTo(index)
      there was one(storage).read(anyString)(any[ExecutionContext]())
    }

    "Use index only when receives search request" in {
      val storage = mock[Storage]

      val index = mock[Index]
      index.search(anyString)(any[ExecutionContext]()) returns Future.successful(Set("key"))

      val actor = system.actorOf(ShardActor.props(storage, index))

      val testProbe = TestProbe()
      testProbe.send(actor, StoreSearch("value"))
      testProbe.expectMsgType[StoreSearchResult](100 millis) must_== StoreSearchResult(Set("key"))

      there was one(index).search(anyString)(any[ExecutionContext]())
      there was noMoreCallsTo(storage)
    }
  }
}
