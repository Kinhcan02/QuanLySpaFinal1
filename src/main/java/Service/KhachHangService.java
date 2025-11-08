package Service;

import Model.KhachHang;
import Repository.KhachHangRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
        // Khi thêm mới, điểm tích lũy luôn là 0
        khachHang.setDiemTichLuy(0);
        try {
            return repository.insert(khachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm khách hàng: " + khachHang.getHoTen(), e);
            
            // Xử lý lỗi trùng số điện thoại
            if (e.getMessage().toLowerCase().contains("duplicate") || 
                e.getMessage().toLowerCase().contains("unique") ||
                e.getSQLState() != null && e.getSQLState().equals("23000")) {
                throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống");
            }
            
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
            
            // Xử lý lỗi trùng số điện thoại
            if (e.getMessage().toLowerCase().contains("duplicate") || 
                e.getMessage().toLowerCase().contains("unique") ||
                e.getSQLState() != null && e.getSQLState().equals("23000")) {
                throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống");
            }
            
            throw new RuntimeException("Không thể cập nhật khách hàng", e);
        }
    }

    public boolean deleteKhachHang(int maKhachHang) {
        if (maKhachHang <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ");
        }
        
        try {
            // Kiểm tra khách hàng có tồn tại không
            KhachHang khachHang = repository.getById(maKhachHang);
            if (khachHang == null) {
                throw new RuntimeException("Khách hàng không tồn tại");
            }
            
            // Kiểm tra dữ liệu liên quan trước khi xóa
            Map<String, Integer> duLieuLienQuan = kiemTraDuLieuLienQuan(maKhachHang);
            if (!duLieuLienQuan.isEmpty()) {
                StringBuilder message = new StringBuilder("Không thể xóa khách hàng vì có dữ liệu liên quan:\n");
                for (Map.Entry<String, Integer> entry : duLieuLienQuan.entrySet()) {
                    message.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" bản ghi\n");
                }
                throw new RuntimeException(message.toString());
            }
            
            return repository.delete(maKhachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa khách hàng: " + maKhachHang, e);
            
            // Xử lý lỗi ràng buộc khóa ngoại
            if (e.getMessage().toLowerCase().contains("constraint") || 
                e.getMessage().toLowerCase().contains("foreign") ||
                e.getMessage().toLowerCase().contains("related") ||
                (e.getSQLState() != null && e.getSQLState().equals("23000"))) {
                
                Map<String, Integer> duLieuLienQuan = kiemTraDuLieuLienQuan(maKhachHang);
                if (!duLieuLienQuan.isEmpty()) {
                    StringBuilder message = new StringBuilder("Không thể xóa khách hàng vì có dữ liệu liên quan:\n");
                    for (Map.Entry<String, Integer> entry : duLieuLienQuan.entrySet()) {
                        message.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" bản ghi\n");
                    }
                    throw new RuntimeException(message.toString());
                }
            }
            
            throw new RuntimeException("Không thể xóa khách hàng: " + e.getMessage());
        }
    }

    public Map<String, Integer> kiemTraDuLieuLienQuan(int maKhachHang) {
        if (maKhachHang <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ");
        }
        
        try {
            return repository.kiemTraDuLieuLienQuan(maKhachHang);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra dữ liệu liên quan: " + maKhachHang, e);
            throw new RuntimeException("Không thể kiểm tra dữ liệu liên quan", e);
        }
    }

    public boolean updateDiemTichLuy(int maKhachHang, int diemTichLuy) {
        if (maKhachHang <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ");
        }
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
        
        try {
            KhachHang khachHang = repository.getById(maKhachHang);
            if (khachHang == null) {
                throw new RuntimeException("Khách hàng không tồn tại");
            }
            
            // Sử dụng phương thức chuyên dụng để cập nhật điểm
            return repository.updateDiemTichLuy(maKhachHang, diemTichLuy);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật điểm tích lũy cho khách hàng: " + maKhachHang, e);
            throw new RuntimeException("Không thể cập nhật điểm tích lũy", e);
        }
    }

    public int getTongSoKhachHang() {
        try {
            return repository.getTongSoKhachHang();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy tổng số khách hàng", e);
            throw new RuntimeException("Không thể lấy tổng số khách hàng", e);
        }
    }

    public List<KhachHang> getTopKhachHangTheoDiem(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        try {
            return repository.getTopKhachHangTheoDiem(soLuong);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy top khách hàng theo điểm: " + soLuong, e);
            throw new RuntimeException("Không thể lấy top khách hàng theo điểm", e);
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
        
        // Không validate NgayTao vì nó sẽ được tự động tạo
        if (khachHang.getDiemTichLuy() < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
    }

    private void validateSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        
        String cleanedSoDienThoai = soDienThoai.trim().replaceAll("\\s+", "");
        
        // Kiểm tra số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0 hoặc +84)
        if (!cleanedSoDienThoai.matches("^(0|\\+84)\\d{9,10}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ. Phải là 10-11 số và bắt đầu bằng 0 hoặc +84");
        }
    }
}