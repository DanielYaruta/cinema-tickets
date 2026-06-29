package com.danielyaruta.cinema.cli;

// OCP: для добавления новой команды достаточно создать класс, реализующий этот
// интерфейс, и зарегистрировать его в CommandRegistry.
// Существующий код CommandRegistry и Cli при этом не изменяется.
//
// ISP: интерфейс минимален — только то, что нужно каждой команде.
public interface Command {
    String getKey();
    String getDescription();
    void execute(ConsoleInput input);
}
