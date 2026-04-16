package de.konstopoly.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.konstopoly.model.fields.PropertyField

// Tests für den Field-Trait und seine Unterklassen.
// Prüft: landOn-Verhalten für PropertyField, TaxField,
// ChanceField und GoField jeweils mit den erwarteten Effekten.
class FieldSpec extends AnyFlatSpec with Matchers:

  val badstrasse: PropertyField = PropertyField(
    name       = "Badstraße",
    colorGroup = "Lila",
    price      = 60,
    housePrice = 50,
    rents      = Vector(2, 10, 30, 90, 160, 250)
  )

  "PropertyField" should "am Anfang keinen Besitzer haben" in {
    badstrasse.isUnowned shouldBe true
  }

  it should "nach dem Kauf einen Besitzer haben" in {
    val gekauft = badstrasse.buyBy("Alice")
    gekauft.owner shouldBe Some("Alice")
  }

  it should "die Grundmiete ohne Häuser zurückgeben" in {
    badstrasse.currentRent shouldBe 2
  }

  it should "nach dem Bau eines Hauses die richtige Miete berechnen" in {
    val mitHaus = badstrasse.buildHouse
    mitHaus.currentRent shouldBe 10
  }

  it should "nach 4 Häusern ein Hotel bauen können" in {
    val mitHotel = badstrasse
      .buildHouse.buildHouse.buildHouse.buildHouse
      .buildHotel
    mitHotel.hasHotel shouldBe true
    mitHotel.currentRent shouldBe 250
  }

  it should "bei Hypothek keine Miete verlangen" in {
    val mitHypothek = badstrasse.mortgage
    mitHypothek.currentRent shouldBe 0
  }
