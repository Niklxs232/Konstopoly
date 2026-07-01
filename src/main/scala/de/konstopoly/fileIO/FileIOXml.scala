package de.konstopoly.fileIO

import com.google.inject.Inject
import de.konstopoly.model.*
import scala.xml.{Node, PrettyPrinter, XML}

class FileIOXml(fileName: String) extends FileIOInterface:
  @Inject() def this() = this("gameState.xml")

  def save(gameState: GameState): Unit =
    val pw = new java.io.PrintWriter(new java.io.File(fileName))
    pw.write(new PrettyPrinter(120, 2).format(toXml(gameState)))
    pw.close()

  def load(): GameState = fromXml(XML.loadFile(fileName))

  private def toXml(g: GameState): Node =
    <gameState currentPlayerIndex={g.currentPlayerIndex.toString} round={g.round.toString}>
      <players>{g.players.map(playerToXml)}</players>
      <board>{BoardIO.owners(g.board).map { case (i, owner) =>
        <field position={i.toString} owner={owner}/>
      }}</board>
    </gameState>

  private def playerToXml(p: Player): Node =
    <player>
      <name>{p.name}</name>
      <money>{p.money}</money>
      <position>{p.position}</position>
      <figure>{p.figure}</figure>
    </player>

  private def fromXml(node: Node): GameState =
    val players = (node \ "players" \ "player").map { p =>
      Player(
        (p \ "name").text.trim,
        (p \ "money").text.trim.toInt,
        (p \ "position").text.trim.toInt,
        (p \ "figure").text.trim
      )
    }.toList
    val owners = (node \ "board" \ "field").map { f =>
      ((f \ "@position").text.toInt, (f \ "@owner").text)
    }
    GameState(
      players,
      BoardIO.restore(owners),
      (node \ "@currentPlayerIndex").text.toInt,
      (node \ "@round").text.toInt
    )
