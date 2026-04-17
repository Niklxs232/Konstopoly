package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Versorgungswerk (Elektrizität oder Wasser). Kaufpreis 150€.
case class UtilityField(
  name: String,
  price: Int = 150,
  owner: Option[String] = None
) extends Field:

  def buyBy(playerName: String): UtilityField = copy(owner = Some(playerName))
  def isOwned: Boolean = owner.isDefined
  def isUnowned: Boolean = owner.isEmpty
