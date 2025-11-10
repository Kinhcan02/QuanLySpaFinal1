package Service;

import Repository.ThuNhapRepository;
import Model.ThuNhap;
import java.math.BigDecimal;
import java.util.List;

public class ThuNhapService {
    private ThuNhapRepository thuNhapRepository;
    
    public ThuNhapService() {
        this.thuNhapRepository = new ThuNhapRepository();
    }
    
    public List<ThuNhap> getAllThuNhap() {
        return thuNhapRepository.getAllThuNhap();
    }
    
    public List<ThuNhap> getThuNhapByThangNam(int thang, int nam) {
        return thuNhapRepository.getThuNhapByThangNam(thang, nam);
    }
    
    public ThuNhap getThuNhapById(int maThu) {
        return thuNhapRepository.getThuNhapById(maThu);
    }
    
    public boolean themThuNhap(ThuNhap thuNhap) {
        return thuNhapRepository.addThuNhap(thuNhap);
    }
    
    public boolean suaThuNhap(ThuNhap thuNhap) {
        return thuNhapRepository.updateThuNhap(thuNhap);
    }
    
    public boolean xoaThuNhap(int maThu) {
        return thuNhapRepository.deleteThuNhap(maThu);
    }
    
    public ThuNhap tinhToanThuNhapThang(int thang, int nam) {
        BigDecimal tongDoanhThu = thuNhapRepository.getTongDoanhThuThang(thang, nam);
        BigDecimal tongLuong = thuNhapRepository.getTongLuongThang(thang, nam);
        
        // Tính thu nhập thực = Tổng doanh thu - Tổng lương
        BigDecimal thuNhapThuc = tongDoanhThu.subtract(tongLuong);
        if (thuNhapThuc.compareTo(BigDecimal.ZERO) < 0) {
            thuNhapThuc = BigDecimal.ZERO;
        }
        
        return new ThuNhap(0, thang, nam, tongDoanhThu, tongLuong, 
                          thuNhapThuc, java.time.LocalDate.now(), "Tính toán tự động");
    }
    
    public List<ThuNhap> getThuNhapByNam(int nam) {
        return thuNhapRepository.getThuNhapByNam(nam);
    }
    
    public BigDecimal getTongThuNhapByNam(int nam) {
        return thuNhapRepository.getTongThuNhapByNam(nam);
    }
     public BigDecimal getTongThuNhapByThang(int thang, int nam) {
        List<ThuNhap> thuNhapThang = thuNhapRepository.getThuNhapByThangNam(thang, nam);
        return thuNhapThang.stream()
                .map(ThuNhap::getThuNhapThuc)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // Phương thức format tiền tệ
    public String formatTienTe(BigDecimal amount) {
        if (amount == null) {
            return "0 VND";
        }
        return String.format("%,.0f VND", amount);
    }
}