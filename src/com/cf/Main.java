package com.cf;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        PointOfSales pos = new PointOfSales();
        pos.start();
    }
}
