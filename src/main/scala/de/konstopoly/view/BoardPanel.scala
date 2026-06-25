package de.konstopoly.view

import de.konstopoly.controller.GameController
import de.konstopoly.model.Field
import de.konstopoly.model.fields.*

import scala.swing.Panel
import java.awt.{BasicStroke, Color, Dimension, Font, Graphics2D}

// Zeichnet das Spielbrett. Die 40 Felder liegen wie bei Monopoly als Ring
// am Rand eines 11x11 Rasters. In der Mitte steht der Titel.
// Die Spielfiguren werden als kleine Kreise auf ihre Felder gemalt.
class BoardPanel(controller: GameController) extends Panel:
  preferredSize = new Dimension(700, 700)

  // Rechnet eine Brett-Position (0-39) in eine Rasterzelle (Zeile, Spalte) um.
  // Start (Los) ist unten rechts, dann geht es gegen den Uhrzeigersinn herum.
  private def cellOf(pos: Int): (Int, Int) =
    if pos <= 10 then (10, 10 - pos)          // untere Reihe (rechts -> links)
    else if pos <= 20 then (20 - pos, 0)      // linke Spalte (unten -> oben)
    else if pos <= 30 then (0, pos - 20)      // obere Reihe (links -> rechts)
    else (pos - 30, 10)                        // rechte Spalte (oben -> unten)

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)

    // weisser Hintergrund
    g.setColor(Color.white)
    g.fillRect(0, 0, size.width, size.height)

    // Solange noch kein Spiel laeuft, gibt es kein Brett.
    if !controller.isStarted then
      g.setColor(Color.darkGray)
      g.setFont(new Font("SansSerif", Font.BOLD, 22))
      g.drawString("Konstopoly - bitte ein neues Spiel starten", 30, size.height / 2)
      return

    val boardSize = math.min(size.width, size.height)
    val cell = boardSize / 11

    // Moeglichkeit 1: ganzes Spielfeld als ein Bild.
    FieldImages.boardBackground.foreach { img =>
      g.drawImage(img, 0, 0, cell * 11, cell * 11, null)
    }

    val state = controller.gameState
    // Name -> Spielernummer (fuer die Besitzer-Farbe)
    val playerIndex = state.players.map(_.name).zipWithIndex.toMap

    // alle 40 Felder zeichnen
    for pos <- 0 until 40 do
      val (row, col) = cellOf(pos)
      val field = state.board.fieldAt(pos)
      val ownerIdx = ownerOf(field).flatMap(playerIndex.get)
      drawField(g, field, col * cell, row * cell, cell, ownerIdx)

    // Titel in der Mitte
    g.setColor(new Color(200, 200, 200))
    g.setFont(new Font("SansSerif", Font.BOLD, 28))
    g.drawString("KONSTOPOLY", cell * 2, cell * 6)

    // Spielfiguren zeichnen
    state.players.zipWithIndex.foreach { case (player, i) =>
      val (row, col) = cellOf(player.position)
      drawToken(g, col * cell, row * cell, cell, i)
    }

  // Ein einzelnes Feld zeichnen.
  private def drawField(g: Graphics2D, field: Field, x: Int, y: Int, cell: Int, ownerIdx: Option[Int]): Unit =
    FieldImages.imageFor(field) match
      // Moeglichkeit 2: einzelne Strasse als Bild.
      case Some(img) =>
        g.drawImage(img, x, y, cell, cell, null)
      // sonst: einfaches farbiges Feld
      case None =>
        g.setColor(new Color(238, 238, 238))
        g.fillRect(x, y, cell, cell)

        // oben ein Farbband fuer die Grundstuecks-Gruppe
        field match
          case p: PropertyField =>
            g.setColor(groupColor(p.colorGroup))
            g.fillRect(x, y, cell, cell / 5)
          case _ =>

        // Feldname (klein, evtl. abgeschnitten)
        g.setColor(Color.black)
        g.setFont(new Font("SansSerif", Font.PLAIN, 8))
        drawName(g, field.name, x + 2, y + cell / 4 + 8, cell - 4)

    // Rahmen
    g.setColor(Color.black)
    g.setStroke(new BasicStroke(1))
    g.drawRect(x, y, cell, cell)

    // unten ein Streifen in der Farbe des Besitzers
    ownerIdx.foreach { idx =>
      g.setColor(PlayerColors.color(idx))
      g.fillRect(x, y + cell - cell / 6, cell, cell / 6)
    }

  // Spielfigur als kleiner Kreis. Bei mehreren Spielern auf einem Feld
  // werden sie ein bisschen versetzt, damit man alle sieht.
  private def drawToken(g: Graphics2D, x: Int, y: Int, cell: Int, i: Int): Unit =
    val r = cell / 4
    val ox = x + 4 + (i % 2) * (r + 2)
    val oy = y + cell / 3 + (i / 2) * (r + 2)
    g.setColor(PlayerColors.color(i))
    g.fillOval(ox, oy, r, r)
    g.setColor(Color.black)
    g.drawOval(ox, oy, r, r)

  // Den Namen notfalls abschneiden, damit er ins Feld passt.
  private def drawName(g: Graphics2D, name: String, x: Int, y: Int, maxWidth: Int): Unit =
    var text = name
    while text.nonEmpty && g.getFontMetrics.stringWidth(text) > maxWidth do
      text = text.dropRight(1)
    g.drawString(text, x, y)

  // Welcher Spieler besitzt das Feld (falls kaufbar)?
  private def ownerOf(field: Field): Option[String] = field match
    case p: PropertyField => p.owner
    case s: StationField  => s.owner
    case u: UtilityField  => u.owner
    case _                => None

  // Farben fuer die Grundstuecks-Gruppen.
  private def groupColor(group: String): Color = group match
    case "braun"      => new Color(150, 75, 0)
    case "blau"       => new Color(135, 206, 235)
    case "pink"       => new Color(255, 105, 180)
    case "orange"     => new Color(255, 165, 0)
    case "rot"        => Color.red
    case "gelb"       => Color.yellow
    case "gruen"      => new Color(0, 153, 0)
    case "dunkelblau" => new Color(0, 0, 139)
    case _            => Color.lightGray
