package searchengine.server

import java.util.concurrent.{TimeUnit, TimeoutException}

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationLike
import searchengine.server.rest.{CreateResult, ReadResult, SearchResult}
import searchengine.sharding._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}

class TextServiceSpec(implicit ee: ExecutionEnv)
  extends TestKit(ActorSystem("TestActorSystem")) with SpecificationLike with Mockito {

  "Text Service" should {

    "Create work properly" in {
      val TestKey = "key"
      val TestData = "test data"

      val shardProbe = TestProbe("ShardProbe")

      val shardConnector = mock[ShardConnector]
      shardConnector.shard(anyInt) answers { args: Array[AnyRef] => system.actorSelection(shardProbe.ref.path) }
      shardConnector.shardCount returns 2

      val textService = new TextService(shardConnector)

      val futureResult = textService.create(TestKey, TestData)
      val storeWriteMsg = shardProbe.expectMsgType[StoreWrite]
      storeWriteMsg.key must_== TestKey
      storeWriteMsg.data must_== TestData

      futureResult.isCompleted must beFalse

      shardProbe.reply(StoreWriteResult(TestKey))

      futureResult must be_==(CreateResult(TestKey)).await
    }


    "Read work properly" in {
      val TestKey = "key"
      val TestData = "test data"

      val shardProbe = TestProbe("ShardProbe")

      val shardConnector = mock[ShardConnector]
      shardConnector.shard(anyInt) answers { args: Array[AnyRef] => system.actorSelection(shardProbe.ref.path) }
      shardConnector.shardCount returns 2

      val textService = new TextService(shardConnector)

      val futureResult = textService.read(TestKey)
      val storeReadMsg = shardProbe.expectMsgType[StoreRead]
      storeReadMsg.key must_== TestKey

      futureResult.isCompleted must beFalse

      shardProbe.reply(StoreReadResult(TestKey, TestData))

      futureResult must be_==(ReadResult(TestKey, TestData)).await
    }

    "Search work properly" in {
      val SearchText = "search text"

      val TestKey1 = "key1"
      val TestKey2 = "key2"

      val SearchResultKeys = Seq(TestKey1, TestKey2)

      val shardProbes = Seq(TestProbe("ShardProbe0"), TestProbe("ShardProbe0"))

      val shardConnector = mock[ShardConnector]
      shardConnector.shard(anyInt) answers {
        args: Array[AnyRef] =>
        system.actorSelection(shardProbes(args(0).asInstanceOf[Int]).ref.path)
      }
      shardConnector.shardCount returns shardProbes.length
      val allShards = (0 until shardConnector.shardCount).map(i => shardConnector.shard(i))
      shardConnector.allShards returns allShards

      val textService = new TextService(shardConnector)

      val futureResult = textService.search(SearchText)
      val storeSearchMsgs = shardProbes.map(probe => probe.expectMsgClass(classOf[StoreSearch]))
      storeSearchMsgs.foreach(msg => msg.text must_== SearchText)

      futureResult.isCompleted must beFalse

      shardProbes.zipWithIndex.foreach(probeWithIndex => {
        val (probe, index) = probeWithIndex
        probe.reply(StoreSearchResult(Set(SearchResultKeys(index))))
      })

      futureResult must be_==(SearchResult(Set(TestKey1, TestKey2))).await
    }

    "Read timeout" in {
      val TestKey = "key"

      val shardProbe = TestProbe("ShardProbe")

      val shardConnector = mock[ShardConnector]
      shardConnector.shard(anyInt) answers { args: Array[AnyRef] => system.actorSelection(shardProbe.ref.path) }
      shardConnector.shardCount returns 2

      val textService = new TextService(shardConnector, FiniteDuration(1, TimeUnit.MILLISECONDS))

      val futureResult = textService.read(TestKey)
      val storeReadMsg = shardProbe.expectMsgType[StoreRead]
      storeReadMsg.key must_== TestKey

      Await.result(futureResult, Duration.Inf) must throwAn[TimeoutException]()
    }
  }
}
