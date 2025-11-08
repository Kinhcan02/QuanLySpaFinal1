package Service;

import Model.ChiTietTienDichVuCuaNhanVien;
import Repository.ChiTietTienDichVuCuaNhanVienRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChiTietTienDichVuCuaNhanVienService {
    private final ChiTietTienDichVuCuaNhanVienRepository repository;
    private static final Logger logger = Logger.getLogger(ChiTietTienDichVuCuaNhanVienService.class.getName());

    public ChiTietTienDichVuCuaNhanVienService() {
        this.repository = new ChiTietTienDichVuCuaNhanVienRepository();
    }

    public List<ChiTietTienDichVuCuaNhanVien> getAllChiTietTienDV() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách chi tiết tiền dịch vụ", e);
            throw new RuntimeException("Không thể lấy danh sách chi tiết tiền dịch vụ", e);
        }
    }

    public ChiTietTienDichVuCuaNhanVien getChiTietTienDVById(int maCTTienDV) {
        try {
            ChiTietTienDichVuCuaNhanVien result = repository.getById(maCTTienDV);
            if (result == null) {
                throw new RuntimeException("Chi tiết tiền dịch vụ với mã " + maCTTienDV + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy chi tiết tiền dịch vụ theo mã: " + maCTTienDV, e);
            throw new RuntimeException("Không thể lấy thông tin chi tiết tiền dịch vụ", e);
        }
    }

    public List<ChiTietTienDichVuCuaNhanVien> getChiTietTienDVByNhanVien(int maNhanVien) {
        try {
            return repository.getByNhanVien(maNhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy chi tiết tiền dịch vụ theo nhân viên: " + maNhanVien, e);
            throw new RuntimeException("Không thể lấy chi tiết tiền dịch vụ theo nhân viên", e);
        }
    }

    public List<ChiTietTienDichVuCuaNhanVien> getChiTietTienDVByThangNam(int maNhanVien, int thang, int nam) {
        try {
            return repository.getByThangNam(maNhanVien, thang, nam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, 
                String.format("Lỗi khi lấy chi tiết tiền dịch vụ theo tháng %d năm %d cho nhân viên %d", 
                    thang, nam, maNhanVien), e);
            throw new RuntimeException("Không thể lấy chi tiết tiền dịch vụ theo tháng năm", e);
        }
    }

    public BigDecimal getTongTienDichVuByThangNam(int maNhanVien, int thang, int nam) {
        try {
            return repository.getTongTienDichVuByThangNam(maNhanVien, thang, nam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, 
                String.format("Lỗi khi tính tổng tiền dịch vụ tháng %d năm %d cho nhân viên %d", 
                    thang, nam, maNhanVien), e);
            throw new RuntimeException("Không thể tính tổng tiền dịch vụ", e);
        }
    }

    public boolean addChiTietTienDV(ChiTietTienDichVuCuaNhanVien chiTiet) {
        validateChiTietTienDV(chiTiet);
        chiTiet.tinhDonGiaThucTe(); // Ensure calculation is done before insert
        try {
            boolean result = repository.insert(chiTiet);
            if (result) {
                // Lấy ID vừa insert và set vào object
                int newId = repository.getLastInsertId();
                chiTiet.setMaCTTienDV(newId);
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm chi tiết tiền dịch vụ", e);
            throw new RuntimeException("Không thể thêm chi tiết tiền dịch vụ", e);
        }
    }

    public boolean updateChiTietTienDV(ChiTietTienDichVuCuaNhanVien chiTiet) {
        validateChiTietTienDV(chiTiet);
        if (chiTiet.getMaCTTienDV() == null || chiTiet.getMaCTTienDV() <= 0) {
            throw new IllegalArgumentException("Mã chi tiết tiền dịch vụ không hợp lệ");
        }
        chiTiet.tinhDonGiaThucTe(); // Ensure calculation is done before update
        try {
            return repository.update(chiTiet);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật chi tiết tiền dịch vụ: " + chiTiet.getMaCTTienDV(), e);
            throw new RuntimeException("Không thể cập nhật chi tiết tiền dịch vụ", e);
        }
    }

    public boolean deleteChiTietTienDV(int maCTTienDV) {
        if (maCTTienDV <= 0) {
            throw new IllegalArgumentException("Mã chi tiết tiền dịch vụ không hợp lệ");
        }
        try {
            return repository.delete(maCTTienDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa chi tiết tiền dịch vụ: " + maCTTienDV, e);
            throw new RuntimeException("Không thể xóa chi tiết tiền dịch vụ", e);
        }
    }

    private void validateChiTietTienDV(ChiTietTienDichVuCuaNhanVien chiTiet) {
        if (chiTiet == null) {
            throw new IllegalArgumentException("Chi tiết tiền dịch vụ không được null");
        }
        if (chiTiet.getMaCTHD() == null || chiTiet.getMaCTHD() <= 0) {
            throw new IllegalArgumentException("Mã chi tiết hóa đơn không hợp lệ");
        }
        if (chiTiet.getMaDichVu() == null || chiTiet.getMaDichVu() <= 0) {
            throw new IllegalArgumentException("Mã dịch vụ không hợp lệ");
        }
        if (chiTiet.getMaNhanVien() == null || chiTiet.getMaNhanVien() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        if (chiTiet.getSoLuong() == null || chiTiet.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (chiTiet.getDonGiaGoc() == null || chiTiet.getDonGiaGoc().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá gốc không hợp lệ");
        }
        if (chiTiet.getTiLePhanTram() == null || chiTiet.getTiLePhanTram() < 0) {
            throw new IllegalArgumentException("Tỉ lệ phần trăm không hợp lệ");
        }
    }
}