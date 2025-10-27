package Controller;

import Service.AuthService;
import View.LoginView;
import ShareInfo.Auth;

public class AuthController {
    private AuthService authService;
    private LoginView loginView;
    
    public AuthController() {
        this.authService = new AuthService();
        this.loginView = new LoginView(this);
    }
    
    public void xuLyDangNhap(String tenDangNhap, String matKhau) {
        boolean thanhCong = authService.dangNhap(tenDangNhap, matKhau);
        
        if (thanhCong) {
            loginView.hienThiThongBaoDangNhapThanhCong();
            chuyenDenManHinhChinh();
        } else {
            loginView.hienThiThongBaoDangNhapThatBai();
        }
    }
    
    public void xuLyDangXuat() {
        authService.dangXuat();
        loginView.hienThiManHinhDangNhap();
    }
    
    public void xuLyThoat() {
        if (loginView.xacNhanThoat()) {
            System.exit(0);
        }
    }
    
    private void chuyenDenManHinhChinh() {
        // Đóng màn hình đăng nhập
        loginView.dongManHinh();
        
        // Mở màn hình chính dựa trên vai trò người dùng
        if (Auth.isAdmin()) {
            // MainController mainController = new MainController();
            // mainController.hienThiManHinhChinh();
            System.out.println("Chuyển đến màn hình Admin");
        } else if (Auth.isThuNgan()) {
            System.out.println("Chuyển đến màn hình Thu Ngân");
        } else if (Auth.isNhanVien()) {
            System.out.println("Chuyển đến màn hình Nhân Viên");
        }
    }
    
    public void khoiDong() {
        loginView.hienThiManHinhDangNhap();
    }
}