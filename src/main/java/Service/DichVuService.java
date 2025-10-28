package Service;

import Model.DichVu;
import Repository.DichVuRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DichVuService {
    private final DichVuRepository repository;
    private static final Logger logger = Logger.getLogger(DichVuService.class.getName());

    public DichVuService() {
        this.repository = new DichVuRepository();
    }

    public List<DichVu> getAllDichVu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách dịch vụ", e);
            throw new RuntimeException("Không thể lấy danh sách dịch vụ", e);
        }
    }

    public DichVu getDichVuById(int maDichVu) {
        try {
            DichVu result = repository.getById(maDichVu);
            if (result == null) {
                throw new RuntimeException("Dịch vụ với mã " + maDichVu + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dịch vụ theo mã: " + maDichVu, e);
            throw new RuntimeException("Không thể lấy thông tin dịch vụ", e);
        }
    }

    public List<DichVu> getDichVuByMaLoaiDV(int maLoaiDV) {
        try {
            return repository.getByMaLoaiDV(maLoaiDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dịch vụ theo loại: " + maLoaiDV, e);
            throw new RuntimeException("Không thể lấy dịch vụ theo loại", e);
        }
    }

    public List<DichVu> searchDichVuByTen(String tenDichVu) {
        if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ tìm kiếm không được để trống");
        }
        try {
            return repository.searchByTen(tenDichVu.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm dịch vụ theo tên: " + tenDichVu, e);
            throw new RuntimeException("Không thể tìm kiếm dịch vụ", e);
        }
    }

    public boolean addDichVu(DichVu dichVu) {
        validateDichVu(dichVu);
        try {
            return repository.insert(dichVu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm dịch vụ: " + dichVu.getTenDichVu(), e);
            throw new RuntimeException("Không thể thêm dịch vụ", e);
        }
    }

    public boolean updateDichVu(DichVu dichVu) {
        validateDichVu(dichVu);
        if (dichVu.getMaDichVu() <= 0) {
            throw new IllegalArgumentException("Mã dịch vụ không hợp lệ");
        }
        try {
            return repository.update(dichVu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật dịch vụ: " + dichVu.getMaDichVu(), e);
            throw new RuntimeException("Không thể cập nhật dịch vụ", e);
        }
    }

    public boolean deleteDichVu(int maDichVu) {
        if (maDichVu <= 0) {
            throw new IllegalArgumentException("Mã dịch vụ không hợp lệ");
        }
        try {
            return repository.delete(maDichVu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa dịch vụ: " + maDichVu, e);
            throw new RuntimeException("Không thể xóa dịch vụ", e);
        }
    }

    private void validateDichVu(DichVu dichVu) {
        if (dichVu == null) {
            throw new IllegalArgumentException("Dịch vụ không được null");
        }
        if (dichVu.getTenDichVu() == null || dichVu.getTenDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        if (dichVu.getGia() == null || dichVu.getGia().doubleValue() <= 0) {
            throw new IllegalArgumentException("Giá dịch vụ phải lớn hơn 0");
        }
        // ThoiGian và MaLoaiDV có thể null
    }
}