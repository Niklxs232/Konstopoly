package de.konstopoly.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PlayerConfigSpec extends AnyWordSpec {

  "PlayerConfig" should {

    "have startMoney of 1500" in {
      PlayerConfig.startMoney should be(1500)
    }

    "have minPlayers of 2" in {
      PlayerConfig.minPlayers should be(2)
    }

    "have maxPlayers of 4" in {
      PlayerConfig.maxPlayers should be(4)
    }

    "have goBonus of 200" in {
      PlayerConfig.goBonus should be(200)
    }

    "have jailBail of 50" in {
      PlayerConfig.jailBail should be(50)
    }

    "be used as default money in Player" in {
      val player = Player("Test")
      player.money should be(PlayerConfig.startMoney)
    }
  }
}
