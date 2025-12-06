# LogWatchAi

**Dokumentation: ./doc**

**LogWatchAi** ist ein lokales Java-Tool zur Überwachung von Logdateien.  
Es analysiert neue Einträge in Echtzeit mithilfe von KI und dokumentiert oder meldet automatisch passende Maßnahmen oder Warnungen.

**Hinweis**:
Einen OpenAi-Key erhält man unter  https://platform.openai.com/  
Diesen dann in ./logwatchai/config/logwatchai.yaml unter 'ai:key:' einfügen.  Entsprechende Typen sind vorbereitet.


## Projekt-URLs

- Log-Generator Healthcheck: `http://localhost:9090/health`
- LogWatch-AI Healthcheck: `http://localhost:8080/health`
- LogWatch-AI Swagger: `http://localhost:8080/swagger-ui/index.html#/`
- Mailhog (Mail Testing): `http://localhost:8025/`
- LogWatch Webapp: `http://localhost:8081`  
(Zugangsdaten in der ./config/logwatchai.yaml, oder admin:secret123)

---

## Projekt starten
**Hinweis**: Das Projekt läuft innerhalb einer Docker-Umgebung.  
Dies setzt zwingend eine Docker-Desktop- oder eine andere Docker-Installation voraus.  
Siehe hier: https://docs.docker.com/get-started/get-docker/ zur Installation.

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

## Lokales Debugging in IntelliJ  

Du kannst die LogWatch-AI API auch lokal ohne Docker starten, z. B. für Debugging oder Entwicklung in IntelliJ.

### 1. Run/Debug Configuration anlegen

- Öffne IntelliJ → Run → Edit Configurations
- Klicke auf + → Spring Boot und wähle das Hauptklasse bbu.solution.logwatchai.LogwatchaiApplication
- Setze den Arbeitsordner auf das Projektverzeichnis (logwatchai)

### 2. Environment-Variablen eintragen

**Füge unter Environment variables die folgenden Werte hinzu:**

Variable	Wert / Beschreibung  
APP_CONFIG_FILE	**Lokaler Pfad zur Konfigurationsdatei, z. B. C:/Users/bbuec/OneDrive/Desktop/IHK-Projekt/project/logwatchai/logwatchai/config/logwatchai.yaml**  
JWT_SECRET	**JWT-Secret für Authentifizierung (siehe Docker-Compose)**  
SPRING_DATASOURCE_URL	**JDBC-URL zur lokalen Datenbank, z. B. jdbc:mariadb://localhost:3306/logwatchai**  
SPRING_DATASOURCE_USERNAME	**Datenbank-Benutzername (loguser)**  
SPRING_DATASOURCE_PASSWORD	**Datenbank-Passwort (logpass)**  

**Hinweis: Auf anderen Maschinen müssen die Pfade (APP_CONFIG_FILE) entsprechend angepasst werden.**

### 3. Starten

Starte die Anwendung über IntelliJ Run/Debug → die Konsole sollte zeigen:  
**Loading config from: C:/Users/.../logwatchai/config/logwatchai.yaml**

Die App läuft lokal auf Port 8080 (wie in application.yaml definiert)  
Alle API-Endpunkte, Swagger UI und Healthchecks stehen wie im Docker-Setup zur Verfügung

### 4. Optional: Debugging
Du kannst Breakpoints setzen und die Anwendung direkt im Debug-Modus starten  
Änderungen am Code werden sofort wirksam, ohne dass Docker neu gebaut werden muss