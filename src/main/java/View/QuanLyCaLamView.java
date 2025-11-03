package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import Model.NhanVien;

public class QuanLyCaLamView extends JPanel {
    
    // Components
    private JTable tblCaLam;
    private DefaultTableModel tableModel;
    
    // Calendar components
    private JPanel pnCalendar;
    private JLabel lblThangNam;
    private JButton btnThangTruoc, btnThangSau;
    private JPanel pnNgay;
    
    // Form components
    private JTextField txtMaCa;
    private JComboBox<NhanVien> cboNhanVien;
    private JTextField txtNgayLam;
    private JTextField txtGioBatDau;
    private JTextField txtGioKetThuc;
    private JTextField txtSoGioLam;
    private JTextField txtSoGioTangCa;
    private JTextField txtSoLuongKhach;
    private JTextField txtTienTip;
    private JTextArea txtGhiChuTip;
    
    // Buttons
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnThemTip;
    private JButton btnXemLichSuTip;
    
    // Current date
    private LocalDate currentDate;
    private LocalDate selectedDate;
    
    // Colors
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_SELECTED = new Color(0x3A, 0x7B, 0x47);
    
    public QuanLyCaLamView() {
        this.currentDate = LocalDate.now();
        this.selectedDate = LocalDate.now();
        initComponents();
        setupUI();
        updateCalendar();
    }
    
    private void initComponents() {
        // Initialize calendar
        initCalendar();
        
        // Initialize table
        String[] columnNames = {
            "Mã Ca", "Nhân Viên", "Ngày Làm", "Giờ Bắt Đầu", "Giờ Kết Thúc", 
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
        
        // Initialize form components
        txtMaCa = createStyledTextField(8, false);
        cboNhanVien = new JComboBox<>();
        styleComboBox(cboNhanVien);
        txtNgayLam = createStyledTextField(10, true);
        txtGioBatDau = createStyledTextField(8, true);
        txtGioKetThuc = createStyledTextField(8, true);
        txtSoGioLam = createStyledTextField(6, false);
        txtSoGioTangCa = createStyledTextField(6, true);
        txtSoLuongKhach = createStyledTextField(6, true);
        txtTienTip = createStyledTextField(8, true);
        txtGhiChuTip = new JTextArea(3, 20);
        txtGhiChuTip.setLineWrap(true);
        txtGhiChuTip.setWrapStyleWord(true);
        txtGhiChuTip.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        
        // Set default values
        txtNgayLam.setText(selectedDate.toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtSoLuongKhach.setText("0");
        txtTienTip.setText("0");
        txtSoGioTangCa.setText("0");
        
        // Initialize buttons
        btnThem = createStyledButton("Thêm Ca", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm Mới", COLOR_BUTTON);
        btnThemTip = createStyledButton("Thêm Tip", new Color(0xFF, 0xA5, 0x00));
        btnXemLichSuTip = createStyledButton("Lịch Sử Tip", new Color(0x99, 0x66, 0xCC));
    }
    
    private void initCalendar() {
        pnCalendar = new JPanel(new BorderLayout());
        pnCalendar.setBackground(COLOR_BACKGROUND);
        
        // Header with month navigation
        JPanel pnHeader = new JPanel(new BorderLayout());
        pnHeader.setBackground(COLOR_BACKGROUND);
        
        btnThangTruoc = createStyledButton("←", COLOR_BUTTON);
        btnThangSau = createStyledButton("→", COLOR_BUTTON);
        lblThangNam = new JLabel("", JLabel.CENTER);
        lblThangNam.setFont(new Font("Arial", Font.BOLD, 14));
        lblThangNam.setForeground(COLOR_BUTTON);
        
        pnHeader.add(btnThangTruoc, BorderLayout.WEST);
        pnHeader.add(lblThangNam, BorderLayout.CENTER);
        pnHeader.add(btnThangSau, BorderLayout.EAST);
        
        // Days panel
        pnNgay = new JPanel(new GridLayout(0, 7, 2, 2));
        pnNgay.setBackground(COLOR_BACKGROUND);
        
        pnCalendar.add(pnHeader, BorderLayout.NORTH);
        pnCalendar.add(pnNgay, BorderLayout.CENTER);
    }
    
    private void updateCalendar() {
        pnNgay.removeAll();
        
        // Set month year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        lblThangNam.setText(currentDate.format(formatter));
        
        // Add day headers
        String[] days = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String day : days) {
            JLabel lblDay = new JLabel(day, JLabel.CENTER);
            lblDay.setFont(new Font("Arial", Font.BOLD, 11));
            lblDay.setForeground(COLOR_BUTTON);
            lblDay.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            pnNgay.add(lblDay);
        }
        
        // Get first day of month
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Convert to Sunday start
        
        // Add empty cells for days before first day
        for (int i = 0; i < dayOfWeek; i++) {
            pnNgay.add(new JLabel(""));
        }
        
        // Add days
        int daysInMonth = currentDate.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JButton btnDay = createDayButton(day);
            pnNgay.add(btnDay);
        }
        
        pnNgay.revalidate();
        pnNgay.repaint();
    }
    
    private JButton createDayButton(int day) {
        LocalDate buttonDate = currentDate.withDayOfMonth(day);
        JButton btnDay = new JButton(String.valueOf(day));
        
        btnDay.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDay.setFocusPainted(false);
        btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style based on date
        if (buttonDate.equals(selectedDate)) {
            btnDay.setBackground(COLOR_SELECTED);
            btnDay.setForeground(COLOR_TEXT);
        } else if (buttonDate.equals(LocalDate.now())) {
            btnDay.setBackground(COLOR_BUTTON);
            btnDay.setForeground(COLOR_TEXT);
        } else {
            btnDay.setBackground(Color.WHITE);
            btnDay.setForeground(Color.BLACK);
        }
        
        btnDay.addActionListener(e -> {
            selectedDate = buttonDate;
            txtNgayLam.setText(selectedDate.toString());
            updateCalendar();
            // Load ca làm cho ngày được chọn
            if (onDateSelected != null) {
                onDateSelected.onDateSelected(selectedDate);
            }
        });
        
        return btnDay;
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BACKGROUND);
        
        // Left panel with calendar
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COLOR_BACKGROUND);
        leftPanel.setPreferredSize(new Dimension(300, 400));
        
