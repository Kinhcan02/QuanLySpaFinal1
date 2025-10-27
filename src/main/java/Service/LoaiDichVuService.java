package Service;

import Model.LoaiDichVu;
import Repository.LoaiDichVuRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoaiDichVuService {
    private final LoaiDichVuRepository repository;
    private static final Logger logger = Logger.getLogger(LoaiDichVuService.class.getName());

    public LoaiDichVuService() {
        this.repository = new LoaiDichVuRepository();
    }

    public List<LoaiDichVu> getAllLoaiDichVu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách loại dịch vụ", e);
            throw new RuntimeException("Không thể lấy danh sách loại dịch vụ", e);
        }
    }

    public LoaiDichVu getLoaiDichVuById(int maLoaiDV) {
        try {
            LoaiDichVu result = repository.getById(maLoaiDV);
            if (result == null) {
                throw new RuntimeException("Loại dịch vụ với mã " + maLoaiDV + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy loại dịch vụ theo mã: " + maLoaiDV, e);
            throw new RuntimeException("Không thể lấy thông tin loại dịch vụ", e);
        }
    }

    public boolean addLoaiDichVu(LoaiDichVu loaiDV) {
        validateLoaiDichVu(loaiDV);
        try {
            return repository.insert(loaiDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm loại dịch vụ: " + loaiDV.getTenLoaiDV(), e);
            throw new RuntimeException("Không thể thêm loại dịch vụ", e);
        }
    }

    public boolean updateLoaiDichVu(LoaiDichVu loaiDV) {
        validateLoaiDichVu(loaiDV);
        if (loaiDV.getMaLoaiDV() <= 0) {
            throw new IllegalArgumentException("Mã loại dịch vụ không hợp lệ");
        }
        try {
            return repository.update(loaiDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật loại dịch vụ: " + loaiDV.getMaLoaiDV(), e);
            throw new RuntimeException("Không thể cập nhật loại dịch vụ", e);
        }
    }

    public boolean deleteLoaiDichVu(int maLoaiDV) {
        if (maLoaiDV <= 0) {
            throw new IllegalArgumentException("Mã loại dịch vụ không hợp lệ");
        }
        try {
            return repository.delete(maLoaiDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa loại dịch vụ: " + maLoaiDV, e);
            throw new RuntimeException("Không thể xóa loại dịch vụ", e);
        }
    }

    private void validateLoaiDichVu(LoaiDichVu loaiDV) {
        if (loaiDV == null) {
            throw new IllegalArgumentException("Loại dịch vụ không được null");
        }
        if (loaiDV.getTenLoaiDV() == null || loaiDV.getTenLoaiDV().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại dịch vụ không được để trống");
        }
    }
}