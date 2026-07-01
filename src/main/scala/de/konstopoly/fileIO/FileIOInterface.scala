package de.konstopoly.fileIO

import de.konstopoly.model.GameState

trait FileIOInterface:
  def save(gameState: GameState): Unit
  def load(): GameState
