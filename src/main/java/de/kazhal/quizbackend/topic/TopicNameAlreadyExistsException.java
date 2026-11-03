package de.kazhal.quizbackend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicNameAlreadyExistsException extends RuntimeException {
    public TopicNameAlreadyExistsException(String name) {
        super("Ein Thema mit dem Namen '" + name + "' existiert bereits");
    }
}
/*
                     TopicNotFoundException	        TopicNameAlreadyExistsException
Anlass	                Thema gibt es nicht	        Name gibt es schon
Status	                NOT_FOUND → 404	            CONFLICT → 409
Parameter	            Long id	                    String name

Der Parameter ist ein String, weil es hier um den Namen geht, nicht um eine ID. Er wird in die Meldung eingebaut.

Warum 409 Conflict?

Die Anfrage selbst ist korrekt: gültiges JSON, alle Felder da. Sie passt aber nicht zum aktuellen Zustand auf dem Server.
Genau dafür ist 409 gedacht.

400 wäre falsch, denn die Anfrage ist nicht kaputt.
404 wäre falsch, denn es fehlt nichts.
Wo sie geworfen wird (Schritt 5, TopicService)

Beim Anlegen:

java
if (topicRepository.existsByNameIgnoreCase(request.name())) {
    throw new TopicNameAlreadyExistsException(request.name());
}

Beim Ändern, nur wenn sich der Name wirklich ändert:

java
boolean nameChanged = !topic.getName().equalsIgnoreCase(request.name());
if (nameChanged && topicRepository.existsByNameIgnoreCase(request.name())) {
    throw new TopicNameAlreadyExistsException(request.name());
}

Ohne nameChanged würde dein eigenes Thema als „schon vorhanden“ gelten, sobald du nur die Beschreibung änderst.

Im Test: Anfrage 5 und 6
### 5. POST "Spring Boot"   → 201 Created
### 6. POST "spring boot"   → 409 Conflict

Anfrage 6 ist kleingeschrieben und wird trotzdem erkannt. Das liegt an IgnoreCase. Antwort:

json
{
  "status": 409,
  "error": "Conflict",
  "message": "Ein Thema mit dem Namen 'spring boot' existiert bereits",
  "path": "/api/topics"
}
Eine gute Frage, die man sich stellen kann

In deiner Tabelle steht schon name VARCHAR(100) NOT NULL UNIQUE (V1). Die Datenbank würde ein Duplikat also
sowieso ablehnen. Warum dann noch die Prüfung im Service?

Saubere Antwort: Ohne die Prüfung wirft die Datenbank einen technischen Fehler, und der Client bekommt 500
mit einer kryptischen Meldung. Mit der Prüfung bekommt er 409 und einen verständlichen Text.
Groß-/Kleinschreibung: UNIQUE in PostgreSQL unterscheidet Spring Boot und spring boot. Die Datenbank würde
beide zulassen. Erst dein Service mit IgnoreCase verhindert das.
 */