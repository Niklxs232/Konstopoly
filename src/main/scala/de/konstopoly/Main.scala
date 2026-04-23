package de.konstopoly

import de.konstopoly.controller.GameController
import de.konstopoly.model.PlayerConfig
import scala.io.StdIn

@main def main(): Unit =
  val controller = new GameController
  println("=== Willkommen bei Konstopoly! ===")
  println()

  val count = readPlayerCount()
  val names = (1 to count).map { i =>
    print(s"Name Spieler $i: ")
    StdIn.readLine().trim
  }.toList

  controller.startGame(names)
  println(controller.message)
  gameLoop(controller)

private def readPlayerCount(): Int =
  print(s"Anzahl Spieler (${PlayerConfig.minPlayers}-${PlayerConfig.maxPlayers}): ")
  StdIn.readLine().trim.toIntOption match
    case Some(n) if n >= PlayerConfig.minPlayers && n <= PlayerConfig.maxPlayers => n
    case _ =>
      println(s"Bitte eine Zahl zwischen ${PlayerConfig.minPlayers} und ${PlayerConfig.maxPlayers} eingeben.")
      readPlayerCount()

private def gameLoop(controller: GameController): Unit =
  var running = true
  while running do
    println()
    printState(controller)
    if !controller.hasRolled then
      println("Befehle: (r)oll | (q)uit")
    else
      println("Befehle: (b)uy | (e)nd | (q)uit")
    print("> ")
    val input = StdIn.readLine()
    if input == null then
      running = false
    else
      input.trim.toLowerCase match
        case "roll" | "r" =>
          controller.rollDice()
          println(controller.message)
        case "buy" | "b" =>
          if controller.buyProperty() then
            println(controller.message)
          else
            println("Kaufen nicht möglich.")
        case "end" | "e" =>
          controller.endTurn()
          println(controller.message)
        case "quit" | "q" =>
          running = false
        case _ =>
          println("Unbekannter Befehl.")

      controller.gameState.winner.foreach { w =>
        println(s"\n${w.name} hat gewonnen!")
        running = false
      }

  println("Spiel beendet. Auf Wiedersehen!")

private def printState(controller: GameController): Unit =
  val state = controller.gameState
  val current = state.currentPlayer
  println(s"--- Runde ${state.round} ---")
  state.players.foreach { p =>
    val marker = if p.name == current.name then " <--" else ""
    val fieldName = state.board.fieldAt(p.position).name
    println(s"  ${p.name}: ${p.money}€ | Feld ${p.position} (${fieldName})$marker")
  }
