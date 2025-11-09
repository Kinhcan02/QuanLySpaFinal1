package Service;

import Model.LuongNhanVien;
import Model.NhanVien;
import Repository.LuongNhanVienRepository;
import java.math.BigDecimal;
import java.util.List;

public class LuongNhanVienService {

    private final LuongNhanVienRepository repository;

    public LuongNhanVienService() {
        this.repository = new LuongNhanVienRepository();
    }

    public boolean tinhLuongThang(Integer thang, Integer nam) {
        try {
            System.out.println("🚀 BẮT ĐẦU TÍNH LƯƠNG THÁNG " + thang + "/" + nam);
            return repository.tinhLuongThang(thang, nam);
        } catch (Exception e) {
            System.err.println("💥 LỖI TÍNH LƯƠNG: " + e.getMessage());
            return false;
        }
    }

    public List<LuongNhanVien> getAllLuong() {
        return repository.getAll();
    }

    public List<LuongNhanVien> getLuongByThangNam(Integer thang, Integer nam) {
        return repository.getByThangNam(thang, nam);
    }

    // THÊM PHƯƠNG THỨC BỊ THIẾU
    public List<LuongNhanVien> getLuongByNhanVienThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        return repository.getByNhanVienThangNam(maNhanVien, thang, nam);
    }

    public boolean capNhatTrangThai(Integer maLuong, String trangThai) {
        return repository.capNhatTrangThai(maLuong, trangThai);
    }

    public boolean xoaLuong(Integer maLuong) {
        return repository.xoaLuong(maLuong);
    }

    public List<NhanVien> getAllNhanVien() {
        return repository.getAllNhanVien();
    }

    public List<LuongNhanVien> getLichSuTinhLuong(Integer thang, Integer nam) {
        return repository.getLichSuTinhLuong(thang, nam);
    }

   

}
