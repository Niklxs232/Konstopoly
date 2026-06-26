package de.konstopoly.view

import de.konstopoly.model.Field

import java.awt.image.BufferedImage
import javax.imageio.ImageIO


object FieldImages:

  private val cache = scala.collection.mutable.Map[String, Option[BufferedImage]]()

  def boardBackground: Option[BufferedImage] =
    load("/images/board.png")

  def imageFor(field: Field): Option[BufferedImage] =
    load(s"/images/fields/${keyOf(field.name)}.png")

  // Aus einem Feldnamen einen einfachen Dateinamen machen.
  private def keyOf(name: String): String =
    name.toLowerCase
      .replaceAll("[^a-z0-9]+", "-") // alles ausser Buchstaben/Zahlen -> "- "
      .replaceAll("^-+|-+$", "")     // "-" am Anfang/Ende weg

  private def load(path: String): Option[BufferedImage] =
    cache.getOrElseUpdate(path, {
      val stream = getClass.getResourceAsStream(path)
      if stream == null then None
      else Option(ImageIO.read(stream))
    })
