package de.konstopoly.model

import de.konstopoly.model.fields.*

// Repräsentiert das Spielbrett. Enthält alle Felder als Liste,
// stellt Methoden bereit um ein Feld anhand einer Position abzurufen
// und initialisiert das Brett mit den Standard-Feldern.
case class Board(fields: Vector[Field]):
  def fieldAt(position: Int): Field = fields(position % fields.size)
  def size: Int = fields.size

object Board:

  val ereignisKarten: Vector[Card] = Vector(
    Card("Rücke vor bis zu Los – erhalte 200€", MoveToField(0)),
    Card("Gehe in das Gefängnis", GoToJail),
    Card("Rücke 3 Felder zurück", MoveRelative(-3)),
    Card("Zahle 15€ Strafe", PayMoney(15)),
    Card("Erhalte 150€ aus dem Bankfonds", ReceiveMoney(150)),
    Card("Rücke vor zum nächsten Bahnhof", MoveToNextStation),
    Card("Zahle 50€ Reparaturkosten", PayMoney(50)),
    Card("Du gewinnst 10€ in einem Schönheitswettbewerb", ReceiveMoney(10))
  )

  val gemeinschaftsKarten: Vector[Card] = Vector(
    Card("Bankfehler zu deinen Gunsten – erhalte 200€", ReceiveMoney(200)),
    Card("Ärztliche Behandlung – zahle 50€", PayMoney(50)),
    Card("Erhalte 100€ Jahresvergütung", ReceiveMoney(100)),
    Card("Zahle 100€ Schulgebühren", PayMoney(100)),
    Card("Erhalte 20€ Zinsen", ReceiveMoney(20)),
    Card("Rücke vor bis zu Los – erhalte 200€", MoveToField(0)),
    Card("Erhalte 10€ von jedem Mitspieler", CollectFromPlayers(10)),
    Card("Zahle 50€ Krankenhauskosten", PayMoney(50))
  )

  val defaultFields: Vector[Field] = Vector(
    GoField(),
    PropertyField("Untere Laube", 60, "braun", 2),
    ChanceField("Gemeinschaftsfeld", gemeinschaftsKarten),
    PropertyField("Konzilstrasse", 60, "braun", 4),
    TaxField("Einkommensteuer", 200),
    StationField("Hauptbahnhof"),
    PropertyField("Bahnhofstrasse", 100, "blau", 6),
    ChanceField("Ereignisfeld", ereignisKarten),
    PropertyField("Bodanstrasse", 100, "blau", 6),
    PropertyField("Wilhelm-von-Scholz-Weg", 120, "blau", 8),
    JailField(),
    PropertyField("Wessenbergstrasse", 140, "pink", 10),
    UtilityField("Elektrizitaetswerk"),
    PropertyField("St.Stephans-Platz", 140, "pink", 10),
    PropertyField("Theatergasse", 160, "pink", 12),
    StationField("Bahnhof Petershausen"),
    PropertyField("Rosgartenstrasse", 180, "orange", 14),
    ChanceField("Gemeinschaftsfeld", gemeinschaftsKarten),
    PropertyField("Hussenstrasse", 180, "orange", 14),
    PropertyField("Kanzleistrasse", 200, "orange", 16),
    FreeParkingField(),
    PropertyField("Obermarkt", 220, "rot", 18),
    ChanceField("Ereignisfeld", ereignisKarten),
    PropertyField("Muensterplatz", 220, "rot", 18),
    PropertyField("Benediktinerplatz", 240, "rot", 20),
    StationField("Faehrehafen Konstanz-Staad"),
    PropertyField("Rheingasse", 260, "gelb", 22),
    PropertyField("Tullengasse", 260, "gelb", 22),
    UtilityField("Wasserwerk"),
    PropertyField("Universitaetsstrasse", 280, "gelb", 24),
    GoToJailField(),
    PropertyField("Hafenstrasse", 300, "gruen", 26),
    PropertyField("Markstaette", 300, "gruen", 26),
    ChanceField("Gemeinschaftsfeld", gemeinschaftsKarten),
    PropertyField("Insel Mainau", 320, "gruen", 28),
    StationField("Konstanzer Hafen"),
    ChanceField("Ereignisfeld", ereignisKarten),
    PropertyField("Seestrasse", 350, "dunkelblau", 35),
    TaxField("Zusatzsteuer", 100),
    PropertyField("Auf der Insel", 400, "dunkelblau", 50)
  )

  def apply(): Board = Board(defaultFields)
