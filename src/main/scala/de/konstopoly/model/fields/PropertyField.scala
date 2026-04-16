package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Ein kaufbares Grundstücksfeld. Kaufen bedeutet gleichzeitig ein Haus bauen.
// Beim Betreten wird Miete fällig falls das Feld einem anderen Spieler gehört.
case class PropertyField(
  name: String,
  price: Int,
  colorGroup: String,
  rent: Int,
  owner: Option[String] = None
) extends Field:

  def currentRent: Int = if owner.isDefined then rent else 0

  def buyBy(playerName: String): PropertyField = copy(owner = Some(playerName))

  def isOwned: Boolean = owner.isDefined
  def isUnowned: Boolean = owner.isEmpty
