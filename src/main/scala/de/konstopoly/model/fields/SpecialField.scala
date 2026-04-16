package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Ein Sonderfeld ohne Kauf- oder Mietzweck.
// Wird für Gefängnis/Besuch, Frei Parken und Gehe in Gefängnis verwendet.
case class SpecialField(name: String) extends Field
