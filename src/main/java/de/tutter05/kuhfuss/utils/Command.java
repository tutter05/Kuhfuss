package de.tutter05.kuhfuss.utils;

import java.util.Optional;

public abstract class Command {

    private final String name, description;
    private final Optional<String> usage;

    public Command(final String name, final String usage, final String description) {
        this.name = name;
        this.usage = Optional.of(usage);
        this.description = description;
    }

    public Command(final String name, final String description) {
        this.name = name;
        this.usage = Optional.empty();
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public Optional<String> getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public abstract void executeCommand(String[] args);

}
