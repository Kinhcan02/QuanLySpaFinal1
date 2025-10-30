package Service;

import Model.Giuong;
import Model.DatLich;
import Model.DichVu;
import Repository.GiuongRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class GiuongService {
    private final GiuongRepository repository;
    private final DatLichService datLichService;

    public GiuongService() {
        this.repository = new GiuongRepository();
        this.datLichService = new DatLichService();
    }

    // Lấy tất cả giường với trạng thái cập nhật
    public List<Giuong> getAllGiuongWithStatus() {
        try {
            List<Giuong> giuongs = repository.getAll();
            
            // Cập nhật trạng thái cho từng giường
            for (Giuong giuong : giuongs) {
                updateGiuongStatus(giuong);
            }
            
            return giuongs;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách giường: " + e.getMessage(), e);
        }
    }

    // Cập nhật trạng thái giường dựa trên lịch đặt
    public void updateGiuongStatus(Giuong giuong) {
        if (giuong == null || giuong.getMaGiuong() == null) return;

        try {
            // Nếu giường đang bảo trì, giữ nguyên trạng thái
            if (giuong.isBaoTri()) {
                return;
            }

            // Kiểm tra nếu giường đang được sử dụng (có lịch đang diễn ra)
            if (isGiuongDangSuDung(giuong.getMaGiuong())) {
                if (!giuong.isDangSuDung()) {
                    giuong.markDangSuDung();
                    repository.updateTrangThai(giuong.getMaGiuong(), "Đang sử dụng");
                }
            }
            // Kiểm tra nếu giường đã được đặt cho hôm nay
            else if (isGiuongDaDatHomNay(giuong.getMaGiuong())) {
                if (!giuong.isDaDat()) {
                    giuong.markDaDat();
                    repository.updateTrangThai(giuong.getMaGiuong(), "Đã đặt");
                }
            }
            // Nếu không có gì thì trở về trạng thái trống
            else if (!giuong.isTrong()) {
                giuong.markTrong();
                repository.updateTrangThai(giuong.getMaGiuong(), "Trống");
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái giường: " + e.getMessage());
        }
    }

    // Kiểm tra giường đang được sử dụng
    private boolean isGiuongDangSuDung(Integer maGiuong) {
        if (maGiuong == null) return false;
        
        try {
            List<DatLich> datLichHomNay = datLichService.getDatLichHomNay();
            LocalTime now = LocalTime.now();
            
            for (DatLich datLich : datLichHomNay) {
                if (maGiuong.equals(datLich.getMaGiuong()) && 
                    datLich.isDaXacNhan() && 
                    !datLich.isDaHuy() && 
                    !datLich.isHoanThanh()) {
                    
                    // Kiểm tra xem có đang trong giờ sử dụng không
                    LocalTime gioBatDau = datLich.getGioDat();
                    LocalTime gioKetThuc = gioBatDau.plusMinutes(getThoiGianDichVu(datLich.getMaDichVu()));
                    
                    if (now.isAfter(gioBatDau) && now.isBefore(gioKetThuc)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Kiểm tra giường đã được đặt cho hôm nay
    private boolean isGiuongDaDatHomNay(Integer maGiuong) {
        if (maGiuong == null) return false;
        
        try {
            List<DatLich> datLichHomNay = datLichService.getDatLichHomNay();
            LocalTime now = LocalTime.now();
            
            for (DatLich datLich : datLichHomNay) {
                if (maGiuong.equals(datLich.getMaGiuong()) && 
                    datLich.isDaXacNhan() && 
                    !datLich.isDaHuy() && 
                    !datLich.isHoanThanh() &&
                    !datLich.isQuaGio()) {
                    
                    LocalTime gioBatDau = datLich.getGioDat();
                    LocalTime gioKetThuc = gioBatDau.plusMinutes(getThoiGianDichVu(datLich.getMaDichVu()));
                    
                    // Giường đã được đặt nhưng chưa đến giờ sử dụng hoặc đang trong tương lai
                    if (now.isBefore(gioBatDau) || (now.isAfter(gioBatDau) && now.isBefore(gioKetThuc))) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private int getThoiGianDichVu(Integer maDichVu) {
        if (maDichVu == null) return 60;
        try {
            DichVuService dichVuService = new DichVuService();
            DichVu dv = dichVuService.getDichVuById(maDichVu);
            return dv != null && dv.getThoiGian() != null ? dv.getThoiGian() : 60;
        } catch (Exception e) {
            return 60;
        }
    }

    // Các phương thức cơ bản
    public List<Giuong> getAllGiuong() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách giường: " + e.getMessage(), e);
        }
    }

    public Giuong getGiuongById(Integer maGiuong) {
        try {
            return repository.getById(maGiuong);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin giường: " + e.getMessage(), e);
        }
    }

    public boolean addGiuong(Giuong giuong) {
        try {
            return repository.insert(giuong);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm giường: " + e.getMessage(), e);
        }
    }

    public boolean updateGiuong(Giuong giuong) {
        try {
            return repository.update(giuong);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật giường: " + e.getMessage(), e);
        }
    }

    public boolean deleteGiuong(Integer maGiuong) {
        try {
            return repository.delete(maGiuong);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa giường: " + e.getMessage(), e);
        }
    }

    // Các phương thức lọc theo trạng thái
    public List<Giuong> getGiuongTrong() {
        try {
            return repository.getGiuongTrong();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giường trống: " + e.getMessage(), e);
        }
    }

    public List<Giuong> getGiuongDaDat() {
        try {
            return repository.getGiuongDaDat();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giường đã đặt: " + e.getMessage(), e);
        }
    }

    public List<Giuong> getGiuongDangSuDung() {
        try {
            return repository.getGiuongDangSuDung();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giường đang sử dụng: " + e.getMessage(), e);
        }
    }

    public List<Giuong> getGiuongBaoTri() {
        try {
            return repository.getGiuongBaoTri();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giường bảo trì: " + e.getMessage(), e);
        }
    }
}