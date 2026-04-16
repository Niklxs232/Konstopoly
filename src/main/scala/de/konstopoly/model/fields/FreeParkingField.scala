package de.konstopoly.model.fields

import de.konstopoly.model.Field

// Freiparkfeld (Position 20). Keine Auswirkung beim Betreten.
case class FreeParkingField(name: String = "Frei Parken") extends Field
