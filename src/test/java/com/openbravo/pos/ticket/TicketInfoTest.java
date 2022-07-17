package com.openbravo.pos.ticket;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TicketInfoTest {

    @Test
    public void shouldGroupFourOfTheSameProduct() {
        TicketInfo ticket = addTwoOfTheSameProduct(null);
        Assert.assertEquals(2, ticket.getLinesCount());
        runScript(ticket);
        Assert.assertEquals(3, ticket.getLinesCount());
        TicketInfo updatedTicket = addTwoOfTheSameProduct(ticket);
        Assert.assertEquals(5, updatedTicket.getLinesCount());
        runScript(updatedTicket);
        Assert.assertEquals(6, updatedTicket.getLinesCount());
    }

    @Test
    public void shouldGroupTwoOfTheSameProduct() {
        TicketInfo ticket = addTwoOfTheSameProduct(null);
        Assert.assertEquals(2, ticket.getLinesCount());
        runScript(ticket);
        Assert.assertEquals(3, ticket.getLinesCount());
    }

    @Test
    public void shouldGroupDiscountTwoDifferentProducts() {
        TicketInfo ticket = twoDifferentProducts();
        Assert.assertEquals(2, ticket.getLinesCount());
        runScript(ticket);
        Assert.assertEquals(3, ticket.getLinesCount());
    }

    private void runScript(TicketInfo ticket) {
        TicketLineInfo line = ticket.getLine(1);
        int count = Integer.parseInt(line.getProperty("COUNT", "0"));
        Double newPrice1 = new Double(line.getProperty("PRICE", "0"));
        String groupName = line.getProperty("GROUP", "0");
        int productCount = 0;
        int discountsAlreadyApplied = 0;

        for (TicketLineInfo ticketLine : ticket.getLines()) {
            boolean grouped = groupName.equals(ticketLine.getProperty("GROUP", "0"));
            boolean multi = count > 0;
            if (multi && grouped) {
                productCount++;
            }
            if (ticketLine.getProductName().contains("Buy")) {
               discountsAlreadyApplied = discountsAlreadyApplied + count;
            }
        }


        if (count == (productCount - discountsAlreadyApplied)) {

            double newPrice2 = -(line.getPriceTax() * productCount) + newPrice1;

            TicketLineInfo updatedTicketLine = new TicketLineInfo("Buy " + productCount + " for £" + (line.getProperty("PRICE")),
                    line.getProductTaxCategoryID(),
                    "",
                    1,
                    0,
                    line.getTaxInfo());
            ticket.insertLine(ticket.getLinesCount(),updatedTicketLine);
        }
    }

    private TicketInfo twoDifferentProducts() {
        TicketInfo ticket = new TicketInfo();

        ArrayList<TicketLineInfo> ticketLines = new ArrayList<>();
        TaxInfo stdTax = new TaxInfo("1", "STD", "001", null, null, 10, false, 1);

        TicketLineInfo ticketLineInfo1 = new TicketLineInfo("1234", "test1", "001", "", 1, 10, stdTax);
        ticketLineInfo1.getProductID();
        ticketLineInfo1.setProperty("GROUP", "BOGO");
        ticketLineInfo1.setProperty("COUNT", "2");
        ticketLineInfo1.setProperty("PRICE", "5");

        TicketLineInfo ticketLineInfo2 = new TicketLineInfo("1235", "test2", "001", "", 1, 10, stdTax);
        ticketLineInfo2.setProperty("GROUP", "BOGO");
        ticketLineInfo2.setProperty("COUNT", "2");
        ticketLineInfo2.setProperty("PRICE", "5");

        ticketLines.add(ticketLineInfo1);
        ticketLines.add(ticketLineInfo2);
        ticket.setLines(ticketLines);

        return ticket;
    }

    private TicketInfo addTwoOfTheSameProduct(TicketInfo ticket) {

        if (ticket == null) {
            ticket = new TicketInfo();
        }
        List<TicketLineInfo> ticketLines = ticket.getLines();
        if (ticketLines == null) {
            ticketLines = new ArrayList<>();
        }

        TaxInfo stdTax = new TaxInfo("1", "STD", "001", null, null, 10, false, 1);

        TicketLineInfo ticketLineInfo1 = new TicketLineInfo("1234", "test1", "001", "", 1, 10, stdTax);
        ticketLineInfo1.getProductID();
        ticketLineInfo1.setProperty("GROUP", "BOGO");
        ticketLineInfo1.setProperty("COUNT", "2");
        ticketLineInfo1.setProperty("PRICE", "5");

        ticketLines.add(ticketLineInfo1);
        ticketLines.add(ticketLineInfo1);
        ticket.setLines(ticketLines);

        return ticket;
    }

}