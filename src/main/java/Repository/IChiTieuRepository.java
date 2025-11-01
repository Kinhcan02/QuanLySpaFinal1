package Repository;

import Model.ChiTieu;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public interface IChiTieuRepository {
    List<ChiTieu> getAllChiTieu();
    List<ChiTieu> getChiTieuByDateRange(LocalDate fromDate, LocalDate toDate);
    List<ChiTieu> getChiTieuByMonth(int month, int year);
    List<ChiTieu> getChiTieuByYear(int year);
    boolean addChiTieu(ChiTieu chiTieu);
    boolean updateChiTieu(ChiTieu chiTieu);
    boolean deleteChiTieu(int maChi);
    BigDecimal getTongChiTieu(LocalDate fromDate, LocalDate toDate);
    BigDecimal getTongChiTieuByNguyenLieu(LocalDate fromDate, LocalDate toDate);
}