package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.service.TicketService;

public class BookTicketCommand implements Command {

    private final TicketService ticketService;

    public BookTicketCommand(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override public String getKey()         { return "5"; }
    @Override public String getDescription() { return "Book ticket"; }

    @Override
    public void execute(ConsoleInput input) {
        long   sessionId    = input.readLong("Session ID: ");
        int    seatNumber   = input.readInt("Seat number: ");
        String customerName = input.readLine("Customer name: ");

        Ticket ticket = ticketService.bookTicket(sessionId, seatNumber, customerName);
        System.out.println("Ticket booked successfully!");
        System.out.println(ticket);
    }
}
