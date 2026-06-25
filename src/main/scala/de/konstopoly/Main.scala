package de.konstopoly

import de.konstopoly.controller.GameController
import de.konstopoly.view.{GUI, TUI}

@main def main(): Unit =
  val controller = new GameController
  // GUI und TUI sind beide Observer desselben Controllers und laufen
  // gleichzeitig. Die GUI oeffnet ein Fenster, die TUI laeuft in der Konsole.
  val gui = new GUI(controller)
  val tui = new TUI(controller)
  tui.run()
