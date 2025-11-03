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
    private JComboBox<NhanVien> cboNhanVien;
    private JTextField txtNgayLam;
    private JTextField txtGioBatDau;
    private JTextField txtGioKetThuc;
    private JTextField txtSoGioLam;
    private JTextField txtSoGioTangCa;
    private JTextField txtTienTip;
    private JTextArea txtGhiChuTip;

    // Buttons
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnThemTip;

    // Current date
    private LocalDate currentDate;
    private LocalDate selectedDate;

    // Colors
    private final Color COLOR_BACKGROUND = new Color(0xF0, 0xF8, 0xF0);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_SELECTED = new Color(0x3A, 0x7B, 0x47);
    private final Color COLOR_TODAY = new Color(0xE8, 0xF5, 0xE8);

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
            "Nhân Viên", "Ngày Làm", "Giờ Bắt Đầu", "Giờ Kết Thúc",
            "Số Giờ", "Tăng Ca", "Tiền Tip"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCaLam = new JTable(tableModel);
        tblCaLam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCaLam.setRowHeight(35);

        // Initialize form components
        cboNhanVien = new JComboBox<>();
        styleComboBox(cboNhanVien);
        txtNgayLam = createStyledTextField(12, true);
        txtGioBatDau = createStyledTextField(8, true);
        txtGioKetThuc = createStyledTextField(8, true);
        txtSoGioLam = createStyledTextField(8, false);
        txtSoGioTangCa = createStyledTextField(8, true);
        txtTienTip = createStyledTextField(12, true);
        txtGhiChuTip = new JTextArea(3, 20);
        txtGhiChuTip.setLineWrap(true);
        txtGhiChuTip.setWrapStyleWord(true);
        txtGhiChuTip.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        txtGhiChuTip.setFont(new Font("Arial", Font.PLAIN, 12));

        // Set default values
        txtNgayLam.setText(selectedDate.toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtTienTip.setText("0");
        txtSoGioTangCa.setText("0");

        // Initialize buttons
        btnThem = createStyledButton("Thêm Ca", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm Mới", COLOR_BUTTON);
        btnThemTip = createStyledButton("Thêm Tip", new Color(0xFF, 0xA5, 0x00));
    }

    private void initCalendar() {
        pnCalendar = new JPanel(new BorderLayout());
        pnCalendar.setBackground(COLOR_BACKGROUND);
        pnCalendar.setPreferredSize(new Dimension(350, 280));
        pnCalendar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Header with month navigation
        JPanel pnHeader = new JPanel(new BorderLayout());
        pnHeader.setBackground(COLOR_BACKGROUND);
        pnHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        btnThangTruoc = createStyledButton("←", COLOR_BUTTON);
        btnThangSau = createStyledButton("→", COLOR_BUTTON);

        lblThangNam = new JLabel("", JLabel.CENTER);
        lblThangNam.setFont(new Font("Arial", Font.BOLD, 16));
        lblThangNam.setForeground(COLOR_BUTTON);

        pnHeader.add(btnThangTruoc, BorderLayout.WEST);
        pnHeader.add(lblThangNam, BorderLayout.CENTER);
        pnHeader.add(btnThangSau, BorderLayout.EAST);

        // Days panel
        pnNgay = new JPanel(new GridLayout(0, 7, 3, 3));
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
            JLabel lblDay = createDayHeaderLabel(day);
            pnNgay.add(lblDay);
        }

        // Get first day of month
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Convert to Sunday start

        // Add empty cells for days before first day
        for (int i = 0; i < dayOfWeek; i++) {
            JLabel emptyLabel = createEmptyDayLabel();
            pnNgay.add(emptyLabel);
        }

        // Add days
        int daysInMonth = currentDate.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JButton btnDay = createDayButton(day);
            pnNgay.add(btnDay);
        }

        // Fill remaining cells to maintain grid structure
        int totalCells = dayOfWeek + daysInMonth;
        int remainingCells = 42 - totalCells; // 6 rows x 7 columns = 42 cells
        for (int i = 0; i < remainingCells; i++) {
            JLabel emptyLabel = createEmptyDayLabel();
            pnNgay.add(emptyLabel);
        }

        pnNgay.revalidate();
        pnNgay.repaint();
    }

    private JLabel createDayHeaderLabel(String day) {
        JLabel lblDay = new JLabel(day, JLabel.CENTER);
        lblDay.setFont(new Font("Arial", Font.BOLD, 12));
        lblDay.setForeground(COLOR_BUTTON);
        lblDay.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        lblDay.setOpaque(true);
        lblDay.setBackground(new Color(0xE8, 0xF5, 0xE8));
        return lblDay;
    }

    private JLabel createEmptyDayLabel() {
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(40, 40));
        emptyLabel.setOpaque(true);
        emptyLabel.setBackground(COLOR_BACKGROUND);
        return emptyLabel;
    }

    private JButton createDayButton(int day) {
        LocalDate buttonDate = currentDate.withDayOfMonth(day);
        JButton btnDay = new JButton(String.valueOf(day));

        // Fix kích thước cố định để không bị "..."
        btnDay.setPreferredSize(new Dimension(40, 40));
        btnDay.setMinimumSize(new Dimension(40, 40));
        btnDay.setMaximumSize(new Dimension(40, 40));
        btnDay.setFont(new Font("Arial", Font.PLAIN, 12));
        btnDay.setFocusPainted(false);
        btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Căn giữa nội dung
        btnDay.setHorizontalAlignment(SwingConstants.CENTER);
        btnDay.setVerticalAlignment(SwingConstants.CENTER);

        // Style based on date
        if (buttonDate.equals(selectedDate)) {
            btnDay.setBackground(COLOR_SELECTED);
            btnDay.setForeground(COLOR_TEXT);
            btnDay.setBorder(BorderFactory.createLineBorder(COLOR_SELECTED.darker(), 2));
        } else if (buttonDate.equals(LocalDate.now())) {
            btnDay.setBackground(COLOR_TODAY);
            btnDay.setForeground(Color.BLACK);
            btnDay.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON, 1));
        } else {
            btnDay.setBackground(Color.WHITE);
            btnDay.setForeground(Color.BLACK);
            btnDay.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
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
        setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Title panel
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BACKGROUND);

        // Left panel with calendar
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COLOR_BACKGROUND);
        leftPanel.setPreferredSize(new Dimension(380, 320));

        leftPanel.add(pnCalendar, BorderLayout.CENTER);

        // Right panel with table and form
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(COLOR_BACKGROUND);

        rightPanel.add(createTablePanel(), BorderLayout.CENTER);
        rightPanel.add(createFormPanel(), BorderLayout.SOUTH);

        // Split pane for calendar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.3);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        pnTitle.setPreferredSize(new Dimension(getWidth(), 45));

        JLabel lblTitle = new JLabel("QUẢN LÝ CA LÀM - LỊCH LÀM VIỆC");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
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
        tblCaLam.setRowHeight(35);
        tblCaLam.setSelectionBackground(COLOR_BUTTON);
        tblCaLam.setSelectionForeground(COLOR_TEXT);
        tblCaLam.setFont(new Font("Arial", Font.PLAIN, 12));
        tblCaLam.setForeground(Color.BLACK);
        tblCaLam.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblCaLam.getTableHeader().setBackground(COLOR_BUTTON);
        tblCaLam.getTableHeader().setForeground(COLOR_TEXT);
        tblCaLam.getTableHeader().setPreferredSize(new Dimension(0, 35));

        JScrollPane scrollPane = new JScrollPane(tblCaLam);
        scrollPane.setPreferredSize(new Dimension(800, 250));
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
        gbc.insets = new Insets(8, 8, 8, 8);

        // Row 0 - Nhân viên và Ngày
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(createStyledLabel("Nhân Viên*:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cboNhanVien.setPreferredSize(new Dimension(200, 30));
        fieldsPanel.add(cboNhanVien, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        fieldsPanel.add(createStyledLabel("Ngày*:"), gbc);
        gbc.gridx = 4;
        txtNgayLam.setPreferredSize(new Dimension(120, 30));
        fieldsPanel.add(txtNgayLam, gbc);

        // Row 1 - Giờ bắt đầu và kết thúc
        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(createStyledLabel("Giờ bắt đầu*:"), gbc);
        gbc.gridx = 1;
        txtGioBatDau.setPreferredSize(new Dimension(80, 30));
        fieldsPanel.add(txtGioBatDau, gbc);

        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Giờ kết thúc*:"), gbc);
        gbc.gridx = 3;
        txtGioKetThuc.setPreferredSize(new Dimension(80, 30));
        fieldsPanel.add(txtGioKetThuc, gbc);

        gbc.gridx = 4;
        fieldsPanel.add(createStyledLabel("Số giờ:"), gbc);
        gbc.gridx = 5;
        txtSoGioLam.setPreferredSize(new Dimension(80, 30));
        fieldsPanel.add(txtSoGioLam, gbc);

        // Row 2 - Tăng ca và Tiền Tip
        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(createStyledLabel("Tăng ca:"), gbc);
        gbc.gridx = 1;
        txtSoGioTangCa.setPreferredSize(new Dimension(80, 30));
        fieldsPanel.add(txtSoGioTangCa, gbc);

        gbc.gridx = 2;
        fieldsPanel.add(createStyledLabel("Tiền Tip:"), gbc);
        gbc.gridx = 3;
        txtTienTip.setPreferredSize(new Dimension(120, 30));
        fieldsPanel.add(txtTienTip, gbc);

        // Row 3 - Ghi chú tip
        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(createStyledLabel("Ghi chú tip:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JScrollPane scrollTip = new JScrollPane(txtGhiChuTip);
        scrollTip.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(scrollTip, gbc);

        // Row 4 - Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        fieldsPanel.add(createButtonPanel(), gbc);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(COLOR_BACKGROUND);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnThemTip);

        return buttonPanel;
    }

    // Helper methods
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createStyledTextField(int columns, boolean editable) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setEditable(editable);
        textField.setBackground(editable ? Color.WHITE : new Color(240, 240, 240));
        return textField;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON, 1));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Getters
    public JTable getTblCaLam() {
        return tblCaLam;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JComboBox<NhanVien> getCboNhanVien() {
        return cboNhanVien;
    }

    public JTextField getTxtNgayLam() {
        return txtNgayLam;
    }

    public JTextField getTxtGioBatDau() {
        return txtGioBatDau;
    }

    public JTextField getTxtGioKetThuc() {
        return txtGioKetThuc;
    }

    public JTextField getTxtSoGioLam() {
        return txtSoGioLam;
    }

    public JTextField getTxtSoGioTangCa() {
        return txtSoGioTangCa;
    }

    public JTextField getTxtTienTip() {
        return txtTienTip;
    }

    public JTextArea getTxtGhiChuTip() {
        return txtGhiChuTip;
    }

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JButton getBtnThemTip() {
        return btnThemTip;
    }

    public JButton getBtnThangTruoc() {
        return btnThangTruoc;
    }

    public JButton getBtnThangSau() {
        return btnThangSau;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
        updateCalendar();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        txtNgayLam.setText(selectedDate.toString());
        updateCalendar();
    }

    // Date selection callback
    public interface DateSelectionListener {

        void onDateSelected(LocalDate selectedDate);
    }

    private DateSelectionListener onDateSelected;

    public void setOnDateSelected(DateSelectionListener listener) {
        this.onDateSelected = listener;
    }

    // Public methods
    public void loadNhanVienList(List<NhanVien> nhanVienList) {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : nhanVienList) {
            cboNhanVien.addItem(nv);
        }
    }

    public void setFormEditable(boolean editable) {
        cboNhanVien.setEnabled(editable);
        txtNgayLam.setEditable(editable);
        txtGioBatDau.setEditable(editable);
        txtGioKetThuc.setEditable(editable);
        txtSoGioTangCa.setEditable(editable);
        txtTienTip.setEditable(editable);
        txtGhiChuTip.setEditable(editable);
    }

    public void setButtonState(boolean themEnabled, boolean suaEnabled, boolean xoaEnabled) {
        btnThem.setEnabled(themEnabled);
        btnSua.setEnabled(suaEnabled);
        btnXoa.setEnabled(xoaEnabled);
    }

    public void clearForm() {
        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }
        txtNgayLam.setText(selectedDate.toString());
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("17:00");
        txtSoGioLam.setText("");
        txtSoGioTangCa.setText("0");
        txtTienTip.setText("0");
        txtGhiChuTip.setText("");
        tblCaLam.clearSelection();
    }

    public void showAutoCalculatedHours(double hours) {
        txtSoGioLam.setText(String.format("%.1f", hours));
    }
}
