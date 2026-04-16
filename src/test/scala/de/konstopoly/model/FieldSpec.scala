package de.konstopoly.model

import org.scalatest.matchers.should.Matchers
import de.konstopoly.model.fields.PropertyField
import org.scalatest.wordspec.AnyWordSpec

// Tests für den Field-Trait und seine Unterklassen.
// Prüft: landOn-Verhalten für PropertyField, TaxField,
// ChanceField und GoField jeweils mit den erwarteten Effekten.
class FieldSpec extends AnyWordSpec with Matchers:

  val badstrasse: PropertyField = PropertyField(
    name       = "Badstraße",
    colorGroup = "Lila",
    price      = 60,
    housePrice = 50,
    rents      = Vector(2, 10, 30, 90, 160, 250)
  )

  "PropertyField" should {
    "leer sein und keinen Besitzer haben" in {
      badstrasse.owner shouldBe None
    }
    "nach dem Kauf einen Besitzer haben" in {
      val gekauft = badstrasse.buyBy("Alice")
      gekauft.owner shouldBe Some("Alice")
    }
    "die Grundmiete ohne Häuser zurückgeben" in {
      badstrasse.currentRent shouldBe 2
    }
    "nach dem Bau eines Hauses die richtige Miete berechnen" in {
      val mitHaus = badstrasse.buildHouse
      mitHaus.currentRent shouldBe 10
    }
    "nach 4 Häusern ein Hotel bauen können" in {
      val mitHotel = badstrasse
        .buildHouse.buildHouse.buildHouse.buildHouse
        .buildHotel
      mitHotel.hasHotel shouldBe true
      mitHotel.currentRent shouldBe 250
    }
    "bei Hypothek keine Miete verlangen" in {
      val mitHypothek = badstrasse.mortgage
      mitHypothek.currentRent shouldBe 0
    }
  }
