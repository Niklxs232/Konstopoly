package de.konstopoly.view

import de.konstopoly.model.Field

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

// Diese Klasse ist die EINE zentrale Stelle, um die Bilder des Spielfeldes
// auszutauschen. Es gibt zwei Moeglichkeiten:
//
//  1) Ein Bild fuer das ganze Spielfeld:
//     src/main/resources/images/board.png
//
//  2) Ein Bild pro einzelner Strasse/Feld:
//     src/main/resources/images/fields/<feldname>.png
//     Der Dateiname wird aus dem Feldnamen gebildet (klein, Sonderzeichen -> "-").
//     Beispiel: "Untere Laube"  ->  untere-laube.png
//               "St.Stephans-Platz" -> st-stephans-platz.png
//
// Liegt kein Bild da, wird das Feld einfach farbig gezeichnet (siehe BoardPanel).
// So funktioniert das Spiel auch ohne Bilder und man kann sie spaeter
// einfach reinlegen, ohne den Code zu aendern.
object FieldImages:

  // Damit ein Bild nicht bei jedem Neuzeichnen wieder von der Platte
  // gelesen wird, merken wir uns das Ergebnis.
  private val cache = scala.collection.mutable.Map[String, Option[BufferedImage]]()

  def boardBackground: Option[BufferedImage] =
    load("/images/board.png")

  def imageFor(field: Field): Option[BufferedImage] =
    load(s"/images/fields/${keyOf(field.name)}.png")

  // Aus einem Feldnamen einen einfachen Dateinamen machen.
  private def keyOf(name: String): String =
    name.toLowerCase
      .replaceAll("[^a-z0-9]+", "-") // alles ausser Buchstaben/Zahlen -> "-"
      .replaceAll("^-+|-+$", "")     // "-" am Anfang/Ende weg

  private def load(path: String): Option[BufferedImage] =
    cache.getOrElseUpdate(path, {
      val stream = getClass.getResourceAsStream(path)
      if stream == null then None
      else Option(ImageIO.read(stream))
    })
