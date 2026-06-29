package com.danielyaruta.cinema.cli.commands;

import com.danielyaruta.cinema.cli.Command;
import com.danielyaruta.cinema.cli.ConsoleInput;
import com.danielyaruta.cinema.service.TicketService;

public class PayTicketCommand implements Command {

    private final TicketService ticketService;

    public PayTicketCommand(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override public String getKey()         { return "8"; }
    @Override public String getDescription() { return "Pay ticket (BOOKED -> PAID)"; }

    @Override
    public void execute(ConsoleInput input) {
        long ticketId = input.readLong("Ticket ID to pay: ");
        ticketService.payTicket(ticketId);
        System.out.println("Ticket " + ticketId + " marked as PAID.");
    }
}
