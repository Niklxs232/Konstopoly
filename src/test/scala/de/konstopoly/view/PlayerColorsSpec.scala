package de.konstopoly.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PlayerColorsSpec extends AnyWordSpec {

  "PlayerColors" should {
    "give different colors to the first players" in {
      PlayerColors.color(0) should not be PlayerColors.color(1)
      PlayerColors.color(1) should not be PlayerColors.color(2)
    }

    "cycle through the colors with modulo" in {
      // 4 Farben sind hinterlegt, danach beginnt es wieder von vorne
      PlayerColors.color(0) should be(PlayerColors.color(4))
      PlayerColors.color(2) should be(PlayerColors.color(6))
    }
  }
}
