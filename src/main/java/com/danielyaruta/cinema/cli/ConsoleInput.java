package com.danielyaruta.cinema.cli;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// SRP: единственная ответственность — читать типизированный ввод из консоли
// и превращать ошибки парсинга в понятные IllegalArgumentException.
//
// Избавляет каждую команду от дублирования try/catch вокруг parseInt, parseLong и т.д.
public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim();
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected an integer, got: '" + raw + "'");
        }
    }

    public long readLong(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim();
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a number, got: '" + raw + "'");
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim();
        try {
            BigDecimal value = new BigDecimal(raw);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Value cannot be negative: '" + raw + "'");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a decimal number, got: '" + raw + "'");
        }
    }

    public LocalDateTime readLocalDateTime(String prompt, DateTimeFormatter formatter) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim();
        try {
            return LocalDateTime.parse(raw, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Expected date/time in format 'yyyy-MM-dd HH:mm', got: '" + raw + "'");
        }
    }
}
