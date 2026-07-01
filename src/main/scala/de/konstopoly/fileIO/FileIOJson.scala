package de.konstopoly.fileIO

import com.google.inject.Inject
import de.konstopoly.model.*
import play.api.libs.json.*
import scala.io.Source

class FileIOJson(fileName: String) extends FileIOInterface:
  @Inject() def this() = this("gameState.json")

  def save(gameState: GameState): Unit =
    val pw = new java.io.PrintWriter(new java.io.File(fileName))
    pw.write(Json.prettyPrint(toJson(gameState)))
    pw.close()

  def load(): GameState =
    val source = Source.fromFile(fileName)
    val json = Json.parse(source.getLines().mkString)
    source.close()
    fromJson(json)

  private def toJson(g: GameState): JsValue = Json.obj(
    "currentPlayerIndex" -> g.currentPlayerIndex,
    "round"              -> g.round,
    "players" -> g.players.map { p =>
      Json.obj("name" -> p.name, "money" -> p.money, "position" -> p.position, "figure" -> p.figure)
    },
    "board" -> BoardIO.owners(g.board).map { case (i, owner) =>
      Json.obj("position" -> i, "owner" -> owner)
    }
  )

  private def fromJson(json: JsValue): GameState =
    val players = (json \ "players").as[Seq[JsValue]].map { p =>
      Player(
        (p \ "name").as[String],
        (p \ "money").as[Int],
        (p \ "position").as[Int],
        (p \ "figure").as[String]
      )
    }.toList
    val owners = (json \ "board").as[Seq[JsValue]].map { f =>
      ((f \ "position").as[Int], (f \ "owner").as[String])
    }
    GameState(
      players,
      BoardIO.restore(owners),
      (json \ "currentPlayerIndex").as[Int],
      (json \ "round").as[Int]
    )
