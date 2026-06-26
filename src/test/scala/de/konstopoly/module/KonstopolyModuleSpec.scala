package de.konstopoly.module

import com.google.inject.Guice
import de.konstopoly.controller.{ControllerInterface, GameController}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

// Prueft, dass die Dependency Injection wirklich funktioniert:
// Der Injector soll hinter dem ControllerInterface eine GameController-Instanz
// liefern, und zwar immer dieselbe (Singleton).
class KonstopolyModuleSpec extends AnyWordSpec with Matchers:

  "The KonstopolyModule" should {
    val injector = Guice.createInjector(new KonstopolyModule)

    "bind ControllerInterface to a GameController" in {
      val controller = injector.getInstance(classOf[ControllerInterface])
      controller shouldBe a[GameController]
    }

    "always return the same controller instance (Singleton)" in {
      val c1 = injector.getInstance(classOf[ControllerInterface])
      val c2 = injector.getInstance(classOf[ControllerInterface])
      c1 should be theSameInstanceAs c2
    }
  }
