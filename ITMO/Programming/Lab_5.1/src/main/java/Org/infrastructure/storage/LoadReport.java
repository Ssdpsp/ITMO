package Org.infrastructure.storage;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import Org.domain.model.Ticket;

/**
 * Результат загрузки CSV файла.
 */
public final class LoadReport {
    private final LinkedHashSet<Ticket> tickets;
    private final List<ProblemLine> problems;
    private final String message;
    private final boolean fatal;

    /**
     * Создает отчёт о загрузке.
     *
     * @param tickets loaded tickets
     * @param problems invalid lines
     * @param message human readable message
     * @param fatal true when loading failed entirely
     */
    public LoadReport(
            final LinkedHashSet<Ticket> tickets,
            final List<ProblemLine> problems,
            final String message,
            final boolean fatal
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
