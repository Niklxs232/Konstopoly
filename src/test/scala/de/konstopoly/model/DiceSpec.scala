package de.konstopoly.model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class DiceSpec extends AnyWordSpec {

  "A Dice" when {

    //werte
    "rolled" should {

      "have dice1 vulue between 1-6" in {
        val dice = Dice()
        dice.dice1 should be >= 1
        dice.dice1 should be <= 6
      }

      "have dice2 vulue between 1-6" in {
        val dice = Dice()
        dice.dice2 should be >= 1
        dice.dice2 should be <= 6
      }

      //summe
      "have total between 2-12" in {
        val dice = Dice()
        dice.total should be >= 2
        dice.total should be <= 12
      }

      "have total equal to die1 + die2" in {
        val dice = Dice()
        dice.total should be(dice.dice1 + dice.dice2)
      }
    }
  }
}
