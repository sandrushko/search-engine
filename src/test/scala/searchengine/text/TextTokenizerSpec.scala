package searchengine.text

import org.specs2.mutable.Specification

class TextTokenizerSpec extends Specification with TextTokenizer {

  "Tokenizer" should {

    "exclude punctuation chars" in {
      tokenize("hello, my friend!") must_== Set("hello", "my", "friend")
    }

    "produce lowercase words" in {
      tokenize("HELLO, MY FRIEND!") must_== Set("hello", "my", "friend")
    }
  }
}
