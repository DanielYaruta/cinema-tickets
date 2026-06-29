package com.danielyaruta.cinema.model;

public class Ticket {

    private long id;
    private long sessionId;
    private int seatNumber;
    private TicketStatus status;
    private String customerName;

    public Ticket() {}

    public Ticket(long id, long sessionId, int seatNumber, TicketStatus status, String customerName) {
        this.id = id;
        this.sessionId = sessionId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.customerName = customerName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    @Override
    public String toString() {
        return String.format("Ticket{id=%d, sessionId=%d, seat=%d, status=%s, customer='%s'}",
                id, sessionId, seatNumber, status, customerName);
    }
}
