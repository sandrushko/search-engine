package searchengine.sharding.storage

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class IndexSpec(implicit ee: ExecutionEnv) extends Specification {

  "Index" should {

    "search indexed text properly" in {
      val index = new Index

      index.index("key1", "Hello, my friend!") must not throwAn[Exception]()

      index.search("hello") must be_==(Set("key1")).await
      index.search("my") must be_==(Set("key1")).await
      index.search("friend") must be_==(Set("key1")).await
      index.search("my hello") must be_==(Set("key1")).await
      index.search("friend my hello") must be_==(Set("key1")).await
      index.search("friends") must be_==(Set.empty[String]).await
    }
  }
}
