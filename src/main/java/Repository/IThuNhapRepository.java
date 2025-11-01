/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repository;

import Model.ThuNhap;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public interface IThuNhapRepository {
    List<ThuNhap> getAllThuNhap();
    List<ThuNhap> getThuNhapByDateRange(LocalDate fromDate, LocalDate toDate);
    List<ThuNhap> getThuNhapByMonth(int month, int year);
    List<ThuNhap> getThuNhapByYear(int year);
    boolean addThuNhap(ThuNhap thuNhap);
    boolean updateThuNhap(ThuNhap thuNhap);
    boolean deleteThuNhap(int maThu);
    BigDecimal getTongThuNhap(LocalDate fromDate, LocalDate toDate);
    BigDecimal getTongThuNhapByHoaDon(LocalDate fromDate, LocalDate toDate);
}