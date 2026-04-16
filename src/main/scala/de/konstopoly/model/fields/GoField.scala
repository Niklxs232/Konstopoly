package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Das Start-Feld (Los). Wenn ein Spieler dieses Feld betritt
// oder überquert, erhält er das festgelegte Startgeld ausgezahlt.
case class GoField(
  name: String = "Los",
  salary: Int = 200
) extends Field
