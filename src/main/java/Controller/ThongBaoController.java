package Controller;

import View.ThongBaoView;
import Service.ThongBaoService;
import Service.DatLichService;
import Model.ThongBao;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThongBaoController {
    private ThongBaoView thongBaoView;
    private ThongBaoService thongBaoService;
    private DatLichService datLichService;
    private Timer thongBaoTimer;
    private static final Logger logger = Logger.getLogger(ThongBaoController.class.getName());

    public ThongBaoController(ThongBaoView thongBaoView) {
        this.thongBaoView = thongBaoView;
        this.thongBaoService = new ThongBaoService();
        this.datLichService = new DatLichService();
        initController();
        setupThongBaoTimer();
    }

    private void initController() {
        thongBaoView.getBtnDong().addActionListener(e -> dongThongBao());
        thongBaoView.getBtnXemTatCa().addActionListener(e -> xemTatCaThongBao());
        thongBaoView.getBtnDanhDauDaDoc().addActionListener(e -> danhDauDaDoc());
    }

    private void setupThongBaoTimer() {
        thongBaoTimer = new Timer();
        thongBaoTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                kiemTraThongBaoMoi();
            }
        }, 0, 30000); // 30 giây
    }

    private void kiemTraThongBaoMoi() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<ThongBao> thongBaoMoi = thongBaoService.getAllThongBao();
                
                if (thongBaoMoi.isEmpty()) {
                    thongBaoView.anBadge();
                    return;
                }
                
                // Chuyển đổi sang mảng String để hiển thị
                String[] thongBaoArray = thongBaoMoi.stream()
                    .map(tb -> {
                        String icon = "";
                        if ("SINH_NHAT".equals(tb.getLoaiThongBao())) {
                            icon = "🎂 ";
                        } else if ("DAT_LICH".equals(tb.getLoaiThongBao())) {
                            icon = "⏰ ";
                        }
                        return icon + tb.getNoiDung();
                    })
                    .toArray(String[]::new);
                
                // Cập nhật danh sách thông báo
                thongBaoView.capNhatDanhSachThongBao(thongBaoArray);
                
                // Hiển thị badge
                thongBaoView.hienThiBadge(thongBaoMoi.size());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Lỗi khi kiểm tra thông báo", e);
                // Hiển thị thông báo lỗi nhẹ nhàng
                thongBaoView.hienThiThongBao("⚠️ Lỗi khi tải thông báo: " + e.getMessage());
            }
        });
    }

    // Các phương thức khác giữ nguyên...
    public void hienThiThongBao() {
        thongBaoView.hienThi();
        thongBaoView.anBadge();
    }

    private void dongThongBao() {
        thongBaoView.anDi();
    }

    private void xemTatCaThongBao() {
        try {
            List<ThongBao> tatCaThongBao = thongBaoService.getAllThongBao();
            
            JInternalFrame internalFrame = new JInternalFrame(
                    "Tất Cả Thông Báo",
                    true, true, true, true
            );

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(0x8C, 0xC9, 0x80));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            if (tatCaThongBao.isEmpty()) {
                JLabel lblEmpty = new JLabel("Không có thông báo nào", JLabel.CENTER);
                lblEmpty.setFont(new Font("Arial", Font.BOLD, 16));
                panel.add(lblEmpty, BorderLayout.CENTER);
            } else {
                // Tạo bảng thông báo chi tiết
                String[] columnNames = {"Loại", "Nội dung", "Thời gian"};
                Object[][] data = new Object[tatCaThongBao.size()][3];
                
                for (int i = 0; i < tatCaThongBao.size(); i++) {
                    ThongBao tb = tatCaThongBao.get(i);
                    data[i][0] = getTenLoaiThongBao(tb.getLoaiThongBao());
                    data[i][1] = tb.getNoiDung();
                    data[i][2] = tb.getThoiGian().toString();
                }
                
                JTable table = new JTable(data, columnNames);
                table.setFont(new Font("Arial", Font.PLAIN, 12));
                table.setRowHeight(25);
                
                JScrollPane scrollPane = new JScrollPane(table);
                panel.add(scrollPane, BorderLayout.CENTER);
            }

            internalFrame.setContentPane(panel);
            internalFrame.setSize(600, 400);
            thongBaoView.showInternalFrame(internalFrame);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi xem tất cả thông báo", e);
            JOptionPane.showMessageDialog(thongBaoView, 
                "Lỗi khi tải thông báo: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTenLoaiThongBao(String loai) {
        switch (loai) {
            case "SINH_NHAT": return "🎂 Sinh nhật";
            case "DAT_LICH": return "⏰ Lịch hẹn";
            default: return "ℹ️ Hệ thống";
        }
    }

    private void danhDauDaDoc() {
        thongBaoView.xoaTatCaThongBao();
        thongBaoView.hienThiThongBao("Đã đánh dấu tất cả thông báo là đã đọc");
        JOptionPane.showMessageDialog(thongBaoView, 
            "Đã đánh dấu tất cả thông báo là đã đọc", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void dungTimer() {
        if (thongBaoTimer != null) {
            thongBaoTimer.cancel();
        }
    }
}