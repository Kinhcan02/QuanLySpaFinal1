package Controller;

import Model.LoaiDichVu;
import Service.LoaiDichVuService;
import View.QuanLyLoaiDichVuView;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuanLyLoaiDichVuController {

    private QuanLyLoaiDichVuView view;
    private LoaiDichVuService loaiDichVuService;
    private QuanLyDichVuController mainController;

    // Màu sắc giống QuanLyKhachHangController
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyLoaiDichVuController(QuanLyDichVuController mainController) {
        this.view = new QuanLyLoaiDichVuView(null);
        this.loaiDichVuService = new LoaiDichVuService();
        this.mainController = mainController;
        initController();
        loadLoaiDichVuToTable();
    }

    private void initController() {
        view.getBtnThem().addActionListener(e -> showThemLoaiDichVuForm());
        view.getBtnSua().addActionListener(e -> showSuaLoaiDichVuForm());
        view.getBtnXoa().addActionListener(e -> xoaLoaiDichVu());
        view.getBtnLamMoi().addActionListener(e -> loadLoaiDichVuToTable());
        view.getBtnDong().addActionListener(e -> view.dispose());
    }

    public void showView() {
        view.setVisible(true);
    }

    private void loadLoaiDichVuToTable() {
        try {
            view.getTableModel().setRowCount(0);
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();

            for (LoaiDichVu loaiDV : listLoaiDV) {
                view.getTableModel().addRow(new Object[]{
                    loaiDV.getMaLoaiDV(),
                    loaiDV.getTenLoaiDV(),
                    loaiDV.getMoTa() != null ? loaiDV.getMoTa() : ""
                });
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải danh sách loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showThemLoaiDichVuForm() {
        JDialog dialog = createDialog("Thêm Loại Dịch Vụ Mới", 500, 400);
        JPanel mainPanel = createMainPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Tiêu đề
        JPanel titlePanel = createTitlePanel("THÊM LOẠI DỊCH VỤ MỚI");

        // Form nhập liệu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tên loại dịch vụ
        JPanel pnTenLoai = new JPanel(new BorderLayout(5, 5));
        pnTenLoai.setBackground(COLOR_BACKGROUND);
        JLabel lblTenLoai = createStyledLabel("Tên loại dịch vụ:");
        lblTenLoai.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField txtTenLoai = new JTextField();
        txtTenLoai.setPreferredSize(new Dimension(450, 35));
        txtTenLoai.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTenLoai.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x4D, 0x8A, 0x57), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pnTenLoai.add(lblTenLoai, BorderLayout.NORTH);
        pnTenLoai.add(txtTenLoai, BorderLayout.CENTER);

        // Mô tả
        JPanel pnMoTa = new JPanel(new BorderLayout(5, 5));
        pnMoTa.setBackground(COLOR_BACKGROUND);
        JLabel lblMoTa = createStyledLabel("Mô tả:");
        lblMoTa.setFont(new Font("Arial", Font.BOLD, 13));
        JTextArea txtMoTa = new JTextArea(6, 45);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x4D, 0x8A, 0x57), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setPreferredSize(new Dimension(450, 150));
        scrollMoTa.setBorder(BorderFactory.createEmptyBorder());
        pnMoTa.add(lblMoTa, BorderLayout.NORTH);
        pnMoTa.add(scrollMoTa, BorderLayout.CENTER);

        // Thêm vào formPanel với khoảng cách
        formPanel.add(pnTenLoai);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(pnMoTa);

        // Panel nút
        JPanel buttonPanel = createButtonPanel();
        JButton btnThem = createStyledButton("Thêm", COLOR_BUTTON);
        JButton btnHuy = createStyledButton("Hủy", new Color(0x95, 0xA5, 0xA6));

        btnThem.addActionListener(e -> {
            String tenLoai = txtTenLoai.getText().trim();
            String moTa = txtMoTa.getText().trim();

            if (tenLoai.isEmpty()) {
                hienThiThongBao("Tên loại dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LoaiDichVu loaiDV = new LoaiDichVu(tenLoai, moTa);
                boolean success = loaiDichVuService.addLoaiDichVu(loaiDV);

                if (success) {
                    hienThiThongBao("Thêm loại dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadLoaiDichVuToTable();
                    mainController.loadLoaiDichVuToComboBox();
                    dialog.dispose();
                } else {
                    hienThiThongBao("Thêm loại dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                hienThiThongBao("Lỗi khi thêm loại dịch vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnHuy);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showSuaLoaiDichVuForm() {
        int selectedRow = view.getTblLoaiDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một loại dịch vụ để sửa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maLoaiDV = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String tenLoaiDV = (String) view.getTableModel().getValueAt(selectedRow, 1);
            String moTa = (String) view.getTableModel().getValueAt(selectedRow, 2);

            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            if (loaiDV == null) {
                hienThiThongBao("Không tìm thấy loại dịch vụ cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = createDialog("Sửa Loại Dịch Vụ", 500, 400);
            JPanel mainPanel = createMainPanel();
            mainPanel.setLayout(new BorderLayout(10, 10));

            // Tiêu đề
            JPanel titlePanel = createTitlePanel("SỬA LOẠI DỊCH VỤ");

            // Form nhập liệu - Sử dụng layout giống form thêm
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(COLOR_BACKGROUND);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Tên loại dịch vụ
            JPanel pnTenLoai = new JPanel(new BorderLayout(5, 5));
            pnTenLoai.setBackground(COLOR_BACKGROUND);
            JLabel lblTenLoai = createStyledLabel("Tên loại dịch vụ:");
            lblTenLoai.setFont(new Font("Arial", Font.BOLD, 13));
            JTextField txtTenLoai = new JTextField(loaiDV.getTenLoaiDV());
            txtTenLoai.setPreferredSize(new Dimension(450, 35));
            txtTenLoai.setFont(new Font("Arial", Font.PLAIN, 14));
            txtTenLoai.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x4D, 0x8A, 0x57), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            pnTenLoai.add(lblTenLoai, BorderLayout.NORTH);
            pnTenLoai.add(txtTenLoai, BorderLayout.CENTER);

            // Mô tả
            JPanel pnMoTa = new JPanel(new BorderLayout(5, 5));
            pnMoTa.setBackground(COLOR_BACKGROUND);
            JLabel lblMoTa = createStyledLabel("Mô tả:");
            lblMoTa.setFont(new Font("Arial", Font.BOLD, 13));
            JTextArea txtMoTa = new JTextArea(6, 45);
            txtMoTa.setText(loaiDV.getMoTa() != null ? loaiDV.getMoTa() : "");
            txtMoTa.setLineWrap(true);
            txtMoTa.setWrapStyleWord(true);
            txtMoTa.setFont(new Font("Arial", Font.PLAIN, 14));
            txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x4D, 0x8A, 0x57), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
            scrollMoTa.setPreferredSize(new Dimension(450, 150));
            scrollMoTa.setBorder(BorderFactory.createEmptyBorder());
            pnMoTa.add(lblMoTa, BorderLayout.NORTH);
            pnMoTa.add(scrollMoTa, BorderLayout.CENTER);

            // Thêm vào formPanel với khoảng cách
            formPanel.add(pnTenLoai);
            formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            formPanel.add(pnMoTa);

            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnCapNhat = createStyledButton("Cập nhật", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", new Color(0x95, 0xA5, 0xA6));

            btnCapNhat.addActionListener(e -> {
                String tenLoaiMoi = txtTenLoai.getText().trim();
                String moTaMoi = txtMoTa.getText().trim();

                if (tenLoaiMoi.isEmpty()) {
                    hienThiThongBao("Tên loại dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    loaiDV.setTenLoaiDV(tenLoaiMoi);
                    loaiDV.setMoTa(moTaMoi);

                    boolean success = loaiDichVuService.updateLoaiDichVu(loaiDV);

                    if (success) {
                        hienThiThongBao("Cập nhật loại dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadLoaiDichVuToTable();
                        mainController.loadLoaiDichVuToComboBox();
                        dialog.dispose();
                    } else {
                        hienThiThongBao("Cập nhật loại dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    hienThiThongBao("Lỗi khi cập nhật loại dịch vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnCapNhat);
            buttonPanel.add(btnHuy);

            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLoaiDichVu() {
        int selectedRow = view.getTblLoaiDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một loại dịch vụ để xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maLoaiDV = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String tenLoaiDV = (String) view.getTableModel().getValueAt(selectedRow, 1);

            boolean confirmed = hienThiXacNhan("Bạn có chắc muốn xóa loại dịch vụ '" + tenLoaiDV + "' không?");

            if (confirmed) {
                boolean success = loaiDichVuService.deleteLoaiDichVu(maLoaiDV);

                if (success) {
                    hienThiThongBao("Xóa loại dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadLoaiDichVuToTable();
                    mainController.loadLoaiDichVuToComboBox();
                } else {
                    hienThiThongBao("Xóa loại dịch vụ thất bại. Có thể loại dịch vụ đang được sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO GIỐNG QuanLyKhachHangController

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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Xác nhận", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
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

        // Xử lý khi đóng cửa sổ
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
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

    // Các phương thức hỗ trợ tạo giao diện
    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(view, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JPanel createTitlePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x4D, 0x8A, 0x57));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 0),
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0x3D, 0x7A, 0x47))
        ));
        panel.setPreferredSize(new Dimension(panel.getWidth(), 50));
        
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
}