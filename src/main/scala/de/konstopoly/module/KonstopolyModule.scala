package de.konstopoly.module

import com.google.inject.{AbstractModule, Scopes}
import de.konstopoly.controller.{ControllerInterface, GameController}

class KonstopolyModule extends AbstractModule:
  override def configure(): Unit =
    bind(classOf[ControllerInterface])
      .to(classOf[GameController])
      .in(Scopes.SINGLETON)
