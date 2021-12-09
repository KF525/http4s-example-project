package model

import model.reponse.PoemResponse
import util.BaseSpec

class PoemTest extends BaseSpec {

  "createPoem" should "returns a poem" in {
    val title = "title"
    val author = "author"
    val line = "line"
    val poemResponse = PoemResponse(title, author, List(line, line, ""), 3)
    Poem.createPoem(poemResponse) should be(Poem(title, Author(author), List(Line(line), Line(line)), 2))
  }

}
