package de.konstopoly.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class BoardSpec extends AnyWordSpec {

  "A Board" when {

    // ─── 1. INITIALISIERUNG ────────────────────────────────────
    "created" should {

      val board = Board()

      "have exactly 40 fields" in {
        board.fields.length should be(40)
      }

      "start with Los at position 0" in {
        board.getField(0).name should be("Los")
      }

      "have Hauptbahnhof at position 5" in {
        board.getField(5).name should be("Hauptbahnhof")
      }

      "have Frei Parken at position 20" in {
        board.getField(20).name should be("Frei Parken")
      }

      "have Auf der Insel at position 39" in {
        board.getField(39).name should be("Auf der Insel")
      }
    }

    //feld abrufen
    "getting a field" should {

      val board = Board()

      "return the correct field by position" in {
        board.getField(1).name should be("Untere Laube")
      }

      "return the correct price for a field" in {
        board.getField(1).price should be(60)
      }

      "return Bahnhof Petershausen at position 15" in {
        board.getField(15).name should be("Bahnhof Petershausen")
      }

      "return Wasserwerk at position 28" in {
        board.getField(28).name should be("Wasserwerk")
      }
    }

    //feldtyp
    "checking field types" should {

      val board = Board()

      "have a buyable street at position 1" in {
        board.getField(1).isBuyable should be(true)
      }

      "have a non-buyable field at position 0" in {
        board.getField(0).isBuyable should be(false)
      }

      "have a non-buyable field at position 2" in {
        board.getField(2).isBuyable should be(false)
      }
    }
  }
}