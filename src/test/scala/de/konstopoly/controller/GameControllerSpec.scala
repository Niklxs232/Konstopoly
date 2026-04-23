package de.konstopoly.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.konstopoly.model.*
import de.konstopoly.model.fields.*
import de.konstopoly.util.Observer

class GameControllerSpec extends AnyWordSpec {

  class TestObserver extends Observer:
    var updateCount = 0
    def update(): Unit = updateCount += 1

  def controllerWithPlayers(names: String*): GameController =
    val c = new GameController
    c.startGame(names.toList)
    c

  def setPosition(controller: GameController, playerIndex: Int, pos: Int): Unit =
    val state = controller.gameState
    val updated = state.players(playerIndex).copy(position = pos)
    controller.gameState = state.copy(players = state.players.updated(playerIndex, updated))

  "a GameController" when {

    "starting a game" should {
      "initialize with the given players" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.gameState.players.length should be(2)
        c.gameState.players.head.name should be("Anna")
        c.gameState.players(1).name should be("Ben")
      }

      "set all players to position 0 with 1500€" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.gameState.players.foreach { p =>
          p.position should be(0)
          p.money should be(1500)
        }
      }

      "set round to 1 and current player to first" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.gameState.round should be(1)
        c.gameState.currentPlayer.name should be("Anna")
      }

      "set hasRolled to false" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.hasRolled should be(false)
      }
    }

    "rolling dice" should {
      "move the current player by dice total" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(3)
      }

      "set hasRolled to true" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.hasRolled should be(true)
      }

      "not allow rolling twice in one turn" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.rollDice(Dice(3, 3))
        c.gameState.currentPlayer.position should be(3)
        c.message should include("bereits gewürfelt")
      }

      "give 200€ when passing Go" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 38)
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(1)
        c.gameState.currentPlayer.money should be(1700)
      }

      "deduct tax when landing on TaxField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 2))
        c.gameState.currentPlayer.position should be(4)
        c.gameState.currentPlayer.money should be(1300)
      }

      "send player to jail (position 10) when landing on GoToJailField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 25)
        c.rollDice(Dice(2, 3))
        c.gameState.currentPlayer.position should be(10)
      }

      "charge rent when landing on owned PropertyField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.endTurn()
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.money should be(1496)
        c.gameState.players.head.money should be(1444)
      }

      "charge station rent when landing on owned StationField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 3))
        c.buyProperty()
        c.endTurn()
        c.rollDice(Dice(2, 3))
        c.gameState.currentPlayer.money should be(1475)
        c.gameState.players.head.money should be(1325)
      }

      "charge utility rent based on dice total" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.buyProperty()
        c.endTurn()
        val utilityDice = Dice(6, 6)
        c.rollDice(utilityDice)
        c.gameState.currentPlayer.money should be(1500 - 4 * 12)
        c.gameState.players.head.money should be(1350 + 4 * 12)
      }

      "not charge rent when landing on own property" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.endTurn()
        c.rollDice(Dice(1, 2))
        val moneyAfterRent = c.gameState.players.head.money
        c.endTurn()
        setPosition(c, 0, 0)
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.money should be(moneyAfterRent)
      }
    }

    "buying property" should {
      "let player buy an unowned PropertyField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty() should be(true)
        c.gameState.currentPlayer.money should be(1440)
        c.gameState.board.fieldAt(3).asInstanceOf[PropertyField].owner should be(Some("Anna"))
      }

      "let player buy an unowned StationField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 3))
        c.buyProperty() should be(true)
        c.gameState.currentPlayer.money should be(1300)
        c.gameState.board.fieldAt(5).asInstanceOf[StationField].owner should be(Some("Anna"))
      }

      "let player buy an unowned UtilityField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.buyProperty() should be(true)
        c.gameState.currentPlayer.money should be(1350)
        c.gameState.board.fieldAt(12).asInstanceOf[UtilityField].owner should be(Some("Anna"))
      }

      "return false when field is already owned" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty() should be(true)
        c.endTurn()
        c.rollDice(Dice(1, 2))
        c.buyProperty() should be(false)
      }

      "return false when player has not enough money" in {
        val c = controllerWithPlayers("Anna", "Ben")
        val state = c.gameState
        val broke = state.currentPlayer.copy(money = 10)
        c.gameState = state.copy(players = state.players.updated(0, broke))
        c.rollDice(Dice(1, 2))
        c.buyProperty() should be(false)
      }

      "return false when player has not rolled yet" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.buyProperty() should be(false)
      }
    }

    "ending turn" should {
      "switch to next player and reset hasRolled" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.gameState.currentPlayer.name should be("Ben")
        c.hasRolled should be(false)
      }

      "wrap around to first player" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.gameState.currentPlayer.name should be("Anna")
      }

      "increase round after all players had a turn" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.gameState.round should be(2)
      }

      "not allow ending turn before rolling" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.endTurn()
        c.gameState.currentPlayer.name should be("Anna")
        c.message should include("zuerst würfeln")
      }
    }

    "observer notification" should {
      "notify observers on startGame" in {
        val c = new GameController
        val obs = new TestObserver
        c.add(obs)
        c.startGame(List("Anna", "Ben"))
        obs.updateCount should be(1)
      }

      "notify observers on rollDice" in {
        val c = controllerWithPlayers("Anna", "Ben")
        val obs = new TestObserver
        c.add(obs)
        c.rollDice(Dice(1, 2))
        obs.updateCount should be(1)
      }

      "notify observers on buyProperty" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        val obs = new TestObserver
        c.add(obs)
        c.buyProperty()
        obs.updateCount should be(1)
      }

      "notify observers on endTurn" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        val obs = new TestObserver
        c.add(obs)
        c.endTurn()
        obs.updateCount should be(1)
      }

      "allow removing an observer" in {
        val c = controllerWithPlayers("Anna", "Ben")
        val obs = new TestObserver
        c.add(obs)
        c.remove(obs)
        c.rollDice(Dice(1, 2))
        c.endTurn()
        obs.updateCount should be(0)
      }
    }

    "message" should {
      "show roll info" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.message should include("Anna")
        c.message should include("3")
      }

      "show buy info" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.message should include("kauft")
      }

      "show turn info" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.endTurn()
        c.message should include("Ben")
      }
    }
  }
}
