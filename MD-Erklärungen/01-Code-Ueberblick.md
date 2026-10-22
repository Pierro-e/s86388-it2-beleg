# 01 – Der Code grob erklärt

Zur [Dokumentübersicht](README.md) · Weiter: [RTSP-Stand](02-RTSP-Stand-und-Aufgaben.md) · [Klassen für Aufgabe 0.3](03-Aufgabe-0.3-Klassen-anlegen.md)

## Worum geht es?

Das Projekt ist ein Java-Videostreaming-System mit zwei getrennten Programmen: Der **Server** liest ein Video und verschickt dessen Bilder. Der **Client** fordert die Wiedergabe an, empfängt die Bilddaten und zeigt sie in einem Fenster an.

Dabei haben zwei Protokolle unterschiedliche Aufgaben:

| Protokoll | Zweck im Projekt | Transport |
| --- | --- | --- |
| RTSP | Wiedergabe vorbereiten, starten, pausieren und beenden | Textnachrichten über eine TCP-Verbindung |
| RTP | Die eigentlichen Videobilddaten übertragen | Pakete über UDP |

**SDP** beschreibt innerhalb der RTSP-Antwort auf `DESCRIBE` die angebotenen Medien und Parameter. **FEC** ergänzt später Wiederherstellungsinformationen für verlorene RTP-Pakete. **Fehlerkaschierung** versucht anschließend, verbleibende Bildlücken mit Informationen des vorherigen Bildes zu verdecken.

## Dein aktueller Stand: Beginn von Aufgabe 0.3

Das ist ein vorbereitetes Lehrprojekt. Viele Abläufe sind vorhanden; bestimmte Stellen sollen bewusst von dir ergänzt werden. Aktuell fehlen diese vier konkreten Klassen:

- `rtsp.Rtsp`
- `rtp.RtpPacket`
- `rtp.FecHandler`
- `JpegDisplay`

Die bereits vorhandenen abstrakten `...Demo`-Klassen liefern jeweils das Grundgerüst. Andere Klassen importieren oder erzeugen die vier fehlenden Klassen schon. Deshalb lässt sich der vorliegende Stand noch nicht vollständig kompilieren.

In **Aufgabe 0.3** legst du diese Klassen mit `extends`, passenden Konstruktoren und Methodenrümpfen an. Die eigentliche Protokoll- und Korrekturlogik folgt später. Die genaue Anleitung steht in [Dokument 03](03-Aufgabe-0.3-Klassen-anlegen.md).

## Die Projektstruktur

```text
Projektwurzel/
├── src/
│   ├── Client.java, ClientView.java
│   ├── Server.java, ServerView.java
│   ├── JpegDisplayDemo.java
│   ├── rtsp/      RTSP-Steuerung
│   ├── rtp/       RTP-Pakete, JPEG-Verarbeitung, Puffer und FEC
│   ├── video/     Video einlesen und Metadaten bestimmen
│   └── utils/     Hilfsfunktionen und Logging
├── test/          Vorbereitete JUnit-Tests
├── scripts/       Start- und Kompilierskripte
├── statistics/    Material für spätere statistische Auswertungen
├── images/        Abbildungen für die Dokumentation
└── MD-Erklärungen/ Alle Markdown-Unterlagen
```

Der Ordner `videos/` ist im untersuchten Stand noch nicht vorhanden. Er wird erst in Aufgabe 0.5 mit dem Beispielvideo ergänzt. Die vier Java-Dateien aus Aufgabe 0.3 gehören unter `src/`, an die in Dokument 03 angegebenen Stellen.

## Welche Klassen solltest du kennen?

| Datei | Aufgabe |
| --- | --- |
| [Client.java](../src/Client.java) | Einstieg über `main()`. Verknüpft die Buttons mit RTSP, startet den RTP-Empfänger und steuert die Anzeige per Timer. |
| [ClientView.java](../src/ClientView.java) | Baut die Client-Oberfläche: Buttons, Videoanzeige, Statistik und Einstellungen. |
| [Server.java](../src/Server.java) | Einstieg über `main()`. Wartet auf RTSP-Anfragen, verwaltet den Serverzustand und verschickt während `PLAY` regelmäßig Bilder. |
| [ServerView.java](../src/ServerView.java) | Serverfenster mit Zustandsanzeige, Paketverlustrate und FEC-Gruppengröße. |
| [RtspDemo.java](../src/rtsp/RtspDemo.java) | Gemeinsames RTSP-Grundgerüst für Client und Server. Die noch fehlende `Rtsp` ergänzt es. |
| [RtpPacketDemo.java](../src/rtp/RtpPacketDemo.java) | Repräsentiert ein RTP-Paket: Headerfelder, Nutzdaten, Einlesen und Ausgabe als Bytefolge. Das Schreiben des Headers fehlt. |
| [RtpHandler.java](../src/rtp/RtpHandler.java) | Organisiert Versand, Empfang, Zwischenspeicherung, Zusammenstellung von Bildern und Aufrufe der FEC-Korrektur. |
| [JpegFrame.java](../src/rtp/JpegFrame.java) | Wandelt JPEG-Bilder in RTP-Nutzdaten um, zerlegt sie in Fragmente und setzt empfangene Teile wieder zusammen. |
| [FecHandlerDemo.java](../src/rtp/FecHandlerDemo.java) | Bereitet FEC-Pakete vor und verwaltet empfangene FEC-Informationen. Prüfung und Wiederherstellung fehlender Medienpakete sind abstrakt. |
| [FecPacket.java](../src/rtp/FecPacket.java) | Bereits bereitgestellte FEC-Paketverarbeitung mit Verknüpfung von Medienpaketen und Rekonstruktion. Baut auf der noch fehlenden `RtpPacket` auf. |
| [JpegDisplayDemo.java](../src/JpegDisplayDemo.java) | Dekodiert JPEG-Daten für die Anzeige, merkt sich das vorherige Bild und ruft bei Bedarf die noch fehlende Fehlerkaschierung auf. |
| [ReceptionStatistic.java](../src/rtp/ReceptionStatistic.java) | Sammelt Zähler für empfangene, verlorene und korrigierte Pakete sowie Bildverluste und Pufferfüllstand. |
| [VideoReader.java](../src/video/VideoReader.java) | Liest nacheinander einzelne JPEG-Bilder aus der Videodatei. |
| [VideoMetadata.java](../src/video/VideoMetadata.java) | Bestimmt Bildrate und Dauer. Nutzt für AVI/MOV die Parser im selben Package, für MJPEG eine Standardbildrate von 25 Bildern pro Sekunde. |
| [CustomLoggingHandler.java](../src/utils/CustomLoggingHandler.java) / [ByteTasks.java](../src/utils/ByteTasks.java) | Konsolenausgaben formatieren beziehungsweise Bytefolgen für Hilfszwecke darstellen. |

