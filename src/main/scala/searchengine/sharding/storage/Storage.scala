package searchengine.sharding.storage

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{ExecutionContext, Future}

class Storage {

  private val dataMap = new ConcurrentHashMap[String, String]

  def write(key: String, text: String)(implicit ec: ExecutionContext): Future[Unit] = Future {
    dataMap.put(key, text)
  }

  def read(key: String)(implicit ec: ExecutionContext): Future[String] = Future {
    val value = dataMap.get(key)
    if(value ne null) value else throw new IllegalArgumentException(s"No value for key '$key'")
  }
}
