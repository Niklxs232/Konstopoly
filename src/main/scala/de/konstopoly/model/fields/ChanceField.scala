package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Mögliche Effekte einer Ereignis- oder Gemeinschaftskarte.
sealed trait CardEffect
case class ReceiveMoney(amount: Int)       extends CardEffect  // Geld von der Bank erhalten
case class PayMoney(amount: Int)           extends CardEffect  // Geld an die Bank zahlen
case class MoveToField(position: Int)      extends CardEffect  // Direkt zu einem Feld ziehen
case class MoveRelative(steps: Int)        extends CardEffect  // Felder vor-/zurückgehen
case class CollectFromPlayers(amount: Int) extends CardEffect  // Betrag von jedem Mitspieler kassieren
case object GoToJail                       extends CardEffect  // Ins Gefängnis
case object MoveToNextStation             extends CardEffect  // Zum nächsten Bahnhof ziehen

// Karte mit beschreibung und text
case class Card(description: String, effect: CardEffect)

// Ein Ereignisfeld. Beim Betreten wird eine Karte gezogen,
case class ChanceField(
  name: String,
  cards: Vector[Card]
) extends Field:
  require(cards.nonEmpty, "Es muss mindestens eine Ereigniskarte vorhanden sein")

  def drawCard(index: Int): Card = cards(index % cards.size)
