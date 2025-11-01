package Service;

import Repository.ThongKeRepository;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ThongKeService {
    private final ThongKeRepository thongKeRepo;
    
    public ThongKeService() {
        this.thongKeRepo = new ThongKeRepository();
    }
    
    public List<Map<String, Object>> thongKeKhachHangNhieuDichVu(Date fromDate, Date toDate, int limit) {
        return thongKeRepo.getKhachHangNhieuDichVuNhat(fromDate, toDate, limit);
    }
    
    public List<Map<String, Object>> thongKeDoanhThuTheoThang(int year) {
        return thongKeRepo.getDoanhThuTheoThang(year);
    }
    
    public List<Map<String, Object>> thongKeDoanhThuTheoNgay(Date fromDate, Date toDate) {
        return thongKeRepo.getDoanhThuTheoNgay(fromDate, toDate);
    }
    
    public List<Map<String, Object>> thongKeDichVuBanChay(Date fromDate, Date toDate, int limit) {
        return thongKeRepo.getDichVuBanChay(fromDate, toDate, limit);
    }
    
    public Map<String, Object> thongKeTongQuan(Date fromDate, Date toDate) {
        return thongKeRepo.getThongKeTongQuan(fromDate, toDate);
    }
    
    public List<Integer> getDanhSachNam() {
        return thongKeRepo.getDanhSachNam();
    }
}