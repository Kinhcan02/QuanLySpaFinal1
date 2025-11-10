package Controller;

import View.MainView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class MainViewController implements ActionListener {

    private MainView mainView;

    // Màu sắc giống QuanLyDichVuController
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public MainViewController(MainView mainView) {
        this.mainView = mainView;
        // Thêm WindowListener để xử lý nút "X" trên cửa sổ chính
        setupWindowClosingHandler();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == mainView.getBtnThongBao()) {
            mainView.showThongBao();
        } else if (source == mainView.getBtnDatLich()) {
            mainView.showQuanLyDatLich();
        } else if (source == mainView.getBtnDatDichVu()) {
            mainView.showDatDichVu();
        } else if (source == mainView.getBtnQuanLyCaLam()) {
            mainView.showQuanLyCaLam();
        } else if (source == mainView.getBtnQuanLyDichVu()) {
            mainView.showQuanLyDichVu();
        } else if (source == mainView.getBtnQuanLyNhanVien()) {
            mainView.showQuanLyNhanVien();
        } else if (source == mainView.getBtnQuanLyKhachHang()) {
            mainView.showQuanLyKhachHang();
        } else if (source == mainView.getBtnThongKe()) {
            mainView.showThongKe();
        } else if (source == mainView.getBtnQuanLyTaiKhoan()) {
            mainView.showQuanLyTaiKhoan();
        } else if (source == mainView.getBtnThoat()) {
            xacNhanThoatChuongTrinh();
        } else if (source == mainView.getBtnQuanLyThuChi()) {
            mainView.showQuanLyThuChi();
        } else if (source == mainView.getBtnDangXuat()) {
            xuLyDangXuat();
        } else if (source == mainView.getBtnQuanLyLuong()) {
            mainView.showLuong();
        }
    }

    // THIẾT LẬP XỬ LÝ NÚT "X" TRÊN CỬA SỔ CHÍNH
    private void setupWindowClosingHandler() {
        Window mainWindow = SwingUtilities.getWindowAncestor(mainView);
        if (mainWindow != null) {
            // Đặt chế độ đóng cửa sổ là DO_NOTHING_ON_CLOSE để chặn hành vi mặc định
            if (mainWindow instanceof JFrame) {
                ((JFrame) mainWindow).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            } else if (mainWindow instanceof JDialog) {
                ((JDialog) mainWindow).setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            }

            // Thêm WindowListener để xử lý nút "X"
            mainWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Khi nhấn nút "X" trên title bar, hiển thị dialog xác nhận custom
                    xacNhanThoatChuongTrinh();
                }
            });
        }
    }

    // PHƯƠNG THỨC HIỂN THỊ XÁC NHẬN CUSTOM VỚI MÀU XANH TRÀN VIỀN
    private boolean hienThiXacNhan(String message, String title) {
        // Tạo custom buttons
        JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
        JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166));

        // Tạo panel chứa nội dung với màu nền xanh tràn viền
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon question
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(btnCo);
        buttonPanel.add(btnKhong);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo JDialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainView), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(mainView);
        dialog.setResizable(false);

        // Biến để lưu kết quả
        final boolean[] result = {false};

        // Xử lý sự kiện cho nút
        btnCo.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        btnKhong.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        // Xử lý khi đóng cửa sổ (nút "X" trên dialog xác nhận)
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                result[0] = false;
                dialog.dispose();
            }
        });

        // Đặt nút "Không" làm default button
        dialog.getRootPane().setDefaultButton(btnKhong);

        // Hiển thị dialog và đợi
        dialog.setVisible(true);
        
        return result[0];
    }

    // PHƯƠNG THỨC TẠO BUTTON STYLE
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    // PHƯƠNG THỨC XÁC NHẬN THOÁT CHƯƠNG TRÌNH
    private void xacNhanThoatChuongTrinh() {
        boolean confirmed = hienThiXacNhan("Bạn có chắc muốn thoát chương trình không?", "Xác nhận thoát");

        if (confirmed) {
            System.exit(0);
        }
    }

    private void xuLyDangXuat() {
        boolean confirmed = hienThiXacNhan("Bạn có chắc muốn đăng xuất?", "Xác nhận đăng xuất");

        if (confirmed) {
            mainView.dangXuat();
        }
    }
}