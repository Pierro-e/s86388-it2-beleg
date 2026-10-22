# 03 – Aufgabe 0.3: Wo kommen die leeren Klassen hin?

Zur [Dokumentübersicht](README.md) · [Codeüberblick](01-Code-Ueberblick.md) · [RTSP-Stand](02-RTSP-Stand-und-Aufgaben.md)

## Das Ziel dieses Arbeitsschritts

Du legst **vier neue Java-Dateien** an, die von den vorhandenen abstrakten Demo-Klassen erben. Die Demo-Dateien bleiben bestehen. Die neuen Klassen übernehmen mit `extends` deren vorhandene Funktionalität und ergänzen zunächst nur die noch fehlenden Methodenrümpfe.

„Leer“ heißt hier: **Die spätere Fachlogik fehlt noch.** Eine Klasse mit nur `{}` reicht nicht, weil Java für eine konkrete Unterklasse die Implementierung aller abstrakten Methoden verlangt. Methoden mit Rückgabewert brauchen außerdem einen passenden Rückgabewert oder müssen eine Exception werfen.

Diese Anleitung enthält Gerüstbeispiele zum Einfügen. Die Java-Dateien selbst wurden im Rahmen der Dokumentation noch nicht angelegt.

## Die genauen Ablageorte

Alle folgenden Pfade sind relativ zur **Projektwurzel**:

| Neue Datei | Package-Zeile | Klassendeklaration |
| --- | --- | --- |
| `src/rtsp/Rtsp.java` | `package rtsp;` | `public class Rtsp extends RtspDemo` |
| `src/rtp/RtpPacket.java` | `package rtp;` | `public class RtpPacket extends RtpPacketDemo` |
| `src/rtp/FecHandler.java` | `package rtp;` | `public class FecHandler extends FecHandlerDemo` |
| `src/JpegDisplay.java` | **Keine Package-Zeile** | `public class JpegDisplay extends JpegDisplayDemo` |

```text
src/
├── Client.java
├── Server.java
├── JpegDisplayDemo.java
├── JpegDisplay.java          ← NEU, direkt unter src
├── rtsp/
│   ├── RtspDemo.java
│   └── Rtsp.java             ← NEU
└── rtp/
    ├── RtpPacketDemo.java
    ├── RtpPacket.java        ← NEU
    ├── FecHandlerDemo.java
    ├── FecHandler.java       ← NEU
    └── ...
```

Die Demo-Klassen haben keine `public`-Deklaration und sind daher nur in ihrem jeweiligen Package zugänglich. Die Unterklasse muss deshalb im selben Package liegen. `JpegDisplayDemo` liegt im **unbenannten Package**, also direkt in `src/`.

Schreibe die Namen exakt wie oben. In den Unterlagen kommen vereinzelt abweichende Schreibweisen wie `FECHandler` vor; die vorhandenen Java-Aufrufe erwarten **`FecHandler`**.

## Vorgehen in IntelliJ

1. Öffne im Projektbaum `src/rtsp` und lege über **New → Java Class** die Klasse `Rtsp` an.
2. Ergänze `extends RtspDemo` und prüfe `package rtsp;`.
3. Erzeuge die beiden passenden Konstruktoren mit **Generate → Constructor** und die abstrakten Methoden mit **Implement Methods**. Alternativ verwende das vollständige Gerüst unten.
4. Wiederhole dies für `RtpPacket` und `FecHandler` im Ordner `src/rtp`.
5. Lege `JpegDisplay` direkt unter `src` an und ergänze die Ableitung und Methode wie unten.

`src` ist in der vorhandenen Moduldatei bereits als Quellverzeichnis eingetragen. Es gehört kein `src` in die Package-Zeile. Jede öffentliche Klasse kommt in ihre eigene gleichnamige Datei.

## 1. `src/rtsp/Rtsp.java`

Vorlage: [RtspDemo.java](../src/rtsp/RtspDemo.java).

```java
package rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;

public class Rtsp extends RtspDemo {

    public Rtsp(URI url, int rtpRcvPort) {
        super(url, rtpRcvPort);
    }

    public Rtsp(BufferedReader reader, BufferedWriter writer) {
        super(reader, writer);
    }

    @Override
    public boolean play() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public boolean pause() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public boolean teardown() {
        // TODO Aufgabe 1
        return false;
    }

    @Override
    public void describe() {
        // TODO Aufgabe 1
    }

    @Override
    public void options() {
        // TODO Aufgabe 1
    }

    @Override
    public void send_RTSP_request(String request_type) {
        // TODO Aufgabe 1: Platzhalter durch echten Versand ersetzen.
        throw new UnsupportedOperationException(
                "Aufgabe 1: RTSP-Requests sind noch nicht implementiert.");
    }
}
```

