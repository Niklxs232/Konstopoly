package de.konstopoly.view

import de.konstopoly.controller.GameController
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PlayerPanelSpec extends AnyWordSpec {

  "A PlayerPanel" should {
    "show a hint when no game has been started yet" in {
      val panel = new PlayerPanel(new GameController)
      panel.refresh()
      // ein Label fuer den Titel + ein Hinweis-Label
      panel.contents.size should be > 0
    }

    "list all players once a game is running" in {
      val controller = new GameController
      controller.startGame(List("Anna", "Ben"))
      val panel = new PlayerPanel(controller)
      panel.refresh()
      panel.contents.size should be > 1
    }
  }
}
