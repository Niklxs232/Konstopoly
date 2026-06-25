 package de.konstopoly.controller.commands

import de.konstopoly.model.GameState

// Pattern-Command (SE-7)
trait Command:
  def doStep(): Unit
  def undoStep(): Unit

class UndoManager:
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def execute(cmd: Command): Unit =
    cmd.doStep()
    undoStack = cmd :: undoStack
    redoStack = Nil

  def undo(): Boolean =
    undoStack match
      case cmd :: rest =>
        cmd.undoStep()
        undoStack = rest
        redoStack = cmd :: redoStack
        true
      case Nil => false

  def redo(): Boolean =
    redoStack match
      case cmd :: rest =>
        cmd.doStep()
        redoStack = rest
        undoStack = cmd :: undoStack
        true
      case Nil => false
