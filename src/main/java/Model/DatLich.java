package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DatLich {

    private Integer maLich;
    private Integer maKhachHang;
    private LocalDate ngayDat;
    private LocalTime gioDat;
    private Integer maDichVu;
    private String trangThai;
    private Integer maGiuong;
    private String ghiChu;

    // Constructor mặc định
    public DatLich() {
        this.ngayDat = LocalDate.now();
        this.trangThai = "Chờ";
    }

    // Constructor với thông tin cơ bản
    public DatLich(Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat) {
        this();
        this.maKhachHang = maKhachHang;
        this.ngayDat = ngayDat;
        this.gioDat = gioDat;
    }

    // Constructor với dịch vụ
    public DatLich(Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat, Integer maDichVu) {
        this(maKhachHang, ngayDat, gioDat);
        this.maDichVu = maDichVu;
    }

    // Constructor với giường
    public DatLich(Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat, Integer maGiuong, String ghiChu) {
        this(maKhachHang, ngayDat, gioDat);
        this.maGiuong = maGiuong;
        this.ghiChu = ghiChu;
    }

    // Constructor đầy đủ tham số (KHÔNG có khóa chính MaLich)
    public DatLich(Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat,
            Integer maDichVu, String trangThai, Integer maGiuong, String ghiChu) {
        this.maKhachHang = maKhachHang;
        this.ngayDat = ngayDat != null ? ngayDat : LocalDate.now();
        this.gioDat = gioDat;
        this.maDichVu = maDichVu;
        this.trangThai = trangThai != null ? trangThai : "Chờ";
        this.maGiuong = maGiuong;
        this.ghiChu = ghiChu;
    }

    public DatLich(Integer maLich, Integer maKhachHang, LocalDate ngayDat, LocalTime gioDat, Integer maDichVu, String trangThai, Integer maGiuong, String ghiChu) {
        this.maLich = maLich;
        this.maKhachHang = maKhachHang;
        this.ngayDat = ngayDat;
        this.gioDat = gioDat;
        this.maDichVu = maDichVu;
        this.trangThai = trangThai;
        this.maGiuong = maGiuong;
        this.ghiChu = ghiChu;
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

    public Integer getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(Integer maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai != null ? trangThai : "Chờ";
    }

    public Integer getMaGiuong() {
        return maGiuong;
    }

    public void setMaGiuong(Integer maGiuong) {
        this.maGiuong = maGiuong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Phương thức kiểm tra đặt lịch có hợp lệ không
    public boolean isValid() {
        return maKhachHang != null
                && ngayDat != null
                && gioDat != null;
    }

    // Phương thức kiểm tra có đặt dịch vụ không
    public boolean hasDichVu() {
        return maDichVu != null;
    }

    // Phương thức kiểm tra có đặt giường không
    public boolean hasGiuong() {
        return maGiuong != null;
    }

    // Phương thức kiểm tra có ghi chú không
    public boolean hasGhiChu() {
        return ghiChu != null && !ghiChu.trim().isEmpty();
    }

    // Phương thức kiểm tra trạng thái là "Chờ"
    public boolean isCho() {
        return "Chờ xác nhận".equals(trangThai);
    }

    // Phương thức kiểm tra trạng thái là "Đã xác nhận"
    public boolean isDaXacNhan() {
        return "Đã xác nhận".equals(trangThai);
    }

    // Phương thức kiểm tra trạng thái là "Đã hủy"
    public boolean isDaHuy() {
        return "Đã hủy".equals(trangThai);
    }

    // Phương thức kiểm tra trạng thái là "Hoàn thành"
    public boolean isHoanThanh() {
        return "Hoàn thành".equals(trangThai);
    }

    // Phương thức đánh dấu đã xác nhận
    public void markDaXacNhan() {
        this.trangThai = "Đã xác nhận";
    }

    // Phương thức đánh dấu đã hủy
    public void markDaHuy() {
        this.trangThai = "Đã hủy";
    }

    // Phương thức đánh dấu hoàn thành
    public void markHoanThanh() {
        this.trangThai = "Hoàn thành";
    }

    // Phương thức kiểm tra đặt lịch trong quá khứ
    public boolean isInPast() {
        if (ngayDat == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return ngayDat.isBefore(today)
                || (ngayDat.isEqual(today) && gioDat != null && gioDat.isBefore(LocalTime.now()));
    }

    // Phương thức kiểm tra đặt lịch trong tương lai
    public boolean isInFuture() {
        if (ngayDat == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return ngayDat.isAfter(today)
                || (ngayDat.isEqual(today) && gioDat != null && gioDat.isAfter(LocalTime.now()));
    }

    // Phương thức kiểm tra đặt lịch hôm nay
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

    // Phương thức kiểm tra xem đã qua giờ chưa
    public boolean isQuaGio() {
        if (ngayDat == null || gioDat == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(ngayDat, gioDat);

        return now.isAfter(bookingDateTime);
    }

@Override
public String toString() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    return "Lịch " + maLich + " - " + 
           (ngayDat != null ? ngayDat.format(dateFormatter) : "") + " " + 
           (gioDat != null ? gioDat.format(timeFormatter) : "") + " - " + 
           trangThai;
}
}
