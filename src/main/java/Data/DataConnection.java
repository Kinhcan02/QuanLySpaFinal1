package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    // ĐƯỜNG DẪN MỚI - trong thư mục resources/database
    private static final String DB_PATH = "src/main/resources/database/DB_SPA.accdb";
    private static final String DB_URL = "jdbc:ucanaccess://" + DB_PATH;

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            
            // Kiểm tra file có tồn tại không
            java.io.File dbFile = new java.io.File(DB_PATH);
            if (!dbFile.exists()) {
                System.err.println("❌ File database không tồn tại: " + DB_PATH);
                System.err.println("✅ Hãy đặt file DB_SPA.accdb vào: " + dbFile.getAbsolutePath());
                return null;
            }
            
            conn = DriverManager.getConnection(DB_URL, "", "");
            System.out.println("✅ Kết nối Access thành công!");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi kết nối Access: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Đã đóng kết nối Access.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing Access connection...");
        System.out.println("Database path: " + DB_PATH);
        
        Connection testConn = getConnection();
        if (testConn != null) {
            System.out.println("✅ Kết nối Access thành công!");
            closeConnection(testConn);
        } else {
            System.out.println("❌ Kết nối Access thất bại!");
        }
    }
}