package lab5.infrastructure.storage;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import lab5.domain.model.Ticket;

/**
 * Результат загрузки CSV-файла.
 */
public class LoadReport {
    private LinkedHashSet<Ticket> tickets;
    private List<ProblemLine> problems;
    private String message;
    private boolean fatal;

    /**
     * Создает отчет о загрузке.
     *
     * @param tickets загруженные билеты
     * @param problems невалидные строки
     * @param message понятное пользователю сообщение
     * @param fatal true, если загрузка полностью завершилась ошибкой
     */
    public LoadReport(
        LinkedHashSet<Ticket> tickets,
        List<ProblemLine> problems,
        String message,
        boolean fatal
    ) {
        this.tickets = tickets;
        this.problems = problems;
        this.message = message;
        this.fatal = fatal;
    }

    public LinkedHashSet<Ticket> getTickets() {
        return new LinkedHashSet<>(tickets);
    }

    public List<ProblemLine> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    public String getMessage() {
        return message;
    }

    public boolean isFatal() {
        return fatal;
    }
}
