package de.konstopoly.model

// Speichert den gesamten aktuellen Spielzustand: Liste der Spieler,
// aktueller Spieler, Runde und das Spielbrett. Wird vom Controller
// bei jeder Aktion durch einen neuen Zustand ersetzt (immutable).
case class GameState(
                      players: List[Player],
                      board: Board,
                      currentPlayerIndex: Int = 0,
                      round: Int = 1
                    ) {

  val currentPlayer: Player =
    players(currentPlayerIndex)

  def nextPlayer: GameState =
    val nextIndex = (currentPlayerIndex + 1) % players.length
    val newRound = if nextIndex == 0 then round + 1 else round
    copy(currentPlayerIndex = nextIndex, round = newRound)

  def winner: Option[Player] =
    val activePlayers = players.filter(_.money > 0)
    if activePlayers.length == 1 then Some(activePlayers.head)
    else None
}