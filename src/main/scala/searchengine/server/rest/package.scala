package searchengine.server

package object rest {

  case class Create(key: String, text: String)
  case class CreateResult(key: String)

  case class Read(key: String)
  case class ReadResult(key: String, text: String)

  case class Search(text: String)
  case class SearchResult(keys: Set[String])
}
