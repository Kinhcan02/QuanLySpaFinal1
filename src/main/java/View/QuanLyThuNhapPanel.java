package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.math.BigDecimal;

public class QuanLyThuNhapPanel extends JPanel {
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_PANEL = new Color(0xF0, 0xF8, 0xF0);

    private JTable tblThuNhap;
    private DefaultTableModel modelThuNhap;
    private JTextField txtTongDoanhThu, txtTongLuong, txtGhiChu;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTinhToan;
    private JLabel lblTongDoanhThu, lblTongLuong, lblThuNhapThuc;
    private JSpinner spnThang, spnNam;

    public QuanLyThuNhapPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Search panel
        JPanel pnSearch = createSearchPanel();
        add(pnSearch, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane sp = new JScrollPane(tblThuNhap);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách thu nhập"));
        add(sp, BorderLayout.CENTER);

        // Input panel
        JPanel pnInput = createInputPanel();
        add(pnInput, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ THU NHẬP - SPA/BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm và thống kê"));

        // Tháng
        panel.add(new JLabel("Tháng:"));
        spnThang = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThang.setPreferredSize(new Dimension(60, 25));
        panel.add(spnThang);

        // Năm
        panel.add(new JLabel("Năm:"));
        spnNam = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        spnNam.setPreferredSize(new Dimension(80, 25));
        panel.add(spnNam);

        // Nút thống kê
        btnTinhToan = createStyledButton("Tính toán tự động", COLOR_BUTTON);
        panel.add(btnTinhToan);

        // Thống kê chi tiết
        JPanel pnStats = new JPanel(new GridLayout(1, 6, 10, 5));
        pnStats.setBackground(COLOR_BACKGROUND);
        
        pnStats.add(new JLabel("Tổng doanh thu:"));
        lblTongDoanhThu = new JLabel("0 VND");
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongDoanhThu.setForeground(Color.BLUE);
        pnStats.add(lblTongDoanhThu);

        pnStats.add(new JLabel("Tổng lương:"));
        lblTongLuong = new JLabel("0 VND");
        lblTongLuong.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongLuong.setForeground(Color.RED);
        pnStats.add(lblTongLuong);

        pnStats.add(new JLabel("Thu nhập thực:"));
        lblThuNhapThuc = new JLabel("0 VND");
        lblThuNhapThuc.setFont(new Font("Arial", Font.BOLD, 12));
        lblThuNhapThuc.setForeground(new Color(0x8E, 0x44, 0xAD));
        pnStats.add(lblThuNhapThuc);

        panel.add(Box.createHorizontalStrut(20));
        panel.add(pnStats);

        return panel;
    }

    private void createTable() {
        String[] cols = {"Mã thu", "Tháng", "Năm", "Tổng doanh thu", "Tổng lương", "Thu nhập thực", "Ngày tính", "Ghi chú"};
        modelThuNhap = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThuNhap = new JTable(modelThuNhap);
        styleTable(tblThuNhap);

        // Set column widths
        tblThuNhap.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblThuNhap.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblThuNhap.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblThuNhap.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblThuNhap.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblThuNhap.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblThuNhap.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblThuNhap.getColumnModel().getColumn(7).setPreferredWidth(150);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thu nhập"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Tháng, Năm, Tổng doanh thu
        gbc.gridy = 0;
        
        // Tháng
        gbc.gridx = 0;
        panel.add(new JLabel("Tháng:"), gbc);

        gbc.gridx = 1;
        JSpinner spnThangInput = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThangInput.setPreferredSize(new Dimension(60, 25));
        panel.add(spnThangInput, gbc);

        // Năm
        gbc.gridx = 2;
        panel.add(new JLabel("Năm:"), gbc);

        gbc.gridx = 3;
        JSpinner spnNamInput = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        spnNamInput.setPreferredSize(new Dimension(80, 25));
        panel.add(spnNamInput, gbc);

        // Tổng doanh thu
        gbc.gridx = 4;
        panel.add(new JLabel("Tổng doanh thu:"), gbc);

        gbc.gridx = 5;
        txtTongDoanhThu = new JTextField();
        txtTongDoanhThu.setPreferredSize(new Dimension(120, 25));
        panel.add(txtTongDoanhThu, gbc);

        // Hàng 2: Tổng lương, Ghi chú
        gbc.gridy = 1;
        
        // Tổng lương
        gbc.gridx = 0;
        panel.add(new JLabel("Tổng lương:"), gbc);

        gbc.gridx = 1;
        txtTongLuong = new JTextField();
        txtTongLuong.setPreferredSize(new Dimension(120, 25));
        panel.add(txtTongLuong, gbc);

        // Ghi chú
        gbc.gridx = 2;
        panel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        txtGhiChu = new JTextField();
        txtGhiChu.setPreferredSize(new Dimension(200, 25));
        panel.add(txtGhiChu, gbc);

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

    // Getter methods
    public JTable getTblThuNhap() { return tblThuNhap; }
    public DefaultTableModel getModelThuNhap() { return modelThuNhap; }
    public JTextField getTxtTongDoanhThu() { return txtTongDoanhThu; }
    public JTextField getTxtTongLuong() { return txtTongLuong; }
    public JTextField getTxtGhiChu() { return txtGhiChu; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTinhToan() { return btnTinhToan; }
    public JSpinner getSpnThang() { return spnThang; }
    public JSpinner getSpnNam() { return spnNam; }
    public JLabel getLblTongDoanhThu() { return lblTongDoanhThu; }
    public JLabel getLblTongLuong() { return lblTongLuong; }
    public JLabel getLblThuNhapThuc() { return lblThuNhapThuc; }
}