package lab5.shared;

import java.io.Serializable;

/**
 * Типизированный аргумент команды. Да, он специально немного универсальный.
 */
public class CommandArgument implements Serializable {
    private static long serialVersionUID = 1L;

    private String name;
    private Serializable value;

    public CommandArgument(String name, Serializable value) {
        this.name = name;
        this.value = value;
    }

    public static CommandArgument of(String name, Serializable value) {
        return new CommandArgument(name, value);
    }

    public String getName() {
        return name;
    }

    public Serializable getValue() {
        return value;
    }

    public Object as(Class type) {
        if (type.isInstance(value)) {
            return value;
        }
        return null;
    }
}
