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
            showError("Lỗi khi tải danh sách loại dịch vụ: " + e.getMessage());
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
        formPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tên loại dịch vụ
        JPanel pnTenLoai = new JPanel(new BorderLayout(5, 5));
        pnTenLoai.setBackground(new Color(0x8C, 0xC9, 0x80));
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
        pnMoTa.setBackground(new Color(0x8C, 0xC9, 0x80));
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
        JButton btnThem = createStyledButton("Thêm", new Color(0x4D, 0x8A, 0x57));
        JButton btnHuy = createStyledButton("Hủy", new Color(0x95, 0xA5, 0xA6));

        btnThem.addActionListener(e -> {
            String tenLoai = txtTenLoai.getText().trim();
            String moTa = txtMoTa.getText().trim();

            if (tenLoai.isEmpty()) {
                showCustomMessage(dialog, "Tên loại dịch vụ không được để trống");
                return;
            }

            try {
                LoaiDichVu loaiDV = new LoaiDichVu(tenLoai, moTa);
                boolean success = loaiDichVuService.addLoaiDichVu(loaiDV);

                if (success) {
                    showSuccessMessage("Thêm loại dịch vụ thành công");
                    loadLoaiDichVuToTable();
                    mainController.loadLoaiDichVuToComboBox();
                    dialog.dispose();
                } else {
                    showError("Thêm loại dịch vụ thất bại");
                }
            } catch (Exception ex) {
                showError("Lỗi khi thêm loại dịch vụ: " + ex.getMessage());
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
            showErrorMessage("Vui lòng chọn một loại dịch vụ để sửa");
            return;
        }

        try {
            int maLoaiDV = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String tenLoaiDV = (String) view.getTableModel().getValueAt(selectedRow, 1);
            String moTa = (String) view.getTableModel().getValueAt(selectedRow, 2);

            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            if (loaiDV == null) {
                showError("Không tìm thấy loại dịch vụ cần sửa");
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
            formPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Tên loại dịch vụ
            JPanel pnTenLoai = new JPanel(new BorderLayout(5, 5));
            pnTenLoai.setBackground(new Color(0x8C, 0xC9, 0x80));
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
            pnMoTa.setBackground(new Color(0x8C, 0xC9, 0x80));
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
            JButton btnCapNhat = createStyledButton("Cập nhật", new Color(0x4D, 0x8A, 0x57));
            JButton btnHuy = createStyledButton("Hủy", new Color(0x95, 0xA5, 0xA6));

            btnCapNhat.addActionListener(e -> {
                String tenLoaiMoi = txtTenLoai.getText().trim();
                String moTaMoi = txtMoTa.getText().trim();

                if (tenLoaiMoi.isEmpty()) {
                    showCustomMessage(dialog, "Tên loại dịch vụ không được để trống");
                    return;
                }

                try {
                    loaiDV.setTenLoaiDV(tenLoaiMoi);
                    loaiDV.setMoTa(moTaMoi);

                    boolean success = loaiDichVuService.updateLoaiDichVu(loaiDV);

                    if (success) {
                        showSuccessMessage("Cập nhật loại dịch vụ thành công");
                        loadLoaiDichVuToTable();
                        mainController.loadLoaiDichVuToComboBox();
                        dialog.dispose();
                    } else {
                        showError("Cập nhật loại dịch vụ thất bại");
                    }
                } catch (Exception ex) {
                    showError("Lỗi khi cập nhật loại dịch vụ: " + ex.getMessage());
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
            showError("Lỗi khi sửa loại dịch vụ: " + e.getMessage());
        }
    }

    private void xoaLoaiDichVu() {
        int selectedRow = view.getTblLoaiDichVu().getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Vui lòng chọn một loại dịch vụ để xóa");
            return;
        }

        try {
            int maLoaiDV = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String tenLoaiDV = (String) view.getTableModel().getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn có chắc muốn xóa loại dịch vụ '" + tenLoaiDV + "' không?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = loaiDichVuService.deleteLoaiDichVu(maLoaiDV);

                if (success) {
                    showSuccessMessage("Xóa loại dịch vụ thành công");
                    loadLoaiDichVuToTable();
                    mainController.loadLoaiDichVuToComboBox();
                } else {
                    showError("Xóa loại dịch vụ thất bại. Có thể loại dịch vụ đang được sử dụng.");
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi xóa loại dịch vụ: " + e.getMessage());
        }
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
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
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
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker().darker(), 2),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }
        });

        return button;
    }

    private void showCustomMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}