package ShareInfo;

public class Auth {
    private Auth() {
    }
    
    public static int maTaiKhoan = -1;
    public static String tenDangNhap = null;
    public static String matKhau = null;
    public static String loaiNguoiDung = null;
    public static boolean dangNhap = false;
    
    public static void clear() {
        maTaiKhoan = -1;
        tenDangNhap = null;
        matKhau = null;
        loaiNguoiDung = null;
        dangNhap = false;
    }
    
    public static boolean isLogin() {
        return dangNhap;
    }
    
    public static boolean isAdmin() {
        return "Admin".equals(loaiNguoiDung);
    }
    
    public static boolean isThuNgan() {
        return "ThuNgan".equals(loaiNguoiDung);
    }
    
    public static boolean isNhanVien() {
        return "NhanVien".equals(loaiNguoiDung);
    }
}