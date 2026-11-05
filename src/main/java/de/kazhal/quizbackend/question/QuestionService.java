package de.kazhal.quizbackend.question;

import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/*
@Service: Spring erzeugt davon eine Bean. Der QuestionController bekommt sie über seinen Konstruktor.
@Transactional(readOnly = true): Jede Methode läuft standardmäßig in einer lesenden Transaktion. Das ist wichtig,
weil QuestionResponse.from(...) die LAZY-Listen options und acceptedAnswers lädt, und das geht nur bei offener Transaktion.
 */
@Service
@Transactional(readOnly = true)
public class QuestionService {

    /**
     * 2. Die Abhängigkeiten
     * java
     * private final QuestionRepository questionRepository;
     * private final TopicService topicService;
     *
     * Auffällig ist, dass hier der TopicService steht und nicht das TopicRepository.
     * Der Grund: topicService.getTopic(id) enthält schon die Logik
     * „nicht gefunden → TopicNotFoundException → 404“. Die wird wiederverwendet,
     * statt sie hier ein zweites Mal zu schreiben.
     *
     */
    private final QuestionRepository questionRepository;
    private final TopicService topicService;
    /*

     * Der Konstruktor darunter ist die übliche Constructor Injection: Spring übergibt beide Beans.
     */
    public QuestionService(QuestionRepository questionRepository, TopicService topicService) {
        this.questionRepository = questionRepository;
        this.topicService = topicService;
    }
    /**
    3. findQuestions: alle Fragen eines Themas: Zuständig für GET /api/topics/{topicId}/questions und ?difficulty=MEDIUM.

     java
    topicService.getTopic(topicId);   // wirft 404, falls es das Thema nicht gibt

    Der Rückgabewert wird gar nicht benutzt. Die Zeile dient nur als Prüfung.
     Ohne sie bekäme der Client bei /api/topics/999/questions eine leere Liste [] mit 200, und er wüsste nicht,
     ob das Thema keine Fragen hat oder gar nicht existiert. Mit der Zeile kommt eine eindeutige 404.

    java
    List<Question> questions = difficulty == null
        ? questionRepository.findByTopicIdOrderByIdAsc(topicId)
        : questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);

        Das ist der ternäre Operator, eine Kurzform für if/else:

    java
    if (difficulty == null) { alle Fragen des Themas }
    else                    { nur Fragen mit dieser Schwierigkeit }

    difficulty ist null, wenn in der URL kein ?difficulty= steht.
     Dafür sorgt @RequestParam(required = false) im Controller (Quizfrage 4).

Beide Repository-Methoden tragen @EntityGraph(attributePaths = "options").
     Die Optionen werden dadurch gleich in derselben Abfrage mitgeladen,
     statt für jede Frage einzeln nachzuladen (N+1-Problem).

java
return questions.stream().map(QuestionResponse::from).toList();

Das ist ein Fließband, wie bei correctOptionIds() in deiner Question:
List<Question> → jede Frage in eine QuestionResponse umwandeln → wieder als Liste sammeln.
     */
    public List<QuestionResponse> findQuestions(Long topicId, Difficulty difficulty) {
        topicService.getTopic(topicId);   // wirft 404, falls es das Thema nicht gibt
        List<Question> questions = difficulty == null
                ? questionRepository.findByTopicIdOrderByIdAsc(topicId)
                : questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);
        return questions.stream().map(QuestionResponse::from).toList();
    }

    /**
     * 4. findQuestion: eine einzelne Frage
     *
     * Zuständig für GET /api/questions/{id}.
     *
     * Von innen nach außen gelesen:
     *
     * findById(id) liefert ein Optional<Question>, also eine Frage oder nichts.
     * .orElseThrow(...): Ist eine Frage da, wird sie ausgepackt. Ist nichts da, wird die Exception geworfen (→ 404).
     * QuestionResponse.from(...) wandelt die Entity in den Record um.
     *
     * () -> new QuestionNotFoundException(id) ist ein Lambda. Die Exception wird erst erzeugt, wenn sie gebraucht wird.
     * @param id
     * @return
     */
    public QuestionResponse findQuestion(Long id) {
        return QuestionResponse.from(questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id)));
    }

    /**
     * Zuständig für POST /api/topics/{topicId}/questions.
     * @Transactional
     *
     * Diese Annotation überschreibt das readOnly = true der Klasse für diese eine Methode.
     * Hier wird geschrieben, also braucht sie eine schreibende Transaktion.
     *
     * java
     * Topic topic = topicService.getTopic(topicId);
     *
     * Das Thema wird geladen, gleichzeitig als 404-Prüfung, und weil die Frage gleich daran gehängt wird.
     *
     * java
     * Question question = new Question(request.text(), request.type(), request.difficulty(),
     *         request.points(), request.timeLimitSeconds(), request.explanation());
     *
     * Request → Entity: Aus den Daten des Clients wird ein neues Question-Objekt. Es hat noch keine ID und steht noch nicht in der Datenbank.
     * @param topicId
     * @param request
     * @return
     */
    @Transactional
    public QuestionResponse createQuestion(Long topicId, QuestionRequest request) {
        Topic topic = topicService.getTopic(topicId);
        Question question = new Question(request.text(), request.type(), request.difficulty(),
                request.points(), request.timeLimitSeconds(), request.explanation());
        request.options().forEach(o -> question.addOption(o.text(), o.correct()));
        request.acceptedAnswers().forEach(a -> question.addAcceptedAnswer(a.text(),
                a.caseSensitive()));
        topic.addQuestion(question);
        Question saved = questionRepository.save(question);
        return QuestionResponse.from(saved);
    }
}
