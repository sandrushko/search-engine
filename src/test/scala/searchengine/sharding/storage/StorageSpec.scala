package searchengine.sharding.storage

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StorageSpec(implicit ee: ExecutionEnv) extends Specification {

  "Storage" should {

    "store data properly" in {
      val s = new Storage()

      Await.result(s.write("key", "value"), Duration.Inf) must not throwAn[Exception]()
      s.read("key") must be_==("value").await
    }
  }
}