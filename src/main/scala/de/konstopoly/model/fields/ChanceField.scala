package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Ein Ereignisfeld. Beim Betreten wird eine zufällige Ereigniskarte
// gezogen, die positive oder negative Auswirkungen auf den Spieler hat
// (z.B. Geld erhalten, Position wechseln, ins Gefängnis).
case class ChanceField(
  name: String,
  cards: Vector[String]
) extends Field:
  require(cards.nonEmpty, "Es muss mindestens eine Ereigniskarte vorhanden sein")

  def drawCard(index: Int): String = cards(index % cards.size)