Außerdem gibt es [SrtpHandler.java](../src/rtp/SrtpHandler.java) und [JpegEncryptionHandler.java](../src/rtp/JpegEncryptionHandler.java) für Verschlüsselung. Für den Einstieg soll die Verschlüsselung gemäß Projektbeschreibung auf **„keine“** bleiben.

## Wie gelangt ein Bild vom Server zum Client?

```text
Steuerung:
Client-Button → Client.ButtonListener → Rtsp → TCP → Server.handleRequest

Bilddaten:
VideoReader.readNextImage()
  → Server.actionPerformed()                    Sendetimer
  → RtpHandler.sendJpeg()
  → jpegToRtpPackets() + JpegFrame + RtpPacket  Bild in Pakete zerlegen
  → UDP                                        ggf. simulierter Paketverlust
  → RtpHandler.Receiver                         Empfangsthread im Client
  → processRtpPacket()                          Pakete speichern
  → RtpHandler.nextPlaybackImage()              Bildteile zusammenstellen
  → JpegDisplay.nextPlaybackImage()             JPEG dekodieren, ggf. kaschieren
  → ClientView                                  Bild anzeigen
```

Das ist der vorgesehene Ablauf nach Ergänzung der jeweils nötigen Aufgaben. Ein einzelnes Videobild kann aus mehreren RTP-Paketen bestehen. Sie tragen denselben **Zeitstempel**, aber unterschiedliche **Sequenznummern**; das letzte Fragment wird zusätzlich durch das **Marker-Bit** gekennzeichnet.

Der Empfang läuft in einem eigenen Thread. Die Anzeige wird durch einen Swing-Timer gesteuert. Ein **Jitterpuffer** sammelt zunächst mehrere Bilder, damit schwankende Paketankunftszeiten nicht sofort die Wiedergabe stören. Der Client setzt dafür standardmäßig einen Startpuffer von zehn Bildern.

Im Server kann ein Kanalemulator Pakete absichtlich verwerfen. Die späteren Aufgaben untersuchen die dadurch entstehenden Störungen und ergänzen Gegenmaßnahmen. Die serverseitige FEC-Erzeugung ist weitgehend vorgegeben; die clientseitige Entscheidung und Wiederherstellung müssen noch implementiert werden.

## Welche Aufgabe ergänzt welchen Teil?

| Aufgabe | Schwerpunkt |
| --- | --- |
| **0.3 – jetzt** | Vier konkrete Klassen anlegen und alle abstrakten Methoden mit zunächst einfachen Rümpfen überschreiben. |
| 0.4 / 0.5 | Startparameter einstellen und Beispielvideo in `videos/` ablegen. |
| 1 | RTSP-Clientmethoden und Versand der RTSP-Anfragen in `rtsp.Rtsp`. |
| 2 | SDP-Beschreibung durch Überschreiben von `getDescribe(...)` in `rtsp.Rtsp`. |
| 3 | RTP-Header in `rtp.RtpPacket.setRtpHeader()` schreiben; danach sollte das Demovideo abspielbar sein. |
| 4 / 6 | Paket- und Bildverluste messen, berechnen und darstellen. |
| 5 | FEC-Prüfung und Wiederherstellung in `rtp.FecHandler`. |
| 7 / 8 | Restart-Marker untersuchen und Fehlerkaschierung in `JpegDisplay` ergänzen. |
| 9 / 10 | Reed-Solomon-Codierung und eigene Ergebnisdokumentation. |

## Sinnvolle Lesereihenfolge

Lies zunächst [Aufgabe 0](Aufgabenstellung.md#0-vorarbeiten) und die [Anleitung zum Anlegen der Klassen](03-Aufgabe-0.3-Klassen-anlegen.md). Betrachte danach in `Client.java` den `ButtonListener`, in `RtspDemo.java` die Beispielmethode `setup()` und in `Server.java` die Methode `handleRequest()`. Damit erkennst du den Steuerungsablauf, bevor du dich mit den einzelnen Bildbytes beschäftigst.

Die Tests unter `test/` sind nur ein Anfang: Der vorhandene RTP-Test prüft einen Sequenznummern-Getter, die FEC-Testmethoden sind leer. Ein erfolgreicher Lauf dieser Tests allein würde deshalb noch keine korrekte Übertragung belegen.
