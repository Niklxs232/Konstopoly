package de.konstopoly

import de.konstopoly.controller.GameController
import de.konstopoly.view.TUI

@main def main(): Unit =
  val controller = new GameController
  val tui = new TUI(controller)
  tui.run()
