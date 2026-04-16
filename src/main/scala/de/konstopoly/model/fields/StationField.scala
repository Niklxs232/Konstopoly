package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Ein Bahnhofsfeld. Kaufpreis 200€. Die Mieteinnahmen steigen
// je mehr Bahnhöfe ein Spieler besitzt (Logik im Controller).
case class StationField(
  name: String,
  price: Int = 200,
  owner: Option[String] = None
) extends Field:

  def buyBy(playerName: String): StationField = copy(owner = Some(playerName))
  def isOwned: Boolean = owner.isDefined
  def isUnowned: Boolean = owner.isEmpty
