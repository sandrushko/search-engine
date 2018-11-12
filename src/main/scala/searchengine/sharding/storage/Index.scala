package searchengine.sharding.storage

import java.util.concurrent.ConcurrentHashMap
import searchengine.text.TextTokenizer
import scala.concurrent.{ExecutionContext, Future}

class Index extends TextTokenizer {

  protected val index = new ConcurrentHashMap[String, Set[String]]()

  def index(key: String, text: String)(implicit ec: ExecutionContext): Future[Unit] = Future {
    val tokens = tokenize(text)
    tokens.foreach(token => {
      var break: Boolean = false
      while (!break) {
        val oldValue = index.get(token)
        if(oldValue == null) {
          break = index.putIfAbsent(token, Set(key)) == null
        }
        else {
          val newValue = oldValue + key
          break = index.replace(token, oldValue, newValue)
        }
      }
    })
  }

  def search(text: String)(implicit ec: ExecutionContext): Future[Set[String]] = Future {
    val tokens = tokenize(text)
    val resultSet = tokens.map(index.getOrDefault(_, Set.empty[String])).reduceLeft(_ intersect _)
    resultSet
  }
}
