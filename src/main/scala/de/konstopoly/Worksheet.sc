//Player
case class Player(name: String, position: Int = 0, money: Int = 1500)

case class Field(name: String, price: Int, color: String)

case class DiceResult(die1: Int, die2 : Int):
  def sum: Int = die1 + die2

//Field
val board = Vector(
    Field("Los",          0,   "white"),
    Field("HTWG",       150,   "blue"),
    Field("Seerhein",   120,   "blue"),
    Field("Stadtgarten", 100,  "green"),
    Field("Bodensee",   200,   "green"),
    Field("Hafen",       80,   "yellow")
  )

//Dice
def rollDice(): DiceResult =
  val r = new scala.util.Random
  DiceResult(r.nextInt(6) + 1, r.nextInt(6) + 1)

def movePlayer(player: Player, dice: DiceResult): Player =
  player.copy(position = (player.position + dice.sum) % board.length)

def currentField(player: Player): Field =
  board(player.position)

// Spieler erstellen
val player1 = Player("Jasmin")
val player2 = Player("Niklas")

// Daten abrufen
player1.name
player1.position
player1.money

// Das Feld ansehen, auf dem Spieler 1 steht
currentField(player1)
currentField(player1).name

// Würfeln
val wurf = rollDice()
wurf.die1
wurf.die2
wurf.sum

// Spieler bewegen
val player1nachZug = movePlayer(player1, wurf)
player1nachZug.position

// Auf welchem Feld ist Spieler 1 jetzt?
currentField(player1nachZug)
currentField(player1nachZug).name

// Spielbrett anschauen
board.length
board(0).name
board(0).price





