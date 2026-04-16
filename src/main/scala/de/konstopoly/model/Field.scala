package de.konstopoly.model

// Basis-Trait für alle Felder auf dem Spielbrett.
// Definiert gemeinsame Eigenschaften wie Name und Position
// sowie die Methode landOn(player), die beim Betreten ausgeführt wird.
case class Field(
  name: String,
  price: Int,
  owner: Option[Player] = None
  )
