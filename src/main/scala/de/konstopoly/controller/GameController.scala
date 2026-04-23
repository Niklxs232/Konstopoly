package de.konstopoly.controller

import de.konstopoly.model.*
import de.konstopoly.model.fields.*
import de.konstopoly.util.Observable

class GameController extends Observable:
  var gameState: GameState = _
  var message: String = ""
  var hasRolled: Boolean = false

  def startGame(playerNames: List[String]): Unit =
    gameState = GameState(playerNames.map(name => Player(name)), Board())
    hasRolled = false
    message = "Spiel gestartet!"
    notifyObservers()

  def rollDice(): Dice =
    val dice = Dice()
    doRoll(dice)
    dice

  def rollDice(dice: Dice): Unit =
    doRoll(dice)

  private def doRoll(dice: Dice): Unit =
    if hasRolled then
      message = "Du hast diese Runde bereits gewürfelt."
      return
    val player = gameState.currentPlayer
    val oldPos = player.position
    val newPos = (oldPos + dice.total) % 40
    val passedGo = oldPos + dice.total >= 40
    var updated = player.copy(position = newPos)
    if passedGo then
      updated = updated.addMoney(PlayerConfig.goBonus)
      message = s"${player.name} würfelt ${dice.total} und überquert Los (+${PlayerConfig.goBonus}€). "
    else
      message = s"${player.name} würfelt ${dice.total}. "
    gameState = updateCurrentPlayer(updated)
    handleFieldEffect(newPos, dice.total)
    hasRolled = true
    notifyObservers()

  private def handleFieldEffect(position: Int, diceTotal: Int): Unit =
    val field = gameState.board.fieldAt(position)
    val currentName = gameState.currentPlayer.name
    field match
      case t: TaxField =>
        gameState = updateCurrentPlayer(gameState.currentPlayer.removeMoney(t.amount))
        message += s"Steuer: ${t.amount}€"

      case _: GoToJailField =>
        gameState = updateCurrentPlayer(gameState.currentPlayer.copy(position = 10))
        message += "Ab ins Gefängnis!"

      case p: PropertyField if p.isOwned && p.owner.get != currentName =>
        payRent(currentName, p.owner.get, p.rent)
        message += s"Miete ${p.rent}€ an ${p.owner.get}"

      case s: StationField if s.isOwned && s.owner.get != currentName =>
        val ownerName = s.owner.get
        val count = gameState.board.fields.count {
          case sf: StationField => sf.owner.contains(ownerName)
          case _ => false
        }
        val rent = 25 * math.pow(2, count - 1).toInt
        payRent(currentName, ownerName, rent)
        message += s"Bahnhofsmiete ${rent}€ an $ownerName"

      case u: UtilityField if u.isOwned && u.owner.get != currentName =>
        val ownerName = u.owner.get
        val count = gameState.board.fields.count {
          case uf: UtilityField => uf.owner.contains(ownerName)
          case _ => false
        }
        val multiplier = if count >= 2 then 10 else 4
        val rent = multiplier * diceTotal
        payRent(currentName, ownerName, rent)
        message += s"Versorgungsmiete ${rent}€ an $ownerName"

      case c: ChanceField =>
        val card = c.drawCard(scala.util.Random.nextInt(c.cards.size))
        applyCardEffect(card)
        message += s"Karte: ${card.description}"

      case p: PropertyField if p.isUnowned =>
        message += s"${p.name} steht zum Verkauf (${p.price}€)"

      case s: StationField if s.isUnowned =>
        message += s"${s.name} steht zum Verkauf (${s.price}€)"

      case u: UtilityField if u.isUnowned =>
        message += s"${u.name} steht zum Verkauf (${u.price}€)"

      case _ =>
        message += field.name

  private def applyCardEffect(card: Card): Unit =
    val player = gameState.currentPlayer
    card.effect match
      case ReceiveMoney(amount) =>
        gameState = updateCurrentPlayer(player.addMoney(amount))
      case PayMoney(amount) =>
        gameState = updateCurrentPlayer(player.removeMoney(amount))
      case MoveToField(position) =>
        val passedGo = position < player.position
        var updated = player.copy(position = position)
        if passedGo then updated = updated.addMoney(PlayerConfig.goBonus)
        gameState = updateCurrentPlayer(updated)
      case MoveRelative(steps) =>
        val newPos = (player.position + steps + 40) % 40
        gameState = updateCurrentPlayer(player.copy(position = newPos))
      case CollectFromPlayers(amount) =>
        val otherCount = gameState.players.length - 1
        val players = gameState.players.map { pl =>
          if pl.name == player.name then pl.addMoney(amount * otherCount)
          else pl.removeMoney(amount)
        }
        gameState = gameState.copy(players = players)
      case GoToJail =>
        gameState = updateCurrentPlayer(player.copy(position = 10))
      case MoveToNextStation =>
        val pos = player.position
        val stationPositions = gameState.board.fields.zipWithIndex.collect {
          case (_: StationField, i) => i
        }
        val next = stationPositions.find(_ > pos).getOrElse(stationPositions.head)
        val passedGo = next < pos
        var updated = player.copy(position = next)
        if passedGo then updated = updated.addMoney(PlayerConfig.goBonus)
        gameState = updateCurrentPlayer(updated)

  def currentFieldOwner: String =
    gameState.board.fieldAt(gameState.currentPlayer.position) match
      case p: PropertyField => p.owner.getOrElse("Niemand")
      case s: StationField  => s.owner.getOrElse("Niemand")
      case u: UtilityField  => u.owner.getOrElse("Niemand")
      case _                => "Niemand"

  def buyProperty(): Boolean =
    if !hasRolled then return false
    val player = gameState.currentPlayer
    val field = gameState.board.fieldAt(player.position)
    field match
      case p: PropertyField if p.isUnowned && player.money >= p.price =>
        val updatedFields = gameState.board.fields.updated(player.position, p.buyBy(player.name))
        gameState = updateCurrentPlayer(player.removeMoney(p.price)).copy(board = Board(updatedFields))
        message = s"${player.name} kauft ${p.name} für ${p.price}€"
        notifyObservers()
        true
      case s: StationField if s.isUnowned && player.money >= s.price =>
        val updatedFields = gameState.board.fields.updated(player.position, s.buyBy(player.name))
        gameState = updateCurrentPlayer(player.removeMoney(s.price)).copy(board = Board(updatedFields))
        message = s"${player.name} kauft ${s.name} für ${s.price}€"
        notifyObservers()
        true
      case u: UtilityField if u.isUnowned && player.money >= u.price =>
        val updatedFields = gameState.board.fields.updated(player.position, u.buyBy(player.name))
        gameState = updateCurrentPlayer(player.removeMoney(u.price)).copy(board = Board(updatedFields))
        message = s"${player.name} kauft ${u.name} für ${u.price}€"
        notifyObservers()
        true
      case _ => false

  def endTurn(): Unit =
    if !hasRolled then
      message = "Du musst zuerst würfeln."
      return
    hasRolled = false
    gameState = gameState.nextPlayer
    message = s"${gameState.currentPlayer.name} ist am Zug"
    notifyObservers()

  private def payRent(payerName: String, ownerName: String, amount: Int): Unit =
    val players = gameState.players.map { pl =>
      if pl.name == payerName then pl.removeMoney(amount)
      else if pl.name == ownerName then pl.addMoney(amount)
      else pl
    }
    gameState = gameState.copy(players = players)

  private def updateCurrentPlayer(player: Player): GameState =
    val updatedPlayers = gameState.players.updated(gameState.currentPlayerIndex, player)
    gameState.copy(players = updatedPlayers)
