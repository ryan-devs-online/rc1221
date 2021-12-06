package com.company.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class PointOfSales {
    Scanner scanner = new Scanner(System.in);
    Database db = new Database();
    String input;

    public PointOfSales() throws SQLException {
        startupMessage();
        start();
    }

    public void start() throws SQLException {
        input = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

        if(input.equals("TOOLS") || input.equals("TOOL") || input.equals("TOOOLS") || input.equals("TOLS") ||
            input.equals("TOOSL")) {
            db.initDB();
            ResultSet result = db.statement.executeQuery("select * from tools");
            System.out.println("T-Code  T-Type T-Brand Price");
            while(result.next()){
                System.out.println(result.getString(1) + " " + result.getString(2) + " " +
                        result.getString(3)  + " " + result.getString(4));
            }
            db.closeDB();
            start();

        } else if (input.equals("EXIT") || input.equals("EIXT") || input.equals("EXTI")) {
            System.out.println("goodbye!");
            System.exit(0);
        } else {
            RentalAgreement ra = new RentalAgreement();
            ArrayList<String> splitInput = new ArrayList<>(Arrays.asList(input.split(" ")));
            ra.setFields(splitInput);
            if(ra.getAvail()) {
                db.initDB();
                db.statement.executeUpdate("insert into receipts (TOOL_CODE, START_DATE, END_DATE, FULL_RECIEPT) " +
                        "values ('" + ra.getToolCode() +"', '" + ra.getCheckoutDateString() + "', '" +
                        ra.getDueDateString() +"', '" + ra + "')");
                db.closeDB();
                System.out.println("Do you want to see your receipt, y or n?");
                input = scanner.nextLine().toUpperCase(Locale.ROOT).trim();
                if(input.equals("Y")){
                    System.out.println(ra);
                }
            } else {
                start();
            }
            start();
        }
    }

    private void startupMessage() {
        System.out.println("Welcome to the Tool Rental Store");
        System.out.println("Please enter the tool code, rental day count, discount percent, and checkout date in the following format");
        System.out.println("CODE X XX mm/dd/yy");
        System.out.println("For a list of tools and information type \"tools\"");
        System.out.println("Don't enter a date if you wish to start the rental today");
        System.out.println("Type \"exit\" to quit");
    }

}