        leftPanel.add(pnCalendar, BorderLayout.CENTER);
        
        // Right panel with table and form
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(COLOR_BACKGROUND);
        
        rightPanel.add(createTablePanel(), BorderLayout.CENTER);
        rightPanel.add(createFormPanel(), BorderLayout.SOUTH);
        
        // Split pane for calendar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        pnTitle.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel lblTitle = new JLabel("QUẢN LÝ CA LÀM - LỊCH LÀM VIỆC");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
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
        
        // Configure table
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
            "Thông tin ca làm",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            COLOR_BUTTON
        ));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createStyledLabel("Mã Ca:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtMaCa, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Nhân Viên*:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(cboNhanVien, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createStyledLabel("Ngày*:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtNgayLam, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Giờ bắt đầu*:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(txtGioBatDau, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createStyledLabel("Giờ kết thúc*:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtGioKetThuc, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Số giờ:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(txtSoGioLam, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(createStyledLabel("Tăng ca:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtSoGioTangCa, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Số khách:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(txtSoLuongKhach, gbc);
        
        // Row 4 - Tip section
        gbc.gridx = 0; gbc.gridy = 4;
        fieldsPanel.add(createStyledLabel("Tiền Tip:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtTienTip, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(btnThemTip, gbc);
        gbc.gridx = 3;
        fieldsPanel.add(btnXemLichSuTip, gbc);
        
        // Row 5 - Ghi chú tip
        gbc.gridx = 0; gbc.gridy = 5;
        fieldsPanel.add(createStyledLabel("Ghi chú Tip:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChuTip);
        scrollGhiChu.setPreferredSize(new Dimension(200, 60));
        fieldsPanel.add(scrollGhiChu, gbc);
        
        // Button panel
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
    
    // Utility methods
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
    
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        comboBox.setPreferredSize(new Dimension(150, 25));
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(COLOR_BUTTON);
        return label;
    }
    
    // Interface for date selection callback
    public interface OnDateSelected {
        void onDateSelected(LocalDate selectedDate);
    }
    
    private OnDateSelected onDateSelected;
    
    public void setOnDateSelected(OnDateSelected onDateSelected) {
        this.onDateSelected = onDateSelected;
    }
    
    // Getters
    public JTable getTblCaLam() { return tblCaLam; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtMaCa() { return txtMaCa; }
    public JComboBox<NhanVien> getCboNhanVien() { return cboNhanVien; }
    public JTextField getTxtNgayLam() { return txtNgayLam; }
    public JTextField getTxtGioBatDau() { return txtGioBatDau; }
    public JTextField getTxtGioKetThuc() { return txtGioKetThuc; }
    public JTextField getTxtSoGioLam() { return txtSoGioLam; }
    public JTextField getTxtSoGioTangCa() { return txtSoGioTangCa; }
    public JTextField getTxtSoLuongKhach() { return txtSoLuongKhach; }
    public JTextField getTxtTienTip() { return txtTienTip; }
    public JTextArea getTxtGhiChuTip() { return txtGhiChuTip; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnThemTip() { return btnThemTip; }
    public JButton getBtnXemLichSuTip() { return btnXemLichSuTip; }
    public JButton getBtnThangTruoc() { return btnThangTruoc; }
    public JButton getBtnThangSau() { return btnThangSau; }
    public LocalDate getSelectedDate() { return selectedDate; }
    public LocalDate getCurrentDate() { return currentDate; }
    
    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
        updateCalendar();
    }
    
    public void clearForm() {
        txtMaCa.setText("");
        cboNhanVien.setSelectedIndex(-1);
        txtNgayLam.setText(selectedDate.toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtSoGioLam.setText("");
        txtSoGioTangCa.setText("0");
        txtSoLuongKhach.setText("0");
        txtTienTip.setText("0");
        txtGhiChuTip.setText("");
    }
    
    public void setFormEditable(boolean editable) {
        cboNhanVien.setEnabled(editable);
        txtNgayLam.setEditable(editable);
        txtGioBatDau.setEditable(editable);
        txtGioKetThuc.setEditable(editable);
        txtSoGioTangCa.setEditable(editable);
        txtSoLuongKhach.setEditable(editable);
        txtTienTip.setEditable(editable);
        txtGhiChuTip.setEditable(editable);
    }
    
    public void setButtonState(boolean them, boolean sua, boolean xoa) {
        btnThem.setEnabled(them);
        btnSua.setEnabled(sua);
        btnXoa.setEnabled(xoa);
    }
    
    public void showAutoCalculatedHours(double hours) {
        txtSoGioLam.setText(String.format("%.2f", hours));
    }
    
    public void loadNhanVienList(List<NhanVien> nhanVienList) {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : nhanVienList) {
            cboNhanVien.addItem(nv);
        }
    }
}