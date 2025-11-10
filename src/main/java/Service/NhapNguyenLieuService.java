package Service;

import Model.NhapNguyenLieu;
import Repository.NhapNguyenLieuRepository;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NhapNguyenLieuService {

    private final NhapNguyenLieuRepository repository;
    private static final Logger logger = Logger.getLogger(NhapNguyenLieuService.class.getName());

    public NhapNguyenLieuService() {
        this.repository = new NhapNguyenLieuRepository();
    }

    public List<NhapNguyenLieu> getAllNhapNguyenLieu() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách nhập nguyên liệu", e);
            throw new RuntimeException("Không thể lấy danh sách nhập nguyên liệu", e);
        }
    }

    public List<NhapNguyenLieu> getNhapNguyenLieuByThangNam(int thang, int nam) {
        try {
            return repository.getByThangNam(thang, nam);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhập nguyên liệu theo tháng năm: " + thang + "/" + nam, e);
            throw new RuntimeException("Không thể lấy nhập nguyên liệu theo tháng năm", e);
        }
    }

    public NhapNguyenLieu getNhapNguyenLieuById(int maNhap) {
        try {
            NhapNguyenLieu result = repository.getById(maNhap);
            if (result == null) {
                throw new RuntimeException("Nhập nguyên liệu với mã " + maNhap + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhập nguyên liệu theo mã: " + maNhap, e);
            throw new RuntimeException("Không thể lấy thông tin nhập nguyên liệu", e);
        }
    }

    public List<NhapNguyenLieu> getNhapNguyenLieuByMaNguyenLieu(Integer maNguyenLieu) { // Đổi thành Integer
        try {
            return repository.getByMaNguyenLieu(maNguyenLieu);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhập nguyên liệu theo mã nguyên liệu: " + maNguyenLieu, e);
            throw new RuntimeException("Không thể lấy nhập nguyên liệu theo mã nguyên liệu", e);
        }
    }

    public List<NhapNguyenLieu> getNhapNguyenLieuByDateRange(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được để trống");
        }
        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        try {
            return repository.getByDateRange(fromDate, toDate);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy nhập nguyên liệu theo khoảng ngày: " + fromDate + " - " + toDate, e);
            throw new RuntimeException("Không thể lấy nhập nguyên liệu theo khoảng ngày", e);
        }
    }

    public boolean addNhapNguyenLieu(NhapNguyenLieu nhapNL) {
        validateNhapNguyenLieu(nhapNL);
        try {
            return repository.insert(nhapNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm nhập nguyên liệu", e);
            throw new RuntimeException("Không thể thêm nhập nguyên liệu", e);
        }
    }

    public boolean updateNhapNguyenLieu(NhapNguyenLieu nhapNL) {
        validateNhapNguyenLieu(nhapNL);
        if (nhapNL.getMaNhap() <= 0) {
            throw new IllegalArgumentException("Mã nhập nguyên liệu không hợp lệ");
        }
        try {
            return repository.update(nhapNL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật nhập nguyên liệu: " + nhapNL.getMaNhap(), e);
            throw new RuntimeException("Không thể cập nhật nhập nguyên liệu", e);
        }
    }

    public boolean deleteNhapNguyenLieu(int maNhap) {
        if (maNhap <= 0) {
            throw new IllegalArgumentException("Mã nhập nguyên liệu không hợp lệ");
        }
        try {
            return repository.delete(maNhap);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa nhập nguyên liệu: " + maNhap, e);
            throw new RuntimeException("Không thể xóa nhập nguyên liệu", e);
        }
    }

    // THÊM PHƯƠNG THỨC KIỂM TRA PHIẾU NHẬP CÓ THỂ XÓA
    public boolean isPhieuNhapCoTheXoa(Integer maNhap) {
        // LUÔN CHO PHÉP XÓA, KỂ CẢ KHI MaNguyenLieu = NULL
        return true;
    }

    // THÊM PHƯƠNG THỨC KIỂM TRA CÓ PHIẾU NHẬP LIÊN QUAN
    public boolean hasPhieuNhapLienQuan(Integer maNguyenLieu) {
        try {
            List<NhapNguyenLieu> list = getNhapNguyenLieuByMaNguyenLieu(maNguyenLieu);
            return list != null && !list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void validateNhapNguyenLieu(NhapNguyenLieu nhapNL) {
        if (nhapNL == null) {
            throw new IllegalArgumentException("Nhập nguyên liệu không được null");
        }

        // SỬA LẠI: CHO PHÉP MaNguyenLieu = null hoặc = 0
        if (nhapNL.getMaNguyenLieu() != null && nhapNL.getMaNguyenLieu() <= 0) {
            throw new IllegalArgumentException("Mã nguyên liệu không hợp lệ");
        }

        if (nhapNL.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0");
        }
        if (nhapNL.getDonGia() == null || nhapNL.getDonGia().doubleValue() <= 0) {
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0");
        }
        if (nhapNL.getNgayNhap() == null) {
            throw new IllegalArgumentException("Ngày nhập không được để trống");
        }
        // Thêm validate cho TenNguyenLieu và DonViTinh
        if (nhapNL.getTenNguyenLieu() == null || nhapNL.getTenNguyenLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống");
        }
        if (nhapNL.getDonViTinh() == null || nhapNL.getDonViTinh().trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
    }
}
