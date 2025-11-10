package Controller;

import View.ThongBaoView;
import Service.ThongBaoService;
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
    private Timer thongBaoTimer;
    private static final Logger logger = Logger.getLogger(ThongBaoController.class.getName());

    public ThongBaoController(ThongBaoView thongBaoView) {
        this.thongBaoView = thongBaoView;
        this.thongBaoService = new ThongBaoService();
        initController();
        setupThongBaoTimer();
    }

    private void initController() {
        thongBaoView.getBtnXemTatCa().addActionListener(e -> xemTatCaThongBao());
        thongBaoView.getBtnDanhDauDaDoc().addActionListener(e -> danhDauDaDoc());
        
        // Tải thông báo ngay khi khởi tạo
        kiemTraThongBaoMoi();
    }

    private void setupThongBaoTimer() {
        thongBaoTimer = new Timer(true); // Daemon thread
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
                        } else if ("CANH_BAO".equals(tb.getLoaiThongBao())) {
                            icon = "⚠️ ";
                        }
                        return icon + tb.getNoiDung() + " (" + tb.getThoiGian().toString() + ")";
                    })
                    .toArray(String[]::new);
                
                // Cập nhật danh sách thông báo
                thongBaoView.capNhatDanhSachThongBao(thongBaoArray);
                
                // Hiển thị badge
                thongBaoView.hienThiBadge(thongBaoMoi.size());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Lỗi khi kiểm tra thông báo", e);
                // Hiển thị thông báo lỗi trong danh sách
                thongBaoView.hienThiThongBao("⚠️ Lỗi khi tải thông báo: " + e.getMessage());
            }
        });
    }



    private void xemTatCaThongBao() {
        try {
            List<ThongBao> tatCaThongBao = thongBaoService.getAllThongBao();
            
            // Tạo dialog để hiển thị tất cả thông báo
            JDialog dialog = new JDialog();
            dialog.setTitle("Tất Cả Thông Báo");
            dialog.setModal(true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(thongBaoView);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
                table.setEnabled(false); // Chỉ để xem
                
                JScrollPane scrollPane = new JScrollPane(table);
                panel.add(scrollPane, BorderLayout.CENTER);
            }
            
            // Nút đóng
            JButton btnClose = new JButton("Đóng");
            btnClose.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(btnClose);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.setVisible(true);
            
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
            case "CANH_BAO": return "⚠️ Cảnh báo";
            default: return "ℹ️ Hệ thống";
        }
    }

    private void danhDauDaDoc() {
        try {
            // Gọi service để đánh dấu đã đọc (nếu có chức năng này)
            // thongBaoService.danhDauDaDoc();
            
            // Xóa thông báo khỏi view
            thongBaoView.xoaTatCaThongBao();
            
            // Hiển thị thông báo xác nhận
            thongBaoView.hienThiThongBao("✓ Đã đánh dấu tất cả thông báo là đã đọc");
            
            JOptionPane.showMessageDialog(thongBaoView, 
                "Đã đánh dấu tất cả thông báo là đã đọc", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi đánh dấu đã đọc", e);
            JOptionPane.showMessageDialog(thongBaoView, 
                "Lỗi khi đánh dấu đã đọc: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void dungTimer() {
        if (thongBaoTimer != null) {
            thongBaoTimer.cancel();
            thongBaoTimer = null;
        }
    }
    
    // Phương thức để dọn dẹp tài nguyên
    public void cleanup() {
        dungTimer();
    }
}