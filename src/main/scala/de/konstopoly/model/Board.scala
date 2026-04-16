package de.konstopoly.model

// Repräsentiert das Spielbrett. Enthält alle Felder als Liste,
// stellt Methoden bereit um ein Feld anhand einer Position abzurufen
// und initialisiert das Brett mit den Standard-Feldern.
case class Board() {

  val fields: List[Field] = List(
    //Unten
    GoField(),                                            // 0
    PropertyField("Untere Laube",60,"braun"),             // 1
    ChanceField("Gemeinschaftsfeld"),                     // 2
    PropertyField("Konzilstrasse",60,"braun"),            // 3
    TaxField("Einkommensteuer",200),                      // 4
    StationField("Hauptbahnhof"),                         // 5
    PropertyField("Bahnhofstrasse",100,"blau"),           // 6
    ChanceField("Ereignisfeld"),                          // 7
    PropertyField("Bodanstrasse",100,"blau"),             // 8
    PropertyField("Wilhelm-von-Scholz-Weg",120,"blau"),   // 9

    //Links
    JailField(),                                          // 10
    PropertyField("Wessenbergstrasse",140,"pink"),        // 11
    UtilityField("Elektrizitaetswerk"),                   // 12
    PropertyField("St.Stephans-Platz",140,"pink"),        // 13
    PropertyField("Theatergasse",160,"pink"),             // 14
    StationField("Bahnhof Petershausen"),                 // 15
    PropertyField("Rosgartenstrasse",180,"orange"),       // 16
    ChanceField("Gemeinschaftsfeld"),                     // 17
    PropertyField("Hussenstrasse",180,"orange"),          // 18
    PropertyField("Kanzleistrasse",200,"orange"),         // 19

    //Oben
    FreeParkingField(),                                   // 20
    PropertyField("Obermarkt",220,"rot"),                 // 21
    ChanceField("Ereignisfeld"),                          // 22
    PropertyField("Muensterplatz",220,"rot"),             // 23
    PropertyField("Benediktinerplatz",240,"rot"),         // 24
    StationField("Faehrehafen Konstanz-Staad"),           // 25
    PropertyField("Rheingasse",260,"gelb"),               // 26
    PropertyField("Tullengasse",260,"gelb"),              // 27
    UtilityField("Wasserwerk"),                           // 28
    PropertyField("Universitaetsstrasse",280,"gelb"),     // 29

    //Rechts
    GoToJailField(),                                      // 30
    PropertyField("Hafenstrasse",300,"gruen"),            // 31
    PropertyField("Markstaette",300,"gruen"),             // 32
    ChanceField("Gemeinschaftsfeld"),                     // 33
    PropertyField("Insel Mainau",320,"gruen"),            // 34
    StationField("Konstanzer Hafen"),                     // 35
    ChanceField("Ereignisfeld"),                          // 36
    PropertyField("Seestrasse",350,"dunkelblau"),         // 37
    TaxField("Zusatzsteuer",100),                         // 38
    PropertyField("Auf der Insel",400,"dunkelblau")       // 39
  )

  def getField(position: Int): Field =
    fields(position)
}