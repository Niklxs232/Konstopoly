package de.konstopoly.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

// Tests für die Player-Klasse.
// Prüft: Initialisierung mit Name/Startkapital, Bewegung auf dem Brett,
// Geld hinzufügen/abziehen und Grundstücke kaufen/besitzen.
class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" when {

    //initialiesierung
    "created with name, position, money and figure" should {

      val player = Player("Jasmin", figure = "Schiff")

      "have the correct name" in {
        player.name should be ("Jasmin")
      }

      "start with 1500 money" in {
        player.money should be (1500)
      }

      "start at position 0" in {
        player.position should be (0)
      }
      

      "have the correct figure" in {
        player.figure should be ("Schiff")
      }
    }

    //bewegen
    "moving on board" should {
  
      "move forward by the dice value" in {
        val player = Player("Jasmin", 1500, figure = "Schiff")
        val moved = player.move(5)
        moved.position should be (5)
      }

      "wrap around when passing field 40" in {
        val player = Player("Jasmin", 1500, position = 38, figure = "Schiff")
        val moved = player.move(4)
        moved.position should be(2)
      }

      "land exactly on field 40 and wrap to 0" in {
        val player = Player("Jasmin", 1500, position = 36, figure = "Schiff")
        val moved = player.move(4)
        moved.position should be(0)
      }
    }

    //Geld hinzufügen und abzehen
    "managing money" should {
  
      "increase money when reseving payment" in {
        val player = Player("Jasmin", 1500, figure = "Schiff")
        val updated = player.addMoney(200)
        updated.money should be (1700)
       }
  
      "decrease money when paying" in {
        val player = Player("Jasmin", 1500, figure = "Schiff")
        val updated = player.removeMoney (300)
        updated.money should be (1200)
      }
    }
  
    
  
    //Figur auswählen
    "choosing a figure" should {
  
      "have the correct figure after Selected" in {
        val player = Player("Jasmin", 1500, figure = "Schiff")
        player.figure should be ("Schiff")
      }

      "be able to change figure" in {
        val player = Player("Jasmin", 1500, figure = "Schiff")
        val updated = player.chooseFigure("Fahrrad")
        updated.figure should be("Fahrrad")
      }
  
      "two player should have different figures" in {
        val player1 = Player("Jasmin", 1500, figure = "Schiff")
        val player2 = Player ("Niklas", 1500, figure = "Fahrrad")
        player1.figure should not be(player2.figure)
      }

      "should use empty string as default figure" in {
        val player = Player("Test", 1500)
        player.figure should be("")
      }
  
    }
  }
}

