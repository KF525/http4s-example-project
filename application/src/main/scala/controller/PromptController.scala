package controller

import client.PromptClient
import error.CompoundPoemFailure
import model.Poem
import model.reponse.PromptResponse
import zio.ZIO
import zio.clock.Clock
import zio.console.Console

class PromptController(client: PromptClient, clock: Clock, console: Console) {

  //TODO: Solution for head?, already took care of empty case
  def getPrompt: ZIO[Any, CompoundPoemFailure, PromptResponse] =
    for {
      poemResponse <- client.makeRequest.provide(clock ++ console).map(r => r.head)
      poem = Poem.createPoem(poemResponse)
      line = Poem.getRandomLine(poem)
  } yield PromptResponse(poem, line)

}