**Warum zwei Konstruktoren?** `Client.java` erzeugt `Rtsp` mit einer `URI` und dem RTP-Port. `Server.java` verwendet stattdessen einen Reader und Writer. Konstruktoren werden nicht vererbt; du leitest die Argumente mit `super(...)` an die passende Oberklassenvariante weiter.

**Warum die Exception im Sender?** `setup()` ist schon geerbt. Es ruft den Sender auf und wartet danach auf eine Antwort. Ein komplett leerer Sender würde nichts verschicken und könnte das Clientfenster beim Warten blockieren. Der gezeigte Platzhalter meldet stattdessen sofort, dass Aufgabe 1 noch fehlt. Er wird in Aufgabe 1 ersetzt.

`setup()` musst du für 0.3 nicht erneut schreiben. Auch `getDescribe(...)` wird erst in Aufgabe 2 überschrieben, weil dafür schon eine konkrete, wenn auch unvollständige Vorlage existiert.

## 2. `src/rtp/RtpPacket.java`

Vorlage: [RtpPacketDemo.java](../src/rtp/RtpPacketDemo.java).

```java
package rtp;

public class RtpPacket extends RtpPacketDemo {

    public RtpPacket(
            int payloadType,
            int sequenceNumber,
            int timestamp,
            int marker,
            byte[] data,
            int dataLength) {
        super(payloadType, sequenceNumber, timestamp, marker, data, dataLength);
    }

    public RtpPacket(byte[] packet, int packetSize) {
        super(packet, packetSize);
    }

    @Override
    void setRtpHeader() {
        // TODO Aufgabe 3
    }
}
```

Der erste Konstruktor baut ein Paket aus Headerwerten und Nutzdaten auf, der zweite liest ein empfangenes Paket aus einer Bytefolge. Beide werden im vorhandenen Code benötigt. Auch `FecPacket` erbt von deiner neuen `RtpPacket` und ruft deren Konstruktoren auf.

Der erste Konstruktor benötigt **sechs Argumente**, einschließlich `marker`. Übernimm keine ältere Variante ohne dieses Argument.

**Hier bleibt der Methodenrumpf tatsächlich leer.** `RtpPacketDemo` ruft `setRtpHeader()` schon während der Konstruktion auf. Eine Exception an dieser Stelle würde bereits die Objekterzeugung abbrechen. Bis Aufgabe 3 enthält das Headerarray deshalb nur Nullbytes und ist noch kein korrekter RTP-Header.

## 3. `src/rtp/FecHandler.java`

Vorlage: [FecHandlerDemo.java](../src/rtp/FecHandlerDemo.java).

```java
package rtp;

import java.util.HashMap;

public class FecHandler extends FecHandlerDemo {

    public FecHandler(int size) {
        super(size);
    }

    public FecHandler(boolean useFec) {
        super(useFec);
    }

    @Override
    boolean checkCorrection(
            int nr,
            HashMap<Integer, RtpPacket> mediaPackets) {
        // TODO Aufgabe 5
        return false;
    }

    @Override
    RtpPacket correctRtp(
            int nr,
            HashMap<Integer, RtpPacket> mediaPackets) {
        // TODO Aufgabe 5
        return null;
    }
}
```

Der Konstruktor mit `int` wird auf der Serverseite für die Gruppengröße genutzt, der mit `boolean` auf der Clientseite für die FEC-Nutzung. Der Senderteil wird weitgehend aus der Demo geerbt.

`checkCorrection()` meldet vorläufig `false`: Eine Wiederherstellung ist noch nicht implementiert. `correctRtp()` liefert entsprechend noch kein Paket. Die eigentliche Logik folgt in Aufgabe 5.

Die Signaturen müssen zur Vorlage passen: **`HashMap<Integer, RtpPacket>`** darf beim Überschreiben nicht einfach durch `Map<Integer, RtpPacket>` ersetzt werden. Die Methode heißt **`correctRtp`**, auch wenn einzelne Dokumentationsstellen `correctRTP` schreiben.

## 4. `src/JpegDisplay.java`

Vorlage: [JpegDisplayDemo.java](../src/JpegDisplayDemo.java).

```java
import java.awt.image.BufferedImage;
import java.util.List;

public class JpegDisplay extends JpegDisplayDemo {

    @Override
    BufferedImage setTransparency(
            BufferedImage back,
            BufferedImage foreground,
            List<Integer> list) {
        // TODO Aufgabe 8: vorläufig unverändertes aktuelles Bild.
        return foreground;
    }
}
```

Diese Datei erhält **keine `package`-Zeile**. Ein eigener Konstruktor ist hier nicht nötig: Die parameterlose Konstruktion funktioniert bereits über die automatisch erzeugten Konstruktoren.

