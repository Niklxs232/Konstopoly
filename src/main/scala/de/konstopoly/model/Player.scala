package de.konstopoly.model

// Repräsentiert einen einzelnen Spieler im Spiel.
// Enthält:
//   - name:       Anzeigename des Spielers
//   - money:      aktuelles Guthaben (startet bei PlayerConfig.startMoney)
//   - position:   aktuelle Feldposition auf dem Brett (0–39)
//   - properties: Liste der gekauften Grundstücke (PropertyField)
//   - inJail:     ob der Spieler gerade im Gefängnis sitzt
//   - jailTurns:  wie viele Runden der Spieler noch im Gefängnis bleibt
case class Player (
  name: String,
  money: Int = 1500,
  position: Int= 0,
  figure: String = ""
  ) {

  //bewegen
  def move(steps: Int): Player =
    copy(position = (position + steps) % 40)

  //geld
  def addMoney(amount: Int): Player =
    copy(money = money + amount)

  def removeMoney(amount: Int): Player =
    copy(money = money - amount)

  //figure
  def chooseFigure(newFigure: String): Player =
    copy(figure = newFigure)
  
}

