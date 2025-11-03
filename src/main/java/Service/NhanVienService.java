package Service;

import Model.NhanVien;
import Repository.NhanVienRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NhanVienService {

    private final NhanVienRepository repository;
    private static final Logger logger = Logger.getLogger(NhanVienService.class.getName());

    public NhanVienService() {
        this.repository = new NhanVienRepository();
    }

    public List<NhanVien> getAllNhanVien() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách nhân viên", e);
            throw new RuntimeException("Không thể lấy danh sách nhân viên", e);
        }
    }

    public NhanVien getNhanVienById(int maNhanVien) {
        try {
            NhanVien result = repository.getById(maNhanVien);
            if (result == null) {
                throw new RuntimeException("Nhân viên với mã " + maNhanVien + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhân viên theo mã: " + maNhanVien, e);
            throw new RuntimeException("Không thể lấy thông tin nhân viên", e);
        }
    }

    public NhanVien getNhanVienBySoDienThoai(String soDienThoai) {
        validateSoDienThoai(soDienThoai);
        try {
            return repository.getBySoDienThoai(soDienThoai);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhân viên theo số điện thoại: " + soDienThoai, e);
            throw new RuntimeException("Không thể lấy nhân viên theo số điện thoại", e);
        }
    }

    public List<NhanVien> searchNhanVienByHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên tìm kiếm không được để trống");
        }
        try {
            return repository.searchByHoTen(hoTen.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm nhân viên theo họ tên: " + hoTen, e);
            throw new RuntimeException("Không thể tìm kiếm nhân viên", e);
        }
    }

    public List<NhanVien> getNhanVienByChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Chức vụ không được để trống");
        }
        try {
            return repository.getByChucVu(chucVu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhân viên theo chức vụ: " + chucVu, e);
            throw new RuntimeException("Không thể lấy nhân viên theo chức vụ", e);
        }
    }

    public boolean addNhanVien(NhanVien nhanVien) {
        validateNhanVien(nhanVien);
        try {
            // Kiểm tra số điện thoại đã tồn tại chưa
            if (repository.isSoDienThoaiExists(nhanVien.getSoDienThoai(), null)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống");
            }
            return repository.insert(nhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm nhân viên: " + nhanVien.getHoTen(), e);
            throw new RuntimeException("Không thể thêm nhân viên", e);
        }
    }

    public boolean updateNhanVien(NhanVien nhanVien) {
        validateNhanVien(nhanVien);
        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        try {
            // Kiểm tra số điện thoại đã tồn tại chưa (trừ nhân viên hiện tại)
            if (repository.isSoDienThoaiExists(nhanVien.getSoDienThoai(), nhanVien.getMaNhanVien())) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống");
            }
            return repository.update(nhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật nhân viên: " + nhanVien.getMaNhanVien(), e);
            throw new RuntimeException("Không thể cập nhật nhân viên", e);
        }
    }

    public boolean deleteNhanVien(int maNhanVien) {
        if (maNhanVien <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        try {
            return repository.delete(maNhanVien);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa nhân viên: " + maNhanVien, e);
            throw new RuntimeException("Không thể xóa nhân viên", e);
        }
    }

    private void validateNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null");
        }
        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        validateSoDienThoai(nhanVien.getSoDienThoai());
        if (nhanVien.getDiaChi() == null || nhanVien.getDiaChi().trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống");
        }
        if (nhanVien.getChucVu() == null || nhanVien.getChucVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Chức vụ không được để trống");
        }
        if (nhanVien.getNgayVaoLam() == null) {
            throw new IllegalArgumentException("Ngày vào làm không được để trống");
        }
    }

    private void validateSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        // Kiểm tra định dạng số điện thoại (10-11 chữ số)
        if (!soDienThoai.matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10-11 chữ số");
        }
    }
}
