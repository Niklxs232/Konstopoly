package de.konstopoly.controller

import de.konstopoly.model.{Dice, GameState}
import de.konstopoly.util.Observer


trait ControllerInterface:

  // --- sich als Beobachter (Observer) anmelden ---
  def add(observer: Observer): Unit

  // --- Abfragen (nur lesen) ---
  def isStarted: Boolean
  def message: String
  def hasRolled: Boolean
  def minPlayers: Int
  def maxPlayers: Int
  def currentRound: Int
  def currentPlayerName: String
  def winner: Option[String]
  def playerInfoStrings: List[String]
  def currentPlayerProperties: List[String]
  def currentFieldOwner: String
  def gameState: GameState

  // --- Aktionen (das Spiel steuern) ---
  def startGame(playerNames: List[String]): Unit
  def rollDice(): Dice
  def buyProperty(): Boolean
  def endTurn(): Unit
  def undo(): Unit
  def redo(): Unit
  def save(): Unit
  def load(): Unit
