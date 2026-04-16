package de.konstopoly.model

import org.scalatest.matchers.should.Matchers
import de.konstopoly.model.fields.*
import org.scalatest.wordspec.AnyWordSpec

class FieldSpec extends AnyWordSpec with Matchers:

  // PropertyField 

  val untereLabue: PropertyField = PropertyField("Untere Laube", 60, "braun", 2)

  "PropertyField" should {
    "have no owner initially" in {
      untereLabue.isUnowned shouldBe true
      untereLabue.owner shouldBe None
    }
    "have an owner after being bought" in {
      val bought = untereLabue.buyBy("Alice")
      bought.owner shouldBe Some("Alice")
      bought.isOwned shouldBe true
    }
    "charge no rent when unowned" in {
      untereLabue.currentRent shouldBe 0
    }
    "charge the correct rent after being bought" in {
      untereLabue.buyBy("Alice").currentRent shouldBe 2
    }
    "store price and color group correctly" in {
      untereLabue.price shouldBe 60
      untereLabue.colorGroup shouldBe "braun"
    }
  }

  // GoField 

  "GoField" should {
    "have default name 'Los' and salary 200" in {
      val go = GoField()
      go.name shouldBe "Los"
      go.salary shouldBe 200
    }
    "accept a custom salary" in {
      GoField(salary = 400).salary shouldBe 400
    }
  }

  // TaxField

  "TaxField" should {
    "store name and amount correctly" in {
      val tax = TaxField("Einkommensteuer", 200)
      tax.name shouldBe "Einkommensteuer"
      tax.amount shouldBe 200
    }
    "store a different amount correctly" in {
      TaxField("Zusatzsteuer", 100).amount shouldBe 100
    }
  }

  // ChanceField 

  "ChanceField" should {
    val cards = Vector(
      Card("Erhalte 100€", ReceiveMoney(100)),
      Card("Zahle 50€", PayMoney(50)),
      Card("Ins Gefängnis", GoToJail),
      Card("Rücke 3 zurück", MoveRelative(-3)),
      Card("Zum nächsten Bahnhof", MoveToNextStation),
      Card("Zu Los", MoveToField(0)),
      Card("10€ von jedem", CollectFromPlayers(10))
    )
    val chanceField = ChanceField("Ereignis", cards)

    "have the correct name" in {
      chanceField.name shouldBe "Ereignis"
    }
    "draw a card by index" in {
      chanceField.drawCard(0).description shouldBe "Erhalte 100€"
      chanceField.drawCard(0).effect shouldBe ReceiveMoney(100)
    }
    "wrap the index cyclically" in {
      chanceField.drawCard(7).description shouldBe "Erhalte 100€"
    }
    "store all CardEffect types correctly" in {
      chanceField.drawCard(1).effect shouldBe PayMoney(50)
      chanceField.drawCard(2).effect shouldBe GoToJail
      chanceField.drawCard(3).effect shouldBe MoveRelative(-3)
      chanceField.drawCard(4).effect shouldBe MoveToNextStation
      chanceField.drawCard(5).effect shouldBe MoveToField(0)
      chanceField.drawCard(6).effect shouldBe CollectFromPlayers(10)
    }
    "throw an exception when created with no cards" in {
      an[IllegalArgumentException] should be thrownBy ChanceField("Empty", Vector.empty)
    }
  }

  // StationField 

  "StationField" should {
    "have the correct name and default price 200" in {
      val station = StationField("Hauptbahnhof")
      station.name shouldBe "Hauptbahnhof"
      station.price shouldBe 200
    }
    "have no owner initially" in {
      StationField("Hauptbahnhof").isUnowned shouldBe true
    }
    "have an owner after being bought" in {
      val bought = StationField("Hauptbahnhof").buyBy("Bob")
      bought.owner shouldBe Some("Bob")
      bought.isOwned shouldBe true
    }
  }

  // UtilityField 

  "UtilityField" should {
    "have the correct name and default price 150" in {
      val utility = UtilityField("Elektrizitaetswerk")
      utility.name shouldBe "Elektrizitaetswerk"
      utility.price shouldBe 150
    }
    "have no owner initially" in {
      UtilityField("Wasserwerk").isUnowned shouldBe true
    }
    "have an owner after being bought" in {
      val bought = UtilityField("Wasserwerk").buyBy("Bob")
      bought.owner shouldBe Some("Bob")
      bought.isOwned shouldBe true
    }
  }

  // Jail udn free Parkig

  "JailField" should {
    "have the default name 'Gefängnis / Besuch'" in {
      JailField().name shouldBe "Gefängnis / Besuch"
    }
  }

  "FreeParkingField" should {
    "have the default name 'Frei Parken'" in {
      FreeParkingField().name shouldBe "Frei Parken"
    }
  }

  "GoToJailField" should {
    "have the default name 'Gehe in Gefängnis'" in {
      GoToJailField().name shouldBe "Gehe in Gefängnis"
    }
  }
