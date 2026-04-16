package de.konstopoly.model.fields

// Ein kaufbares Grundstücksfeld. Enthält Kaufpreis, Mietpreise,
// Hausanzahl sowie den aktuellen Besitzer. Beim Betreten wird
// Miete fällig falls das Feld einem anderen Spieler gehört.
case class PropertyField(
  name: String,
  colorGroup: String,
  price: Int,
  housePrice: Int,
  // Mietpreise: Index 0 = Grundmiete, 1-4 = Häuser, 5 = Hotel
  rents: Vector[Int],
  houses: Int = 0,
  hasHotel: Boolean = false,
  isMortgaged: Boolean = false,
  owner: Option[String] = None
):
  require(houses >= 0 && houses <= 4, "Häuseranzahl muss zwischen 0 und 4 liegen")
  require(rents.size == 6, "Es müssen genau 6 Mietpreise angegeben werden (0 Häuser bis Hotel)")

  def currentRent: Int =
    if isMortgaged then 0
    else if hasHotel then rents(5)
    else rents(houses)

  def buyBy(playerName: String): PropertyField =
    copy(owner = Some(playerName))

  def buildHouse: PropertyField =
    require(!hasHotel && houses < 4, "Kann kein weiteres Haus bauen")
    copy(houses = houses + 1)

  def buildHotel: PropertyField =
    require(houses == 4, "Für ein Hotel werden 4 Häuser benötigt")
    copy(houses = 0, hasHotel = true)

  def mortgage: PropertyField =
    require(houses == 0 && !hasHotel, "Häuser müssen zuerst verkauft werden")
    copy(isMortgaged = true)

  def unmortgage: PropertyField =
    copy(isMortgaged = false)

  def isOwned: Boolean = owner.isDefined
  def isUnowned: Boolean = owner.isEmpty
