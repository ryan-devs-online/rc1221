package com.company.test;

import com.company.main.RentalAgreement;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

public class PointOfSalesTest {

    public PointOfSalesTest(){
    }

    /*
    This tests if we have illegal input (discount > 100)
     */
    @Test
    public void testDiscountOver100() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("JAKR");
        input.add("5");
        input.add("101");
        input.add("09/03/15");

        RentalAgreement ra = new RentalAgreement();
        Assert.assertFalse(ra.checkInput(input));
    }

    /*
    This checks July 4th + a discount. LADW 1.99 daily charge, Weekend Charge, Holiday Charge
    input: LADW 3 10 7/2/20
    expected charge days 7/4/20, 7/5/20  (no charge on day of, july 4th is celebrated on the 3rd for charging purposes, weekend charge applied on the 4th and 5th.
    Expected outputs: Price Before discount = 5.97
                      Price After discount = 5.37
                      DueDate = 7/6/20
     */
    @Test
    public void testDiscountJulyFourthSaturday() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("LADW");
        input.add("3");
        input.add("10");
        input.add("07/02/20");

        RentalAgreement ra = new RentalAgreement();
        ra.setFields(input);
        Assert.assertEquals(2, ra.getChargeableDays());
        Assert.assertEquals("2020-07-05", ra.getDueDateString());
        Assert.assertEquals("3.98", ra.getPreDiscount());
        Assert.assertEquals("3.58", ra.getFinalCharge());
    }

    /*
    This tests July 4th + discount CHNS 1.49 daily charge no weekend holiday
    Input: CHNS 5 25 07/02/15
    Expected charge days 7/3, 7/6, 7/7 (charge on july 4th (3rd), no weekend charge, regular charge 6th and 7th)
    Expected outputs: Price before discount = 4.47
                      Price After discount = 3.35
                      DueDate 7/7/15
     */
    @Test
    public void testHolidayChargeNoWeekend() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("CHNS");
        input.add("5");
        input.add("25");
        input.add("07/02/15");

        RentalAgreement ra = new RentalAgreement();
        ra.setFields(input);
        Assert.assertEquals(3, ra.getChargeableDays());
        Assert.assertEquals("2015-07-07", ra.getDueDateString());
        Assert.assertEquals("4.47", ra.getPreDiscount());
        Assert.assertEquals("3.35", ra.getFinalCharge());
    }

    /*
    Tests Laborday JAKD 2.99 daily charge  no holiday no weekend
    Input: JAKD 6 0 09/03/15
    Expected charge days: 9/4, 9/8, 9/9 (no charge day of, no weekend, no holiday, regular charge 8th 9th)
    Expected outputs: Price Before discount = 8.97
                      Due Date 9/9
     */
    @Test
    public void testLaborDayNoHolidayNoWeekend() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("JAKD");
        input.add("6");
        input.add("0");
        input.add("09/03/15");

        RentalAgreement ra = new RentalAgreement();
        ra.setFields(input);
        Assert.assertEquals("2015-09-09", ra.getDueDateString());
        Assert.assertEquals("8.97", ra.getFinalCharge());

    }

    /*
    Tests july 4th 2.99 no weekend no holiday
    Inputs: JAKR 9 0 07/02/15
    Expected charge days 7/6-7/10 due 7/11
    Expected outputs: Price Before discount = 14.95
                      Due Date 7/11
     */
    @Test
    public void test() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("JAKR");
        input.add("9");
        input.add("0");
        input.add("07/02/15");

        RentalAgreement ra = new RentalAgreement();
        ra.setFields(input);
        Assert.assertEquals(5, ra.getChargeableDays());
        Assert.assertEquals("2015-07-11", ra.getDueDateString());
        Assert.assertEquals("14.95", ra.getFinalCharge());
    }

    /*
    Tests july 4th 2.99  50% discount no weekend no holiday
    Inputs JAKR 4 50 07/02/20
    Expected Charge days: 7/6
    Expected output: price before discount = 2.99
                     price after discount = 1.5
     */
    @Test
    public void testJulyFourthJackhammerDiscountRounding() throws SQLException {
        ArrayList<String> input = new ArrayList<>();
        input.add("JAKR");
        input.add("4");
        input.add("50");
        input.add("07/02/20");

        RentalAgreement ra = new RentalAgreement();
        ra.setFields(input);
        Assert.assertEquals("2020-07-06", ra.getDueDateString());
        Assert.assertEquals("2.99", ra.getPreDiscount());
        Assert.assertEquals("1.50", ra.getFinalCharge());
    }
}
