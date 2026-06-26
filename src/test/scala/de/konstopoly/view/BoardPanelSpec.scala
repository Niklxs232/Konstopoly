package de.konstopoly.view

import de.konstopoly.controller.GameController
import de.konstopoly.model.Dice
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import java.awt.image.BufferedImage

class BoardPanelSpec extends AnyWordSpec {

  // Zeichnet das Panel in ein Offscreen-Bild. Dadurch wird der Zeichen-Code
  // wirklich ausgefuehrt, ohne dass ein echtes Fenster geoeffnet wird
  // (funktioniert auch auf der headless CI).
  private def paint(panel: BoardPanel): Unit =
    panel.peer.setSize(660, 660)
    val img = new BufferedImage(660, 660, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()
    panel.peer.paint(g)
    g.dispose()

  "A BoardPanel" should {
    "draw a hint when no game is running" in {
      paint(new BoardPanel(new GameController))
      succeed
    }

    "draw the full board with players and an owned field" in {
      val controller = new GameController
      controller.startGame(List("Anna", "Ben"))
      controller.rollDice(Dice(1, 2)) // Anna -> Feld 3 (Konzilstrasse)
      controller.buyProperty()        // Anna besitzt Feld 3 -> Besitzer-Streifen
      paint(new BoardPanel(controller))
      succeed
    }
  }
}
