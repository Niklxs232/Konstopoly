package de.konstopoly.fileIO

import de.konstopoly.model.*
import de.konstopoly.model.fields.*

object BoardIO:

  // Position und Besitzer aller kaufbaren, besetzten Felder.
  def owners(board: Board): Seq[(Int, String)] =
    board.fields.zipWithIndex.collect {
      case (p: PropertyField, i) if p.owner.isDefined => (i, p.owner.get)
      case (s: StationField, i)  if s.owner.isDefined => (i, s.owner.get)
      case (u: UtilityField, i)  if u.owner.isDefined => (i, u.owner.get)
    }

  // Standardbrett mit den gespeicherten Besitzern.
  def restore(owners: Seq[(Int, String)]): Board =
    val fields = owners.foldLeft(Board().fields) { case (fs, (i, owner)) =>
      fs.updated(i, withOwner(fs(i), owner))
    }
    Board(fields)

  private def withOwner(field: Field, owner: String): Field = field match
    case p: PropertyField => p.copy(owner = Some(owner))
    case s: StationField  => s.copy(owner = Some(owner))
    case u: UtilityField  => u.copy(owner = Some(owner))
    case f                => f
