package Service;

import Repository.IThuNhapRepository;
import Model.ThuNhap;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public class ThuNhapService {

    private IThuNhapRepository thuNhapRepository;

    public ThuNhapService(IThuNhapRepository thuNhapRepository) {
        this.thuNhapRepository = thuNhapRepository;
    }

    public List<ThuNhap> getAllThuNhap() {
        return thuNhapRepository.getAllThuNhap();
    }

    public List<ThuNhap> getThuNhapByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được null");
        }
        return thuNhapRepository.getThuNhapByDateRange(fromDate, toDate);
    }

    public List<ThuNhap> getThuNhapByThangNam(int thang, int nam) {
        if (thang < 1 || thang > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1 đến 12");
        }
        return thuNhapRepository.getThuNhapByMonth(thang, nam);
    }

    public List<ThuNhap> getThuNhapByNam(int nam) {
        return thuNhapRepository.getThuNhapByYear(nam);
    }

    public boolean themThuNhap(ThuNhap thuNhap) {
        if (thuNhap == null || !thuNhap.isValid()) {
            return false;
        }
        return thuNhapRepository.addThuNhap(thuNhap);
    }

    public boolean suaThuNhap(ThuNhap thuNhap) {
        if (thuNhap == null || !thuNhap.isValid() || thuNhap.getMaThu() == null) {
            return false;
        }
        return thuNhapRepository.updateThuNhap(thuNhap);
    }

    public boolean xoaThuNhap(int maThu) {
        if (maThu <= 0) {
            return false;
        }
        return thuNhapRepository.deleteThuNhap(maThu);
    }

    public BigDecimal tinhTongThuNhap(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            return BigDecimal.ZERO;
        }
        return thuNhapRepository.getTongThuNhap(fromDate, toDate);
    }

    public BigDecimal tinhTongThuNhapTuHoaDon(LocalDate fromDate, LocalDate toDate) {
        try {
            return thuNhapRepository.getTongThuNhapByHoaDon(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal tinhTongThuThangHienTai() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return tinhTongThuNhap(startOfMonth, endOfMonth);
    }

    public BigDecimal tinhTongThuNamHienTai() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        return tinhTongThuNhap(startOfYear, endOfYear);
    }
}
