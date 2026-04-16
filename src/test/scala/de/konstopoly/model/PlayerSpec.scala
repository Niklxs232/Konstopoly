package de.konstopoly.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

// Tests für die Player-Klasse.
// Prüft: Initialisierung mit Name/Startkapital, Bewegung auf dem Brett,
// Geld hinzufügen/abziehen und Grundstücke kaufen/besitzen.
class PlayerSpec extends AnyWordSpec with Matchers:
  "A Player" when {

    //initialiesierung
    "created with name, position, money and figure" should {

      val player = Player("Jasmin", 1500, figure = "Schiff")

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
        player.properties should be(0)
      }

      "have the correct figure" in {
        player.figure should be
      }

    }
  }

  "moving on board" should {

    "move forward by the dice value" in {
      val player = ("Jasmin", 1500, figure = "Schiff")
      val moved = player.move(5)
      moved.posistion should be (5)
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
       val player = player ("Jasmin", 1500, figure = "Schiff")
       val updated = player.addmoney(200)
       updated.money should be (1700)
     }

    "decrease money when paying" in {
      val player = ("Jasmin", 1500, figure = "Schiff")
      val updated = player.removemoney (300)
      updated.money should be (1200)
    }
  }

  //Grundstücke kaufen und besitzen
  "buying proerties" should {

    "own a proerty after buying" in {
      val player = Player("Jasmin", 1500, figure = "Schiff")
      val field = Field("Münster", 200)
      val updated = player.buyProperty(field)
      updated.properties should contain(field)
    }

    "have less money after buying a property" in {
      val player = Player("Jasmin", 1500, figure = "Schiff")
      val field = Field("Münster", 200)
      val updated = player.buyProperty(field)
      updated.money should be(1300)
    }
  }

  //Figur auswählen
  "choosing a figure" in {

    "have the correct figure after Selected" in {
      val player = ("Jasmin", 1500, figure = ("Schiff")
      player.figure should be ("Schiff")
    }

    "two player should have different figures" in {
      val player1 = Player("Jasmin", 1500, figure = "Schiff")
      val player2 = Player ("Niklas", 1500, figure = "Fahrrad")
      player1.figure should not be(player2.figure)
    }

  }


