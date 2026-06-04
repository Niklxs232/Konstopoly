# SE-05 Architecture — Durchgeführte Änderungen

## Ziel
Saubere MVC-Architektur mit striktem Layering und Observer Pattern gemäß Vorlesung SE-05.

## Neue Dateien

### `util/Observer.scala`
- **Observer** Trait mit `update`-Methode
- **Observable** Klasse mit `add`, `remove`, `notifyObservers`
- Liegt in der untersten Schicht, wird von Controller und View genutzt

### `view/TUI.scala`
- Gesamte TUI-Logik aus `Main.scala` hierher extrahiert
- Implementiert `Observer`, registriert sich beim Controller
- `update`-Methode wird automatisch nach jeder Zustandsänderung aufgerufen
- Importiert **nur** aus `controller` und `util` — kein direkter Model-Zugriff

### `util/ObserverSpec.scala`
- Tests für add, remove, notifyObservers und den Fall ohne Subscriber

## Geänderte Dateien

### `controller/GameController.scala`
- Erbt jetzt von `Observable`
- Ruft `notifyObservers()` am Ende jeder zustandsändernden Methode auf (`startGame`, `rollDice`, `doBuy`, `endTurn`)
- Neue Getter-Methoden für die View-Schicht: `minPlayers`, `maxPlayers`, `currentRound`, `currentPlayerName`, `playerInfoStrings`, `winner`
- So muss die View nie direkt auf Model-Klassen zugreifen

### `Main.scala`
- Von 99 Zeilen auf 9 Zeilen reduziert
- Erstellt nur noch Controller + TUI und startet das Spiel
- Import von `model.PlayerConfig` entfernt (war Layer-Violation)

### `build.sbt`
- Coverage-Exclusion um `TUI` erweitert (interaktive Klasse, nicht unit-testbar)

## Layer-Architektur (keine Violations)

```
View (TUI)          →  importiert Controller, Util
Controller          →  importiert Model, Util
Model               →  importiert nichts (nur intern)
Util (Observer)     →  importiert nichts
```

Abhängigkeiten gehen ausschließlich nach unten — keine Aufwärts-Referenzen.

## Observer-Ablauf

```
1. User gibt Befehl ein        →  TUI
2. TUI ruft Controller-Methode →  z.B. controller.rollDice()
3. Controller ändert Model      →  gameState = ...
4. Controller ruft notifyObservers()
5. TUI.update() wird aufgerufen →  println(controller.message)
6. TUI holt Daten über Getter   →  controller.playerInfoStrings
```

## Testergebnis
130 Tests bestanden (126 bestehende + 4 neue Observer-Tests), keine Kompilierfehler.
