package de.konstopoly.view

import de.konstopoly.controller.ControllerInterface

import scala.swing.{BoxPanel, Label, Orientation, Swing}
import java.awt.{Color, Font}

// Die Seitenleiste mit den Spieler-Infos (Geld, Position).
// Der Spieler, der gerade dran ist, wird mit "<-- am Zug" markiert.
class PlayerPanel(controller: ControllerInterface) extends BoxPanel(Orientation.Vertical):
  border = Swing.EmptyBorder(10)

  // Baut die Anzeige neu auf. Wird von der GUI bei jeder Aenderung gerufen.
  def refresh(): Unit =
    contents.clear()

    val titel = new Label("Spieler")
    titel.font = new Font("SansSerif", Font.BOLD, 16)
    contents += titel
    contents += Swing.VStrut(10)

    if !controller.isStarted then
      contents += new Label("Noch kein Spiel gestartet.")
    else
      val state = controller.gameState
      state.players.zipWithIndex.foreach { case (player, i) =>
        val amZug = player.name == controller.currentPlayerName
        val text = s"${player.name}: ${player.money}€ (Feld ${player.position})" + (if amZug then "  <-- am Zug" else "")
        val label = new Label(text)
        label.foreground = PlayerColors.color(i)
        if amZug then label.font = new Font("SansSerif", Font.BOLD, 13)
        contents += label
        contents += Swing.VStrut(5)
      }

      contents += Swing.VStrut(10)
      contents += new Label(s"Runde: ${controller.currentRound}")

    revalidate()
    repaint()
