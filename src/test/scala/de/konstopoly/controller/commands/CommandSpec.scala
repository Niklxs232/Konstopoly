package de.konstopoly.controller.commands

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.konstopoly.controller.GameController
import de.konstopoly.model.Dice

class CommandSpec extends AnyWordSpec {

  def controllerWithPlayers(names: String*): GameController =
    val c = new GameController
    c.startGame(names.toList)
    c

  "UndoManager" should {
    "undo a roll command" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.gameState.currentPlayer.position should be(0)
      c.rollDice(Dice(1, 2))
      c.gameState.currentPlayer.position should be(3)
      c.undo()
      c.gameState.currentPlayer.position should be(0)
      c.hasRolled should be(false)
    }

    "undo a buy command" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.rollDice(Dice(1, 2))
      c.buyProperty()
      c.gameState.currentPlayer.money should be(1440)
      c.undo()
      c.gameState.currentPlayer.money should be(1500)
    }

    "undo an end turn command" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.rollDice(Dice(1, 2))
      c.endTurn()
      c.gameState.currentPlayer.name should be("Ben")
      c.undo()
      c.gameState.currentPlayer.name should be("Anna")
      c.hasRolled should be(true)
    }

    "handle undo when nothing to undo" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.undo()
      c.message should include("Nichts")
    }

    "redo after undo" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.rollDice(Dice(1, 2))
      c.gameState.currentPlayer.position should be(3)
      c.undo()
      c.gameState.currentPlayer.position should be(0)
      c.redo()
      c.gameState.currentPlayer.position should be(3)
    }

    "handle redo when nothing to redo" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.redo()
      c.message should include("Nichts")
    }
  }

  "Strategy Pattern" should {
    "use custom dice strategy" in {
      val c = controllerWithPlayers("Anna", "Ben")
      c.diceStrategy = () => Dice(3, 3)
      c.rollDice()
      c.gameState.currentPlayer.position should be(6)
    }

    "allow switching strategy at runtime" in {
      val c = controllerWithPlayers("Anna", "Ben")
      // Dice(1,2) = 3 -> Position 3 ist ein normales Grundstueck ohne
      // Zufallseffekt. (Position 2 waere ein Ereignisfeld, das eine zufaellige
      // Karte zieht und den Test dadurch unzuverlaessig machen wuerde.)
      c.diceStrategy = () => Dice(1, 2)
      c.rollDice()
      c.gameState.currentPlayer.position should be(3)
      c.endTurn()
      c.diceStrategy = () => Dice(5, 5)
      c.rollDice()
      c.gameState.currentPlayer.position should be(10)
    }
  }
}
