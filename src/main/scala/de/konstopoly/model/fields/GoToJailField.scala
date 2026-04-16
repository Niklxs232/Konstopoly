package de.konstopoly.model.fields

import de.konstopoly.model.Field

// "Gehe in Gefängnis"-Feld (Position 30). Beim Betreten wird
// der Spieler direkt ins Gefängnis (Position 10) geschickt.
case class GoToJailField(name: String = "Gehe in Gefängnis") extends Field
