# Quiz-App Backend

REST-API für eine Quiz-Lernplattform. Lernende spielen Quizze zu verschiedenen Themen und bekommen ihre Antworten sofort ausgewertet. Admins verwalten Themen und Fragen.

Gebaut in 20 Tagen als Lernprojekt mit Spring Boot, von der ersten Entity bis zum fertigen Docker-Setup.

## Funktionen

- **Themen und Fragen** mit vier Fragetypen: Single Choice, Multiple Choice, Wahr/Falsch und Freitext
- **Quiz spielen:** Versuch starten, Fragen beantworten, sofortige Auswertung mit Punkten, Versuch abschließen
- **Registrierung und Login** mit Tokens
- **Rollen:** `ADMIN` verwaltet Themen und Fragen und sieht die Lösungen, `LEARNER` spielt Quizze
- **Eigene Versuche:** Jeder sieht und bearbeitet nur seine eigenen Versuche
- **Einheitliche Fehlerantworten** im ProblemDetail-Format (RFC 9457) mit eigenem Fehlercode
- **Nächtlicher Aufräum-Job** für abgelaufene und widerrufene Tokens

## Technik

| Bereich | Eingesetzt |
|---|---|
| Sprache und Framework | Java 21, Spring Boot |
| Datenbank | PostgreSQL 17, Spring Data JPA / Hibernate |
| Migrationen | Flyway |
| Sicherheit | Spring Security, BCrypt, Opaque Tokens, `@PreAuthorize` |
| Mapping | MapStruct |
| Tests | JUnit 5, MockMvc, AssertJ, Testcontainers |
| Betrieb | Docker (Multi-Stage-Build, Layered JAR), Docker Compose |

## Architektur und Entscheidungen

**Schichten:** Controller → Service → Repository. Die Controller kennen nur DTOs, nie die Entities.

**Login mit Opaque Tokens statt JWT:** Nach dem Login bekommt der Client ein zufälliges Token (256 Bit). In der Datenbank liegt davon nur der SHA-256-Hash. Ein eigener Filter prüft das Token bei jeder Anfrage.
Vorteil gegenüber JWT: Ein Logout oder ein gesperrtes Konto wirkt **sofort**, weil das Token in der Datenbank widerrufen wird.

**Passwörter** werden mit BCrypt gehasht (mit Salz, absichtlich langsam). Tokens mit SHA-256, weil danach in der Datenbank gesucht werden muss.

**Rechte auf zwei Ebenen:**
- Rollen per `SecurityFilterChain` und `@PreAuthorize` an den Controller-Methoden
- Besitz prüft der Service: Ein fremder Versuch liefert `404` statt `403`, damit nicht verraten wird, dass es ihn gibt (Schutz gegen Broken Object Level Authorization)

**Docker:** Das Image wird in zwei Stufen gebaut (JDK zum Bauen, JRE zum Laufen). Das JAR ist in Schichten zerlegt, damit nach einer Code-Änderung nur die kleine Schicht mit dem eigenen Code neu gebaut wird. Die App läuft nicht als root, und Zugangsdaten kommen nur als Umgebungsvariablen von außen.

## Starten mit Docker

Voraussetzung: Docker Desktop

1. `.env.example` nach `.env` kopieren und die Passwörter eintragen
2. Alles bauen und starten:
   ```
   docker compose up -d --build
   ```
3. Prüfen: `http://localhost:8080/actuator/health` zeigt `{"status":"UP"}`

Beim ersten Start legt Flyway die Tabellen an, und ein Admin-Konto wird mit den Daten aus der `.env` erstellt.

Stoppen:
```
docker compose down        # Container weg, Daten bleiben
docker compose down -v     # Container und Daten weg
```

## Entwickeln

- Nur die Datenbank starten: `docker compose up -d postgres`
- App in IntelliJ mit Profil `dev` starten
- Tests: `./mvnw verify` (braucht Docker für Testcontainers)

## API (Auswahl)

| Methode | Pfad | Zugriff |
|---|---|---|
| `POST` | `/api/auth/register` | öffentlich |
| `POST` | `/api/auth/login` | öffentlich |
| `POST` | `/api/auth/logout` | angemeldet |
| `GET` | `/api/auth/me` | angemeldet |
| `GET` | `/api/topics` | öffentlich |
| `POST` / `PUT` / `DELETE` | `/api/topics…` | ADMIN |
| `GET` | `/api/topics/{id}/questions` (mit Lösungen) | ADMIN |
| `POST` | `/api/attempts` | angemeldet |
| `POST` | `/api/attempts/{id}/answers` | Besitzer des Versuchs |
| `POST` | `/api/attempts/{id}/finish` | Besitzer des Versuchs |
| `GET` | `/api/attempts/{id}` | Besitzer oder ADMIN |
| `GET` | `/api/topics/{id}/attempts` | eigene Versuche |

Angemeldete Anfragen schicken den Header `Authorization: Bearer <token>`.
Beispiele zum Ausprobieren liegen in den `.http`-Dateien (IntelliJ HTTP Client).

## Ausblick

- Angular-Frontend (CORS für `http://localhost:4200` ist bereits freigegeben)
