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
        } 
        else if (source == mainView.getBtnDatLich()) {
            mainView.showQuanLyDatLich(); // Mở quản lý đặt lịch
        }
        else if (source == mainView.getBtnDatDichVu()) {
            hienThiThongBao("Tính năng Đặt dịch vụ đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnQuanLyCaLam()) {
            mainView.showQuanLyCaLam();
        } 
        else if (source == mainView.getBtnQuanLyDichVu()) {
            mainView.showQuanLyDichVu();
        } 
        else if (source == mainView.getBtnQuanLyNhanVien()) {
            mainView.showQuanLyNhanVien();
        } 
        else if (source == mainView.getBtnQuanLyKhachHang()) {
            mainView.showQuanLyKhachHang();
        } 
        else if (source == mainView.getBtnThongKe()) {
            hienThiThongBao("Tính năng Thống kê đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnCaiDat()) {
            hienThiThongBao("Tính năng Cài đặt đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnThoat()) {
            xacNhanThoatChuongTrinh();
        }
        // Các nút có menu accordion đã được xử lý trực tiếp trong MainView
        // nên không cần xử lý ở đây
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

    // PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM VỚI MÀU XANH TRÀN VIỀN
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    // PHƯƠNG THỨC HIỂN THỊ XÁC NHẬN CUSTOM VỚI MÀU XANH TRÀN VIỀN
    private boolean hienThiXacNhan(String message) {
        JDialog dialog = createConfirmationDialog(message);
        final boolean[] result = {false};
        
        // Đợi dialog đóng
        dialog.setVisible(true);
        
        return result[0];
    }

    // PHƯƠNG THỨC TẠO CUSTOM DIALOG
    private JDialog createCustomDialog(String message, String title, int messageType) {
        // Tạo custom button OK
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        // Tạo panel chứa nội dung với màu nền xanh tràn viền
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon tùy theo loại message
        Icon icon = null;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                icon = UIManager.getIcon("OptionPane.errorIcon");
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.informationIcon");
                break;
            case JOptionPane.WARNING_MESSAGE:
                icon = UIManager.getIcon("OptionPane.warningIcon");
                break;
            case JOptionPane.QUESTION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.questionIcon");
                break;
        }

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút OK
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo JDialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainView), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(mainView);
        dialog.setResizable(false);

        // Đặt nút OK làm default button
        dialog.getRootPane().setDefaultButton(okButton);

        return dialog;
    }

    // PHƯƠNG THỨC TẠO DIALOG XÁC NHẬN
    private JDialog createConfirmationDialog(String message) {
        // Tạo custom buttons
        JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
        JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166)); // Màu xám cho nút "Không"
        
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainView), "Xác nhận thoát", true);
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

        return dialog;
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
        boolean confirmed = hienThiXacNhan("Bạn có chắc muốn thoát chương trình không?");
        
        if (confirmed) {
            System.exit(0);
        }
    }
}