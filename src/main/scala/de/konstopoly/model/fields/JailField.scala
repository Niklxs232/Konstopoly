package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Gefängnis-/Besuchsfeld (Position 10). Beim normalen Betreten
// ist der Spieler nur auf Besuch – keine Auswirkung.
case class JailField(name: String = "Gefängnis / Besuch") extends Field
