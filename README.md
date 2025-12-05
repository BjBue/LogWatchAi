# LogWatchAi

**LogWatchAi** ist ein lokales Java-Tool zur Überwachung von Logdateien.  
Es analysiert neue Einträge in Echtzeit mithilfe von KI und dokumentiert oder meldet automatisch passende Maßnahmen oder Warnungen.

# Hinweis
### Einen OpenAi-Key erhält man unter  https://platform.openai.com/
Diesen dann in ./logwatchai/config/logwatchai.yaml unter ai:


## Projekt-URLs

- Log-Generator Healthcheck: `http://localhost:9090/health`
- LogWatch-AI Healthcheck: `http://localhost:8080/health`
- LogWatch-AI Swagger: `http://localhost:8080/swagger-ui/index.html#/`
- Mailhog (Mail Testing): `http://localhost:8025/`
- LogWatch Webapp: `http://localhost:8081`  
(Zugangsdaten in der ./config/logwatchai.yaml, oder admin:secret123)

---

## Projekt neu erstellen

Führe die folgenden Schritte in der Konsole in beiden Verzeichnissen mit der `pom.xml` (`logwatchai` und `loggenerator`) aus:

```bash
# Projekt bauen
.\mvnw.cmd clean package

# Docker-Container herunterfahren (ggf. mit -v, um auch Volumes zu löschen)
docker-compose down

# Docker-Container bauen
docker-compose build

# Docker-Container starten
docker-compose up -d
```
> Hinweis: Das Löschen von Volumes (-v) entfernt alle gespeicherten Datenbanken.

## POSTMAN / API Interaktionen

Du kannst den Log-Generator verwenden, um Testmeldungen zu erzeugen:

- **Eine Logmeldung mit E-Mail generieren:**

```text
http://localhost:9090/write?message="ERROR AuthService Fatal system failure: database not reachable"
```
- **Mehrere pseudozufällige Meldungen generieren (maximal 3 empfohlen):**
```text
http://localhost:9090/generate?count=3
```
> Hinweis: Mehr als 5 Meldungen können die Wartezeit für eine Analyse verlängern,
da nur ein kleines LLM-Modell mit begrenzten Abfragen verwendet wird.




