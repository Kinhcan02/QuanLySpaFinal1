package Model;

public class TaiKhoan {

    private int maTaiKhoan;
    private String tenDangNhap;
    private String matKhauHash;
    private String vaiTro;
    private int maNhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(int maTaiKhoan, String tenDangNhap, String matKhauHash, String vaiTro, int maNhanVien) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.vaiTro = vaiTro;
        this.maNhanVien = maNhanVien;
    }

    // Getters and Setters
    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    public void setMatKhauHash(String matKhauHash) {
        this.matKhauHash = matKhauHash;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
}
