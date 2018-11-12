package searchengine.text

trait TextTokenizer {

  def tokenize(text: String): Set[String] = text.split("\\W+").map(_.trim.toLowerCase).toSet

}
