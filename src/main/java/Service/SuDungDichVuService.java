package Service;

import Model.SuDungDichVu;
import Repository.SuDungDichVuRepository;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuDungDichVuService {
    private final SuDungDichVuRepository repository;
    private static final Logger logger = Logger.getLogger(SuDungDichVuService.class.getName());

    public SuDungDichVuService() {
        this.repository = new SuDungDichVuRepository();
    }

    public List<SuDungDichVu> getAllSuDungDichVu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sử dụng dịch vụ", e);
            throw new RuntimeException("Không thể lấy danh sách sử dụng dịch vụ", e);
        }
    }

    public SuDungDichVu getSuDungDichVuById(int maSuDung) {
        try {
            SuDungDichVu result = repository.getById(maSuDung);
            if (result == null) {
                throw new RuntimeException("Sử dụng dịch vụ với mã " + maSuDung + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sử dụng dịch vụ theo mã: " + maSuDung, e);
            throw new RuntimeException("Không thể lấy thông tin sử dụng dịch vụ", e);
        }
    }

    public List<SuDungDichVu> getSuDungDichVuByMaKhachHang(int maKhachHang) {
        try {
            return repository.getByMaKhachHang(maKhachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sử dụng dịch vụ theo mã khách hàng: " + maKhachHang, e);
            throw new RuntimeException("Không thể lấy sử dụng dịch vụ theo mã khách hàng", e);
        }
    }

    public List<SuDungDichVu> getSuDungDichVuByMaNhanVien(int maNhanVien) {
        try {
            return repository.getByMaNhanVien(maNhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sử dụng dịch vụ theo mã nhân viên: " + maNhanVien, e);
            throw new RuntimeException("Không thể lấy sử dụng dịch vụ theo mã nhân viên", e);
        }
    }

    public List<SuDungDichVu> getSuDungDichVuByDateRange(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được để trống");
        }
        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        try {
            return repository.getByDateRange(fromDate, toDate);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sử dụng dịch vụ theo khoảng ngày: " + fromDate + " - " + toDate, e);
            throw new RuntimeException("Không thể lấy sử dụng dịch vụ theo khoảng ngày", e);
        }
    }

    public boolean addSuDungDichVu(SuDungDichVu suDungDV) {
        validateSuDungDichVu(suDungDV);
        try {
            return repository.insert(suDungDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm sử dụng dịch vụ", e);
            throw new RuntimeException("Không thể thêm sử dụng dịch vụ", e);
        }
    }

    public boolean updateSuDungDichVu(SuDungDichVu suDungDV) {
        validateSuDungDichVu(suDungDV);
        if (suDungDV.getMaSuDung() <= 0) {
            throw new IllegalArgumentException("Mã sử dụng dịch vụ không hợp lệ");
        }
        try {
            return repository.update(suDungDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật sử dụng dịch vụ: " + suDungDV.getMaSuDung(), e);
            throw new RuntimeException("Không thể cập nhật sử dụng dịch vụ", e);
        }
    }

    public boolean deleteSuDungDichVu(int maSuDung) {
        if (maSuDung <= 0) {
            throw new IllegalArgumentException("Mã sử dụng dịch vụ không hợp lệ");
        }
        try {
            return repository.delete(maSuDung);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa sử dụng dịch vụ: " + maSuDung, e);
            throw new RuntimeException("Không thể xóa sử dụng dịch vụ", e);
        }
    }

    private void validateSuDungDichVu(SuDungDichVu suDungDV) {
        if (suDungDV == null) {
            throw new IllegalArgumentException("Sử dụng dịch vụ không được null");
        }
        if (suDungDV.getMaDichVu() <= 0) {
            throw new IllegalArgumentException("Mã dịch vụ không hợp lệ");
        }
        if (suDungDV.getNgaySuDung() == null) {
            throw new IllegalArgumentException("Ngày sử dụng không được để trống");
        }
        // MaKhachHang và MaNhanVien có thể null
        if (suDungDV.getSoTien() != null && suDungDV.getSoTien().doubleValue() < 0) {
            throw new IllegalArgumentException("Số tiền không được âm");
        }
        if (suDungDV.getTienTip() != null && suDungDV.getTienTip().doubleValue() < 0) {
            throw new IllegalArgumentException("Tiền tip không được âm");
        }
    }
}