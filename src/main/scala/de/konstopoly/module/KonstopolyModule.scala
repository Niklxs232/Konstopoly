package de.konstopoly.module

import com.google.inject.{AbstractModule, Scopes}
import de.konstopoly.controller.{ControllerInterface, GameController}
import de.konstopoly.fileIO.{FileIOInterface, FileIOJson, FileIOXml}

class KonstopolyModule extends AbstractModule:
  override def configure(): Unit =
    bind(classOf[ControllerInterface])
      .to(classOf[GameController])
      .in(Scopes.SINGLETON)

    // Hier wird die FileIO-Implementierung gewählt (für XML: FileIOXml).
    bind(classOf[FileIOInterface])
      .to(classOf[FileIOJson])
      .in(Scopes.SINGLETON)
