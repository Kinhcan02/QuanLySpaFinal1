package Service;

import Model.NguyenLieu;
import Repository.NguyenLieuRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NguyenLieuService {
    private final NguyenLieuRepository repository;
    private static final Logger logger = Logger.getLogger(NguyenLieuService.class.getName());

    public NguyenLieuService() {
        this.repository = new NguyenLieuRepository();
    }

    public List<NguyenLieu> getAllNguyenLieu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách nguyên liệu", e);
            throw new RuntimeException("Không thể lấy danh sách nguyên liệu", e);
        }
    }

    public NguyenLieu getNguyenLieuById(int maNguyenLieu) {
        try {
            NguyenLieu result = repository.getById(maNguyenLieu);
            if (result == null) {
                throw new RuntimeException("Nguyên liệu với mã " + maNguyenLieu + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nguyên liệu theo mã: " + maNguyenLieu, e);
            throw new RuntimeException("Không thể lấy thông tin nguyên liệu", e);
        }
    }

    public List<NguyenLieu> getNguyenLieuByMaLoaiNL(int maLoaiNL) {
        try {
            return repository.getByMaLoaiNL(maLoaiNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nguyên liệu theo loại: " + maLoaiNL, e);
            throw new RuntimeException("Không thể lấy nguyên liệu theo loại", e);
        }
    }

    public List<NguyenLieu> getNguyenLieuBySoLuongTon(int soLuongMin) {
        try {
            return repository.getBySoLuongTon(soLuongMin);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nguyên liệu theo số lượng tồn", e);
            throw new RuntimeException("Không thể lấy nguyên liệu theo số lượng tồn", e);
        }
    }

    public boolean addNguyenLieu(NguyenLieu nguyenLieu) {
        validateNguyenLieu(nguyenLieu);
        try {
            return repository.insert(nguyenLieu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm nguyên liệu: " + nguyenLieu.getTenNguyenLieu(), e);
            throw new RuntimeException("Không thể thêm nguyên liệu", e);
        }
    }

    public boolean updateNguyenLieu(NguyenLieu nguyenLieu) {
        validateNguyenLieu(nguyenLieu);
        if (nguyenLieu.getMaNguyenLieu() <= 0) {
            throw new IllegalArgumentException("Mã nguyên liệu không hợp lệ");
        }
        try {
            return repository.update(nguyenLieu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật nguyên liệu: " + nguyenLieu.getMaNguyenLieu(), e);
            throw new RuntimeException("Không thể cập nhật nguyên liệu", e);
        }
    }

    public boolean deleteNguyenLieu(int maNguyenLieu) {
        if (maNguyenLieu <= 0) {
            throw new IllegalArgumentException("Mã nguyên liệu không hợp lệ");
        }
        try {
            return repository.delete(maNguyenLieu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa nguyên liệu: " + maNguyenLieu, e);
            throw new RuntimeException("Không thể xóa nguyên liệu", e);
        }
    }

    public boolean updateSoLuongTon(int maNguyenLieu, int soLuong) {
        if (maNguyenLieu <= 0) {
            throw new IllegalArgumentException("Mã nguyên liệu không hợp lệ");
        }
        if (soLuong < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
        try {
            return repository.updateSoLuongTon(maNguyenLieu, soLuong);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật số lượng tồn: " + maNguyenLieu, e);
            throw new RuntimeException("Không thể cập nhật số lượng tồn", e);
        }
    }

    private void validateNguyenLieu(NguyenLieu nguyenLieu) {
        if (nguyenLieu == null) {
            throw new IllegalArgumentException("Nguyên liệu không được null");
        }
        if (nguyenLieu.getTenNguyenLieu() == null || nguyenLieu.getTenNguyenLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống");
        }
        if (nguyenLieu.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        }
        if (nguyenLieu.getDonViTinh() == null || nguyenLieu.getDonViTinh().trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
    }
    public NguyenLieu getNguyenLieuByTen(String tenNguyenLieu) {
    try {
        return repository.getByTen(tenNguyenLieu);
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Lỗi khi lấy nguyên liệu theo tên: " + tenNguyenLieu, e);
        throw new RuntimeException("Không thể lấy thông tin nguyên liệu", e);
    }
}
}