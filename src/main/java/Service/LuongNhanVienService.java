package Service;

import Model.LuongNhanVien;
import Model.NhanVien;
import Repository.LuongNhanVienRepository;
import Repository.NhanVienRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class LuongNhanVienService {
    private LuongNhanVienRepository luongRepo;
    private NhanVienRepository nhanVienRepo;

    public LuongNhanVienService() {
        this.luongRepo = new LuongNhanVienRepository();
        this.nhanVienRepo = new NhanVienRepository();
    }

    public List<LuongNhanVien> getAllLuong() {
        return luongRepo.getAll();
    }

    public List<LuongNhanVien> getLuongByThangNam(Integer thang, Integer nam) {
        return luongRepo.getByThangNam(thang, nam);
    }

    public List<LuongNhanVien> getLuongByNhanVienThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        return luongRepo.getByMaNhanVienThangNam(maNhanVien, thang, nam);
    }

    public boolean tinhLuongThang(Integer thang, Integer nam) {
        try {
            List<NhanVien> danhSachNhanVien = nhanVienRepo.getAll();
            boolean hasCalculated = false;
            
            for (NhanVien nv : danhSachNhanVien) {
                // Kiểm tra xem đã tính lương cho nhân viên này trong tháng/năm chưa
                if (!luongRepo.exists(nv.getMaNhanVien(), thang, nam)) {
                    // Tính tổng thành tiền từ chi tiết hóa đơn
                    BigDecimal tongThanhTien = luongRepo.tinhTongThanhTienTheoThangNam(
                        nv.getMaNhanVien(), thang, nam);
                    
                    // Tính lương = (hệ số lương / 100) * thành tiền
                    BigDecimal heSoLuong = nv.getHeSoLuong() != null ? nv.getHeSoLuong() : BigDecimal.ONE;
                    BigDecimal tongLuong = tongThanhTien.multiply(heSoLuong)
                                                       .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    
                    // Chỉ tạo bản ghi lương nếu có doanh thu
                    if (tongLuong.compareTo(BigDecimal.ZERO) > 0) {
                        // Tạo bản ghi lương
                        LuongNhanVien luong = new LuongNhanVien();
                        luong.setMaNhanVien(nv.getMaNhanVien());
                        luong.setThang(thang);
                        luong.setNam(nam);
                        luong.setTongLuong(tongLuong);
                        luong.setTrangThai("Chưa thanh toán");
                        
                        // Lưu vào database
                        luongRepo.insert(luong);
                        hasCalculated = true;
                    }
                }
            }
            return hasCalculated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatTrangThai(Integer maLuong, String trangThai) {
        return luongRepo.updateTrangThai(maLuong, trangThai);
    }

    public boolean xoaLuong(Integer maLuong) {
        return luongRepo.delete(maLuong);
    }

    public List<NhanVien> getAllNhanVien() {
        try {
            return nhanVienRepo.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}