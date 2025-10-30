package Controller;

import Model.LoaiNguyenLieu;
import Model.NguyenLieu;
import Service.LoaiNguyenLieuService;
import Service.NguyenLieuService;
import View.QuanLyNguyenLieuView;
import View.QuanLyLoaiNguyenLieuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class QuanLyNguyenLieuController {

    private QuanLyNguyenLieuView view;
    private NguyenLieuService nguyenLieuService;
    private LoaiNguyenLieuService loaiNguyenLieuService;
    private DefaultTableModel model;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    // Biến để lưu kết quả từ dialog
    private boolean dialogResult;

    public QuanLyNguyenLieuController(QuanLyNguyenLieuView view) {
        this.view = view;
        this.nguyenLieuService = new NguyenLieuService();
        this.loaiNguyenLieuService = new LoaiNguyenLieuService();
        this.model = view.getModel();
        initController();
        loadData();
        loadLoaiNguyenLieu();
    }

    private void initController() {
        view.getBtnThem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                themNguyenLieu();
            }
        });

        view.getBtnSua().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suaNguyenLieu();
            }
        });

        view.getBtnXoa().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xoaNguyenLieu();
            }
        });

        view.getBtnLamMoi().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lamMoi();
            }
        });

        view.getBtnTimKiem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timKiem();
            }
        });

        view.getBtnLoaiNguyenLieu().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moQuanLyLoaiNguyenLieu();
            }
        });

        view.getCboLoaiFilter().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                locTheoLoai();
            }
        });
    }

    private void loadData() {
        try {
            List<NguyenLieu> list = nguyenLieuService.getAllNguyenLieu();
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                model.addRow(new Object[]{
                    nl.getMaNguyenLieu(),
                    nl.getTenNguyenLieu(),
                    nl.getSoLuongTon(),
                    nl.getDonViTinh(),
                    tenLoai,
                    nl.getMaLoaiNL()
                });
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLoaiNguyenLieu() {
        try {
            view.getCboLoaiFilter().removeAllItems();
            view.getCboLoaiFilter().addItem("Tất cả");

            List<LoaiNguyenLieu> list = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : list) {
                view.getCboLoaiFilter().addItem(loaiNL.getTenLoaiNL());
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTenLoaiNguyenLieu(Integer maLoaiNL) {
        if (maLoaiNL == null) {
            return "";
        }
        try {
            LoaiNguyenLieu loaiNL = loaiNguyenLieuService.getLoaiNguyenLieuById(maLoaiNL);
            return loaiNL.getTenLoaiNL();
        } catch (Exception e) {
            return "";
        }
    }

    private Integer getMaLoaiFromTen(String tenLoai) {
        if (tenLoai.equals("Tất cả") || tenLoai.isEmpty()) {
            return null;
        }
        try {
            List<LoaiNguyenLieu> list = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : list) {
                if (loaiNL.getTenLoaiNL().equals(tenLoai)) {
                    return loaiNL.getMaLoaiNL();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void themNguyenLieu() {
        try {
            JTextField txtTen = new JTextField();
            JTextField txtSoLuong = new JTextField("0");
            JTextField txtDonVi = new JTextField();

            JComboBox<String> cboLoai = new JComboBox<>();
            cboLoai.addItem(""); // Trống
            List<LoaiNguyenLieu> listLoai = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : listLoai) {
                cboLoai.addItem(loaiNL.getTenLoaiNL());
            }

            Object[] message = {
                "Tên nguyên liệu:", txtTen,
                "Số lượng tồn:", txtSoLuong,
                "Đơn vị tính:", txtDonVi,
                "Loại nguyên liệu:", cboLoai
            };

            boolean confirmed = showCustomInputDialog(message, "Thêm nguyên liệu");

            if (confirmed) {
                String ten = txtTen.getText().trim();
                String soLuongStr = txtSoLuong.getText().trim();
                String donVi = txtDonVi.getText().trim();
                String tenLoai = (String) cboLoai.getSelectedItem();

                if (ten.isEmpty()) {
                    hienThiThongBao("Tên nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (donVi.isEmpty()) {
                    hienThiThongBao("Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong < 0) {
                        hienThiThongBao("Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer maLoai = getMaLoaiFromTen(tenLoai);
                NguyenLieu nguyenLieu = new NguyenLieu(ten, soLuong, donVi, maLoai);

                boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn thêm nguyên liệu này?");

                if (xacNhan) {
                    boolean success = nguyenLieuService.addNguyenLieu(nguyenLieu);
                    if (success) {
                        hienThiThongBao("Thêm nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                        loadLoaiNguyenLieu();
                    } else {
                        hienThiThongBao("Thêm nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNguyenLieu() {
        int selectedRow = view.getTblNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một nguyên liệu để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNguyenLieu = (Integer) model.getValueAt(selectedRow, 0);
            String tenHienTai = (String) model.getValueAt(selectedRow, 1);
            Integer soLuongHienTai = (Integer) model.getValueAt(selectedRow, 2);
            String donViHienTai = (String) model.getValueAt(selectedRow, 3);
            String loaiHienTai = (String) model.getValueAt(selectedRow, 4);

            JTextField txtTen = new JTextField(tenHienTai);
            JTextField txtSoLuong = new JTextField(soLuongHienTai.toString());
            JTextField txtDonVi = new JTextField(donViHienTai);

            JComboBox<String> cboLoai = new JComboBox<>();
            cboLoai.addItem(""); // Trống
            List<LoaiNguyenLieu> listLoai = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : listLoai) {
                cboLoai.addItem(loaiNL.getTenLoaiNL());
                if (loaiNL.getTenLoaiNL().equals(loaiHienTai)) {
                    cboLoai.setSelectedItem(loaiNL.getTenLoaiNL());
                }
            }

            Object[] message = {
                "Tên nguyên liệu:", txtTen,
                "Số lượng tồn:", txtSoLuong,
                "Đơn vị tính:", txtDonVi,
                "Loại nguyên liệu:", cboLoai
            };

            boolean confirmed = showCustomInputDialog(message, "Sửa nguyên liệu");

            if (confirmed) {
                String ten = txtTen.getText().trim();
                String soLuongStr = txtSoLuong.getText().trim();
                String donVi = txtDonVi.getText().trim();
                String tenLoai = (String) cboLoai.getSelectedItem();

                if (ten.isEmpty()) {
                    hienThiThongBao("Tên nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (donVi.isEmpty()) {
                    hienThiThongBao("Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong < 0) {
                        hienThiThongBao("Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer maLoai = getMaLoaiFromTen(tenLoai);
                NguyenLieu nguyenLieu = new NguyenLieu(maNguyenLieu, ten, soLuong, donVi, maLoai);

                boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn sửa nguyên liệu này?");

                if (xacNhan) {
                    boolean success = nguyenLieuService.updateNguyenLieu(nguyenLieu);
                    if (success) {
                        hienThiThongBao("Sửa nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                        loadLoaiNguyenLieu();
                    } else {
                        hienThiThongBao("Sửa nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNguyenLieu() {
        int selectedRow = view.getTblNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một nguyên liệu để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNguyenLieu = (Integer) model.getValueAt(selectedRow, 0);
            String tenNguyenLieu = (String) model.getValueAt(selectedRow, 1);

            boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn xóa nguyên liệu '" + tenNguyenLieu + "'?");

            if (xacNhan) {
                boolean success = nguyenLieuService.deleteNguyenLieu(maNguyenLieu);
                if (success) {
                    hienThiThongBao("Xóa nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    hienThiThongBao("Xóa nguyên liệu thất bại! Có thể đang có dữ liệu liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<NguyenLieu> list = nguyenLieuService.getAllNguyenLieu();
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                if (nl.getTenNguyenLieu().toLowerCase().contains(keyword.toLowerCase())
                        || (nl.getDonViTinh() != null && nl.getDonViTinh().toLowerCase().contains(keyword.toLowerCase()))) {
                    String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                    model.addRow(new Object[]{
                        nl.getMaNguyenLieu(),
                        nl.getTenNguyenLieu(),
                        nl.getSoLuongTon(),
                        nl.getDonViTinh(),
                        tenLoai,
                        nl.getMaLoaiNL()
                    });
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locTheoLoai() {
        String selectedLoai = (String) view.getCboLoaiFilter().getSelectedItem();
        if (selectedLoai == null || selectedLoai.equals("Tất cả")) {
            loadData();
            return;
        }

        try {
            Integer maLoai = getMaLoaiFromTen(selectedLoai);
            if (maLoai == null) {
                loadData();
                return;
            }

            List<NguyenLieu> list = nguyenLieuService.getNguyenLieuByMaLoaiNL(maLoai);
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                model.addRow(new Object[]{
                    nl.getMaNguyenLieu(),
                    nl.getTenNguyenLieu(),
                    nl.getSoLuongTon(),
                    nl.getDonViTinh(),
                    tenLoai,
                    nl.getMaLoaiNL()
                });
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi lọc theo loại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
        loadData();
    }

    private void moQuanLyLoaiNguyenLieu() {
        try {
            JFrame frame = new JFrame("Quản lý loại nguyên liệu");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            QuanLyLoaiNguyenLieuView loaiNLView = new QuanLyLoaiNguyenLieuView();
            new QuanLyLoaiNguyenLieuController(loaiNLView);

            frame.add(loaiNLView);
            frame.setVisible(true);

            // Reload lại danh sách loại nguyên liệu khi đóng cửa sổ
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadLoaiNguyenLieu();
                    loadData();
                }
            });
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi mở quản lý loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    private boolean hienThiXacNhan(String message) {
        JDialog dialog = createConfirmationDialog(message);
        dialog.setVisible(true);
        return dialogResult;
    }

    private boolean showCustomInputDialog(Object[] message, String title) {
        JDialog dialog = createInputDialog(message, title);
        dialog.setVisible(true);
        return dialogResult;
    }

    private JDialog createCustomDialog(String message, String title, int messageType) {
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);

        dialog.getRootPane().setDefaultButton(okButton);

        return dialog;
    }

    private JDialog createConfirmationDialog(String message) {
        JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
        JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(btnCo);
        buttonPanel.add(btnKhong);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Xác nhận", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);

        btnCo.addActionListener(e -> {
            dialogResult = true;
            dialog.dispose();
        });

        btnKhong.addActionListener(e -> {
            dialogResult = false;
            dialog.dispose();
        });

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dialogResult = false;
                dialog.dispose();
            }
        });

        dialog.getRootPane().setDefaultButton(btnKhong);

        return dialog;
    }

    private JDialog createInputDialog(Object[] message, String title) {
        JButton btnOK = createStyledButton("OK", COLOR_BUTTON);
        JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166));

        // Tạo panel chứa các component input với kích thước lớn hơn
        JPanel inputPanel = new JPanel(new GridLayout(message.length / 2, 2, 10, 15));
        inputPanel.setBackground(COLOR_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Định nghĩa font và kích thước lớn hơn
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(250, 35);

        for (int i = 0; i < message.length; i += 2) {
            JLabel label = new JLabel(message[i].toString());
            label.setForeground(COLOR_TEXT);
            label.setFont(labelFont);
            inputPanel.add(label);

            if (message[i + 1] instanceof JTextField) {
                JTextField textField = (JTextField) message[i + 1];
                textField.setFont(fieldFont);
                textField.setPreferredSize(fieldSize);
                textField.setMinimumSize(fieldSize);
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
                inputPanel.add(textField);
            } else if (message[i + 1] instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) message[i + 1];
                comboBox.setFont(fieldFont);
                comboBox.setPreferredSize(fieldSize);
                comboBox.setMinimumSize(fieldSize);
                comboBox.setBackground(Color.WHITE);
                comboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (c instanceof JLabel) {
                            ((JLabel) c).setFont(fieldFont);
                        }
                        return c;
                    }
                });
                inputPanel.add(comboBox);
            }
        }

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);

        // Tăng kích thước nút
        btnOK.setPreferredSize(new Dimension(100, 40));
        btnOK.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));

        buttonPanel.add(btnOK);
        buttonPanel.add(btnHuy);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(panel);
        dialog.pack();

        // Đặt kích thước tối thiểu cho dialog
        dialog.setMinimumSize(new Dimension(500, 350));
        dialog.setSize(new Dimension(500, 350));

        dialog.setLocationRelativeTo(view);
        dialog.setResizable(true);

        // Thêm Enter key listener cho các text field
        ActionListener okAction = e -> {
            dialogResult = true;
            dialog.dispose();
        };

        btnOK.addActionListener(okAction);

        // Cho phép nhấn Enter để xác nhận
        for (int i = 0; i < message.length; i += 2) {
            if (message[i + 1] instanceof JTextField) {
                JTextField textField = (JTextField) message[i + 1];
                textField.addActionListener(okAction);
            }
        }

        btnHuy.addActionListener(e -> {
            dialogResult = false;
            dialog.dispose();
        });

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dialogResult = false;
                dialog.dispose();
            }
        });

        dialog.getRootPane().setDefaultButton(btnOK);

        return dialog;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker().darker(), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }
}
