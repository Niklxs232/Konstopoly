package de.konstopoly.view

import java.awt.Color

// Kleine Hilfe, damit Spielfiguren und die Spieler-Liste die gleichen

object PlayerColors:
  private val colors = Vector(
    Color.red,
    Color.blue,
    new Color(0, 153, 0),   // gruen
    new Color(255, 140, 0)  // orange
  )

  def color(i: Int): Color = colors(i % colors.size)
