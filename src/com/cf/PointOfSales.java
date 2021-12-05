package com.cf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class PointOfSales {
    Scanner scanner = new Scanner(System.in);
    Database db = new Database();

    public PointOfSales() throws SQLException {
        startupMessage();
    }

    public void start() throws SQLException {
        String input = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

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
            ArrayList<String> splitInput = new ArrayList<>(Arrays.asList(input.split(" ")));
            if(checkInput(splitInput)) {
                RentalAgreement ra = new RentalAgreement(splitInput);
                // put ra into db.
                // check if they want to see receipt

                System.out.println("Do you want to see your receipt, y or n?");
                input = scanner.nextLine().toUpperCase(Locale.ROOT);
                if(input.equals("Y")){
                    System.out.println(ra);
                }

                System.out.println("Thanks!");
                start();
            } else {
                start();
            }
        }
    }

    private void startupMessage() {
        System.out.println("Welcome to the CF Tool Rental Store");
        System.out.println("Please enter the tool code, rental day count, discount percent, and checkout date in the following format");
        System.out.println("CODE X XX mm/dd/yy");
        System.out.println("For a list of tools and information type \"tools\"");
        System.out.println("Don't enter a date if you wish to start the rental today");
        System.out.println("Type \"exit\" to quit");
    }

    private boolean checkInput(ArrayList<String> input) throws SQLException {
        boolean isValid = true;

        if(input.size() != 3 && input.size() != 4) {
            System.out.println("Please use the format: CODE # ## mm/dd/yy");
            return false;
        }
        db.initDB();
        ResultSet toolCode = db.statement.executeQuery("select TOOL_CODE from tools");
        ArrayList<String> tCode= new ArrayList<>();
        while(toolCode.next()){
            tCode.add(toolCode.getString("TOOL_CODE"));
        }
        db.conn.close();
        if(!tCode.contains(input.get(0))) {
            isValid = false;
            System.out.println("Please enter a valid tool code. Type tools for a list of valid codes.");
        }

        if(!Util.isInt(input.get(1)) || Integer.valueOf(input.get(1)) < 1) {
            isValid = false;
            System.out.println("Please enter the rental day count the form of an integer greater than one.");
        }

        if(!Util.isInt(input.get(2)) ||
                (Integer.valueOf(input.get(1) )< 0 || Integer.valueOf(input.get(1))> 100)){
            isValid = false;
            System.out.println("Please enter the discount percent in the form of an integer between 0 and 100.");
        }

        if(input.size() == 4) {
            if(!Util.isValidDate(input.get(3))) {
                isValid = false;
                System.out.println("Please enter the date in the form of mm/dd/yy.");
            }
        }
        return isValid;
    }

}
