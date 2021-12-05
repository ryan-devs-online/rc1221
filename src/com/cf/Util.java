package com.cf;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static boolean isInt(String string) {
        try {
            int i = Integer.parseInt(string);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean isDouble(String string) {
        try {
            double d = Double.parseDouble(string);
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean isValidDate(String string) {
        String regex = "^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static LocalDate localDateOnNthDayOfWeek(DayOfWeek dayOfWeek, int ordinal,  LocalDate startDate) {
        return startDate.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek));
    }

    public static LocalDate convertSQLToLocalDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

}
