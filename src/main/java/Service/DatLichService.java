package Service;

import Model.DatLich;
import Model.DatLichChiTiet;
import Repository.DatLichRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DatLichService {

    private final DatLichRepository repository;

    public DatLichService() {
        this.repository = new DatLichRepository();
    }

    public List<DatLich> getAllDatLich() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách đặt lịch: " + e.getMessage(), e);
        }
    }

    public DatLich getDatLichById(int maLich) {
        try {
            return repository.getById(maLich);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin đặt lịch: " + e.getMessage(), e);
        }
    }

    public List<DatLich> getDatLichTheoNgay(LocalDate ngay) {
        try {
            return repository.getByNgay(ngay);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy đặt lịch theo ngày: " + e.getMessage(), e);
        }
    }

    public List<DatLich> getDatLichHomNay() {
        try {
            return repository.getByNgay(LocalDate.now());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy đặt lịch hôm nay: " + e.getMessage(), e);
        }
    }

    public boolean addDatLich(DatLich datLich) {
        try {
            return repository.insert(datLich);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm đặt lịch: " + e.getMessage(), e);
        }
    }

    public boolean updateDatLich(DatLich datLich) {
        try {
            return repository.update(datLich);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật đặt lịch: " + e.getMessage(), e);
        }
    }

    public boolean deleteDatLich(int maLich) {
        try {
            return repository.delete(maLich);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa đặt lịch: " + e.getMessage(), e);
        }
    }

    public boolean updateTrangThai(int maLich, String trangThai) {
        try {
            return repository.updateTrangThai(maLich, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái đặt lịch: " + e.getMessage(), e);
        }
    }

    public boolean isGiuongAvailable(Integer maGiuong, LocalDate ngayDat, LocalTime gioDat, Integer currentMaLich) {
        try {
            // Kiểm tra nếu maGiuong là null thì không cần kiểm tra
            if (maGiuong == null) {
                return true;
            }

            // Lấy tất cả lịch đặt của ngày đó
            List<DatLich> datLichList = repository.getByNgay(ngayDat);

            for (DatLich dl : datLichList) {
                // Bỏ qua lịch hiện tại đang sửa
                if (currentMaLich != null && dl.getMaLich().equals(currentMaLich)) {
                    continue;
                }

                // Kiểm tra nếu giường trùng và lịch chưa bị hủy
                if (maGiuong.equals(dl.getMaGiuong())
                        && !"Đã hủy".equals(dl.getTrangThai())
                        && !"Hoàn thành".equals(dl.getTrangThai())) {

                    // Kiểm tra xem có trùng giờ không
                    LocalTime gioBatDau = dl.getGioDat();
                    LocalTime gioKetThuc = gioBatDau.plusMinutes(dl.tinhTongThoiGian());

                    // Nếu giờ đặt mới nằm trong khoảng thời gian của lịch cũ
                    if ((gioDat.isAfter(gioBatDau) && gioDat.isBefore(gioKetThuc))
                            || gioDat.equals(gioBatDau)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra giường: " + e.getMessage(), e);
        }
    }
}
