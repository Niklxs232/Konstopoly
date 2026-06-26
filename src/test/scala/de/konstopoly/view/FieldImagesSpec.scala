package de.konstopoly.view

import de.konstopoly.model.fields.{GoField, PropertyField}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class FieldImagesSpec extends AnyWordSpec {

  "FieldImages" should {
    "return None for the board background when no image exists" in {
      FieldImages.boardBackground should be(None)
    }

    "return None for a field that has no image" in {
      FieldImages.imageFor(PropertyField("Untere Laube", 60, "braun", 2)) should be(None)
    }

    "give the same result twice (cache works)" in {
      val first  = FieldImages.imageFor(GoField())
      val second = FieldImages.imageFor(GoField())
      first should be(second)
    }
  }
}
