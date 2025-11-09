package Service;

import Model.ChiTietTienDichVuCuaNhanVien;
import Repository.ChiTietTienDichVuCuaNhanVienRepository;
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

    public boolean taoChiTietTienDichVuTuDong(int maCTHD) {
        try {
            return repository.taoChiTietTienDichVuTuDong(maCTHD);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tạo chi tiết tiền dịch vụ tự động cho MaCTHD: " + maCTHD, e);
            throw new RuntimeException("Không thể tạo chi tiết tiền dịch vụ tự động", e);
        }
    }

    public boolean addChiTietTienDV(ChiTietTienDichVuCuaNhanVien chiTiet) {
        validateChiTietTienDV(chiTiet);
        
        // Tính toán đơn giá thực tế trước khi lưu
        chiTiet.tinhDonGiaThucTe();
        
        try {
            boolean result = repository.insert(chiTiet);
            if (result) {
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
        
        // Tính toán đơn giá thực tế trước khi cập nhật
        chiTiet.tinhDonGiaThucTe();
        
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
        if (chiTiet.getMaPhanTram() == null || chiTiet.getMaPhanTram() <= 0) {
            throw new IllegalArgumentException("Mã phân trăm không hợp lệ");
        }
    }
}