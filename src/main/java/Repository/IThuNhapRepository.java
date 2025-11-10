package Repository;

import Model.ThuNhap;
import java.util.List;
import java.math.BigDecimal;

public interface IThuNhapRepository {
    List<ThuNhap> getAllThuNhap();
    List<ThuNhap> getThuNhapByThangNam(int thang, int nam);
    ThuNhap getThuNhapById(int maThu);
    boolean addThuNhap(ThuNhap thuNhap);
    boolean updateThuNhap(ThuNhap thuNhap);
    boolean deleteThuNhap(int maThu);
    BigDecimal getTongDoanhThuThang(int thang, int nam);
    BigDecimal getTongLuongThang(int thang, int nam);
    List<ThuNhap> getThuNhapByNam(int nam);
    BigDecimal getTongThuNhapByNam(int nam);
}