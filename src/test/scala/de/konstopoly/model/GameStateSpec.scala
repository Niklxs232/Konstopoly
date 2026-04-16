package de.konstopoly.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
// Tests für den GameState.
// Prüft: korrekter aktiver Spieler, Spielerwechsel zur nächsten Runde,
// Erkennung von Spielende (alle außer einem Spieler bankrott).
class GameStateSpec extends AnyWordSpec{
  val board = Board()
  val player1 = Player("Jasmin", figure = "Schiff")
  val player2 = Player("Niklas", figure = "Fahrrad")
  val player3 = Player("Leon", figure = "Imperia")
  val player4 = Player("Paul", figure = "Bier")

  "a Gamestate" when {

    //initialisirung
    "created with 2 players" should {

      val state = GameState(List(player1, player2), board)

      "have correct number of players" in {
        state.players.length should be(2)
      }

      "start at round 1" in {
        state.round should be(1)
      }

      "have the first player as current player" in {
        state.currentPlayer.name should be ("Jasmin")
      }

      "have a board with 40 fields" in {
        state.board.fields.length should be(40)
      }
    }

    //spieleranzahl
    "check player count" should {

      "allow 2 player" in {
        val state = GameState(List(player1, player2), board)
        state.players.length should be(2)
      }

      "allow 3 player" in {
        val state = GameState(List(player1, player2, player3), board)
        state.players.length should be(3)
      }

      "allow 4 player" in {
        val state = GameState(List(player1, player2, player3, player4), board)
        state.players.length should be(4)
      }
    }

    //spielerwechsel
    "switching to next player" should {

      "switch from player1 to player2" in {
        val state = GameState(List(player1, player2), board)
        val next = state.nextPlayer
        next.currentPlayer.name should be("Niklas")
      }

      "wrap around from last player to first player" in {
        val state = GameState(List(player1, player2), board, currentPlayerIndex = 1)
        val next = state.nextPlayer
        next.currentPlayer.name should be("Jasmin")
      }

      "increase round when all players have had their turn" in {
        val state = GameState(List(player1, player2), board, currentPlayerIndex = 1)
        val next = state.nextPlayer
        next.round should be(2)
      }
    }

    //gewinner
    "checking for winner" should {

      "have no winner when all players have money" in {
        val state = GameState(List(player1, player2), board)
        state.winner should be(None)
      }

      "have a winner when only one player has money" in {
        val broke = player2.copy(money = 0)
        val state = GameState(List(player1, broke), board)
        state.winner should be(Some(player1))
      }

      "have no winner when two players still have money" in {
        val state = GameState(List(player1, player2, player3), board)
        state.winner should be(None)
      }
    }
  }
}
