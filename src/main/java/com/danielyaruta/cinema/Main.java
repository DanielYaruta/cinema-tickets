package com.danielyaruta.cinema;

import com.danielyaruta.cinema.cli.Cli;
import com.danielyaruta.cinema.cli.CommandRegistry;
import com.danielyaruta.cinema.cli.commands.*;
import com.danielyaruta.cinema.dao.*;
import com.danielyaruta.cinema.db.SchemaInitializer;
import com.danielyaruta.cinema.service.*;

import java.util.Scanner;

// Main — единственное место, где создаются конкретные классы (DIP composition root).
// Все слои (сервисы, DAO) знают только об интерфейсах; конкретные реализации
// передаются сюда через конструкторы — ручной dependency injection без Spring.
public class Main {

    public static void main(String[] args) {

        // 1. Инициализация схемы БД при старте (CREATE TABLE IF NOT EXISTS)
        new SchemaInitializer().initialize();

        // 2. DAO — конкретные JDBC-реализации
        //    DIP: снизу вверх переменные объявлены как интерфейсы
        MovieDao   movieDao   = new MovieDaoJdbc();
        HallDao    hallDao    = new HallDaoJdbc();
        SessionDao sessionDao = new SessionDaoJdbc();
        TicketDao  ticketDao  = new TicketDaoJdbc();

        // 3. Service — получают DAO-интерфейсы, не конкретные классы (DIP)
        MovieService   movieService   = new MovieService(movieDao);
        HallService    hallService    = new HallService(hallDao);
        SessionService sessionService = new SessionService(sessionDao, movieDao, hallDao);
        TicketService  ticketService  = new TicketService(ticketDao, sessionDao, hallDao);

        // 4. Регистрация команд (OCP: добавляем новые команды без изменения Cli/CommandRegistry)
        CommandRegistry registry = new CommandRegistry();
        registry.register(new AddMovieCommand(movieService));
        registry.register(new AddHallCommand(hallService));
        registry.register(new AddSessionCommand(sessionService, movieService, hallService));
        registry.register(new ListSessionsCommand(sessionService, movieService, hallService));
        registry.register(new BookTicketCommand(ticketService));
        registry.register(new CancelTicketCommand(ticketService));
        registry.register(new ListTicketsBySessionCommand(ticketService));
        registry.register(new PayTicketCommand(ticketService));
        registry.register(new ListMoviesCommand(movieService));
        registry.register(new ListHallsCommand(hallService));

        // 5. Запуск CLI-цикла
        new Cli(registry, new Scanner(System.in)).run();
    }
}
