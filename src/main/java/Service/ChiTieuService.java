package Service;

import Repository.IChiTieuRepository;
import Model.ChiTieu;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public class ChiTieuService {

    private IChiTieuRepository chiTieuRepository;

    public ChiTieuService(IChiTieuRepository chiTieuRepository) {
        this.chiTieuRepository = chiTieuRepository;
    }

    public List<ChiTieu> getAllChiTieu() {
        return chiTieuRepository.getAllChiTieu();
    }

    public List<ChiTieu> getChiTieuByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được null");
        }
        return chiTieuRepository.getChiTieuByDateRange(fromDate, toDate);
    }

    public List<ChiTieu> getChiTieuByThangNam(int thang, int nam) {
        if (thang < 1 || thang > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1 đến 12");
        }
        return chiTieuRepository.getChiTieuByMonth(thang, nam);
    }

    public List<ChiTieu> getChiTieuByNam(int nam) {
        return chiTieuRepository.getChiTieuByYear(nam);
    }

    public boolean themChiTieu(ChiTieu chiTieu) {
        if (chiTieu == null || !chiTieu.isValid()) {
            return false;
        }
        return chiTieuRepository.addChiTieu(chiTieu);
    }

    public boolean suaChiTieu(ChiTieu chiTieu) {
        if (chiTieu == null || !chiTieu.isValid() || chiTieu.getMaChi() == null) {
            return false;
        }
        return chiTieuRepository.updateChiTieu(chiTieu);
    }

    public boolean xoaChiTieu(int maChi) {
        if (maChi <= 0) {
            return false;
        }
        return chiTieuRepository.deleteChiTieu(maChi);
    }

    public BigDecimal tinhTongChiTieu(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            return BigDecimal.ZERO;
        }
        return chiTieuRepository.getTongChiTieu(fromDate, toDate);
    }

    public BigDecimal tinhTongChiTuNguyenLieu(LocalDate fromDate, LocalDate toDate) {
        try {
            return chiTieuRepository.getTongChiTieuByNguyenLieu(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal tinhTongChiThangHienTai() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return tinhTongChiTieu(startOfMonth, endOfMonth);
    }

    public BigDecimal tinhTongChiNamHienTai() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        return tinhTongChiTieu(startOfYear, endOfYear);
    }

    public BigDecimal tinhLoiNhuan(LocalDate fromDate, LocalDate toDate, BigDecimal tongThu, BigDecimal tongChi) {
        if (tongThu == null) {
            tongThu = BigDecimal.ZERO;
        }
        if (tongChi == null) {
            tongChi = BigDecimal.ZERO;
        }
        return tongThu.subtract(tongChi);
    }
}
