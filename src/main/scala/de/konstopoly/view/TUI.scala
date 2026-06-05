package de.konstopoly.view

import de.konstopoly.controller.GameController
import de.konstopoly.util.Observer
import scala.io.StdIn
import scala.util.Try

class TUI(controller: GameController) extends Observer:
  controller.add(this)

  def update: Unit =
    println(controller.message)

  def run(): Unit =
    println("=== Willkommen bei Konstopoly! ===")
    println()
    val count = readPlayerCount()
    val names = readPlayerNames(count)
    controller.startGame(names)
    gameLoop()


  private def readPlayerCount(): Int =
    print(s"Anzahl Spieler (${controller.minPlayers}-${controller.maxPlayers}): ")
    val input = Try(StdIn.readLine().trim.toInt).toOption
    input.filter(n => n >= controller.minPlayers && n <= controller.maxPlayers) match
      case Some(n) => n
      case None =>
        println(s"Bitte eine Zahl zwischen ${controller.minPlayers} und ${controller.maxPlayers} eingeben.")
        readPlayerCount()

  private def readPlayerNames(count: Int): List[String] =
    (1 to count).map { i =>
      print(s"Name Spieler $i: ")
      StdIn.readLine().trim
    }.toList

  private def gameLoop(): Unit =
    var running = true
    while running do
      println()
      printState()
      printCommands()
      print("> ")
      Option(StdIn.readLine()) match
        case Some(input) => running = handleInput(input.trim.toLowerCase)
        case None => running = false
    println("Spiel beendet. Auf Wiedersehen!")

  private def printState(): Unit =
    println(s"--- Runde ${controller.currentRound} ---")
    controller.playerInfoStrings.foreach(println)

  private def printCommands(): Unit =
    if !controller.hasRolled then
      println("Befehle: (r)oll | (u)ndo | (p)roperties | (q)uit")
    else
      println("Befehle: (b)uy | (e)nd | (u)ndo | (p)roperties | (q)uit")

  private def handleInput(input: String): Boolean =
    input match
      case "roll" | "r" =>
        controller.rollDice()
      case "buy" | "b" =>
        if !controller.buyProperty() then
          println("Kaufen nicht möglich. Besitzer ist: " + controller.currentFieldOwner)
      case "end" | "e" =>
        controller.endTurn()
      case "undo" | "u" =>
        controller.undo()
      case "redo" =>
        controller.redo()
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
    checkWinner()

  private def checkWinner(): Boolean =
    controller.winner match
      case Some(name) =>
        println(s"\n$name hat gewonnen!")
        false
      case None =>
        true
