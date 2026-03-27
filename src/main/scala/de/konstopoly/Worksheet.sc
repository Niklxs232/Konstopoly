//Player
case class Player(name: String, position: Int = 0, money: Int = 1500)

val p1 = Player("Jasmin")
val p2 = p1.copy(position = p1.position + 1)
printf("Geht ein Feld weiter")

val p3 = p2.copy(money = p2.money -150)
printf("Zahlt miete für Straße HTWG - 150")

//Street
case class Street(name: String, price: Int, color: String)
val go = Street(name = "Los", price = 0, color = "red")
val Field1 = Street (name = "HTWG", price = 150, color = "blue")

//Dice
case class DiceResult(die1: Int, die2: Int) {
  def sum: Int = die1 + die2
}
def rollDice(): DiceResult = {
  val r = new scala.util.Random
  DiceResult(r.nextInt(6) + 1, r.nextInt(6) + 1)
}

// Test im Worksheet:
val throw1 = rollDice()
// Ergebnis z.B.: throw1: DiceResult = DiceResult(3,5)
println(s"Du hast eine ${throw1.sum} gewürfelt!")


