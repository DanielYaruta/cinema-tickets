package com.danielyaruta.cinema.cli;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

// SRP: единственная ответственность — хранить и предоставлять команды по ключу.
//
// OCP: расширяется через метод register() без изменения этого класса.
// Новые команды добавляются снаружи (в Main.java), не затрагивая существующий код.
public class CommandRegistry {

    // LinkedHashMap сохраняет порядок вставки — меню выводится в нужном порядке
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        commands.put(command.getKey(), command);
    }

    public Optional<Command> get(String key) {
        return Optional.ofNullable(commands.get(key));
    }

    public Collection<Command> getAll() {
        return Collections.unmodifiableCollection(commands.values());
    }
}
