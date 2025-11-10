package ShareInfo;

public class Auth {
    public static int maTaiKhoan;
    public static String tenDangNhap;
    public static String loaiNguoiDung;
    public static boolean dangNhap = false;
    
    public static boolean isLogin() {
        return dangNhap;
    }
    
    public static boolean isAdmin() {
        return dangNhap && "ADMIN".equalsIgnoreCase(loaiNguoiDung);
    }
    
    public static boolean isNhanVien() {
        return dangNhap && "NHANVIEN".equalsIgnoreCase(loaiNguoiDung);
    }
    
    public static void clear() {
        maTaiKhoan = 0;
        tenDangNhap = null;
        loaiNguoiDung = null;
        dangNhap = false;
    }
}