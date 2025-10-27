package Controller;

import View.QuanLyDichVuView;
import Model.DichVu;
import Model.LoaiDichVu;
import Service.DichVuService;
import Service.LoaiDichVuService;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class QuanLyDichVuController {
    private QuanLyDichVuView view;
    private DichVuService dichVuService;
    private LoaiDichVuService loaiDichVuService;

    public QuanLyDichVuController(QuanLyDichVuView view) {
        this.view = view;
        this.dichVuService = new DichVuService();
        this.loaiDichVuService = new LoaiDichVuService();
        
        initController();
        loadAllDichVu();
        loadLoaiDichVuToComboBox();
    }

    private void initController() {
        view.getBtnThem().addActionListener(e -> themDichVu());
        view.getBtnSua().addActionListener(e -> suaDichVu());
        view.getBtnXoa().addActionListener(e -> xoaDichVu());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiemDichVu());
        view.getBtnLoaiDichVu().addActionListener(e -> showQuanLyLoaiDichVu());
    }

    private void lamMoi() {
        loadAllDichVu();
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
    }

    private void loadLoaiDichVuToComboBox() {
        try {
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            JComboBox<String> cboLoaiFilter = view.getCboLoaiFilter();
            cboLoaiFilter.removeAllItems();
            cboLoaiFilter.addItem("Tất cả");
            
            for (LoaiDichVu loaiDV : listLoaiDV) {
                cboLoaiFilter.addItem(loaiDV.getTenLoaiDV());
            }
        } catch (Exception e) {
            showError("Lỗi khi tải loại dịch vụ: " + e.getMessage());
        }
    }

    private void loadAllDichVu() {
        try {
            List<DichVu> listDichVu = dichVuService.getAllDichVu();
            displayDichVuOnTable(listDichVu);
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách dịch vụ: " + e.getMessage());
        }
    }

    private void displayDichVuOnTable(List<DichVu> listDichVu) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        for (DichVu dv : listDichVu) {
            model.addRow(new Object[]{
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                formatCurrency(dv.getGia()),
                "60 phút",
                getTenLoaiDichVu(dv.getMaLoaiDV()),
                dv.getGhiChu()
            });
        }
    }

    private String getTenLoaiDichVu(Integer maLoaiDV) {
        if (maLoaiDV == null) return "Chưa phân loại";
        
        try {
            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            return loaiDV.getTenLoaiDV();
        } catch (Exception e) {
            return "Không xác định";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,d VNĐ", amount.longValue());
    }

    private void showQuanLyLoaiDichVu() {
        try {
            JDialog dialog = createDialog("Quản Lý Loại Dịch Vụ", 500, 300);
            JPanel mainPanel = createMainPanel();
            
            // Tiêu đề
            JPanel titlePanel = createTitlePanel("QUẢN LÝ LOẠI DỊCH VỤ");
            
            // Form nhập liệu
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            formPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            
            JTextField txtTenLoai = new JTextField();
            JTextArea txtMoTa = new JTextArea(2, 20);
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
            
            formPanel.add(createStyledLabel("Tên loại dịch vụ:"));
            formPanel.add(txtTenLoai);
            formPanel.add(createStyledLabel("Mô tả:"));
            formPanel.add(scrollMoTa);
            
            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnThemLoai = createStyledButton("Thêm loại dịch vụ", new Color(0x4D, 0x8A, 0x57));
            JButton btnDong = createStyledButton("Đóng", new Color(0x4D, 0x8A, 0x57));
            
            btnThemLoai.addActionListener(e -> themLoaiDichVu(txtTenLoai, txtMoTa, dialog));
            btnDong.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnThemLoai);
            buttonPanel.add(btnDong);
            
            // Thêm các panel vào dialog
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            showError("Lỗi khi mở quản lý loại dịch vụ: " + e.getMessage());
        }
    }

    private void themLoaiDichVu(JTextField txtTenLoai, JTextArea txtMoTa, JDialog dialog) {
        try {
            String tenLoai = txtTenLoai.getText().trim();
            String moTa = txtMoTa.getText().trim();
            
            if (tenLoai.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tên loại dịch vụ không được để trống");
                return;
            }
            
            LoaiDichVu loaiDV = new LoaiDichVu(tenLoai, moTa);
            boolean success = loaiDichVuService.addLoaiDichVu(loaiDV);
            
            if (success) {
                JOptionPane.showMessageDialog(view, "Thêm loại dịch vụ thành công");
                txtTenLoai.setText("");
                txtMoTa.setText("");
                loadLoaiDichVuToComboBox();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm loại dịch vụ thất bại");
            }
        } catch (Exception e) {
            showError("Lỗi khi thêm loại dịch vụ: " + e.getMessage());
        }
    }

    private void themDichVu() {
        try {
            JDialog dialog = createDialog("Thêm Dịch Vụ Mới", 500, 450); // Tăng chiều cao lên 450
            JPanel mainPanel = createMainPanel();
            
            // Form nhập liệu với BorderLayout để kiểm soát tốt hơn
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            
            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            basicInfoPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            
            JTextField txtTenDV = new JTextField();
            JTextField txtGia = new JTextField();
            JComboBox<String> cboLoaiDV = new JComboBox<>();
            
            // Load loại dịch vụ
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");
            for (LoaiDichVu loai : listLoaiDV) {
                cboLoaiDV.addItem(loai.getTenLoaiDV());
            }
            
            basicInfoPanel.add(createStyledLabel("Tên dịch vụ:"));
            basicInfoPanel.add(txtTenDV);
            basicInfoPanel.add(createStyledLabel("Giá dịch vụ:"));
            basicInfoPanel.add(txtGia);
            basicInfoPanel.add(createStyledLabel("Loại dịch vụ:"));
            basicInfoPanel.add(cboLoaiDV);
            
            // Panel cho ghi chú với kích thước lớn hơn
            JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
            ghiChuPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            
            JLabel lblGhiChu = createStyledLabel("Ghi chú:");
            JTextArea txtGhiChu = new JTextArea(6, 30); // Tăng số dòng lên 6
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            scrollGhiChu.setPreferredSize(new Dimension(400, 120)); // Đặt kích thước cố định
            
            ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
            ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);
            
            // Thêm các panel vào form chính
            formPanel.add(basicInfoPanel, BorderLayout.NORTH);
            formPanel.add(ghiChuPanel, BorderLayout.CENTER);
            
            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnThem = createStyledButton("Thêm", new Color(0x4D, 0x8A, 0x57));
            JButton btnHuy = createStyledButton("Hủy", new Color(0x4D, 0x8A, 0x57));
            
            btnThem.addActionListener(e -> handleThemDichVu(dialog, txtTenDV, txtGia, cboLoaiDV, txtGhiChu, listLoaiDV));
            btnHuy.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnThem);
            buttonPanel.add(btnHuy);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            showError("Lỗi khi thêm dịch vụ: " + e.getMessage());
        }
    }

    private void handleThemDichVu(JDialog dialog, JTextField txtTenDV, JTextField txtGia, 
                                 JComboBox<String> cboLoaiDV, JTextArea txtGhiChu, 
                                 List<LoaiDichVu> listLoaiDV) {
        if (txtTenDV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Tên dịch vụ không được để trống");
            return;
        }
        
        BigDecimal gia;
        try {
            gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Giá dịch vụ không hợp lệ");
            return;
        }

        Integer maLoaiDV = null;
        if (cboLoaiDV.getSelectedIndex() > 0) {
            maLoaiDV = listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV();
        }

        DichVu dichVu = new DichVu(
            txtTenDV.getText().trim(),
            gia,
            maLoaiDV,
            txtGhiChu.getText().trim()
        );

        boolean success = dichVuService.addDichVu(dichVu);
        if (success) {
            JOptionPane.showMessageDialog(dialog, "Thêm dịch vụ thành công");
            loadAllDichVu();
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, "Thêm dịch vụ thất bại");
        }
    }

    private void suaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một dịch vụ để sửa");
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            DichVu dichVu = dichVuService.getDichVuById(maDichVu);
            // Thêm code xử lý sửa dịch vụ ở đây
        } catch (Exception e) {
            showError("Lỗi khi sửa dịch vụ: " + e.getMessage());
        }
    }

    private void xoaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một dịch vụ để xóa");
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa dịch vụ: " + tenDichVu + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dichVuService.deleteDichVu(maDichVu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa dịch vụ thành công");
                    loadAllDichVu();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa dịch vụ thất bại");
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi xóa dịch vụ: " + e.getMessage());
        }
    }

    private void timKiemDichVu() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String loaiFilter = (String) view.getCboLoaiFilter().getSelectedItem();
            
            List<DichVu> ketQua = !tuKhoa.isEmpty() ? 
                dichVuService.searchDichVuByTen(tuKhoa) : 
                dichVuService.getAllDichVu();
            
            // Lọc theo loại dịch vụ
            if (!"Tất cả".equals(loaiFilter)) {
                ketQua.removeIf(dv -> !loaiFilter.equals(getTenLoaiDichVu(dv.getMaLoaiDV())));
            }
            
            displayDichVuOnTable(ketQua);
            
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    // Các phương thức hỗ trợ tạo giao diện
    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(view);
        dialog.setModal(true);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createTitlePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x4D, 0x8A, 0x57));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}