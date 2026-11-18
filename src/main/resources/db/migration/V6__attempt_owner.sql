-- Bisherige Versuche haben keinen Besitzer. Es sind nur Testdaten aus der Entwicklung,
-- deshalb werden sie entfernt (Antworten und Auswahl verschwinden per ON DELETE CASCADE mit).
DELETE FROM attempt;
ALTER TABLE attempt
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES app_user (id) ON DELETE CASCADE;
CREATE INDEX idx_attempt_user ON attempt (user_id);