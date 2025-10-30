package Service;

import Model.DatLich;
import Model.DichVu;
import Repository.DatLichRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatLichService {

    private final DatLichRepository repository;
    private static final Logger logger = Logger.getLogger(DatLichService.class.getName());

    // Danh sách lắng nghe thông báo
    private List<ThongBaoListener> listeners = new ArrayList<>();

    public DatLichService() {
        this.repository = new DatLichRepository();
    }

    // Interface cho lắng nghe thông báo
    public interface ThongBaoListener {
        void onThongBaoSapToiGio(DatLich datLich);
    }

    public void addThongBaoListener(ThongBaoListener listener) {
        listeners.add(listener);
    }

    public void removeThongBaoListener(ThongBaoListener listener) {
        listeners.remove(listener);
    }

    // Kiểm tra và gửi thông báo
    public void kiemTraThongBao() {
        try {
            List<DatLich> tatCaDatLich = repository.getAll();
            for (DatLich datLich : tatCaDatLich) {
                if (datLich.isSapToiGio() && datLich.isDaXacNhan()) {
                    // Gửi thông báo đến tất cả listeners
                    for (ThongBaoListener listener : listeners) {
                        listener.onThongBaoSapToiGio(datLich);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra thông báo đặt lịch", e);
        }
    }

    public List<DatLich> getAllDatLich() {
        try {
            return repository.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách đặt lịch", e);
            throw new RuntimeException("Không thể lấy danh sách đặt lịch", e);
        }
    }

    // XÓA PHƯƠNG THỨC TRÙNG LẶP NÀY
    // public List<DatLich> getDatLichTheoNgay(LocalDate ngay) {
    //     try {
    //         List<DatLich> tatCaDatLich = repository.getAll();
    //         List<DatLich> ketQua = new ArrayList<>();
    //
    //         for (DatLich datLich : tatCaDatLich) {
    //             if (datLich.getNgayDat().equals(ngay)) {
    //                 ketQua.add(datLich);
    //             }
    //         }
    //         return ketQua;
    //     } catch (SQLException e) {
    //         logger.log(Level.SEVERE, "Lỗi khi lấy đặt lịch theo ngày: " + ngay, e);
    //         throw new RuntimeException("Không thể lấy đặt lịch theo ngày", e);
    //     }
    // }

    public List<DatLich> getDatLichHomNay() {
        return getDatLichTheoNgay(LocalDate.now());
    }

    public DatLich getDatLichById(int maLich) {
        try {
            DatLich result = repository.getById(maLich);
            if (result == null) {
                throw new RuntimeException("Đặt lịch với mã " + maLich + " không tồn tại");
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy đặt lịch theo mã: " + maLich, e);
            throw new RuntimeException("Không thể lấy thông tin đặt lịch", e);
        }
    }

    public boolean addDatLich(DatLich datLich) {
        validateDatLich(datLich);
        try {
            return repository.insert(datLich);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thêm đặt lịch", e);
            throw new RuntimeException("Không thể thêm đặt lịch", e);
        }
    }

    public boolean updateDatLich(DatLich datLich) {
        validateDatLich(datLich);
        if (datLich.getMaLich() <= 0) {
            throw new IllegalArgumentException("Mã đặt lịch không hợp lệ");
        }
        try {
            return repository.update(datLich);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật đặt lịch: " + datLich.getMaLich(), e);
            throw new RuntimeException("Không thể cập nhật đặt lịch", e);
        }
    }

    public boolean updateTrangThai(int maLich, String trangThai) {
        if (maLich <= 0) {
            throw new IllegalArgumentException("Mã đặt lịch không hợp lệ");
        }
        try {
            return repository.updateTrangThai(maLich, trangThai);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đặt lịch: " + maLich, e);
            throw new RuntimeException("Không thể cập nhật trạng thái đặt lịch", e);
        }
    }

    public boolean deleteDatLich(int maLich) {
        if (maLich <= 0) {
            throw new IllegalArgumentException("Mã đặt lịch không hợp lệ");
        }
        try {
            return repository.delete(maLich);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi xóa đặt lịch: " + maLich, e);
            throw new RuntimeException("Không thể xóa đặt lịch", e);
        }
    }

    // Kiểm tra giường có trống trong khoảng thời gian không
    public boolean isGiuongTrong(Integer maGiuong, LocalDate ngay, LocalTime gioBatDau, int thoiGianDichVu) {
        if (maGiuong == null) {
            return true;
        }

        try {
            List<DatLich> datLichTheoNgay = getDatLichTheoNgay(ngay);
            LocalTime gioKetThuc = gioBatDau.plusMinutes(thoiGianDichVu);

            for (DatLich datLich : datLichTheoNgay) {
                if (maGiuong.equals(datLich.getMaGiuong())
                        && !datLich.isDaHuy()
                        && datLich.isDaXacNhan()) {

                    LocalTime batDauHienTai = datLich.getGioDat();
                    // SỬA LẠI: Sử dụng phương thức tính tổng thời gian từ danh sách dịch vụ
                    LocalTime ketThucHienTai = batDauHienTai.plusMinutes(datLich.tinhTongThoiGian());

                    // Kiểm tra xung đột thời gian
                    if (gioBatDau.isBefore(ketThucHienTai) && gioKetThuc.isAfter(batDauHienTai)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi kiểm tra giường trống", e);
            return false;
        }
    }

    private int getThoiGianDichVu(Integer maDichVu) {
        if (maDichVu == null) {
            return 60;
        }
        try {
            DichVuService dichVuService = new DichVuService();
            DichVu dv = dichVuService.getDichVuById(maDichVu);
            return dv != null && dv.getThoiGian() != null ? dv.getThoiGian() : 60;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Không thể lấy thời gian dịch vụ, sử dụng mặc định 60 phút", e);
            return 60;
        }
    }

    private void validateDatLich(DatLich datLich) {
        if (datLich == null) {
            throw new IllegalArgumentException("Đặt lịch không được null");
        }
        if (datLich.getMaKhachHang() == null) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống");
        }
        if (datLich.getNgayDat() == null) {
            throw new IllegalArgumentException("Ngày đặt không được để trống");
        }
        if (datLich.getGioDat() == null) {
            throw new IllegalArgumentException("Giờ đặt không được để trống");
        }
        if (datLich.getNgayDat().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể đặt lịch trong quá khứ");
        }
    }

    // PHƯƠNG THỨC MỚI SỬ DỤNG REPOSITORY
    public List<DatLich> getDatLichTheoNgay(LocalDate ngay) {
        try {
            return repository.getByNgay(ngay);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy đặt lịch theo ngày: " + ngay, e);
            throw new RuntimeException("Không thể lấy đặt lịch theo ngày", e);
        }
    }
}