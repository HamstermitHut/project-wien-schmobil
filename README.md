## Programmbeschreibung

Bei diesem Programm handelt es sich um eine Client-Server Architektur, wobei der Server als threaded Server geführt wird der mehrere Clients annehmen kann. Das Programm erfüllt die Funktion, dass es einem Benutzer eine Oberfläche als GUI anbietet, wo man je nach Auswahl die Abfahrtszeiten einer Haltestelle auswählen und einsehen kann. Die Haltestellenliste wird mit einem Request auf https://www.data.gv.at abgerufen.

Der Client kommuniziert mit einem Server, welcher je nach Anfrage auf die API für die aktuellen Zeit zugreift mit bestimmten Parametern.

# Must Have Features (Genügend)

- [ ] Mehrere Clients können eine Applikation mit einem Server aufrufen, welcher als threaded Server geführt wird und immer einen Server-Thread startet
- [X] Ein Client kann eine GUI Applikation aufrufen
- [ ] Der GUI-Client kann mit dem Server interagieren
- [X] Es wird ein CSV-File mit allen Haltestellen in Wien eingelesen und als Auswahl in der GUI angezeigt
- [X] Ein Benutzer kann eine Haltestelle auswählen aus der Liste und alle Abfahrtszeiten angezeigten lassen
- [X] Der Client kann die Applikation mit einem Button schließen, alle Ressourcen werden sauber geschlossen

## Weitere Features

- [ ] Der Client kann die Haltestellen filtern nach Bezirken 1
- [ ] Der Client kann die Haltestellen filtern nach Beförderungsmittel (Bus, U-Bahn, ....) 2
- [X] Der Client kann die Abfahrtszeitenliste filtern nach Beförderungsmittel 2
- [ ] Fahrtrichtung auswählen, wenn nur ein Beförderungsmittel auswählt ist 2
- [ ] Gleis oder Steig des Fahrzeugs anzeigen lassen 3
- [ ] Verbleibende Minuten bis zur Abfahrt anzeigen lassen 3
- [ ] Verspätungen rot anzeigen lassen 1
- [ ] Es gibt einen Button "Speichern", wo die aktuelle Ansicht als File abgespeichert wird 1
- [ ] Server-Log um zu Messen, wie viele NutzerInnen sich auf die App einloggen. 3

