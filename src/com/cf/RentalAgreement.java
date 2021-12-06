package com.cf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class RentalAgreement {
    Database db = new Database();

    private final String toolCode;
    private final int rentalDays;
    private final int discount;
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
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public RentalAgreement(ArrayList<String> splitInput) throws SQLException {
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
                this.available = result.getBoolean("AVAILABLE");
            }
            db.closeDB();
        } catch (SQLException e) {
            System.out.println("Error connecting to the db.");
            e.printStackTrace();
        }
    }

    private void calculateFields() throws SQLException {
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
        int period = Period.between(checkoutDate, dueDate).getDays();
        int weekdaysBetween = 0;
        int totalDays = rentalDays;

        LocalDate julyFourth = LocalDate.of(dueDate.getYear(), Month.JULY, 4);
        LocalDate laborDay = Util.localDateOnNthDayOfWeek(DayOfWeek.MONDAY, 1,
                LocalDate.of(dueDate.getYear(), Month.SEPTEMBER, 1));

        if (isWithinRange(laborDay, checkoutDate, dueDate) && !holidayCharge) {
            totalDays -= 1;
        }

        if(isWithinRange(julyFourth, checkoutDate, dueDate) && !holidayCharge) {
            if(julyFourth.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dueDate = dueDate.plusDays(1);
            } if(julyFourth.getDayOfWeek() == DayOfWeek.SATURDAY) {
                dueDate = dueDate.minusDays(1);
            }
            totalDays -= 1;
        }

        if(!weekendCharge) {
            Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            weekdaysBetween = (int) checkoutDate.datesUntil(dueDate).filter(d -> !weekend.contains(d.getDayOfWeek())).count();
            totalDays -= (period - weekdaysBetween);
        }

        if(!weekdayCharge) {
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
