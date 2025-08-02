package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://150.95.110.129:3306/hd9906295d_wuangvinh3020?useSSL=false&serverTimezone=UTC";
    private static final String USER = "hd9906295d_wuangvinh3020";
    private static final String PASSWORD = "0123Cam0123@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
} 