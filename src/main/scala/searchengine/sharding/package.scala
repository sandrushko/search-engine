package searchengine

package object sharding {

    case class StoreWrite(key: String, data: String)
    case class StoreWriteResult(key: String)

    case class StoreRead(key: String)
    case class StoreReadResult(key: String, text: String)

    case class StoreSearch(text: String)
    case class StoreSearchResult(keys: Set[String])
}
