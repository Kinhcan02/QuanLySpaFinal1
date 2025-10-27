package Service;

import Model.KhachHang;
import Repository.KhachHangRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KhachHangService {
    private final KhachHangRepository repository;
    private static final Logger logger = Logger.getLogger(KhachHangService.class.getName());

    public KhachHangService() {
        this.repository = new KhachHangRepository();
    }

    public List<KhachHang> getAllKhachHang() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách khách hàng", e);
            throw new RuntimeException("Không thể lấy danh sách khách hàng", e);
        }
    }

    public KhachHang getKhachHangById(int maKhachHang) {
        try {
            KhachHang result = repository.getById(maKhachHang);
            if (result == null) {
                throw new RuntimeException("Khách hàng với mã " + maKhachHang + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo mã: " + maKhachHang, e);
            throw new RuntimeException("Không thể lấy thông tin khách hàng", e);
        }
    }

    public KhachHang getKhachHangBySoDienThoai(String soDienThoai) {
        validateSoDienThoai(soDienThoai);
        try {
            return repository.getBySoDienThoai(soDienThoai);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo số điện thoại: " + soDienThoai, e);
            throw new RuntimeException("Không thể lấy khách hàng theo số điện thoại", e);
        }
    }

    public List<KhachHang> searchKhachHangByHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên tìm kiếm không được để trống");
        }
        try {
            return repository.searchByHoTen(hoTen.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tìm kiếm khách hàng theo họ tên: " + hoTen, e);
            throw new RuntimeException("Không thể tìm kiếm khách hàng", e);
        }
    }

    public List<KhachHang> getKhachHangByLoai(String loaiKhach) {
        if (loaiKhach == null || loaiKhach.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại khách không được để trống");
        }
        try {
            return repository.getByLoaiKhach(loaiKhach);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo loại: " + loaiKhach, e);
            throw new RuntimeException("Không thể lấy khách hàng theo loại", e);
        }
    }

    public boolean addKhachHang(KhachHang khachHang) {
        validateKhachHang(khachHang);
        try {
            return repository.insert(khachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm khách hàng: " + khachHang.getHoTen(), e);
            throw new RuntimeException("Không thể thêm khách hàng", e);
        }
    }

    public boolean updateKhachHang(KhachHang khachHang) {
        validateKhachHang(khachHang);
        if (khachHang.getMaKhachHang() <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ");
        }
        try {
            return repository.update(khachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật khách hàng: " + khachHang.getMaKhachHang(), e);
            throw new RuntimeException("Không thể cập nhật khách hàng", e);
        }
    }

    public boolean deleteKhachHang(int maKhachHang) {
        if (maKhachHang <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ");
        }
        try {
            return repository.delete(maKhachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa khách hàng: " + maKhachHang, e);
            throw new RuntimeException("Không thể xóa khách hàng", e);
        }
    }

    private void validateKhachHang(KhachHang khachHang) {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không được null");
        }
        if (khachHang.getHoTen() == null || khachHang.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (khachHang.getLoaiKhach() == null || khachHang.getLoaiKhach().trim().isEmpty()) {
            throw new IllegalArgumentException("Loại khách không được để trống");
        }
        validateSoDienThoai(khachHang.getSoDienThoai());
        if (khachHang.getNgayTao() == null) {
            throw new IllegalArgumentException("Ngày tạo không được để trống");
        }
    }

    private void validateSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!soDienThoai.matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
    }
}