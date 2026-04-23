package de.konstopoly.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.konstopoly.model.*
import de.konstopoly.model.fields.*

class GameControllerSpec extends AnyWordSpec {

  def controllerWithPlayers(names: String*): GameController =
    val c = new GameController
    c.startGame(names.toList)
    c

  def setPosition(controller: GameController, playerIndex: Int, pos: Int): Unit =
    val state = controller.gameState
    val updated = state.players(playerIndex).copy(position = pos)
    controller.gameState = state.copy(players = state.players.updated(playerIndex, updated))

  def putChanceCard(controller: GameController, position: Int, card: Card): Unit =
    val singleCardField = ChanceField("Testfeld", Vector(card))
    val fields = controller.gameState.board.fields.updated(position, singleCardField)
    controller.gameState = controller.gameState.copy(board = Board(fields))

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

      "work with random dice" in {
        val c = controllerWithPlayers("Anna", "Ben")
        val dice = c.rollDice()
        c.hasRolled should be(true)
        dice.total should be >= 2
        dice.total should be <= 12
        c.gameState.currentPlayer.position should be(dice.total)
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

      "not change money of uninvolved player when rent is paid" in {
        val c = controllerWithPlayers("Anna", "Ben", "Carl")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.endTurn()
        c.rollDice(Dice(1, 2))
        c.gameState.players(2).money should be(1500)
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

      "charge utility rent with multiplier 4 when owner has one utility" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.buyProperty()
        c.endTurn()
        val utilityDice = Dice(6, 6)
        c.rollDice(utilityDice)
        c.gameState.currentPlayer.money should be(1500 - 4 * 12)
        c.gameState.players.head.money should be(1350 + 4 * 12)
      }

      "charge utility rent with multiplier 10 when owner has both utilities" in {
        val c = controllerWithPlayers("Anna", "Ben")
        // Give Anna both utilities directly on the board
        val fields = c.gameState.board.fields
        val f12 = fields(12).asInstanceOf[UtilityField].buyBy("Anna")
        val f28 = fields(28).asInstanceOf[UtilityField].buyBy("Anna")
        c.gameState = c.gameState.copy(board = Board(fields.updated(12, f12).updated(28, f28)))
        // Anna rolls somewhere safe (FreeParkingField at 20)
        setPosition(c, 0, 19)
        c.rollDice(Dice(1, 0))
        c.endTurn()
        // Ben lands on Elektrizitaetswerk at position 12
        c.rollDice(Dice(6, 6))
        c.gameState.currentPlayer.money should be(1500 - 10 * 12)
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

      "show field name when landing on JailField (visit)" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 6)
        c.rollDice(Dice(2, 2))
        c.gameState.currentPlayer.position should be(10)
        c.message should include("Gefängnis")
      }

      "show field name when landing on FreeParkingField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 15)
        c.rollDice(Dice(2, 3))
        c.gameState.currentPlayer.position should be(20)
        c.message should include("Frei Parken")
      }

      "show for sale message for unowned StationField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 3))
        c.message should include("Hauptbahnhof")
        c.message should include("Verkauf")
      }

      "show for sale message for unowned UtilityField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.message should include("Elektrizitaetswerk")
        c.message should include("Verkauf")
      }
    }

    "card effects" should {
      "handle ReceiveMoney" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("Erhalte 100€", ReceiveMoney(100)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.money should be(1600)
        c.message should include("Karte")
      }

      "handle PayMoney" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("Zahle 50€", PayMoney(50)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.money should be(1450)
      }

      "handle GoToJail" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("Ins Gefängnis", GoToJail))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(10)
      }

      "handle MoveToField with Go bonus" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 10)
        putChanceCard(c, 13, Card("Zu Los", MoveToField(0)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(0)
        c.gameState.currentPlayer.money should be(1700)
      }

      "handle MoveToField without Go bonus" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("Zu Feld 10", MoveToField(10)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(10)
        c.gameState.currentPlayer.money should be(1500)
      }

      "handle MoveRelative" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("3 zurück", MoveRelative(-3)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(0)
      }

      "handle CollectFromPlayers" in {
        val c = controllerWithPlayers("Anna", "Ben", "Carl")
        putChanceCard(c, 3, Card("10€ von jedem", CollectFromPlayers(10)))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.money should be(1520)
        c.gameState.players(1).money should be(1490)
        c.gameState.players(2).money should be(1490)
      }

      "handle MoveToNextStation" in {
        val c = controllerWithPlayers("Anna", "Ben")
        putChanceCard(c, 3, Card("Nächster Bahnhof", MoveToNextStation))
        c.rollDice(Dice(1, 2))
        c.gameState.currentPlayer.position should be(5)
      }

      "handle MoveToNextStation with wrap around and Go bonus" in {
        val c = controllerWithPlayers("Anna", "Ben")
        setPosition(c, 0, 34)
        putChanceCard(c, 36, Card("Nächster Bahnhof", MoveToNextStation))
        c.rollDice(Dice(1, 1))
        c.gameState.currentPlayer.position should be(5)
        c.gameState.currentPlayer.money should be(1700)
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

    "showing properties" should {
      "return empty list when player owns nothing" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.currentPlayerProperties should be(empty)
      }

      "list bought PropertyField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        val props = c.currentPlayerProperties
        props.length should be(1)
        props.head should include("Konzilstrasse")
      }

      "list bought StationField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 3))
        c.buyProperty()
        val props = c.currentPlayerProperties
        props.length should be(1)
        props.head should include("Hauptbahnhof")
      }

      "list bought UtilityField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.buyProperty()
        val props = c.currentPlayerProperties
        props.length should be(1)
        props.head should include("Elektrizitaetswerk")
      }

      "list multiple properties" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.endTurn()
        c.rollDice(Dice(2, 3))
        c.endTurn()
        setPosition(c, 0, 0)
        c.rollDice(Dice(2, 3))
        c.buyProperty()
        c.currentPlayerProperties.length should be(2)
      }

      "not show properties of other players" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.endTurn()
        c.currentPlayerProperties should be(empty)
      }
    }

    "currentFieldOwner" should {
      "return owner name for owned PropertyField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.buyProperty()
        c.currentFieldOwner should be("Anna")
      }

      "return Niemand for unowned PropertyField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(1, 2))
        c.currentFieldOwner should be("Niemand")
      }

      "return Niemand for non-buyable field" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.currentFieldOwner should be("Niemand")
      }

      "return owner name for owned StationField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(2, 3))
        c.buyProperty()
        c.currentFieldOwner should be("Anna")
      }

      "return owner name for owned UtilityField" in {
        val c = controllerWithPlayers("Anna", "Ben")
        c.rollDice(Dice(6, 6))
        c.buyProperty()
        c.currentFieldOwner should be("Anna")
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
