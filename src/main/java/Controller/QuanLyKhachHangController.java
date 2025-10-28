package Controller;

import Model.KhachHang;
import Service.KhachHangService;
import View.QuanLyKhachHangView;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyKhachHangController {
    private QuanLyKhachHangView view;
    private KhachHangService service;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter displayFormatter;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyKhachHangController(QuanLyKhachHangView view, KhachHangService service) {
        this.view = view;
        this.service = service;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        initController();
        loadAllKhachHang();
    }

    private void initController() {
        // Xử lý sự kiện button Thêm
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themKhachHang();
            }
        });

        // Xử lý sự kiện button Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaKhachHang();
            }
        });

        // Xử lý sự kiện button Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaKhachHang();
            }
        });

        // Xử lý sự kiện button Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemKhachHang();
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
        view.getTblKhachHang().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    hienThiThongTinKhachHangDuocChon();
                }
            }
        });

        // Xử lý sự kiện filter loại khách
        view.getCboLoaiFilter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                locTheoLoaiKhach();
            }
        });
    }

    private void themKhachHang() {
        try {
            KhachHang khachHang = layThongTinKhachHangTuForm();
            if (khachHang == null) return;
            
            boolean success = service.addKhachHang(khachHang);
            if (success) {
                hienThiThongBao("Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllKhachHang();
                view.clearForm();
            } else {
                hienThiThongBao("Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhachHang() {
        try {
            String maKHText = view.getTxtMaKhachHang().getText();
            if (maKHText.isEmpty()) {
                hienThiThongBao("Vui lòng chọn khách hàng cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            KhachHang khachHang = layThongTinKhachHangTuForm();
            if (khachHang == null) return;
            
            khachHang.setMaKhachHang(Integer.parseInt(maKHText));
            
            boolean success = service.updateKhachHang(khachHang);
            if (success) {
                hienThiThongBao("Cập nhật khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllKhachHang();
                view.clearForm();
            } else {
                hienThiThongBao("Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        try {
            String maKHText = view.getTxtMaKhachHang().getText();
            if (maKHText.isEmpty()) {
                hienThiThongBao("Vui lòng chọn khách hàng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int maKhachHang = Integer.parseInt(maKHText);
            
            boolean confirmed = hienThiXacNhan("Bạn có chắc chắn muốn xóa khách hàng này?");
            
            if (confirmed) {
                boolean success = service.deleteKhachHang(maKhachHang);
                if (success) {
                    hienThiThongBao("Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllKhachHang();
                    view.clearForm();
                } else {
                    hienThiThongBao("Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemKhachHang() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            if (tuKhoa.isEmpty()) {
                loadAllKhachHang();
                return;
            }
            
            List<KhachHang> dsKhachHang = service.searchKhachHangByHoTen(tuKhoa);
            hienThiDanhSachKhachHang(dsKhachHang);
            
        } catch (Exception ex) {
            hienThiThongBao("Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locTheoLoaiKhach() {
        try {
            String loaiKhach = (String) view.getCboLoaiFilter().getSelectedItem();
            if ("Tất cả".equals(loaiKhach)) {
                loadAllKhachHang();
            } else {
                List<KhachHang> dsKhachHang = service.getKhachHangByLoai(loaiKhach);
                hienThiDanhSachKhachHang(dsKhachHang);
            }
        } catch (Exception ex) {
            hienThiThongBao("Lỗi lọc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        view.clearForm();
        loadAllKhachHang();
    }

    private void hienThiThongTinKhachHangDuocChon() {
        int selectedRow = view.getTblKhachHang().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModel();
            
            view.getTxtMaKhachHang().setText(model.getValueAt(selectedRow, 0).toString());
            view.getTxtHoTen().setText(model.getValueAt(selectedRow, 1).toString());
            
            Object ngaySinh = model.getValueAt(selectedRow, 2);
            view.getTxtNgaySinh().setText(ngaySinh != null ? ngaySinh.toString() : "");
            
            view.getCboLoaiKhach().setSelectedItem(model.getValueAt(selectedRow, 3));
            view.getTxtSoDienThoai().setText(model.getValueAt(selectedRow, 4).toString());
            
            Object ghiChu = model.getValueAt(selectedRow, 5);
            view.getTxtGhiChu().setText(ghiChu != null ? ghiChu.toString() : "");
            
            view.getTxtNgayTao().setText(model.getValueAt(selectedRow, 6).toString());
        }
    }

    private KhachHang layThongTinKhachHangTuForm() {
        try {
            String hoTen = view.getTxtHoTen().getText().trim();
            String ngaySinhText = view.getTxtNgaySinh().getText().trim();
            String loaiKhach = (String) view.getCboLoaiKhach().getSelectedItem();
            String soDienThoai = view.getTxtSoDienThoai().getText().trim();
            String ghiChu = view.getTxtGhiChu().getText().trim();
            
            // Validate dữ liệu
            if (hoTen.isEmpty()) {
                hienThiThongBao("Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            if (soDienThoai.isEmpty()) {
                hienThiThongBao("Số điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            
            return new KhachHang(hoTen, ngaySinh, loaiKhach, soDienThoai, ghiChu);
            
        } catch (Exception ex) {
            hienThiThongBao("Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadAllKhachHang() {
        try {
            List<KhachHang> dsKhachHang = service.getAllKhachHang();
            hienThiDanhSachKhachHang(dsKhachHang);
        } catch (Exception ex) {
            hienThiThongBao("Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDanhSachKhachHang(List<KhachHang> dsKhachHang) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);
        
        for (KhachHang kh : dsKhachHang) {
            Object[] row = {
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getNgaySinh() != null ? kh.getNgaySinh().format(dateFormatter) : "",
                kh.getLoaiKhach(),
                kh.getSoDienThoai(),
                kh.getGhiChu(),
                kh.getNgayTao().format(displayFormatter)
            };
            model.addRow(row);
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
}