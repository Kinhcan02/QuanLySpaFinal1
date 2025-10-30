package Model;

public class Giuong {

    private Integer maGiuong;
    private String soHieu;
    private String trangThai; // "Trống", "Đã đặt", "Đang sử dụng", "Bảo trì"
    private String ghiChu;  // Đổi từ moTa thành ghiChu

    // Constructor
    public Giuong() {
        this.trangThai = "Trống";
    }

    public Giuong(Integer maGiuong, String soHieu, String trangThai, String ghiChu) {
        this.maGiuong = maGiuong;
        this.soHieu = soHieu;
        this.trangThai = trangThai != null ? trangThai : "Trống";
        this.ghiChu = ghiChu;
    }

    // Getter và Setter
    public Integer getMaGiuong() {
        return maGiuong;
    }

    public void setMaGiuong(Integer maGiuong) {
        this.maGiuong = maGiuong;
    }

    public String getSoHieu() {
        return soHieu;
    }

    public void setSoHieu(String soHieu) {
        this.soHieu = soHieu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai != null ? trangThai : "Trống";
    }

    public String getGhiChu() {
        return ghiChu;
    }  // Đổi từ getMoTa thành getGhiChu

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }  // Đổi từ setMoTa thành setGhiChu

    // Phương thức kiểm tra trạng thái
    public boolean isTrong() {
        return "Trống".equals(trangThai);
    }

    public boolean isDaDat() {
        return "Đã đặt".equals(trangThai);
    }

    public boolean isDangSuDung() {
        return "Đang sử dụng".equals(trangThai);
    }

    public boolean isBaoTri() {
        return "Bảo trì".equals(trangThai);
    }

    // Phương thức chuyển trạng thái
    public void markTrong() {
        this.trangThai = "Trống";
    }

    public void markDaDat() {
        this.trangThai = "Đã đặt";
    }

    public void markDangSuDung() {
        this.trangThai = "Đang sử dụng";
    }

    public void markBaoTri() {
        this.trangThai = "Bảo trì";
    }

    @Override
    public String toString() {
        if (getMaGiuong() == null) {
            return "-- Chọn giường --";
        }
        return getSoHieu() + " - " + getTrangThai();
    }
}
