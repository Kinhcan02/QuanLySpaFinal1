package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatLich {

    private Integer maLich;
    private Integer maKhachHang;
    private LocalDate ngayDat;
    private LocalTime gioDat;
    private String trangThai;
    private Integer maGiuong;
    private Integer thoiGianDuKien;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Integer maNhanVienTao;
    private Integer soLuongNguoi; // Thêm trường số lượng người

    // Danh sách dịch vụ chi tiết
    private List<DatLichChiTiet> danhSachDichVu;

    // Thêm trường tongThoiGian để lưu tổng thời gian tính từ database
    private Integer tongThoiGian;

    public DatLich() {
        this.ngayDat = LocalDate.now();
        this.trangThai = "Chờ xác nhận";
        this.danhSachDichVu = new ArrayList<>();
        this.soLuongNguoi = 1; // Mặc định 1 người
        this.tongThoiGian = 0;
    }

    // Constructor đầy đủ - THÊM soLuongNguoi
    public DatLich(Integer maLich, Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat,
            String trangThai, Integer maGiuong, Integer thoiGianDuKien,
            String ghiChu, LocalDateTime ngayTao, LocalDateTime ngayCapNhat,
            Integer maNhanVienTao, Integer soLuongNguoi) {
        this.maLich = maLich;
        this.maKhachHang = maKhachHang;
        this.ngayDat = ngayDat;
        this.gioDat = gioDat;
        this.trangThai = trangThai != null ? trangThai : "Chờ xác nhận";
        this.maGiuong = maGiuong;
        this.thoiGianDuKien = thoiGianDuKien;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.maNhanVienTao = maNhanVienTao;
        this.soLuongNguoi = soLuongNguoi != null ? soLuongNguoi : 1;
        this.danhSachDichVu = new ArrayList<>();
        this.tongThoiGian = thoiGianDuKien != null ? thoiGianDuKien : 0;
    }

    // Getter và Setter cho tongThoiGian
    public Integer getTongThoiGian() {
        return tongThoiGian;
    }

    public void setTongThoiGian(Integer tongThoiGian) {
        this.tongThoiGian = tongThoiGian != null ? tongThoiGian : 0;
    }

    // Getter và Setter cho soLuongNguoi
    public Integer getSoLuongNguoi() {
        return soLuongNguoi;
    }

    public void setSoLuongNguoi(Integer soLuongNguoi) {
        this.soLuongNguoi = soLuongNguoi != null ? soLuongNguoi : 1;
    }

    // Getter và Setter
    public Integer getMaLich() {
        return maLich;
    }

    public void setMaLich(Integer maLich) {
        this.maLich = maLich;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat != null ? ngayDat : LocalDate.now();
    }

    public LocalTime getGioDat() {
        return gioDat;
    }

    public void setGioDat(LocalTime gioDat) {
        this.gioDat = gioDat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai != null ? trangThai : "Chờ xác nhận";
    }

    public Integer getMaGiuong() {
        return maGiuong;
    }

    public void setMaGiuong(Integer maGiuong) {
        this.maGiuong = maGiuong;
    }

    public Integer getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(Integer thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
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
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public Integer getMaNhanVienTao() {
        return maNhanVienTao;
    }

    public void setMaNhanVienTao(Integer maNhanVienTao) {
        this.maNhanVienTao = maNhanVienTao;
    }

    public List<DatLichChiTiet> getDanhSachDichVu() {
        return danhSachDichVu;
    }

    public void setDanhSachDichVu(List<DatLichChiTiet> danhSachDichVu) {
        this.danhSachDichVu = danhSachDichVu;
    }

    // Phương thức tiện ích
    public boolean isValid() {
        return maKhachHang != null && ngayDat != null && gioDat != null;
    }

    public boolean hasGiuong() {
        return maGiuong != null;
    }

    public boolean hasDichVu() {
        return danhSachDichVu != null && !danhSachDichVu.isEmpty();
    }

    public boolean isCho() {
        return "Chờ xác nhận".equals(trangThai);
    }

    public boolean isDaXacNhan() {
        return "Đã xác nhận".equals(trangThai);
    }

    public boolean isDaHuy() {
        return "Đã hủy".equals(trangThai);
    }

    public boolean isHoanThanh() {
        return "Hoàn thành".equals(trangThai);
    }

    public boolean isDangThucHien() {
        return "Đang thực hiện".equals(trangThai);
    }

    public void markDaXacNhan() {
        this.trangThai = "Đã xác nhận";
    }

    public void markDaHuy() {
        this.trangThai = "Đã hủy";
    }

    public void markHoanThanh() {
        this.trangThai = "Hoàn thành";
    }

    public void markDangThucHien() {
        this.trangThai = "Đang thực hiện";
    }

    public boolean isInPast() {
        if (ngayDat == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return ngayDat.isBefore(today)
                || (ngayDat.isEqual(today) && gioDat != null && gioDat.isBefore(LocalTime.now()));
    }

    public boolean isInFuture() {
        if (ngayDat == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return ngayDat.isAfter(today)
                || (ngayDat.isEqual(today) && gioDat != null && gioDat.isAfter(LocalTime.now()));
    }

    public boolean isToday() {
        return ngayDat != null && ngayDat.equals(LocalDate.now());
    }

    public boolean isSapToiGio() {
        if (ngayDat == null || gioDat == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(ngayDat, gioDat);
        LocalDateTime reminderTime = bookingDateTime.minusMinutes(10);
        return now.isAfter(reminderTime) && now.isBefore(bookingDateTime);
    }

    public boolean isQuaGio() {
        if (ngayDat == null || gioDat == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(ngayDat, gioDat);
        return now.isAfter(bookingDateTime);
    }

    // Tính tổng thời gian từ danh sách dịch vụ
    public int tinhTongThoiGian() {
        if (danhSachDichVu == null || danhSachDichVu.isEmpty()) {
            return 60; // Mặc định 60 phút
        }
        return danhSachDichVu.stream()
                .mapToInt(ct -> ct.getDichVu() != null && ct.getDichVu().getThoiGian() != null
                ? ct.getDichVu().getThoiGian() : 0)
                .sum();
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return "Lịch " + maLich + " - "
                + (ngayDat != null ? ngayDat.format(dateFormatter) : "") + " "
                + (gioDat != null ? gioDat.format(timeFormatter) : "") + " - " + trangThai;
    }
}