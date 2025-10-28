package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class QuanLyCaLamView extends JPanel {
    
    // Components
    private JTable tblCaLam;
    private DefaultTableModel tableModel;
    
    private JTextField txtMaCa;
    private JTextField txtMaNhanVien;
    private JTextField txtNgayLam;
    private JTextField txtGioBatDau;
    private JTextField txtGioKetThuc;
    private JTextField txtSoGioLam;
    private JTextField txtSoGioTangCa;
    private JTextField txtSoLuongKhach;
    private JTextField txtTienTip;
    
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnTimKiem;
    private JButton btnTinhGioTuDong;
    
    private JComboBox<String> cboTimKiem;
    private JTextField txtTimKiem;
    
    
    // Màu sắc (giống QuanLyNhanVienView)
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff
    
    public QuanLyCaLamView() {
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        // Initialize table
        String[] columnNames = {
            "Mã Ca", "Mã NV", "Ngày Làm", "Giờ Bắt Đầu", "Giờ Kết Thúc", 
            "Số Giờ", "Tăng Ca", "Số Khách", "Tiền Tip"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCaLam = new JTable(tableModel);
        tblCaLam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCaLam.setRowHeight(25);
        
        // Initialize text fields với giá trị mặc định
        txtMaCa = createStyledTextField(8, false);
        txtMaNhanVien = createStyledTextField(8, true);
        txtNgayLam = createStyledTextField(10, true);
        txtGioBatDau = createStyledTextField(8, true);
        txtGioKetThuc = createStyledTextField(8, true);
        txtSoGioLam = createStyledTextField(6, false); // Tính tự động
        txtSoGioTangCa = createStyledTextField(6, true);
        txtSoLuongKhach = createStyledTextField(6, true);
        txtTienTip = createStyledTextField(8, true);
        
        // Đặt giá trị mặc định
        txtNgayLam.setText(LocalDate.now().toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtSoLuongKhach.setText("0");
        txtTienTip.setText("0");
        txtSoGioTangCa.setText("0");
        
        // Initialize buttons - SỬ DỤNG CÙNG STYLE VỚI QUANLYNHANVIENVIEW
        btnThem = createStyledButton("Thêm mới", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm mới", COLOR_BUTTON);
        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTinhGioTuDong = createStyledButton("Tính Giờ", COLOR_BUTTON);
        
        // Set preferred size cho các button chính
        Dimension buttonSize = new Dimension(90, 30);
        btnThem.setPreferredSize(buttonSize);
        btnSua.setPreferredSize(buttonSize);
        btnXoa.setPreferredSize(buttonSize);
        btnLamMoi.setPreferredSize(buttonSize);
        btnTimKiem.setPreferredSize(new Dimension(100, 25));
        btnTinhGioTuDong.setPreferredSize(new Dimension(100, 30));
        
        // Initialize search components
        cboTimKiem = new JComboBox<>(new String[]{"Tất cả", "Theo mã NV", "Theo ngày", "Theo tháng"});
        styleComboBox(cboTimKiem);
        txtTimKiem = createStyledTextField(15, true);
    }
    
    private JTextField createStyledTextField(int columns, boolean editable) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 11));
        textField.setEditable(editable);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BUTTON),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        return textField;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        comboBox.setPreferredSize(new Dimension(120, 25));
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(COLOR_BUTTON);
        return label;
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel tiêu đề
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BACKGROUND);
        
        // Search panel
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        
        // Table panel
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Form panel
        mainPanel.add(createFormPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        pnTitle.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel lblTitle = new JLabel("QUẢN LÝ CA LÀM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(COLOR_BACKGROUND);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BUTTON, 1), 
            "Tìm kiếm nhanh",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            COLOR_BUTTON
        ));
        
        searchPanel.add(createStyledLabel("Tìm kiếm:"));
        searchPanel.add(cboTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        searchPanel.add(btnLamMoi);
        
        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(COLOR_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BUTTON, 1), 
            "Danh sách ca làm",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            COLOR_BUTTON
        ));
        
        // Cấu hình bảng
        tblCaLam.setFillsViewportHeight(true);
        tblCaLam.setRowHeight(30);
        tblCaLam.setSelectionBackground(COLOR_BUTTON);
        tblCaLam.setSelectionForeground(COLOR_TEXT);
        tblCaLam.setFont(new Font("Arial", Font.PLAIN, 11));
        tblCaLam.setForeground(Color.BLACK);
        tblCaLam.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tblCaLam.getTableHeader().setBackground(COLOR_BUTTON);
        tblCaLam.getTableHeader().setForeground(COLOR_TEXT);
        
        JScrollPane scrollPane = new JScrollPane(tblCaLam);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BUTTON, 1), 
            "Thông tin ca làm - Nhập nhanh",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            COLOR_BUTTON
        ));
        
        // Form fields panel - Layout đơn giản hóa
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createStyledLabel("Mã NV*:"), gbc);
        
        gbc.gridx = 1;
        fieldsPanel.add(txtMaNhanVien, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Ngày*:"), gbc);
        
        gbc.gridx = 3;
        JPanel ngayPanel = new JPanel(new BorderLayout());
        ngayPanel.setBackground(COLOR_BACKGROUND);
        ngayPanel.add(txtNgayLam, BorderLayout.CENTER);
        fieldsPanel.add(ngayPanel, gbc);
        
        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createStyledLabel("Giờ bắt đầu*:"), gbc);
        
        gbc.gridx = 1;
        fieldsPanel.add(txtGioBatDau, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Giờ kết thúc*:"), gbc);
        
        gbc.gridx = 3;
        fieldsPanel.add(txtGioKetThuc, gbc);
        
        gbc.gridx = 4;
        fieldsPanel.add(btnTinhGioTuDong, gbc);
        
        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createStyledLabel("Số giờ:"), gbc);
        
        gbc.gridx = 1;
        fieldsPanel.add(txtSoGioLam, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Tăng ca:"), gbc);
        
        gbc.gridx = 3;
        fieldsPanel.add(txtSoGioTangCa, gbc);
        
        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(createStyledLabel("Số khách:"), gbc);
        
        gbc.gridx = 1;
        fieldsPanel.add(txtSoLuongKhach, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Tiền tip:"), gbc);
        
        gbc.gridx = 3;
        fieldsPanel.add(txtTienTip, gbc);
        
        // Button panel - CHỈ CÓ 4 NÚT CHÍNH: THÊM, SỬA, XÓA, LÀM MỚI
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    // Getter methods
    public JTable getTblCaLam() { return tblCaLam; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtMaCa() { return txtMaCa; }
    public JTextField getTxtMaNhanVien() { return txtMaNhanVien; }
    public JTextField getTxtNgayLam() { return txtNgayLam; }
    public JTextField getTxtGioBatDau() { return txtGioBatDau; }
    public JTextField getTxtGioKetThuc() { return txtGioKetThuc; }
    public JTextField getTxtSoGioLam() { return txtSoGioLam; }
    public JTextField getTxtSoGioTangCa() { return txtSoGioTangCa; }
    public JTextField getTxtSoLuongKhach() { return txtSoLuongKhach; }
    public JTextField getTxtTienTip() { return txtTienTip; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JButton getBtnTinhGioTuDong() { return btnTinhGioTuDong; }
    public JComboBox<String> getCboTimKiem() { return cboTimKiem; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    
    // Utility methods
    public void clearForm() {
        txtMaCa.setText("");
        txtMaNhanVien.setText("");
        txtNgayLam.setText(LocalDate.now().toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtSoGioLam.setText("");
        txtSoGioTangCa.setText("0");
        txtSoLuongKhach.setText("0");
        txtTienTip.setText("0");
    }
    
    public void setFormEditable(boolean editable) {
        txtMaNhanVien.setEditable(editable);
        txtNgayLam.setEditable(editable);
        txtGioBatDau.setEditable(editable);
        txtGioKetThuc.setEditable(editable);
        txtSoGioTangCa.setEditable(editable);
        txtSoLuongKhach.setEditable(editable);
        txtTienTip.setEditable(editable);
    }
    
    public void setButtonState(boolean them, boolean sua, boolean xoa) {
        btnThem.setEnabled(them);
        btnSua.setEnabled(sua);
        btnXoa.setEnabled(xoa);
    }
    
    public void showAutoCalculatedHours(double hours) {
        txtSoGioLam.setText(String.format("%.2f", hours));
    }
}