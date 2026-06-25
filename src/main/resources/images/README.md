# Bilder für das Spielfeld

Hier kann man das Aussehen des Spielfeldes ändern, **ohne den Code anzufassen**.
Es reicht, Bilddateien in dieses Verzeichnis zu legen. Zuständig dafür ist die
Klasse `view/FieldImages.scala`.

## Möglichkeit 1: ganzes Spielfeld austauschen

Lege ein Bild ab unter:

```
images/board.png
```

Es wird als Hintergrund über das gesamte Brett gezeichnet (11x11 Raster).

## Möglichkeit 2: einzelne Straßen / Felder austauschen

Lege pro Feld ein Bild ab unter:

```
images/fields/<feldname>.png
```

Der Dateiname wird aus dem Feldnamen gebildet: alles klein geschrieben und
alle Sonderzeichen/Leerzeichen werden zu `-`.

Beispiele:

| Feld im Spiel        | Dateiname                       |
|----------------------|---------------------------------|
| Untere Laube         | `untere-laube.png`              |
| Konzilstrasse        | `konzilstrasse.png`             |
| Bahnhofstrasse       | `bahnhofstrasse.png`            |
| St.Stephans-Platz    | `st-stephans-platz.png`         |
| Insel Mainau         | `insel-mainau.png`              |
| Hauptbahnhof         | `hauptbahnhof.png`              |

## Wenn kein Bild da ist

Findet das Programm kein passendes Bild, zeichnet es das Feld einfach selbst
(grauer Kasten mit Farbband und Name). Das Spiel funktioniert also auch ganz
ohne Bilder – man kann sie später nach und nach ergänzen.
