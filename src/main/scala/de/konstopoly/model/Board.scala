package de.konstopoly.model

import de.konstopoly.model.fields.*

// Repräsentiert das Spielbrett. Enthält alle Felder als Liste,
// stellt Methoden bereit um ein Feld anhand einer Position abzurufen
// und initialisiert das Brett mit den Standard-Feldern.
case class Board(fields: Vector[Field]):
  def fieldAt(position: Int): Field = fields(position % fields.size)
  def size: Int = fields.size

object Board:

  private val ereignisKarten: Vector[String] = Vector(
    "Rücke vor bis zu Los – erhalte 200€",
    "Gehe in das Gefängnis",
    "Rücke 3 Felder zurück",
    "Zahle 15€ Strafe",
    "Erhalte 150€ aus dem Bankfonds",
    "Rücke vor zum nächsten Bahnhof",
    "Zahle 50€ Reparaturkosten",
    "Du gewinnst 10€ in einem Schönheitswettbewerb"
  )

  private val gemeinschaftsKarten: Vector[String] = Vector(
    "Bankfehler zu deinen Gunsten – erhalte 200€",
    "Ärztliche Behandlung – zahle 50€",
    "Erhalte 100€ Jahresvergütung",
    "Zahle 100€ Schulgebühren",
    "Erhalte 20€ Zinsen",
    "Rücke vor bis zu Los – erhalte 200€",
    "Erhalte 10€ von jedem Mitspieler",
    "Zahle 50€ Krankenhauskosten"
  )

  val defaultFields: Vector[Field] = Vector(
      GoField(),
      PropertyField("Badstraße",         "Lila",        60,  50,  Vector(2,  10,  30,  90,  160,  250)),
      ChanceField("Gemeinschaft", gemeinschaftsKarten),
      PropertyField("Turmstraße",         "Lila",        60,  50,  Vector(4,  20,  60,  180,  320,  450)),
      TaxField("Einkommensteuer", 200),
      PropertyField("Südbahnhof",          "Bahnhof",    200,   0,  Vector(25, 50, 100,  200,    0,    0)),
      PropertyField("Chausseestraße",      "Hellblau",  100,  50,  Vector(6,  30,  90,  270,  400,  550)),
      ChanceField("Ereignis", ereignisKarten),
      PropertyField("Elisenstraße",        "Hellblau",  100,  50,  Vector(6,  30,  90,  270,  400,  550)),
      PropertyField("Poststraße",          "Hellblau",  120,  50,  Vector(8,  40, 100,  300,  450,  600)),
      SpecialField("Gefängnis / Besuch"),
      PropertyField("Seestraße",           "Rosa",      140, 100,  Vector(10, 50, 150,  450,  625,  750)),
      PropertyField("Elektrizitätswerk",   "Werk",      150,   0,  Vector(4,  10,   0,    0,    0,    0)),
      PropertyField("Hafenstraße",          "Rosa",      140, 100,  Vector(10, 50, 150,  450,  625,  750)),
      PropertyField("Neue Straße",          "Rosa",      160, 100,  Vector(12, 60, 180,  500,  700,  900)),
      PropertyField("Westbahnhof",          "Bahnhof",  200,   0,  Vector(25, 50, 100,  200,    0,    0)),
      PropertyField("Münchener Straße",     "Orange",   180, 100,  Vector(14, 70, 200,  550,  750,  950)),
      ChanceField("Gemeinschaft", gemeinschaftsKarten),
      PropertyField("Wiener Straße",        "Orange",   180, 100,  Vector(14, 70, 200,  550,  750,  950)),
      PropertyField("Berliner Straße",      "Orange",   200, 100,  Vector(16, 80, 220,  600,  800, 1000)),
      SpecialField("Frei Parken"),
      PropertyField("Theaterstraße",        "Rot",      220, 150,  Vector(18, 90, 250,  700,  875, 1050)),
      ChanceField("Ereignis", ereignisKarten),
      PropertyField("Museumsstraße",        "Rot",      220, 150,  Vector(18, 90, 250,  700,  875, 1050)),
      PropertyField("Opernplatz",           "Rot",      240, 150,  Vector(20,100, 300,  750,  925, 1100)),
      PropertyField("Nordbahnhof",          "Bahnhof",  200,   0,  Vector(25, 50, 100,  200,    0,    0)),
      PropertyField("Lessingstraße",        "Gelb",     260, 150,  Vector(22,110, 330,  800,  975, 1150)),
      PropertyField("Schillerstraße",       "Gelb",     260, 150,  Vector(22,110, 330,  800,  975, 1150)),
      PropertyField("Wasserwerk",           "Werk",     150,   0,  Vector(4,  10,   0,    0,    0,    0)),
      PropertyField("Goethestraße",         "Gelb",     280, 150,  Vector(24,120, 360,  850, 1025, 1200)),
      SpecialField("Gehe in Gefängnis"),
      PropertyField("Rathausplatz",         "Grün",     300, 200,  Vector(26,130, 390,  900, 1100, 1275)),
      ChanceField("Gemeinschaft", gemeinschaftsKarten),
      PropertyField("Hauptstraße",          "Grün",     300, 200,  Vector(26,130, 390,  900, 1100, 1275)),
      PropertyField("Bahnhofstraße",        "Grün",     320, 200,  Vector(28,150, 450, 1000, 1200, 1400)),
      PropertyField("Hauptbahnhof",         "Bahnhof",  200,   0,  Vector(25, 50, 100,  200,    0,    0)),
      ChanceField("Ereignis", ereignisKarten),
      PropertyField("Parkstraße",           "Dunkelblau",350, 200, Vector(35,175, 500, 1100, 1300, 1500)),
      TaxField("Zusatzsteuer", 100),
      PropertyField("Schlossallee",         "Dunkelblau",400, 200, Vector(50,200, 600, 1400, 1700, 2000))
  )

  def apply(): Board = Board(defaultFields)
