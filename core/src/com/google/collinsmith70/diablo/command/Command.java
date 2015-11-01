package com.google.collinsmith70.diablo.command;

import com.badlogic.gdx.Gdx;
import com.google.common.base.MoreObjects;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.Collection;
import java.util.SortedMap;

public class Command {

private static final String TAG = Command.class.getSimpleName();

private static final Trie<String, Command> COMMANDS = new PatriciaTrie<Command>();

public static SortedMap<String, Command> search(String command) {
    return Command.COMMANDS.prefixMap(command);
}

public static Command get(String command) {
    return Command.COMMANDS.get(command);
}

public static Collection<Command> getCommands() {
    return Command.COMMANDS.values();
}

private final String COMMAND;
private final ParameterResolver[] RESOLVERS;
private final Action ACTION;

public Command(String command, Action action, ParameterResolver... resolvers) {
    if (command == null) {
        throw new IllegalArgumentException("Command should not be null");
    } else if (command.isEmpty()) {
        throw new IllegalArgumentException("Command should not be empty");
    }

    this.COMMAND = command;
    this.ACTION = MoreObjects.firstNonNull(action, Action.EMPTY_ACTION);
    this.RESOLVERS = resolvers;

    Command.COMMANDS.put(getCommand(), this); // potentially unsafe (technically object is not constructed yet)
    Gdx.app.log(TAG, "Registered " + toString());
}

public String getCommand() {
    return COMMAND;
}

public void execute(String[] args) {
    for (int i = 1; i <= getNumParamters(); i++) {
        getResolver(i-1).resolve(args[i]);
    }

    ACTION.onActionExecuted();
}

public int getNumParamters() {
    return getResolvers().length;
}

public ParameterResolver getResolver(int i) {
    return getResolvers()[i];
}

private ParameterResolver[] getResolvers() {
    return RESOLVERS;
}

private String getParametersHint() {
    StringBuilder sb = new StringBuilder();
    for (ParameterResolver pr : getResolvers()) {
        sb.append(String.format("<%s> ", pr));
    }

    return sb.toString().trim();
}

public void load() {
    //...
}

@Override
public String toString() {
    if (getNumParamters() == 0) {
        return getCommand();
    }

    return String.format("%s %s", getCommand(), getParametersHint());
}

}
