package com.company.main;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class Util {

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean isValidDate(String string) {
        DateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        sdf.setLenient(false);
        try {
            sdf.parse(string);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static LocalDate localDateOnNthDayOfWeek(DayOfWeek dayOfWeek, int ordinal,  LocalDate startDate) {
        return startDate.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek));
    }

    public static LocalDate convertSQLToLocalDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

}
