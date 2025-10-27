package Service;

import Model.LoaiNguyenLieu;
import Repository.LoaiNguyenLieuRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoaiNguyenLieuService {
    private final LoaiNguyenLieuRepository repository;
    private static final Logger logger = Logger.getLogger(LoaiNguyenLieuService.class.getName());

    public LoaiNguyenLieuService() {
        this.repository = new LoaiNguyenLieuRepository();
    }

    public List<LoaiNguyenLieu> getAllLoaiNguyenLieu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách loại nguyên liệu", e);
            throw new RuntimeException("Không thể lấy danh sách loại nguyên liệu", e);
        }
    }

    public LoaiNguyenLieu getLoaiNguyenLieuById(int maLoaiNL) {
        try {
            LoaiNguyenLieu result = repository.getById(maLoaiNL);
            if (result == null) {
                throw new RuntimeException("Loại nguyên liệu với mã " + maLoaiNL + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy loại nguyên liệu theo mã: " + maLoaiNL, e);
            throw new RuntimeException("Không thể lấy thông tin loại nguyên liệu", e);
        }
    }

    public boolean addLoaiNguyenLieu(LoaiNguyenLieu loaiNL) {
        validateLoaiNguyenLieu(loaiNL);
        try {
            return repository.insert(loaiNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm loại nguyên liệu: " + loaiNL.getTenLoaiNL(), e);
            throw new RuntimeException("Không thể thêm loại nguyên liệu", e);
        }
    }

    public boolean updateLoaiNguyenLieu(LoaiNguyenLieu loaiNL) {
        validateLoaiNguyenLieu(loaiNL);
        if (loaiNL.getMaLoaiNL() <= 0) {
            throw new IllegalArgumentException("Mã loại nguyên liệu không hợp lệ");
        }
        try {
            return repository.update(loaiNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật loại nguyên liệu: " + loaiNL.getMaLoaiNL(), e);
            throw new RuntimeException("Không thể cập nhật loại nguyên liệu", e);
        }
    }

    public boolean deleteLoaiNguyenLieu(int maLoaiNL) {
        if (maLoaiNL <= 0) {
            throw new IllegalArgumentException("Mã loại nguyên liệu không hợp lệ");
        }
        try {
            return repository.delete(maLoaiNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa loại nguyên liệu: " + maLoaiNL, e);
            throw new RuntimeException("Không thể xóa loại nguyên liệu", e);
        }
    }

    private void validateLoaiNguyenLieu(LoaiNguyenLieu loaiNL) {
        if (loaiNL == null) {
            throw new IllegalArgumentException("Loại nguyên liệu không được null");
        }
        if (loaiNL.getTenLoaiNL() == null || loaiNL.getTenLoaiNL().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại nguyên liệu không được để trống");
        }
    }
}