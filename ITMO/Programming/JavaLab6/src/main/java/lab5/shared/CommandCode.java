package lab5.shared;

import java.util.Arrays;
import java.util.Optional;

/**
 * Имена команд, передаваемых между клиентом и сервером.
 */
public enum CommandCode {
    HELP("help"),
    INFO("info"),
    SHOW("show"),
    ADD("add"),
    UPDATE("update"),
    REMOVE_BY_ID("remove_by_id"),
    CLEAR("clear"),
    SAVE("save"),
    EXECUTE_SCRIPT("execute_script"),
    EXIT("exit"),
    ADD_IF_MAX("add_if_max"),
    REMOVE_GREATER("remove_greater"),
    REMOVE_LOWER("remove_lower"),
    GROUP_COUNTING_BY_CREATION_DATE("group_counting_by_creation_date"),
    FILTER_CONTAINS_NAME("filter_contains_name"),
    FILTER_LESS_THAN_REFUNDABLE("filter_less_than_refundable"),
    HISTORY("history");

    private String commandName;

    CommandCode(String commandName) {
        this.commandName = commandName;
    }

    public String commandName() {
        return commandName;
    }

    public static Optional<CommandCode> fromName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String normalized = name.trim().toLowerCase();
        return Arrays.stream(values())
            .filter(code -> code.commandName.equals(normalized))
            .findFirst();
    }
}
