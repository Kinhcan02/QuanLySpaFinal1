package Controller;

import Model.LoaiNguyenLieu;
import Service.LoaiNguyenLieuService;
import View.QuanLyLoaiNguyenLieuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class QuanLyLoaiNguyenLieuController {
    private QuanLyLoaiNguyenLieuView view;
    private LoaiNguyenLieuService service;
    private DefaultTableModel model;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    // Biến để lưu kết quả từ dialog
    private boolean dialogResult;

    public QuanLyLoaiNguyenLieuController(QuanLyLoaiNguyenLieuView view) {
        this.view = view;
        this.service = new LoaiNguyenLieuService();
        this.model = view.getModel();
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnThem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                themLoaiNguyenLieu();
            }
        });

        view.getBtnSua().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suaLoaiNguyenLieu();
            }
        });

        view.getBtnXoa().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xoaLoaiNguyenLieu();
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

        view.getBtnDong().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dong();
            }
        });
    }

    private void loadData() {
        try {
            List<LoaiNguyenLieu> list = service.getAllLoaiNguyenLieu();
            model.setRowCount(0);
            for (LoaiNguyenLieu loaiNL : list) {
                model.addRow(new Object[]{
                    loaiNL.getMaLoaiNL(),
                    loaiNL.getTenLoaiNL(),
                    loaiNL.getMoTa()
                });
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themLoaiNguyenLieu() {
        try {
            JTextField txtTen = new JTextField();
            JTextArea txtMoTa = new JTextArea(3, 20);
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);

            Object[] message = {
                "Tên loại nguyên liệu:", txtTen,
                "Mô tả:", scrollMoTa
            };

            boolean confirmed = showCustomInputDialog(message, "Thêm loại nguyên liệu");
            
            if (confirmed) {
                String ten = txtTen.getText().trim();
                String moTa = txtMoTa.getText().trim();

                if (ten.isEmpty()) {
                    hienThiThongBao("Tên loại nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LoaiNguyenLieu loaiNL = new LoaiNguyenLieu(ten, moTa);
                
                boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn thêm loại nguyên liệu này?");
                
                if (xacNhan) {
                    boolean success = service.addLoaiNguyenLieu(loaiNL);
                    if (success) {
                        hienThiThongBao("Thêm loại nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        hienThiThongBao("Thêm loại nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaLoaiNguyenLieu() {
        int selectedRow = view.getTblLoaiNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một loại nguyên liệu để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maLoaiNL = (Integer) model.getValueAt(selectedRow, 0);
            String tenHienTai = (String) model.getValueAt(selectedRow, 1);
            String moTaHienTai = (String) model.getValueAt(selectedRow, 2);

            JTextField txtTen = new JTextField(tenHienTai);
            JTextArea txtMoTa = new JTextArea(moTaHienTai, 3, 20);
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);

            Object[] message = {
                "Tên loại nguyên liệu:", txtTen,
                "Mô tả:", scrollMoTa
            };

            boolean confirmed = showCustomInputDialog(message, "Sửa loại nguyên liệu");
            
            if (confirmed) {
                String ten = txtTen.getText().trim();
                String moTa = txtMoTa.getText().trim();

                if (ten.isEmpty()) {
                    hienThiThongBao("Tên loại nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LoaiNguyenLieu loaiNL = new LoaiNguyenLieu(maLoaiNL, ten, moTa);
                
                boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn sửa loại nguyên liệu này?");
                
                if (xacNhan) {
                    boolean success = service.updateLoaiNguyenLieu(loaiNL);
                    if (success) {
                        hienThiThongBao("Sửa loại nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        hienThiThongBao("Sửa loại nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLoaiNguyenLieu() {
        int selectedRow = view.getTblLoaiNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một loại nguyên liệu để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maLoaiNL = (Integer) model.getValueAt(selectedRow, 0);
            String tenLoaiNL = (String) model.getValueAt(selectedRow, 1);

            boolean xacNhan = hienThiXacNhan("Bạn có chắc chắn muốn xóa loại nguyên liệu '" + tenLoaiNL + "'?");
            
            if (xacNhan) {
                boolean success = service.deleteLoaiNguyenLieu(maLoaiNL);
                if (success) {
                    hienThiThongBao("Xóa loại nguyên liệu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    hienThiThongBao("Xóa loại nguyên liệu thất bại! Có thể đang có nguyên liệu thuộc loại này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<LoaiNguyenLieu> list = service.getAllLoaiNguyenLieu();
            model.setRowCount(0);
            for (LoaiNguyenLieu loaiNL : list) {
                if (loaiNL.getTenLoaiNL().toLowerCase().contains(keyword.toLowerCase()) ||
                    (loaiNL.getMoTa() != null && loaiNL.getMoTa().toLowerCase().contains(keyword.toLowerCase()))) {
                    model.addRow(new Object[]{
                        loaiNL.getMaLoaiNL(),
                        loaiNL.getTenLoaiNL(),
                        loaiNL.getMoTa()
                    });
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        view.getTxtTimKiem().setText("");
        loadData();
    }

    private void dong() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
        if (frame != null) {
            frame.dispose();
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM (giống với QuanLyNguyenLieuController)
    
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
        
        // Tạo panel chứa các component input
        JPanel inputPanel = new JPanel(new GridLayout(message.length / 2, 2, 5, 5));
        inputPanel.setBackground(COLOR_BACKGROUND);
        
        for (int i = 0; i < message.length; i += 2) {
            JLabel label = new JLabel(message[i].toString());
            label.setForeground(COLOR_TEXT);
            inputPanel.add(label);
            
            if (message[i + 1] instanceof JTextField) {
                inputPanel.add((JTextField) message[i + 1]);
            } else if (message[i + 1] instanceof JComboBox) {
                inputPanel.add((JComboBox<?>) message[i + 1]);
            } else if (message[i + 1] instanceof JScrollPane) {
                inputPanel.add((JScrollPane) message[i + 1]);
            }
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(btnOK);
        buttonPanel.add(btnHuy);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);

        btnOK.addActionListener(e -> {
            dialogResult = true;
            dialog.dispose();
        });

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