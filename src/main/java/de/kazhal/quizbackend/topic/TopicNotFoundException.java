package de.kazhal.quizbackend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/*
Bis Tag 08
public class TopicNotFoundException extends RuntimeException {

    public TopicNotFoundException(Long id) {
        super("Thema mit ID " + id + " nicht gefunden");
    }
}

 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(Long id) {
        super("Thema mit ID " + id + " nicht gefunden");
    }
}

/*
HttpStatus: ein Enum mit allen HTTP-Statuscodes

Damit du nicht mit nackten Zahlen wie 404 arbeiten musst, liefert Spring ein Enum mit lesbaren Namen:

Enum-Wert	                Code
HttpStatus.OK	            200
HttpStatus.CREATED	        201
HttpStatus.NO_CONTENT	    204
HttpStatus.BAD_REQUEST	    400
HttpStatus.NOT_FOUND	    404
HttpStatus.CONFLICT	        409

@ResponseStatus: „Bei dieser Exception antworte mit diesem Code“

Die Annotation verbindet eine Exception mit einem HTTP-Statuscode.

Ohne @ResponseStatus sieht Spring nur, dass irgendeine unbehandelte Exception geflogen ist, und antwortet mit 500 Internal Server Error. Aus Sicht des Clients ist der Server dann kaputt, obwohl einfach nur das Thema fehlt.

Mit @ResponseStatus(HttpStatus.NOT_FOUND) weiß Spring, dass diese Exception „nicht gefunden“ bedeutet, und antwortet mit 404.

Der Ablauf bei GET /api/topics/999
TopicController.getTopic(999)
   └─ topicService.findTopic(999)
        └─ getTopic(999)
             └─ findById(999) → leer
                  └─ throw new TopicNotFoundException(999)   💥
                                    │
   Spring fängt die Exception ab ◄──┘
   sieht @ResponseStatus(NOT_FOUND)
   → Antwort: 404
 */