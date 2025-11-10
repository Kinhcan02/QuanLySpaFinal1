package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class QuanLyChiTieuPanel extends JPanel {

    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_PANEL = new Color(0xF0, 0xF8, 0xF0);

    private JTable tblChiTieu;
    private DefaultTableModel modelChiTieu;
    private JTextField txtSoTien, txtMucDich;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnThongKe;
    private JLabel lblTongChi, lblChiNguyenLieu, lblChiDien, lblChiNuoc, lblChiVeSinh, lblChiWifi, lblChiKhac;
    private JComboBox<String> cboLoaiChi;
    private JSpinner spnThang, spnNam;
    private JSpinner spnThangInput, spnNamInput; // Thêm spinner cho form nhập

    public QuanLyChiTieuPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Search and Stats panel
        JPanel pnSearchStats = new JPanel(new BorderLayout());
        pnSearchStats.setBackground(COLOR_BACKGROUND);

        JPanel pnSearch = createSearchPanel();
        JPanel pnStats = createStatsPanel();

        pnSearchStats.add(pnSearch, BorderLayout.NORTH);
        pnSearchStats.add(pnStats, BorderLayout.CENTER);

        add(pnSearchStats, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane sp = new JScrollPane(tblChiTieu);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiêu"));
        add(sp, BorderLayout.CENTER);

        // Input panel
        JPanel pnInput = createInputPanel();
        add(pnInput, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ CHI TIÊU - SPA/BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm theo tháng/năm"));

        // Tháng
        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblThang);

        spnThang = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThang.setPreferredSize(new Dimension(60, 25));
        panel.add(spnThang);

        // Năm
        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblNam);

        int currentYear = LocalDate.now().getYear();
        spnNam = new JSpinner(new SpinnerNumberModel(currentYear, 2020, currentYear + 5, 1));
        spnNam.setPreferredSize(new Dimension(80, 25));
        panel.add(spnNam);

        // Nút thống kê
        btnThongKe = createStyledButton("Thống kê", COLOR_BUTTON);
        panel.add(btnThongKe);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 7, 10, 5));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê chi tiêu"));

        // Hàng 1 - Labels
        panel.add(createStatsLabel("Tổng chi:"));
        panel.add(createStatsLabel("Chi nguyên liệu:"));
        panel.add(createStatsLabel("Chi điện:"));
        panel.add(createStatsLabel("Chi nước:"));
        panel.add(createStatsLabel("Chi vệ sinh:"));
        panel.add(createStatsLabel("Chi wifi:"));
        panel.add(createStatsLabel("Chi khác:"));

        // Hàng 2 - Values
        lblTongChi = createStatsValueLabel("0 VND", Color.RED);
        lblChiNguyenLieu = createStatsValueLabel("0 VND", Color.BLUE);
        lblChiDien = createStatsValueLabel("0 VND", new Color(0x2E, 0xCC, 0x71));
        lblChiNuoc = createStatsValueLabel("0 VND", new Color(0xF3, 0x9C, 0x12));
        lblChiVeSinh = createStatsValueLabel("0 VND", new Color(0xE7, 0x4C, 0x3C));
        lblChiWifi = createStatsValueLabel("0 VND", new Color(0x34, 0x98, 0xDB));
        lblChiKhac = createStatsValueLabel("0 VND", new Color(0x8E, 0x44, 0xAD));

        panel.add(lblTongChi);
        panel.add(lblChiNguyenLieu);
        panel.add(lblChiDien);
        panel.add(lblChiNuoc);
        panel.add(lblChiVeSinh);
        panel.add(lblChiWifi);
        panel.add(lblChiKhac);

        return panel;
    }

    private JLabel createStatsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JLabel createStatsValueLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(color);
        return label;
    }

    private void createTable() {
        String[] cols = {"Mã chi", "Tháng", "Năm", "Ngày chi", "Số tiền", "Mục đích", "Loại chi", "Ngày tạo"};
        modelChiTieu = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTieu = new JTable(modelChiTieu);
        styleTable(tblChiTieu);

        // Set column widths
        tblChiTieu.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblChiTieu.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblChiTieu.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblChiTieu.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblChiTieu.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblChiTieu.getColumnModel().getColumn(5).setPreferredWidth(200);
        tblChiTieu.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblChiTieu.getColumnModel().getColumn(7).setPreferredWidth(100);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiêu"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Tháng, Năm, Số tiền
        gbc.gridy = 0;
        
        // Tháng
        gbc.gridx = 0;
        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblThang, gbc);

        gbc.gridx = 1;
        spnThangInput = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThangInput.setPreferredSize(new Dimension(60, 25));
        panel.add(spnThangInput, gbc);

        // Năm
        gbc.gridx = 2;
        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblNam, gbc);

        gbc.gridx = 3;
        int currentYear = LocalDate.now().getYear();
        spnNamInput = new JSpinner(new SpinnerNumberModel(currentYear, 2020, currentYear + 5, 1));
        spnNamInput.setPreferredSize(new Dimension(80, 25));
        panel.add(spnNamInput, gbc);

        // Số tiền
        gbc.gridx = 4;
        JLabel lblSoTien = new JLabel("Số tiền (VND):");
        lblSoTien.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblSoTien, gbc);

        gbc.gridx = 5;
        txtSoTien = new JTextField();
        txtSoTien.setPreferredSize(new Dimension(150, 25));
        panel.add(txtSoTien, gbc);

        // Hàng 2: Loại chi, Mục đích
        gbc.gridy = 1;
        
        // Loại chi
        gbc.gridx = 0;
        JLabel lblLoaiChi = new JLabel("Loại chi:");
        lblLoaiChi.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLoaiChi, gbc);

        gbc.gridx = 1;
        cboLoaiChi = new JComboBox<>(new String[]{"Nguyên liệu", "Điện", "Nước", "Vệ sinh", "Wifi", "Khác"});
        cboLoaiChi.setPreferredSize(new Dimension(150, 25));
        panel.add(cboLoaiChi, gbc);

        // Mục đích
        gbc.gridx = 2;
        JLabel lblMucDich = new JLabel("Mục đích:");
        lblMucDich.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblMucDich, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        txtMucDich = new JTextField();
        txtMucDich.setPreferredSize(new Dimension(400, 25));
        panel.add(txtMucDich, gbc);

        // Hàng 3: Buttons
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 6;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);

        btnThem = createStyledButton("Thêm", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm mới", COLOR_BUTTON);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setSelectionBackground(COLOR_BUTTON);
        table.setSelectionForeground(COLOR_TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_BUTTON);
        table.getTableHeader().setForeground(COLOR_TEXT);
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

    // Getter methods mới
    public JSpinner getSpnThangInput() {
        return spnThangInput;
    }

    public JSpinner getSpnNamInput() {
        return spnNamInput;
    }

    // Các getter methods cũ
    public JTable getTblChiTieu() {
        return tblChiTieu;
    }

    public DefaultTableModel getModelChiTieu() {
        return modelChiTieu;
    }

    public JTextField getTxtSoTien() {
        return txtSoTien;
    }

    public JTextField getTxtMucDich() {
        return txtMucDich;
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

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JComboBox<String> getCboLoaiChi() {
        return cboLoaiChi;
    }

    public JSpinner getSpnThang() {
        return spnThang;
    }

    public JSpinner getSpnNam() {
        return spnNam;
    }

    public JLabel getLblTongChi() {
        return lblTongChi;
    }

    public JLabel getLblChiNguyenLieu() {
        return lblChiNguyenLieu;
    }

    public JLabel getLblChiDien() {
        return lblChiDien;
    }

    public JLabel getLblChiNuoc() {
        return lblChiNuoc;
    }

    public JLabel getLblChiVeSinh() {
        return lblChiVeSinh;
    }

    public JLabel getLblChiWifi() {
        return lblChiWifi;
    }

    public JLabel getLblChiKhac() {
        return lblChiKhac;
    }
}