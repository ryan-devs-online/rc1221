package com.company.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class RentalAgreement {
    Database db = new Database();

    private String toolCode;
    private int rentalDays;
    private int discount;
    private LocalDate checkoutDate;

    private String toolType;
    private String toolBrand;
    private double dailyCharge;
    private boolean weekdayCharge;
    private boolean weekendCharge;
    private boolean holidayCharge;
    private boolean available;

    private LocalDate dueDate;
    private int chargeableDays;
    private double preDiscount;
    private double discountAmount;
    private double finalCharge;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public RentalAgreement() {
    }

    public void setFields(ArrayList<String> splitInput) throws SQLException {
        boolean isValid = checkInput(splitInput);
        if(isValid) {
            this.toolCode = splitInput.get(0);
            this.rentalDays = Integer.parseInt(splitInput.get(1));
            this.discount = Integer.parseInt(splitInput.get(2));

            if(splitInput.size() == 3) {
                this.checkoutDate = LocalDate.now();
            } else if (splitInput.size() == 4) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
                this.checkoutDate = LocalDate.parse(splitInput.get(3), formatter);
            }

            getDbFields();
            calculateFields();
        }
    }

    private void getDbFields(){
        try {
            db.initDB();
            ResultSet result = db.statement.executeQuery("select * from tools where TOOL_CODE='" + toolCode + "'");
            if(result.next()) {
                this.toolType = result.getString("TOOL_TYPE");
                this.toolBrand = result.getString("BRAND");
                this.dailyCharge = result.getDouble("DAILY_CHARGE");
                this.weekdayCharge = result.getBoolean("WEEKDAY_CHARGE");
                this.weekendCharge = result.getBoolean("WEEKEND_CHARGE");
                this.holidayCharge = result.getBoolean("HOLIDAY_CHARGE");
            }
            db.closeDB();
        } catch (SQLException e) {
            System.out.println("Error connecting to the db.");
            e.printStackTrace();
        }
    }

    private void calculateFields() {
        chargeableDays = chargeDays(checkoutDate, rentalDays);
        calculatePrice(chargeableDays);

        available = checkAvailability(checkoutDate, dueDate, toolCode);
    }

    private void calculatePrice(int chargeDays) {
        preDiscount = chargeDays * dailyCharge;
        if(discount == 0 ) {
            discountAmount = 0;
        } else {
            discountAmount = preDiscount * ((double) discount / 100);
        }
        finalCharge = preDiscount - discountAmount;
    }

    private int chargeDays(LocalDate checkoutDate, int rentalDays) {
        dueDate = checkoutDate.plusDays(rentalDays);
        int weekdaysBetween;
        int totalDays = rentalDays;

        LocalDate julyFourth = LocalDate.of(dueDate.getYear(), Month.JULY, 4);
        LocalDate laborDay = Util.localDateOnNthDayOfWeek(DayOfWeek.MONDAY, 1,
                LocalDate.of(dueDate.getYear(), Month.SEPTEMBER, 1));

        if (isWithinRange(laborDay, checkoutDate, dueDate) && !holidayCharge) {
            totalDays -= 1;
        }

        if(isWithinRange(julyFourth, checkoutDate, dueDate) && !holidayCharge) {
            if(julyFourth.getDayOfWeek() == DayOfWeek.SATURDAY && dueDate.isEqual(julyFourth) ) {
                totalDays--;
            } else if(julyFourth.getDayOfWeek() == DayOfWeek.SUNDAY && dueDate.isAfter(julyFourth)) {
                totalDays--;
            } else if(julyFourth.getDayOfWeek() == DayOfWeek.SATURDAY && dueDate.isAfter(julyFourth)) {
                totalDays--;
            }
        }

        if(!weekendCharge) {
            Set<DayOfWeek> weekday = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
            int weekendsBetween = (int) checkoutDate.datesUntil(dueDate.plusDays(1)).filter(
                    d -> !weekday.contains(d.getDayOfWeek())).count();
            totalDays = totalDays - weekendsBetween;
        }

        if(!weekdayCharge) {
            Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            weekdaysBetween = (int) checkoutDate.datesUntil(dueDate.plusDays(1)).filter(
                    d -> !weekend.contains(d.getDayOfWeek())).count();
            totalDays -= weekdaysBetween;
        }

        return totalDays;
    }

    private boolean isWithinRange(LocalDate holiday, LocalDate checkoutDate, LocalDate endDate) {
        return !(holiday.isBefore(checkoutDate) || holiday.isAfter(endDate));
    }

    private boolean checkAvailability(LocalDate startDate, LocalDate endDate, String toolCode) {
        ResultSet resultSet;
        try {
            db.initDB();
            resultSet = db.statement.executeQuery(
                    "select START_DATE, END_DATE from receipts where TOOL_CODE='" + toolCode + "'");
            while(resultSet.next()){
                LocalDate resultStart = Util.convertSQLToLocalDate(resultSet.getDate("START_DATE"));
                LocalDate resultEnd = Util.convertSQLToLocalDate(resultSet.getDate("END_DATE"));

                if(resultStart.isBefore(endDate) && resultEnd.isAfter(startDate)) {
                    db.closeDB();
                    System.out.println("This tool is not available for these days. " +
                            "Would you like to rent a different one?");
                    return false;
                }
            }
            db.closeDB();
        } catch (SQLException e) {
            System.out.println("Unable to validate availability");
            e.printStackTrace();
        }
        return true;
    }

    public boolean checkInput(ArrayList<String> input) throws SQLException {
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
        db.closeDB();
        if(!tCode.contains(input.get(0))) {
            isValid = false;
            System.out.println("Please enter a valid tool code. Type tools for a list of valid codes.");
        }

        if(!Util.isInt(input.get(1)) || Integer.parseInt(input.get(1)) < 1) {
            isValid = false;
            System.out.println("Please enter the rental day count the form of an integer greater than one.");
        }

        if(!Util.isInt(input.get(2)) ||
                (Integer.parseInt(input.get(2) ) < 0 || Integer.parseInt(input.get(2)) > 100)){
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

    public boolean getAvail() {
        return available;
    }

    public String getToolCode() {
        return toolCode;
    }

    public String getCheckoutDateString() {
        return checkoutDate.toString();
    }

    public String getDueDateString() {
        return dueDate.toString();
    }

    public String getFinalCharge() {
        return  df.format(finalCharge);
    }

    public int getChargeableDays() {
        return chargeableDays;
    }

    public String getPreDiscount() {
        return df.format(preDiscount);
    }

    @Override
    public String toString(){
        return "Your tool rental receipt \nTool code: " + toolCode + "\n" + "Tool type: " + toolType + "\n" +
                "Tool brand: " + toolBrand + "\n" + "Rental Days: " + rentalDays + "\n" +
                "Checkout date: " + checkoutDate.format(DateTimeFormatter.ofPattern("MM/dd/yy")) + "\n" +
                "Due date: " + dueDate.format(DateTimeFormatter.ofPattern("MM/dd/yy")) + "\n" +
                "Daily rental Charge: $" + df.format(dailyCharge) + "\n" + "Charge Days: " + chargeableDays + "\n" +
                "Pre-discount charge: $" + df.format(preDiscount) + "\n" + "Discount percent: " + discount + "%" + "\n" +
                "Discount amount: $" + df.format(discountAmount) + "\n" + "Final charge: $" + df.format(finalCharge);
    }
}
