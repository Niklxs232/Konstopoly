package de.konstopoly

import de.konstopoly.controller.GameController
import de.konstopoly.model.PlayerConfig
import scala.io.StdIn

@main def main(): Unit =
  val controller = new GameController
  println("=== Willkommen bei Konstopoly! ===")
  println()

  val count = readPlayerCount()
  val names = readPlayerNames(count)

  controller.startGame(names)
  println(controller.message)
  gameLoop(controller)

private def readPlayerCount(): Int =
  print(s"Anzahl Spieler (${PlayerConfig.minPlayers}-${PlayerConfig.maxPlayers}): ")
  val input = StdIn.readLine().trim.toIntOption
  if input.isDefined && input.get >= PlayerConfig.minPlayers && input.get <= PlayerConfig.maxPlayers then
    input.get
  else
    println(s"Bitte eine Zahl zwischen ${PlayerConfig.minPlayers} und ${PlayerConfig.maxPlayers} eingeben.")
    readPlayerCount()

private def readPlayerNames(count: Int): List[String] =
  (1 to count).map { i =>
    print(s"Name Spieler $i: ")
    StdIn.readLine().trim
  }.toList

private def gameLoop(controller: GameController): Unit =
  var running = true
  while running do
    println()
    printState(controller)
    printCommands(controller)
    print("> ")
    val input = StdIn.readLine()
    if input == null then
      running = false
    else
      running = handleInput(input.trim.toLowerCase, controller)

  println("Spiel beendet. Auf Wiedersehen!")

private def printCommands(controller: GameController): Unit =
  if !controller.hasRolled then
    println("Befehle: (r)oll | (p)roperties | (q)uit")
  else
    println("Befehle: (b)uy | (e)nd | (p)roperties | (q)uit")

private def handleInput(input: String, controller: GameController): Boolean =
  input match
    case "roll" | "r" =>
      controller.rollDice()
      println(controller.message)
    case "buy" | "b" =>
      if controller.buyProperty() then
        println(controller.message)
      else
        println("Kaufen nicht möglich. Besitzer ist: " + controller.currentFieldOwner)
    case "end" | "e" =>
      controller.endTurn()
      println(controller.message)
    case "properties" | "p" =>
      val props = controller.currentPlayerProperties
      if props.isEmpty then
        println("Du besitzt noch keine Grundstücke.")
      else
        println("Deine Grundstücke:")
        props.foreach(p => println(s"  - $p"))
    case "quit" | "q" =>
      return false
    case _ =>
      println("Unbekannter Befehl.")

  checkWinner(controller)

private def checkWinner(controller: GameController): Boolean =
  controller.gameState.winner match
    case Some(w) =>
      println(s"\n${w.name} hat gewonnen!")
      false
    case None =>
      true

private def printState(controller: GameController): Unit =
  val state = controller.gameState
  val current = state.currentPlayer
  println(s"--- Runde ${state.round} ---")
  state.players.foreach { p =>
    val marker = if p.name == current.name then " <--" else ""
    val fieldName = state.board.fieldAt(p.position).name
    println(s"  ${p.name}: ${p.money}€ | Feld ${p.position} (${fieldName})$marker")
  }
