package de.konstopoly.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class ObserverSpec extends AnyWordSpec {

  "An Observable" when {

    "a subscriber is added" should {
      "notify the subscriber on notifyObservers" in {
        val observable = new Observable
        var updated = false
        val observer = new Observer:
          def update: Unit = updated = true
        observable.add(observer)
        observable.notifyObservers()
        updated should be(true)
      }
    }

    "a subscriber is removed" should {
      "no longer notify the removed subscriber" in {
        val observable = new Observable
        var updateCount = 0
        val observer = new Observer:
          def update: Unit = updateCount += 1
        observable.add(observer)
        observable.notifyObservers()
        updateCount should be(1)
        observable.remove(observer)
        observable.notifyObservers()
        updateCount should be(1)
      }
    }

    "multiple subscribers are added" should {
      "notify all subscribers" in {
        val observable = new Observable
        var count1 = 0
        var count2 = 0
        val observer1 = new Observer:
          def update: Unit = count1 += 1
        val observer2 = new Observer:
          def update: Unit = count2 += 1
        observable.add(observer1)
        observable.add(observer2)
        observable.notifyObservers()
        count1 should be(1)
        count2 should be(1)
      }
    }

    "no subscribers are added" should {
      "not fail on notifyObservers" in {
        val observable = new Observable
        noException should be thrownBy observable.notifyObservers()
      }
    }
  }
}
