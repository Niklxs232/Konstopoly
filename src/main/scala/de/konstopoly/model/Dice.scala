package de.konstopoly.model

import scala.util.Random

case class Dice (
  dice1: Int = Random.between(1,7),
  dice2: Int = Random.between(1, 7)
                ) {
  val total: Int = dice1 + dice2
}
