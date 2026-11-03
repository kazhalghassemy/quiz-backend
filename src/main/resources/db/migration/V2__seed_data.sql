INSERT INTO topic (id, name, description) VALUES
                                              (1, 'Java-Grundlagen', 'Sprachkern, Datentypen und Vererbung'),
                                              (2, 'Datenbanken', 'SQL-Grundlagen und relationale Modelle');
INSERT INTO question (id, topic_id, text, type, difficulty, points, time_limit_seconds, explanation)
VALUES
    (1, 1, 'Welches Schlüsselwort verhindert, dass eine Klasse erweitert wird?',
     'SINGLE_CHOICE', 'EASY', 10, 30,
     'Eine als final deklarierte Klasse kann nicht abgeleitet werden.'),
    (2, 1, 'Welche dieser Typen sind primitive Datentypen?',
     'MULTIPLE_CHOICE', 'EASY', 15, 45,
     'int und boolean sind primitiv, String und Integer sind Klassen.'),
    (3, 1, 'Mit welchem Schlüsselwort erbt eine Klasse von einer anderen?',
     'FREE_TEXT', 'MEDIUM', 10, NULL,
     'Mit extends erbt eine Klasse. Java unterscheidet Groß- und Kleinschreibung, daher zählt nur
die kleingeschriebene Form.'),
    (4, 2, 'Mit welcher SQL-Anweisung liest man Daten aus einer Tabelle?',
     'FREE_TEXT', 'EASY', 10, NULL,
     'SELECT liest Daten, INSERT/UPDATE/DELETE verändern sie. SQL-Schlüsselwörter sind nicht
case-sensitiv.'),
    (5, 2, 'Welche Befehle gehören zur DML (Data Manipulation Language)?',
     'MULTIPLE_CHOICE', 'MEDIUM', 15, 60,
     'INSERT, UPDATE und DELETE verändern Daten (DML). CREATE TABLE verändert die Struktur und
gehört zur DDL.'),
    (6, 2, 'Ein Primärschlüssel darf NULL-Werte enthalten.',
     'TRUE_FALSE', 'EASY', 5, 20,
     'Ein Primärschlüssel ist immer eindeutig und NOT NULL.');
INSERT INTO answer_option (id, question_id, text, correct) VALUES
                                                               (1, 1, 'final', TRUE), (2, 1, 'static', FALSE), (3, 1, 'private', FALSE),
                                                               (10, 2, 'int', TRUE), (11, 2, 'String', FALSE), (12, 2, 'boolean', TRUE), (13, 2, 'Integer',
                                                                                                                                          FALSE),
                                                               (30, 5, 'INSERT', TRUE), (31, 5, 'UPDATE', TRUE), (32, 5, 'CREATE TABLE', FALSE), (33, 5,
                                                                                                                                                  'DELETE', TRUE),
                                                               (40, 6, 'Wahr', FALSE), (41, 6, 'Falsch', TRUE);
INSERT INTO accepted_answer (id, question_id, text, case_sensitive) VALUES
                                                                        (1, 3, 'extends', TRUE),
                                                                        (2, 4, 'SELECT', FALSE);-- IDs wurden oben von Hand vergeben. Die Zähler der Tabellen auf den höchsten Wert setzen,-- sonst kollidiert die nächste automatisch vergebene ID mit einer bestehenden.
SELECT setval(pg_get_serial_sequence('topic', 'id'), (SELECT MAX(id) FROM topic));
SELECT setval(pg_get_serial_sequence('question', 'id'), (SELECT MAX(id) FROM question));
SELECT setval(pg_get_serial_sequence('answer_option', 'id'), (SELECT MAX(id) FROM answer_option));
SELECT setval(pg_get_serial_sequence('accepted_answer', 'id'), (SELECT MAX(id) FROM
    accepted_answer));