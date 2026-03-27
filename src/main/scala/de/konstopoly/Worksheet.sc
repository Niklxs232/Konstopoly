case class Player(name: String, position: Int = 0, money: Int = 1500)

case class Street(name: String, price: Int)

// Test zugriff
val p1 = Player("Jasmin")
val p2 = Player("Niklas")

val go = Street(name = "Los", price = 0)
val Field1 = Street (name = "HTWG", price = 150)
val Field2 = Street (name = "Schottenplatz", price = 150)