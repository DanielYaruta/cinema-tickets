package com.danielyaruta.cinema.cli;

import java.util.Scanner;

// SRP: единственная ответственность — управление главным циклом ввода/вывода.
// Не знает о SQL, не содержит бизнес-логики, не зависит от конкретных команд.
public class Cli {

    private final CommandRegistry registry;
    private final ConsoleInput consoleInput;

    public Cli(CommandRegistry registry, Scanner scanner) {
        this.registry = registry;
        this.consoleInput = new ConsoleInput(scanner);
    }

    public void run() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   Cinema Ticket System       ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            printMenu();
            String choice = consoleInput.readLine("Enter command: ");

            if (choice.equals("0") || choice.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            registry.get(choice).ifPresentOrElse(
                    cmd -> {
                        System.out.println();
                        try {
                            cmd.execute(consoleInput);
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            System.out.println("[Error] " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("[Unexpected error] " + e.getMessage());
                        }
                    },
                    () -> System.out.println("Unknown command '" + choice + "'. Try again.")
            );
        }
    }

    private void printMenu() {
        System.out.println("\n--- Menu ---");
        for (Command cmd : registry.getAll()) {
            System.out.printf("  %-3s %s%n", cmd.getKey() + ".", cmd.getDescription());
        }
        System.out.println("  0.  Exit");
    }
}
