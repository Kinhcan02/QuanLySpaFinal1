package Service;

import Model.ThongBao;
import Repository.ThongBaoRepository;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThongBaoService {
    private final ThongBaoRepository repository;
    private static final Logger logger = Logger.getLogger(ThongBaoService.class.getName());

    public ThongBaoService() {
        this.repository = new ThongBaoRepository();
    }

    public List<ThongBao> getThongBaoSinhNhat() {
        try {
            return repository.getThongBaoSinhNhat();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông báo sinh nhật", e);
            throw new RuntimeException("Không thể lấy thông báo sinh nhật", e);
        }
    }

    public List<ThongBao> getThongBaoDatLichSapToi() {
        try {
            return repository.getThongBaoDatLichSapToi();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thông báo đặt lịch, sử dụng phương thức thay thế", e);
            // Thử sử dụng phương thức thay thế
            return repository.getThongBaoDatLichSapToiAlternative();
        }
    }

    public List<ThongBao> getAllThongBao() {
        try {
            List<ThongBao> thongBaoSinhNhat = getThongBaoSinhNhat();
            List<ThongBao> thongBaoDatLich = getThongBaoDatLichSapToi();
            
            thongBaoSinhNhat.addAll(thongBaoDatLich);
            return thongBaoSinhNhat;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy tất cả thông báo", e);
            // Trả về danh sách rỗng thay vì throw exception để tránh crash ứng dụng
            return java.util.Collections.emptyList();
        }
    }

    public int getSoLuongThongBaoMoi() {
        try {
            return getAllThongBao().size();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Lỗi khi đếm thông báo mới", e);
            return 0;
        }
    }
}