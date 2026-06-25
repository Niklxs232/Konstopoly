package de.konstopoly.view

import de.konstopoly.controller.GameController
import de.konstopoly.util.Observer

import scala.swing.*
import scala.swing.event.ButtonClicked
import scala.util.Try


// GUI und TUI laufen gleichzeitig und teilen sich denselben Controller.
class GUI(controller: GameController) extends Observer:
  controller.add(this)

  // die einzelnen Teile der Oberflaeche
  private val boardPanel  = new BoardPanel(controller)
  private val playerPanel = new PlayerPanel(controller)
  private val messageLabel = new Label("Willkommen bei Konstopoly!")

  // die Knoepfe
  private val newGameButton = new Button("Neues Spiel")
  private val rollButton    = new Button("Würfeln")
  private val buyButton     = new Button("Kaufen")
  private val endButton     = new Button("Zug beenden")
  private val undoButton    = new Button("Rückgängig")
  private val redoButton    = new Button("Wiederholen")

  private val buttonPanel = new GridPanel(6, 1):
    contents ++= Seq(newGameButton, rollButton, buyButton, endButton, undoButton, redoButton)

  // das Fenster
  private val frame = new MainFrame:
    title = "Konstopoly"
    preferredSize = new Dimension(950, 750)

    menuBar = new MenuBar:
      contents += new Menu("Spiel"):
        contents += new MenuItem(Action("Neues Spiel") { startNewGame() })
        contents += new MenuItem(Action("Beenden") { System.exit(0) })

    contents = new BorderPanel:
      layout(boardPanel) = BorderPanel.Position.Center
      layout(new BoxPanel(Orientation.Vertical) {
        contents += playerPanel
        contents += buttonPanel
      }) = BorderPanel.Position.East
      layout(messageLabel) = BorderPanel.Position.South

    // auf die Knoepfe reagieren (Publisher/Reactor aus den Folien)
    listenTo(newGameButton, rollButton, buyButton, endButton, undoButton, redoButton)
    reactions += {
      case ButtonClicked(`newGameButton`) => startNewGame()
      case ButtonClicked(`rollButton`)    => controller.rollDice()
      case ButtonClicked(`buyButton`)     => controller.buyProperty()
      case ButtonClicked(`endButton`)     => controller.endTurn()
      case ButtonClicked(`undoButton`)    => controller.undo()
      case ButtonClicked(`redoButton`)    => controller.redo()
    }

  // Fenster anzeigen
  Swing.onEDT {
    refresh()
    frame.visible = true
  }
  
  def update: Unit = Swing.onEDT { refresh() }

  // Die Anzeige an den aktuellen Spielzustand anpassen.
  private def refresh(): Unit =
    boardPanel.repaint()
    playerPanel.refresh()
    messageLabel.text = if controller.message.isEmpty then " " else controller.message

    val laeuft   = controller.isStarted
    val gewonnen = laeuft && controller.winner.isDefined
    rollButton.enabled = laeuft && !controller.hasRolled && !gewonnen
    buyButton.enabled  = laeuft && controller.hasRolled && !gewonnen
    endButton.enabled  = laeuft && controller.hasRolled && !gewonnen
    undoButton.enabled = laeuft
    redoButton.enabled = laeuft

  // Fragt nach der Spieleranzahl und startet ein neues Spiel mit
  private def startNewGame(): Unit =
    val eingabe = Dialog.showInput(
      boardPanel,
      s"Anzahl Spieler (${controller.minPlayers}-${controller.maxPlayers}):",
      "Neues Spiel",
      Dialog.Message.Question,
      Swing.EmptyIcon,
      Nil,
      "2"
    )
    eingabe.foreach { text =>
      val anzahl = Try(text.trim.toInt).getOrElse(controller.minPlayers)
      val gueltig = math.max(controller.minPlayers, math.min(controller.maxPlayers, anzahl))
      val namen = (1 to gueltig).map(i => s"Spieler $i").toList
      controller.startGame(namen)
    }
