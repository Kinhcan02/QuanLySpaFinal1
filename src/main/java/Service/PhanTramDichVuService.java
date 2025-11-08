package Service;

import Model.PhanTramDichVu;
import Repository.PhanTramDichVuRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhanTramDichVuService {
    private final PhanTramDichVuRepository repository;
    private static final Logger logger = Logger.getLogger(PhanTramDichVuService.class.getName());

    public PhanTramDichVuService() {
        this.repository = new PhanTramDichVuRepository();
    }

    public List<PhanTramDichVu> getAllPhanTramDichVu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách phân trăm dịch vụ", e);
            throw new RuntimeException("Không thể lấy danh sách phân trăm dịch vụ", e);
        }
    }

    public PhanTramDichVu getPhanTramDichVuById(int maPhanTram) {
        try {
            PhanTramDichVu result = repository.getById(maPhanTram);
            if (result == null) {
                throw new RuntimeException("Phân trăm dịch vụ với mã " + maPhanTram + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy phân trăm dịch vụ theo mã: " + maPhanTram, e);
            throw new RuntimeException("Không thể lấy thông tin phân trăm dịch vụ", e);
        }
    }

    public List<PhanTramDichVu> getPhanTramByNhanVien(int maNhanVien) {
        try {
            return repository.getByNhanVien(maNhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy phân trăm dịch vụ theo nhân viên: " + maNhanVien, e);
            throw new RuntimeException("Không thể lấy phân trăm dịch vụ theo nhân viên", e);
        }
    }

    public PhanTramDichVu getPhanTramByNhanVienAndLoaiDV(int maNhanVien, int maLoaiDV) {
        try {
            return repository.getByNhanVienAndLoaiDV(maNhanVien, maLoaiDV);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, 
                String.format("Lỗi khi lấy phân trăm cho nhân viên %d và loại DV %d", maNhanVien, maLoaiDV), e);
            throw new RuntimeException("Không thể lấy phân trăm dịch vụ", e);
        }
    }

    public boolean addPhanTramDichVu(PhanTramDichVu phanTram) {
        validatePhanTramDichVu(phanTram);
        try {
            // Kiểm tra xem đã tồn tại chưa
            if (repository.exists(phanTram.getMaNhanVien(), phanTram.getMaLoaiDV())) {
                throw new RuntimeException("Đã tồn tại phân trăm dịch vụ cho nhân viên và loại dịch vụ này");
            }
            
            boolean result = repository.insert(phanTram);
            if (result) {
                // Lấy ID vừa insert và set vào object
                int newId = repository.getLastInsertId();
                phanTram.setMaPhanTram(newId);
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm phân trăm dịch vụ", e);
            throw new RuntimeException("Không thể thêm phân trăm dịch vụ", e);
        }
    }

    public boolean updatePhanTramDichVu(PhanTramDichVu phanTram) {
        validatePhanTramDichVu(phanTram);
        if (phanTram.getMaPhanTram() == null || phanTram.getMaPhanTram() <= 0) {
            throw new IllegalArgumentException("Mã phân trăm dịch vụ không hợp lệ");
        }
        try {
            return repository.update(phanTram);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật phân trăm dịch vụ: " + phanTram.getMaPhanTram(), e);
            throw new RuntimeException("Không thể cập nhật phân trăm dịch vụ", e);
        }
    }

    public boolean deletePhanTramDichVu(int maPhanTram) {
        if (maPhanTram <= 0) {
            throw new IllegalArgumentException("Mã phân trăm dịch vụ không hợp lệ");
        }
        try {
            return repository.delete(maPhanTram);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa phân trăm dịch vụ: " + maPhanTram, e);
            throw new RuntimeException("Không thể xóa phân trăm dịch vụ", e);
        }
    }

    private void validatePhanTramDichVu(PhanTramDichVu phanTram) {
        if (phanTram == null) {
            throw new IllegalArgumentException("Phân trăm dịch vụ không được null");
        }
        if (phanTram.getMaLoaiDV() == null || phanTram.getMaLoaiDV() <= 0) {
            throw new IllegalArgumentException("Mã loại dịch vụ không hợp lệ");
        }
        if (phanTram.getMaNhanVien() == null || phanTram.getMaNhanVien() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        if (phanTram.getTiLePhanTram() == null || phanTram.getTiLePhanTram() < 0) {
            throw new IllegalArgumentException("Tỉ lệ phần trăm không hợp lệ");
        }
    }
}