`foreground` ist das aktuelle Bild, `back` das vorherige Bild, und `list` benennt fehlende Bildbereiche. Der Platzhalter gibt nur das aktuelle Bild weiter. `null` wäre hier ungeeignet, weil die geerbte Anzeigelogik das Ergebnis anschließend als Bild verwendet.

Die Methode `nextPlaybackImage(...)` brauchst du nicht zu kopieren. Sie wird aus der Demo geerbt und erzeugt intern eine `JpegDisplay`-Instanz für den Aufruf von `setTransparency(...)`. Die Fehlerkaschierung ergänzt du erst in Aufgabe 8.

## Was bedeuten die Java-Schlüsselwörter?

| Ausdruck | Bedeutung hier |
| --- | --- |
| `extends` | Die neue Klasse erbt die vorhandenen Methoden und Felder der Demo-Klasse. |
| `super(...)` | Ruft den passenden Konstruktor der Oberklasse auf. |
| `@Override` | Lässt Java prüfen, dass die Methode wirklich eine Oberklassenmethode überschreibt. |
| `abstract` in der Demo | Die Klasse ist als Vorlage gedacht und kann nicht direkt mit `new` erzeugt werden. Die Unterklasse muss ihre abstrakten Methoden ergänzen. |
| Kein Sichtbarkeitswort vor einer Methode | Die Methode ist innerhalb des Packages zugänglich. So sind die RTP-, FEC- und JPEG-Methoden bereits vorgegeben. |

Die neue Klasse selbst soll jeweils `public` sein und **nicht** wieder `abstract`. Die vorgegebenen öffentlichen RTSP-Methoden müssen öffentlich bleiben. Bei den anderen gezeigten Overrides ist die vorhandene Package-Sichtbarkeit korrekt; `public` wäre ebenfalls erlaubt, `private` nicht.

## Abschluss von 0.3 und die nächsten Schritte

Prüfe nach dem Einfügen:

- Alle vier Dateien liegen an den oben genannten Stellen.
- Die Dateinamen, Packages und `extends`-Angaben stimmen exakt.
- Beide Konstruktoren sind jeweils in `Rtsp`, `RtpPacket` und `FecHandler` vorhanden.
- IntelliJ meldet keine fehlenden abstrakten Methoden mehr.
- Die Abhängigkeiten aus **Aufgabe 0.2** sind eingerichtet: Webcam-Bibliothek, nativer Webcam-Treiber und JUnit. Auch ohne Webcam-Nutzung werden die Importe des Servers beim Bauen benötigt.

Die vorhandene `.iml`-Datei enthält noch keine Bibliothekseinträge; aus dem Repository lässt sich daher keine abgeschlossene Einrichtung von 0.2 nachweisen. Ein JDK muss ebenfalls im Projekt eingestellt sein. Die verwendete Java-Syntax enthält Switch-Ausdrücke; wähle einen passenden JDK-Sprachstand, beispielsweise 17 oder neuer.

Baue das Projekt anschließend in IntelliJ. **Ein startbares Gerüst ist das Ziel von 0.3; die Videowiedergabe folgt erst nach den weiteren Aufgaben.** Mit diesen Platzhaltern bewirken Options/Describe noch keine Anfrage, die booleschen RTSP-Methoden liefern `false`, und Setup meldet die oben erklärte Exception.

Für den anschließenden Start gemäß **0.4** erstellst du getrennte Run-Konfigurationen:

| Einstellung | Server | Client |
| --- | --- | --- |
| Main class | `Server` | `Client` |
| Program arguments | `8554` | `rtsp://localhost:8554/htw.mjpeg` |
| Working directory | Projektwurzel | Projektwurzel |

Starte zuerst den Server. Der Client öffnet beim Klick auf **Connect** bereits die TCP-Verbindung. Ohne laufenden Server entsteht dort ein Verbindungsfehler. In **0.5** kommt `videos/htw.mjpeg` hinzu; bei der Client-URL wird kein `videos/` angegeben.

Die vorhandenen Shellskripte sind Vorlagen: Sie erwarten einen festen Ausgabepfad namens `out/production/RTSP-Streaming`, das Clientskript verwendet `htw-r1.mjpeg`, und der Kompilier-Classpath enthält den im Server importierten nativen Webcam-Treiber nicht ausdrücklich. Die tatsächlichen Abhängigkeiten und Ausgabepfade sollten vor ihrer Nutzung geprüft werden. Für den Einstieg sind die oben beschriebenen IDE-Konfigurationen übersichtlicher.

Die Beispiele wurden mit den Signaturen und Aufrufstellen im vorhandenen Code abgeglichen. Eine Kompilierung wurde für diese Dokumentation nicht durchgeführt; über `java` und `javac` war in der untersuchten Terminalumgebung kein Java-Runtime/JDK verfügbar.
