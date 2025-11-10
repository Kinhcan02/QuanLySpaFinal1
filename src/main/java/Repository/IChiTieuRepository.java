package Repository;

import Model.ChiTieu;
import java.util.List;
import java.math.BigDecimal;

public interface IChiTieuRepository {
    List<ChiTieu> getAllChiTieu();
    List<ChiTieu> getChiTieuByThangNam(int thang, int nam);
    List<ChiTieu> getChiTieuByNam(int nam);
    ChiTieu getChiTieuById(int maChi);
    boolean addChiTieu(ChiTieu chiTieu);
    boolean updateChiTieu(ChiTieu chiTieu);
    boolean deleteChiTieu(int maChi);
    BigDecimal getTongChiTieuByThangNam(int thang, int nam);
    BigDecimal getTongChiTieuByLoai(int thang, int nam, String loaiChi);
    
    // Phương thức mới để lấy tất cả chi phí bao gồm cả nguyên liệu
    List<ChiTieu> getAllChiTieuWithNguyenLieu(int thang, int nam);
}