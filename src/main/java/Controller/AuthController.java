package Controller;

import View.LoginView;
import View.MainView;
import Service.AuthService;
import ShareInfo.Auth;

public class AuthController {

    private AuthService authService;
    private LoginView loginView;
    private MainView mainView;

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

        // Clear thông tin đăng nhập
        Auth.clear();

        if (mainView != null) {
            mainView.setVisible(false);
            mainView = null;
        } else {
        }

        // Hiển thị lại màn hình đăng nhập
        if (loginView != null) {
            loginView.hienThiManHinhDangNhap();
        } else {
        }
    }

    public void xuLyThoat() {
        if (loginView.xacNhanThoat()) {
            System.exit(0);
        }
    }

    private void chuyenDenManHinhChinh() {
        // Đóng màn hình đăng nhập
        loginView.dongManHinh();

        // Tạo và hiển thị MainView với thông tin người dùng
        this.mainView = new MainView(this); // TRUYỀN CONTROLLER VÀO ĐÂY
        mainView.setVisible(true);
        // Thiết lập quyền truy cập dựa trên vai trò
        phanQuyenChoMainView();
    }

    private void phanQuyenChoMainView() {
        String vaiTro = Auth.loaiNguoiDung;
        String tenNguoiDung = Auth.tenDangNhap;

        // Cập nhật thông tin người dùng trên MainView
        mainView.capNhatThongTinNguoiDung(tenNguoiDung, vaiTro);

        // Phân quyền theo vai trò
        if ("NHANVIEN".equalsIgnoreCase(vaiTro)) {
            phanQuyenNhanVien();

        } else if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            phanQuyenAdmin();
        }
    }

    private void phanQuyenNhanVien() {
        // Nhân viên chỉ được đặt dịch vụ và xem thông tin cá nhân
        mainView.getBtnDatDichVu().setEnabled(true);
        mainView.getBtnQuanLyNhanVien().setEnabled(false);
        mainView.getBtnQuanLyKhachHang().setEnabled(true);
        mainView.getBtnQuanLyDichVu().setEnabled(false);
        mainView.getBtnThongKe().setEnabled(false);
        mainView.getBtnQuanLyTaiKhoan().setEnabled(false);

        // Thay đổi tooltip để hiển thị không có quyền
        mainView.getBtnQuanLyNhanVien().setToolTipText("Không có quyền truy cập");
        mainView.getBtnQuanLyKhachHang().setToolTipText("Không có quyền truy cập");
        mainView.getBtnQuanLyDichVu().setToolTipText("Không có quyền truy cập");
        mainView.getBtnThongKe().setToolTipText("Không có quyền truy cập");
        mainView.getBtnQuanLyTaiKhoan().setToolTipText("Không có quyền truy cập");
    }

    private void phanQuyenAdmin() {
        // Admin có toàn quyền
        mainView.getBtnDatDichVu().setEnabled(true);
        mainView.getBtnQuanLyNhanVien().setEnabled(true);
        mainView.getBtnQuanLyKhachHang().setEnabled(true);
        mainView.getBtnQuanLyDichVu().setEnabled(true);
        mainView.getBtnThongKe().setEnabled(true);
        mainView.getBtnQuanLyTaiKhoan().setEnabled(true);
        mainView.getBtnDangXuat().setEnabled(true);
        mainView.getBtnThoat().setEnabled(true);
    }

    public void khoiDong() {
        loginView.hienThiManHinhDangNhap();
    }
}
