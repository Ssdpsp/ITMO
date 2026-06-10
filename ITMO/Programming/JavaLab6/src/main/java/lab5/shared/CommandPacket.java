package lab5.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lab5.domain.model.TicketDraft;

/**
 * Сериализуемый объект запроса, отправляемый клиентом.
 */
public class CommandPacket implements Serializable {
    private static long serialVersionUID = 1L;

    private long requestId;
    private CommandCode command;
    private List<CommandArgument> arguments;
    private TicketDraft element;

    public CommandPacket(
        long requestId,
        CommandCode command,
        List<CommandArgument> arguments,
        TicketDraft element
    ) {
        this.requestId = requestId;
        this.command = command;
        this.arguments = arguments == null ? List.of() : new ArrayList<>(arguments);
        this.element = element;
    }

    public static CommandPacket noArgs(long requestId, CommandCode command) {
        return new CommandPacket(requestId, command, List.of(), null);
    }

    public long getRequestId() {
        return requestId;
    }

    public CommandCode getCommand() {
        return command;
    }

    public List<CommandArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public TicketDraft getElement() {
        return element;
    }

    public Optional<CommandArgument> argument(String name) {
        return arguments.stream()
            .filter(arg -> arg.getName().equals(name))
            .findFirst();
    }

    public Object argumentValue(String name, Class type) {
        Optional<CommandArgument> found = argument(name);
        if (found.isEmpty()) {
            return null;
        }
        return found.get().as(type);
    }
}
