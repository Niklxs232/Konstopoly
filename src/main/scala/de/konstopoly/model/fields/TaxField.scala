package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Ein Steuerfeld. Beim Betreten muss der Spieler einen
// festgelegten Betrag an die Bank zahlen.
case class TaxField(
  name: String,
  amount: Int
) extends Field

