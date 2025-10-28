package Service;

import Model.CaLam;
import Repository.CaLamRepository;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CaLamService {
    private final CaLamRepository repository;
    private static final Logger logger = Logger.getLogger(CaLamService.class.getName());

    public CaLamService() {
        this.repository = new CaLamRepository();
    }

    public List<CaLam> getAllCaLam() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách ca làm", e);
            throw new RuntimeException("Không thể lấy danh sách ca làm", e);
        }
    }

    public CaLam getCaLamById(int maCa) {
        try {
            CaLam result = repository.getById(maCa);
            if (result == null) {
                throw new RuntimeException("Ca làm với mã " + maCa + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy ca làm theo mã: " + maCa, e);
            throw new RuntimeException("Không thể lấy thông tin ca làm", e);
        }
    }

    public List<CaLam> getCaLamByMaNhanVien(int maNhanVien) {
        try {
            return repository.getByMaNhanVien(maNhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy ca làm theo nhân viên: " + maNhanVien, e);
            throw new RuntimeException("Không thể lấy ca làm theo nhân viên", e);
        }
    }

    public List<CaLam> getCaLamByNgay(Date ngayLam) {
        try {
            return repository.getByNgay(ngayLam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy ca làm theo ngày: " + ngayLam, e);
            throw new RuntimeException("Không thể lấy ca làm theo ngày", e);
        }
    }

    public boolean addCaLam(CaLam caLam) {
        validateCaLam(caLam);
        try {
            return repository.insert(caLam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm ca làm", e);
            throw new RuntimeException("Không thể thêm ca làm", e);
        }
    }

    public boolean updateCaLam(CaLam caLam) {
        validateCaLam(caLam);
        if (caLam.getMaCa() <= 0) {
            throw new IllegalArgumentException("Mã ca làm không hợp lệ");
        }
        try {
            return repository.update(caLam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật ca làm: " + caLam.getMaCa(), e);
            throw new RuntimeException("Không thể cập nhật ca làm", e);
        }
    }

    public boolean deleteCaLam(int maCa) {
        if (maCa <= 0) {
            throw new IllegalArgumentException("Mã ca làm không hợp lệ");
        }
        try {
            return repository.delete(maCa);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa ca làm: " + maCa, e);
            throw new RuntimeException("Không thể xóa ca làm", e);
        }
    }

    private void validateCaLam(CaLam caLam) {
        if (caLam == null) {
            throw new IllegalArgumentException("Ca làm không được null");
        }
        if (caLam.getMaNhanVien() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        if (caLam.getNgayLam() == null) {
            throw new IllegalArgumentException("Ngày làm không được để trống");
        }
        if (caLam.getGioBatDau() == null || caLam.getGioKetThuc() == null) {
            throw new IllegalArgumentException("Giờ bắt đầu và kết thúc không được để trống");
        }
        if (caLam.getGioBatDau().isAfter(caLam.getGioKetThuc())) {
            throw new IllegalArgumentException("Giờ bắt đầu phải trước giờ kết thúc");
        }
        if (caLam.getSoGioLam() == null || caLam.getSoGioLam().doubleValue() < 0) {
            throw new IllegalArgumentException("Số giờ làm không hợp lệ");
        }
        if (caLam.getSoLuongKhachPhucVu() < 0) {
            throw new IllegalArgumentException("Số lượng khách phục vụ không được âm");
        }
        if (caLam.getTienTip() != null && caLam.getTienTip().doubleValue() < 0) {
            throw new IllegalArgumentException("Tiền tip không được âm");
        }
    }
}