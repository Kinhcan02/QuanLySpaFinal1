package Service;

import Model.TaiKhoan;
import Repository.TaiKhoanRepository;
import ShareInfo.Auth;
import java.util.List;

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

    // CRUD Methods
    public List<TaiKhoan> layTatCaTaiKhoan() {
        return taiKhoanRepository.findAll();
    }
    
    public TaiKhoan layTaiKhoanTheoMa(int maTaiKhoan) {
        return taiKhoanRepository.findById(maTaiKhoan);
    }
    
    public TaiKhoan layTaiKhoanTheoTenDangNhap(String tenDangNhap) {
        return taiKhoanRepository.findByTenDangNhap(tenDangNhap);
    }
    
    public boolean themTaiKhoan(TaiKhoan taiKhoan) {
        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (taiKhoanRepository.isTenDangNhapExists(taiKhoan.getTenDangNhap())) {
            return false;
        }
        return taiKhoanRepository.insert(taiKhoan);
    }
    
    public boolean capNhatTaiKhoan(TaiKhoan taiKhoan) {
        // Kiểm tra tên đăng nhập đã tồn tại (trừ tài khoản hiện tại)
        if (taiKhoanRepository.isTenDangNhapExists(taiKhoan.getTenDangNhap(), taiKhoan.getMaTaiKhoan())) {
            return false;
        }
        return taiKhoanRepository.update(taiKhoan);
    }
    
    public boolean doiMatKhau(int maTaiKhoan, String matKhauMoi) {
        return taiKhoanRepository.changePassword(maTaiKhoan, matKhauMoi);
    }
    
    public boolean xoaTaiKhoan(int maTaiKhoan) {
        // Không cho xóa tài khoản đang đăng nhập
        if (Auth.isLogin() && Auth.maTaiKhoan == maTaiKhoan) {
            return false;
        }
        return taiKhoanRepository.delete(maTaiKhoan);
    }
    
    // Kiểm tra quyền admin
    public boolean laAdmin() {
        return coQuyen("Admin");
    }
}