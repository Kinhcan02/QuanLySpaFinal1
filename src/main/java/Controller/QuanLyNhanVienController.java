package Controller;

import Model.NhanVien;
import Service.NhanVienService;
import View.QuanLyNhanVienView;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyNhanVienController {
    private QuanLyNhanVienView view;
    private NhanVienService service;
    private DateTimeFormatter dateFormatter;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyNhanVienController(QuanLyNhanVienView view, NhanVienService service) {
        this.view = view;
        this.service = service;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        initController();
        loadAllNhanVien();
    }

    private void initController() {
        // Xử lý sự kiện button Thêm
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themNhanVien();
            }
        });

        // Xử lý sự kiện button Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaNhanVien();
            }
        });

        // Xử lý sự kiện button Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaNhanVien();
            }
        });

        // Xử lý sự kiện button Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemNhanVien();
            }
        });

        // Xử lý sự kiện button Làm mới
        view.getBtnLamMoi().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoi();
            }
        });

        // Xử lý sự kiện chọn hàng trong table
        view.getTblNhanVien().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    hienThiThongTinNhanVienDuocChon();
                }
            }
        });

        // Xử lý sự kiện filter chức vụ
        view.getCboChucVuFilter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                locTheoChucVu();
            }
        });
    }

    private void themNhanVien() {
        try {
            NhanVien nhanVien = layThongTinNhanVienTuForm();
            if (nhanVien == null) return;
            
            boolean success = service.addNhanVien(nhanVien);
            if (success) {
                hienThiThongBao("Thêm nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllNhanVien();
                view.clearForm();
            } else {
                hienThiThongBao("Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhanVien() {
        try {
            String maNVText = view.getTxtMaNhanVien().getText();
            if (maNVText.isEmpty()) {
                hienThiThongBao("Vui lòng chọn nhân viên cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            NhanVien nhanVien = layThongTinNhanVienTuForm();
            if (nhanVien == null) return;
            
            nhanVien.setMaNhanVien(Integer.parseInt(maNVText));
            
            boolean success = service.updateNhanVien(nhanVien);
            if (success) {
                hienThiThongBao("Cập nhật nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllNhanVien();
                view.clearForm();
            } else {
                hienThiThongBao("Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        try {
            String maNVText = view.getTxtMaNhanVien().getText();
            if (maNVText.isEmpty()) {
                hienThiThongBao("Vui lòng chọn nhân viên cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int maNhanVien = Integer.parseInt(maNVText);
            
            boolean confirmed = hienThiXacNhan("Bạn có chắc chắn muốn xóa nhân viên này?");
            
            if (confirmed) {
                boolean success = service.deleteNhanVien(maNhanVien);
                if (success) {
                    hienThiThongBao("Xóa nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllNhanVien();
                    view.clearForm();
                } else {
                    hienThiThongBao("Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemNhanVien() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            if (tuKhoa.isEmpty()) {
                loadAllNhanVien();
                return;
            }
            
            List<NhanVien> dsNhanVien = service.searchNhanVienByHoTen(tuKhoa);
            hienThiDanhSachNhanVien(dsNhanVien);
            
        } catch (Exception ex) {
            hienThiThongBao("Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locTheoChucVu() {
        try {
            String chucVu = (String) view.getCboChucVuFilter().getSelectedItem();
            if ("Tất cả".equals(chucVu)) {
                loadAllNhanVien();
            } else {
                List<NhanVien> dsNhanVien = service.getNhanVienByChucVu(chucVu);
                hienThiDanhSachNhanVien(dsNhanVien);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi lọc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        view.clearForm();
        loadAllNhanVien();
    }

    private void hienThiThongTinNhanVienDuocChon() {
        int selectedRow = view.getTblNhanVien().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModel();
            
            view.getTxtMaNhanVien().setText(model.getValueAt(selectedRow, 0).toString());
            view.getTxtHoTen().setText(model.getValueAt(selectedRow, 1).toString());
            
            Object ngaySinh = model.getValueAt(selectedRow, 2);
            view.getTxtNgaySinh().setText(ngaySinh != null ? ngaySinh.toString() : "");
            
            view.getTxtSoDienThoai().setText(model.getValueAt(selectedRow, 3).toString());
            
            Object diaChi = model.getValueAt(selectedRow, 4);
            view.getTxtDiaChi().setText(diaChi != null ? diaChi.toString() : "");
            
            view.getCboChucVu().setSelectedItem(model.getValueAt(selectedRow, 5));
            
            Object ngayVaoLam = model.getValueAt(selectedRow, 6);
            view.getTxtNgayVaoLam().setText(ngayVaoLam != null ? ngayVaoLam.toString() : "");
        }
    }

    private NhanVien layThongTinNhanVienTuForm() {
        try {
            String hoTen = view.getTxtHoTen().getText().trim();
            String ngaySinhText = view.getTxtNgaySinh().getText().trim();
            String soDienThoai = view.getTxtSoDienThoai().getText().trim();
            String diaChi = view.getTxtDiaChi().getText().trim();
            String chucVu = (String) view.getCboChucVu().getSelectedItem();
            String ngayVaoLamText = view.getTxtNgayVaoLam().getText().trim();
            
            // Validate dữ liệu
            if (hoTen.isEmpty()) {
                hienThiThongBao("Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            if (soDienThoai.isEmpty()) {
                hienThiThongBao("Số điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            if (diaChi.isEmpty()) {
                hienThiThongBao("Địa chỉ không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            if (chucVu == null || chucVu.isEmpty()) {
                hienThiThongBao("Chức vụ không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            LocalDate ngaySinh = null;
            if (!ngaySinhText.isEmpty()) {
                try {
                    ngaySinh = LocalDate.parse(ngaySinhText, dateFormatter);
                } catch (DateTimeParseException e) {
                    hienThiThongBao("Định dạng ngày sinh không hợp lệ! Sử dụng yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
            
            LocalDate ngayVaoLam = LocalDate.now(); // Mặc định là ngày hiện tại
            if (!ngayVaoLamText.isEmpty()) {
                try {
                    ngayVaoLam = LocalDate.parse(ngayVaoLamText, dateFormatter);
                } catch (DateTimeParseException e) {
                    hienThiThongBao("Định dạng ngày vào làm không hợp lệ! Sử dụng yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
            
            return new NhanVien(hoTen, ngaySinh, soDienThoai, diaChi, chucVu, ngayVaoLam);
            
        } catch (Exception ex) {
            hienThiThongBao("Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadAllNhanVien() {
        try {
            List<NhanVien> dsNhanVien = service.getAllNhanVien();
            hienThiDanhSachNhanVien(dsNhanVien);
        } catch (Exception ex) {
            hienThiThongBao("Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDanhSachNhanVien(List<NhanVien> dsNhanVien) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);
        
        for (NhanVien nv : dsNhanVien) {
            Object[] row = {
                nv.getMaNhanVien(),
                nv.getHoTen(),
                nv.getNgaySinh() != null ? nv.getNgaySinh().format(dateFormatter) : "",
                nv.getSoDienThoai(),
                nv.getDiaChi(),
                nv.getChucVu(),
                nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(dateFormatter) : "",
                nv.getThamNien() + " năm"
            };
            model.addRow(row);
        }
    }

    // Các phương thức hiển thị thông báo custom (giống QuanLyKhachHangController)
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    private boolean hienThiXacNhan(String message) {
        JDialog dialog = createConfirmationDialog(message);
        final boolean[] result = {false};
        
        dialog.setVisible(true);
        
        return result[0];
    }

    private JDialog createCustomDialog(String message, String title, int messageType) {
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) window.dispose();
        });

        JPanel panel = createDialogPanel(message, messageType);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return createDialog(panel, title);
    }

    private JDialog createConfirmationDialog(String message) {
        JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
        JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166));
        
        JPanel panel = createDialogPanel(message, JOptionPane.QUESTION_MESSAGE);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(btnCo);
        buttonPanel.add(btnKhong);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = createDialog(panel, "Xác nhận");
        final boolean[] result = {false};

        btnCo.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        btnKhong.addActionListener(e -> { result[0] = false; dialog.dispose(); });

        return dialog;
    }

    private JPanel createDialogPanel(String message, int messageType) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Icon icon = getIconForMessageType(messageType);
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) contentPanel.add(new JLabel(icon));
        contentPanel.add(messageLabel);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JDialog createDialog(JPanel contentPanel, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);
        return dialog;
    }

    private Icon getIconForMessageType(int messageType) {
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE: return UIManager.getIcon("OptionPane.errorIcon");
            case JOptionPane.INFORMATION_MESSAGE: return UIManager.getIcon("OptionPane.informationIcon");
            case JOptionPane.WARNING_MESSAGE: return UIManager.getIcon("OptionPane.warningIcon");
            case JOptionPane.QUESTION_MESSAGE: return UIManager.getIcon("OptionPane.questionIcon");
            default: return null;
        }
    }

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
}