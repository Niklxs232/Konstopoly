package de.konstopoly

import com.google.inject.Guice
import de.konstopoly.controller.ControllerInterface
import de.konstopoly.module.KonstopolyModule
import de.konstopoly.view.{GUI, TUI}

@main def main(): Unit =

  val injector = Guice.createInjector(new KonstopolyModule)

  val controller = injector.getInstance(classOf[ControllerInterface])

  val gui = new GUI(controller)
  val tui = new TUI(controller)
  tui.run()
