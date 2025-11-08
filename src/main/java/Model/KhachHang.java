package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class KhachHang {

    private Integer maKhachHang;
    private String hoTen;
    private LocalDate ngaySinh;
    private String loaiKhach;
    private String soDienThoai;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat; // THÊM TRƯỜNG MỚI
    private Integer diemTichLuy;

    // Constructor mặc định
    public KhachHang() {
        this.ngayTao = LocalDateTime.now();
        this.diemTichLuy = 0;
    }

    // Constructor với họ tên
    public KhachHang(String hoTen) {
        this();
        this.hoTen = hoTen;
    }

    // Constructor với họ tên và số điện thoại
    public KhachHang(String hoTen, String soDienThoai) {
        this();
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    // Constructor với họ tên, số điện thoại và loại khách
    public KhachHang(String hoTen, String soDienThoai, String loaiKhach) {
        this();
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.loaiKhach = loaiKhach;
    }

    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaKhachHang)
    public KhachHang(String hoTen, LocalDate ngaySinh, String loaiKhach,
            String soDienThoai, String ghiChu, LocalDateTime ngayTao, Integer diemTichLuy) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao != null ? ngayTao : LocalDateTime.now();
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    public KhachHang(Integer maKhachHang, String hoTen, LocalDate ngaySinh, String loaiKhach, 
                    String soDienThoai, String ghiChu, LocalDateTime ngayTao, Integer diemTichLuy) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    // Constructor mới với ngayCapNhat
    public KhachHang(Integer maKhachHang, String hoTen, LocalDate ngaySinh, String loaiKhach, 
                    String soDienThoai, String ghiChu, LocalDateTime ngayTao, 
                    LocalDateTime ngayCapNhat, Integer diemTichLuy) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    // Constructor không có ngày tạo (tự động set ngày hiện tại)
    public KhachHang(String hoTen, LocalDate ngaySinh, String loaiKhach,
            String soDienThoai, String ghiChu) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.loaiKhach = loaiKhach;
        this.soDienThoai = soDienThoai;
        this.ghiChu = ghiChu;
        this.ngayTao = LocalDateTime.now();
        this.diemTichLuy = 0;
    }

    // Getter và Setter
    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getLoaiKhach() {
        return loaiKhach;
    }

    public void setLoaiKhach(String loaiKhach) {
        this.loaiKhach = loaiKhach;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao != null ? ngayTao : LocalDateTime.now();
    }

    // THÊM GETTER VÀ SETTER CHO NGAYCAPNHAT
    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public Integer getDiemTichLuy() {
        return diemTichLuy != null ? diemTichLuy : 0;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy != null ? diemTichLuy : 0;
    }

    // PHƯƠNG THỨC MỚI - Cập nhật thời gian sửa đổi
    public void capNhatThoiGian() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    // PHƯƠNG THỨC MỚI - Thêm điểm tích lũy
    public void themDiemTichLuy(int diemThem) {
        if (diemThem > 0) {
            this.diemTichLuy = (this.diemTichLuy != null ? this.diemTichLuy : 0) + diemThem;
            capNhatThoiGian();
        }
    }

    // PHƯƠNG THỨC MỚI - Sử dụng điểm tích lũy
    public boolean suDungDiemTichLuy(int diemSuDung) {
        if (diemSuDung > 0 && this.diemTichLuy >= diemSuDung) {
            this.diemTichLuy -= diemSuDung;
            capNhatThoiGian();
            return true;
        }
        return false;
    }

    // PHƯƠNG THỨC MỚI - Reset điểm tích lũy
    public void resetDiemTichLuy() {
        this.diemTichLuy = 0;
        capNhatThoiGian();
    }

    // PHƯƠNG THỨC MỚI - Nâng cấp loại khách hàng dựa trên điểm
    public void nangCapLoaiKhach() {
        int diem = getDiemTichLuy();
        if (diem >= 1000) {
            this.loaiKhach = "VIP";
        } else if (diem >= 500) {
            this.loaiKhach = "Thân thiết";
        } else if (diem >= 100) {
            this.loaiKhach = "Thường xuyên";
        } else {
            this.loaiKhach = "Mới";
        }
        capNhatThoiGian();
    }

    // PHƯƠNG THỨC MỚI - Kiểm tra tính hợp lệ của số điện thoại
    public boolean isSoDienThoaiHopLe() {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
        // Kiểm tra số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
        String sdt = soDienThoai.trim().replaceAll("\\s+", "");
        return sdt.matches("^(0|\\+84)(\\d{9,10})$");
    }

    // PHƯƠNG THỨC MỚI - Định dạng số điện thoại
    public String getSoDienThoaiFormatted() {
        if (!isSoDienThoaiHopLe()) {
            return soDienThoai;
        }
        String sdt = soDienThoai.trim().replaceAll("\\s+", "");
        if (sdt.startsWith("+84")) {
            sdt = "0" + sdt.substring(3);
        }
        // Định dạng: 0123 456 789
        if (sdt.length() == 10) {
            return sdt.replaceFirst("(\\d{4})(\\d{3})(\\d{3})", "$1 $2 $3");
        } else if (sdt.length() == 11) {
            return sdt.replaceFirst("(\\d{4})(\\d{3})(\\d{4})", "$1 $2 $3");
        }
        return sdt;
    }

    // PHƯƠNG THỨC MỚI - Tính tuổi chính xác
    public Integer getTuoiChinhXac() {
        if (ngaySinh == null) {
            return null;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    // PHƯƠNG THỨC MỚI - Kiểm tra có phải sinh nhật trong tháng này không
    public boolean isSinhNhatTrongThang() {
        if (ngaySinh == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return ngaySinh.getMonth() == now.getMonth();
    }

    // PHƯƠNG THỨC MỚI - Sao chép thông tin từ khách hàng khác
    public void saoChepThongTin(KhachHang khachHangNguon) {
        if (khachHangNguon != null) {
            this.hoTen = khachHangNguon.hoTen;
            this.ngaySinh = khachHangNguon.ngaySinh;
            this.loaiKhach = khachHangNguon.loaiKhach;
            this.soDienThoai = khachHangNguon.soDienThoai;
            this.ghiChu = khachHangNguon.ghiChu;
            this.diemTichLuy = khachHangNguon.diemTichLuy;
            capNhatThoiGian();
        }
    }

    // PHƯƠNG THỨC MỚI - Reset thông tin khách hàng
    public void reset() {
        this.hoTen = null;
        this.ngaySinh = null;
        this.loaiKhach = null;
        this.soDienThoai = null;
        this.ghiChu = null;
        this.diemTichLuy = 0;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    // PHƯƠNG THỨC MỚI - Clone khách hàng
    public KhachHang clone() {
        return new KhachHang(
            this.maKhachHang,
            this.hoTen,
            this.ngaySinh,
            this.loaiKhach,
            this.soDienThoai,
            this.ghiChu,
            this.ngayTao,
            this.ngayCapNhat,
            this.diemTichLuy
        );
    }

    // Các phương thức hiện có (giữ nguyên)
    public boolean isValid() {
        return hoTen != null && !hoTen.trim().isEmpty();
    }

    public boolean hasSoDienThoai() {
        return soDienThoai != null && !soDienThoai.trim().isEmpty();
    }

    public boolean hasNgaySinh() {
        return ngaySinh != null;
    }

    public boolean hasLoaiKhach() {
        return loaiKhach != null && !loaiKhach.trim().isEmpty();
    }

    public boolean hasGhiChu() {
        return ghiChu != null && !ghiChu.trim().isEmpty();
    }

    public Integer getTuoi() {
        if (ngaySinh == null) {
            return null;
        }
        return LocalDate.now().getYear() - ngaySinh.getYear();
    }

    public boolean isKhachThanThiet() {
        return "Thân thiết".equals(loaiKhach);
    }

    public boolean isKhachThuongXuyen() {
        return "Thường xuyên".equals(loaiKhach);
    }

    public boolean isKhachMoi() {
        if (ngayTao == null) {
            return false;
        }
        return ngayTao.isAfter(LocalDateTime.now().minusDays(30));
    }

    @Override
    public String toString() {
        if (getMaKhachHang() == null) {
            return "-- Chọn khách hàng --";
        }
        return getHoTen() + " - " + getSoDienThoaiFormatted();
    }

    // PHƯƠNG THỨC MỚI - Hiển thị thông tin chi tiết
    public String toDetailedString() {
        return String.format("Mã KH: %d | Họ tên: %s | SĐT: %s | Loại: %s | Điểm: %d",
            maKhachHang != null ? maKhachHang : 0,
            hoTen != null ? hoTen : "N/A",
            getSoDienThoaiFormatted(),
            loaiKhach != null ? loaiKhach : "N/A",
            diemTichLuy != null ? diemTichLuy : 0
        );
    }
}