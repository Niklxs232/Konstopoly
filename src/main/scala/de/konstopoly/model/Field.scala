package de.konstopoly.model

// Basis-Trait für alle Felder auf dem Spielbrett.
// Jedes Feld hat mindestens einen Namen.
// Konkrete Felder (PropertyField, GoField, TaxField, ChanceField)
// erweitern diesen Trait und fügen ihr spezifisches Verhalten hinzu.
trait Field:
  def name: String
