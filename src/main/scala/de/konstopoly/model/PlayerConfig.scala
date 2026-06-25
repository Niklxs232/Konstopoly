package de.konstopoly.model

// Pattern-Singleton (SE-7)
object PlayerConfig:
  val minPlayers: Int = 2
  val maxPlayers: Int = 4
  val startMoney: Int = 1500
  val goBonus: Int = 200
  val jailBail: Int = 50
