package de.konstopoly.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

// Tests für die Player-Klasse.
// Prüft: Initialisierung mit Name/Startkapital, Bewegung auf dem Brett,
// Geld hinzufügen/abziehen und Grundstücke kaufen/besitzen.
class PlayerSpec extends AnyWordSpec{
  "A Player" when {

    //initialiesierung
    "created with name and starting capital" should {

      val player = Player("Jasmin", 1500)

      "have the correct name" in {
        player.name should be ("Jasmin")
      }

      "start with 1500 money" in {
        player.money should be ("1500")
      }

      "start at position 0" in {
        player.position should be (0)
      }

      "start with no properties" in {
        player.properties should be (0)
      }
      
      "start with selected figure" in {
        player.figure
      }
    }

    //bewegung

    
  }
}
