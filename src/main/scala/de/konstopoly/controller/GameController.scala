package de.konstopoly.controller

import com.google.inject.Inject
import de.konstopoly.model.*
import de.konstopoly.model.fields.*
import de.konstopoly.util.Observable
import de.konstopoly.controller.commands.*
import de.konstopoly.fileIO.{FileIOInterface, FileIOJson}
import scala.util.{Failure, Success, Try}

class GameController @Inject() (fileIO: FileIOInterface = new FileIOJson) extends Observable, ControllerInterface:
  // Pattern-Option statt null (SE-8)
  private var _gameState: Option[GameState] = None
  def gameState: GameState = _gameState.getOrElse(throw IllegalStateException("Spiel nicht gestartet"))
  def gameState_=(state: GameState): Unit = _gameState = Some(state)

  def isStarted: Boolean = _gameState.isDefined

  var message: String = ""
  var hasRolled: Boolean = false

  private val undoManager = new UndoManager

  // Pattern-Strategy (SE-7)
  var diceStrategy: () => Dice = () => Dice()

  def minPlayers: Int = PlayerConfig.minPlayers
  def maxPlayers: Int = PlayerConfig.maxPlayers
  def currentRound: Int = gameState.round
  def currentPlayerName: String = gameState.currentPlayer.name

  def winner: Option[String] = gameState.winner.map(_.name)

  def playerInfoStrings: List[String] = gameState.players.map { p =>
    val marker = if p.name == gameState.currentPlayer.name then " <--" else ""
    val fieldName = gameState.board.fieldAt(p.position).name
    s"  ${p.name}: ${p.money}€ | Feld ${p.position} (${fieldName})$marker"
  }

  def startGame(playerNames: List[String]): Unit =
    gameState = GameState(playerNames.map(Player(_)), Board())
    hasRolled = false
    message = "Spiel gestartet!"
    notifyObservers()

  def rollDice(): Dice =
    val dice = diceStrategy()
    rollDice(dice)
    dice

  def rollDice(dice: Dice): Unit =
    if hasRolled then
      message = "Du hast diese Runde bereits gewürfelt."
      notifyObservers()
      return

    val prevState = gameState
    val prevRolled = hasRolled

    val player = gameState.currentPlayer
    val newPos = (player.position + dice.total) % 40
    val passedGo = player.position + dice.total >= 40

    var moved = player.copy(position = newPos)
    if passedGo then
      moved = moved.addMoney(PlayerConfig.goBonus)
      message = s"${player.name} würfelt ${dice.total} und überquert Los (+${PlayerConfig.goBonus}€). "
    else
      message = s"${player.name} würfelt ${dice.total}. "

    gameState = updateCurrentPlayer(moved)
    handleFieldEffect(newPos, dice.total)
    hasRolled = true

    val capturedState = gameState
    val capturedRolled = hasRolled
    val capturedMessage = message
    undoManager.execute(new Command:
      def doStep(): Unit =
        gameState = capturedState
        hasRolled = capturedRolled
        message = capturedMessage
      def undoStep(): Unit =
        gameState = prevState
        hasRolled = prevRolled
    )
    notifyObservers()

  private def handleFieldEffect(position: Int, diceTotal: Int): Unit =
    val field = gameState.board.fieldAt(position)
    val player = gameState.currentPlayer

    field match
      case t: TaxField =>
        gameState = updateCurrentPlayer(player.removeMoney(t.amount))
        message += s"Steuer: ${t.amount}€"

      case _: GoToJailField =>
        gameState = updateCurrentPlayer(player.copy(position = 10))
        message += "Ab ins Gefängnis!"

      case p: PropertyField if p.isOwned && p.owner.get != player.name =>
        payRent(player.name, p.owner.get, p.rent)
        message += s"Miete ${p.rent}€ an ${p.owner.get}"

      case s: StationField if s.isOwned && s.owner.get != player.name =>
        val count = gameState.board.fields.count {
          case sf: StationField => sf.owner.contains(s.owner.get)
          case _                => false
        }
        val rent = 25 * math.pow(2, count - 1).toInt
        payRent(player.name, s.owner.get, rent)
        message += s"Bahnhofsmiete ${rent}€ an ${s.owner.get}"

      case u: UtilityField if u.isOwned && u.owner.get != player.name =>
        val count = gameState.board.fields.count {
          case uf: UtilityField => uf.owner.contains(u.owner.get)
          case _                => false
        }
        val multiplier = if count >= 2 then 10 else 4
        val rent = multiplier * diceTotal
        payRent(player.name, u.owner.get, rent)
        message += s"Versorgungsmiete ${rent}€ an ${u.owner.get}"

      case c: ChanceField =>
        val card = c.drawCard(scala.util.Random.nextInt(c.cards.size))
        applyCardEffect(card)
        message += s"Karte: ${card.description}"

      case p: PropertyField if p.isUnowned => message += s"${p.name} steht zum Verkauf (${p.price}€)"
      case s: StationField if s.isUnowned  => message += s"${s.name} steht zum Verkauf (${s.price}€)"
      case u: UtilityField if u.isUnowned  => message += s"${u.name} steht zum Verkauf (${u.price}€)"
      case _                               => message += field.name

  private def applyCardEffect(card: Card): Unit =
    val player = gameState.currentPlayer
    card.effect match
      case ReceiveMoney(amount) => gameState = updateCurrentPlayer(player.addMoney(amount))
      case PayMoney(amount)     => gameState = updateCurrentPlayer(player.removeMoney(amount))
      case GoToJail             => gameState = updateCurrentPlayer(player.copy(position = 10))
      case MoveRelative(steps)  => gameState = updateCurrentPlayer(player.copy(position = (player.position + steps + 40) % 40))
      case MoveToField(pos) =>
        var updated = player.copy(position = pos)
        if pos < player.position then updated = updated.addMoney(PlayerConfig.goBonus)
        gameState = updateCurrentPlayer(updated)
      case CollectFromPlayers(amount) =>
        val players = gameState.players.map { pl =>
          if pl.name == player.name then pl.addMoney(amount * (gameState.players.length - 1))
          else pl.removeMoney(amount)
        }
        gameState = gameState.copy(players = players)
      case MoveToNextStation =>
        val stations = gameState.board.fields.zipWithIndex.collect { case (_: StationField, i) => i }
        val next = stations.find(_ > player.position).getOrElse(stations.head)
        var updated = player.copy(position = next)
        if next < player.position then updated = updated.addMoney(PlayerConfig.goBonus)
        gameState = updateCurrentPlayer(updated)

  def currentPlayerProperties: List[String] =
    val name = gameState.currentPlayer.name
    gameState.board.fields.collect {
      case p: PropertyField if p.owner.contains(name) => s"${p.name} (${p.colorGroup}, Miete ${p.rent}€)"
      case s: StationField if s.owner.contains(name)  => s"${s.name} (Bahnhof)"
      case u: UtilityField if u.owner.contains(name)  => s"${u.name} (Versorgungswerk)"
    }.toList

  def currentFieldOwner: String =
    gameState.board.fieldAt(gameState.currentPlayer.position) match
      case p: PropertyField => p.owner.getOrElse("Niemand")
      case s: StationField  => s.owner.getOrElse("Niemand")
      case u: UtilityField  => u.owner.getOrElse("Niemand")
      case _                => "Niemand"

  def buyProperty(): Boolean =
    if !hasRolled then return false
    val player = gameState.currentPlayer
    gameState.board.fieldAt(player.position) match
      case p: PropertyField if p.isUnowned && player.money >= p.price => doBuy(p.name, p.price, p.buyBy(player.name))
      case s: StationField if s.isUnowned && player.money >= s.price  => doBuy(s.name, s.price, s.buyBy(player.name))
      case u: UtilityField if u.isUnowned && player.money >= u.price  => doBuy(u.name, u.price, u.buyBy(player.name))
      case _ => false

  private def doBuy(fieldName: String, price: Int, boughtField: Field): Boolean =
    val prevState = gameState
    val player = gameState.currentPlayer
    val updatedFields = gameState.board.fields.updated(player.position, boughtField)
    gameState = updateCurrentPlayer(player.removeMoney(price)).copy(board = Board(updatedFields))
    message = s"${player.name} kauft $fieldName für ${price}€"

    val capturedState = gameState
    val capturedMessage = message
    undoManager.execute(new Command:
      def doStep(): Unit =
        gameState = capturedState
        message = capturedMessage
      def undoStep(): Unit =
        gameState = prevState
    )
    notifyObservers()
    true

  def endTurn(): Unit =
    if !hasRolled then
      message = "Du musst zuerst würfeln."
      notifyObservers()
      return

    val prevState = gameState
    val prevRolled = hasRolled
    hasRolled = false
    gameState = gameState.nextPlayer
    message = s"${gameState.currentPlayer.name} ist am Zug"

    val capturedState = gameState
    val capturedMessage = message
    undoManager.execute(new Command:
      def doStep(): Unit =
        gameState = capturedState
        hasRolled = false
        message = capturedMessage
      def undoStep(): Unit =
        gameState = prevState
        hasRolled = prevRolled
    )
    notifyObservers()

  def undo(): Unit =
    if undoManager.undo() then
      message = "Rückgängig gemacht."
    else
      message = "Nichts zum Rückgängig machen."
    notifyObservers()

  def redo(): Unit =
    if undoManager.redo() then
      message = "Wiederholt."
    else
      message = "Nichts zum Wiederholen."
    notifyObservers()

  def save(): Unit =
    fileIO.save(gameState)
    message = "Spiel gespeichert."
    notifyObservers()

  def load(): Unit =
    Try(fileIO.load()) match
      case Success(state) =>
        gameState = state
        hasRolled = false
        message = "Spiel geladen."
      case Failure(_) =>
        message = "Laden fehlgeschlagen."
    notifyObservers()

  private def payRent(payerName: String, ownerName: String, amount: Int): Unit =
    val players = gameState.players.map { pl =>
      if pl.name == payerName then pl.removeMoney(amount)
      else if pl.name == ownerName then pl.addMoney(amount)
      else pl
    }
    gameState = gameState.copy(players = players)

  private def updateCurrentPlayer(player: Player): GameState =
    gameState.copy(players = gameState.players.updated(gameState.currentPlayerIndex, player))
