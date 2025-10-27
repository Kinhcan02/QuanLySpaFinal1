package Service;

import Model.TaiKhoan;
import Repository.TaiKhoanRepository;
import ShareInfo.Auth;

public class AuthService {
    private TaiKhoanRepository taiKhoanRepository;
    
    public AuthService() {
        this.taiKhoanRepository = new TaiKhoanRepository();
    }
    
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        boolean thanhCong = taiKhoanRepository.kiemTraDangNhap(tenDangNhap, matKhau);
        
        if (thanhCong) {
            TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
            Auth.maTaiKhoan = taiKhoan.getMaTaiKhoan();
            Auth.tenDangNhap = taiKhoan.getTenDangNhap();
            Auth.loaiNguoiDung = taiKhoan.getVaiTro();
            Auth.dangNhap = true;
        }
        
        return thanhCong;
    }
    
    public void dangXuat() {
        Auth.clear();
    }
    
    // Thêm phương thức kiểm tra quyền
    public boolean coQuyen(String vaiTro) {
        return Auth.isLogin() && vaiTro.equals(Auth.loaiNguoiDung);
    }
}