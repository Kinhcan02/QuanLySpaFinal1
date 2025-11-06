package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JDateChooser;

public class QuanLyThuChiView extends JPanel {

    // Colors
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_PANEL = new Color(0xF0, 0xF8, 0xF0);

    // Components
    private JTabbedPane tabbedPane;

    // Thu nhập components
    private JTable tblThuNhap;
    private DefaultTableModel modelThuNhap;
    private JTextField txtTimKiemThu, txtSoTienThu, txtNoiDungThu;
    private JDateChooser dateTuNgayThu, dateDenNgayThu, dateNgayThu;
    private JButton btnThemThu, btnSuaThu, btnXoaThu, btnLamMoiThu, btnTimKiemThu;
    private JLabel lblTongThu, lblTongThuHoaDon;
    private JComboBox<String> cboThangThu, cboNamThu;

    // Chi tiêu components
    private JTable tblChiTieu;
    private DefaultTableModel modelChiTieu;
    private JTextField txtTimKiemChi, txtSoTienChi, txtMucDichChi;
    private JDateChooser dateTuNgayChi, dateDenNgayChi, dateNgayChi;
    private JButton btnThemChi, btnSuaChi, btnXoaChi, btnLamMoiChi, btnTimKiemChi;
    private JLabel lblTongChi, lblTongChiNguyenLieu;
    private JComboBox<String> cboThangChi, cboNamChi;

    // Tổng quan components
    private JLabel lblTongThuTongQuan, lblTongChiTongQuan, lblLoiNhuanTongQuan;
    private JDateChooser dateTuNgayTQ, dateDenNgayTQ;
    private JButton btnXemBaoCao;

    public QuanLyThuChiView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);

        // Title panel
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BACKGROUND);
        tabbedPane.setForeground(COLOR_TEXT);

        // Tabs
        tabbedPane.addTab("QUẢN LÝ THU NHẬP", createThuNhapPanel());
        tabbedPane.addTab("QUẢN LÝ CHI TIÊU", createChiTieuPanel());
        tabbedPane.addTab("TỔNG QUAN THU CHI", createTongQuanPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ THU CHI - SPA/BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createThuNhapPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        panel.add(createThuNhapSearchPanel(), BorderLayout.NORTH);

        // Table
        createThuNhapTable();
        JScrollPane sp = new JScrollPane(tblThuNhap);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách thu nhập"));
        panel.add(sp, BorderLayout.CENTER);

        // Input panel
        panel.add(createThuNhapInputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createThuNhapSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Từ ngày
        panel.add(new JLabel("Từ ngày:"));
        dateTuNgayThu = new JDateChooser();
        dateTuNgayThu.setDate(getFirstDayOfMonth());
        dateTuNgayThu.setPreferredSize(new Dimension(120, 25));
        panel.add(dateTuNgayThu);

        // Đến ngày
        panel.add(new JLabel("Đến ngày:"));
        dateDenNgayThu = new JDateChooser();
        dateDenNgayThu.setDate(new java.util.Date());
        dateDenNgayThu.setPreferredSize(new Dimension(120, 25));
        panel.add(dateDenNgayThu);

        // Nút tìm kiếm
        btnTimKiemThu = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        panel.add(btnTimKiemThu);

        // Thống kê
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Tổng thu:"));
        lblTongThu = new JLabel("0 VND");
        lblTongThu.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongThu.setForeground(Color.RED);
        panel.add(lblTongThu);

        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("Từ hóa đơn:"));
        lblTongThuHoaDon = new JLabel("0 VND");
        lblTongThuHoaDon.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongThuHoaDon.setForeground(Color.BLUE);
        panel.add(lblTongThuHoaDon);

        return panel;
    }

    private void createThuNhapTable() {
        String[] cols = {"Mã thu", "Ngày thu", "Số tiền", "Nội dung", "Loại thu"};
        modelThuNhap = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 ->
                        Integer.class; // Mã thu
                    case 1 ->
                        LocalDate.class; // Ngày thu
                    case 2 ->
                        String.class; // Số tiền
                    case 3 ->
                        String.class; // Nội dung
                    case 4 ->
                        String.class; // Loại thu
                    default ->
                        Object.class;
                };
            }
        };

        tblThuNhap = new JTable(modelThuNhap);
        styleTable(tblThuNhap);

        // Set column widths
        tblThuNhap.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblThuNhap.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblThuNhap.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblThuNhap.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblThuNhap.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private JPanel createThuNhapInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thu nhập"));

        // Ngày thu
        JLabel lblNgayThu = new JLabel("Ngày thu:");
        lblNgayThu.setForeground(Color.BLACK); // THÊM DÒNG NÀY
        panel.add(lblNgayThu);

        dateNgayThu = new JDateChooser();
        dateNgayThu.setDate(new java.util.Date());
        dateNgayThu.setPreferredSize(new Dimension(120, 25));
        panel.add(dateNgayThu);

        // Số tiền
        JLabel lblSoTienThu = new JLabel("Số tiền:");
        lblSoTienThu.setForeground(Color.BLACK); // THÊM DÒNG NÀY
        panel.add(lblSoTienThu);

        txtSoTienThu = new JTextField();
        txtSoTienThu.setPreferredSize(new Dimension(120, 25));
        txtSoTienThu.setForeground(Color.BLACK); // THÊM DÒNG NÀY
        panel.add(txtSoTienThu);

        // Nội dung
        JLabel lblNoiDungThu = new JLabel("Nội dung:");
        lblNoiDungThu.setForeground(Color.BLACK); // THÊM DÒNG NÀY
        panel.add(lblNoiDungThu);

        txtNoiDungThu = new JTextField();
        txtNoiDungThu.setPreferredSize(new Dimension(200, 25));
        txtNoiDungThu.setForeground(Color.BLACK); // THÊM DÒNG NÀY
        panel.add(txtNoiDungThu);

        // Buttons
        btnThemThu = createStyledButton("Thêm", COLOR_BUTTON);
        btnSuaThu = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoaThu = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoiThu = createStyledButton("Làm mới", COLOR_BUTTON);

        panel.add(btnThemThu);
        panel.add(btnSuaThu);
        panel.add(btnXoaThu);
        panel.add(btnLamMoiThu);

        return panel;
    }

    private JPanel createChiTieuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        panel.add(createChiTieuSearchPanel(), BorderLayout.NORTH);

        // Table
        createChiTieuTable();
        JScrollPane sp = new JScrollPane(tblChiTieu);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiêu"));
        panel.add(sp, BorderLayout.CENTER);

        // Input panel
        panel.add(createChiTieuInputPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createChiTieuSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Từ ngày
        panel.add(new JLabel("Từ ngày:"));
        dateTuNgayChi = new JDateChooser();
        dateTuNgayChi.setDate(getFirstDayOfMonth());
        dateTuNgayChi.setPreferredSize(new Dimension(120, 25));
        panel.add(dateTuNgayChi);

        // Đến ngày
        panel.add(new JLabel("Đến ngày:"));
        dateDenNgayChi = new JDateChooser();
        dateDenNgayChi.setDate(new java.util.Date());
        dateDenNgayChi.setPreferredSize(new Dimension(120, 25));
        panel.add(dateDenNgayChi);

        // Nút tìm kiếm
        btnTimKiemChi = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        panel.add(btnTimKiemChi);

        // Thống kê
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Tổng chi:"));
        lblTongChi = new JLabel("0 VND");
        lblTongChi.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongChi.setForeground(Color.RED);
        panel.add(lblTongChi);

        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("Từ nguyên liệu:"));
        lblTongChiNguyenLieu = new JLabel("0 VND");
        lblTongChiNguyenLieu.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongChiNguyenLieu.setForeground(Color.BLUE);
        panel.add(lblTongChiNguyenLieu);

        return panel;
    }

    private void createChiTieuTable() {
        String[] cols = {"Mã chi", "Ngày chi", "Số tiền", "Mục đích"};
        modelChiTieu = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 ->
                        Integer.class; // Mã chi
                    case 1 ->
                        LocalDate.class; // Ngày chi
                    case 2 ->
                        String.class; // Số tiền
                    case 3 ->
                        String.class; // Mục đích
                    default ->
                        Object.class;
                };
            }
        };

        tblChiTieu = new JTable(modelChiTieu);
        styleTable(tblChiTieu);

        // Set column widths
        tblChiTieu.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblChiTieu.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblChiTieu.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblChiTieu.getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    private JPanel createChiTieuInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiêu"));

        // Ngày chi
        JLabel lblNgayChi = new JLabel("Ngày chi:");
        lblNgayChi.setForeground(Color.BLACK);
        panel.add(lblNgayChi);

        dateNgayChi = new JDateChooser();
        dateNgayChi.setDate(new java.util.Date());
        dateNgayChi.setPreferredSize(new Dimension(120, 25));
        panel.add(dateNgayChi);

        // Số tiền
        JLabel lblSoTienChi = new JLabel("Số tiền:");
        lblSoTienChi.setForeground(Color.BLACK);
        panel.add(lblSoTienChi);

        txtSoTienChi = new JTextField();
        txtSoTienChi.setPreferredSize(new Dimension(120, 25));
        txtSoTienChi.setForeground(Color.BLACK);
        panel.add(txtSoTienChi);

        // Mục đích
        JLabel lblMucDichChi = new JLabel("Mục đích:");
        lblMucDichChi.setForeground(Color.BLACK);
        panel.add(lblMucDichChi);

        txtMucDichChi = new JTextField();
        txtMucDichChi.setPreferredSize(new Dimension(200, 25));
        txtMucDichChi.setForeground(Color.BLACK);
        panel.add(txtMucDichChi);

        // Buttons
        btnThemChi = createStyledButton("Thêm", COLOR_BUTTON);
        btnSuaChi = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoaChi = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoiChi = createStyledButton("Làm mới", COLOR_BUTTON);

        panel.add(btnThemChi);
        panel.add(btnSuaChi);
        panel.add(btnXoaChi);
        panel.add(btnLamMoiChi);

        return panel;
    }

    private JPanel createTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Summary cards
        JPanel pnCards = createSummaryCards();
        panel.add(pnCards, BorderLayout.NORTH);

        // Date filter and chart area
        JPanel pnContent = new JPanel(new BorderLayout(10, 10));
        pnContent.setBackground(COLOR_BACKGROUND);
        pnContent.add(createSummaryDateFilter(), BorderLayout.NORTH);

        // Có thể thêm biểu đồ ở đây
        JPanel pnChartPlaceholder = new JPanel();
        pnChartPlaceholder.setBackground(Color.WHITE);
        pnChartPlaceholder.setBorder(BorderFactory.createTitledBorder("Biểu đồ thu chi"));
        pnChartPlaceholder.setPreferredSize(new Dimension(800, 300));
        pnContent.add(pnChartPlaceholder, BorderLayout.CENTER);

        panel.add(pnContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryCards() {
        JPanel pnCards = new JPanel(new GridLayout(1, 3, 20, 20));
        pnCards.setBackground(COLOR_BACKGROUND);

        // Thu nhập card
        JPanel cardThu = createSummaryCard("TỔNG THU NHẬP", "0 VND", COLOR_BUTTON);
        lblTongThuTongQuan = (JLabel) ((JPanel) cardThu.getComponent(1)).getComponent(0);

        // Chi tiêu card
        JPanel cardChi = createSummaryCard("TỔNG CHI TIÊU", "0 VND", new Color(0xE7, 0x4C, 0x3C));
        lblTongChiTongQuan = (JLabel) ((JPanel) cardChi.getComponent(1)).getComponent(0);

        // Lợi nhuận card
        JPanel cardLoiNhuan = createSummaryCard("LỢI NHUẬN", "0 VND", new Color(0x2E, 0xCC, 0x71));
        lblLoiNhuanTongQuan = (JLabel) ((JPanel) cardLoiNhuan.getComponent(1)).getComponent(0);

        pnCards.add(cardThu);
        pnCards.add(cardChi);
        pnCards.add(cardLoiNhuan);

        return pnCards;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(color);
        valuePanel.add(lblValue, BorderLayout.CENTER);

        card.add(valuePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSummaryDateFilter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel.setBackground(COLOR_BACKGROUND);

        panel.add(new JLabel("Từ ngày:"));
        dateTuNgayTQ = new JDateChooser();
        dateTuNgayTQ.setDate(getFirstDayOfMonth());
        dateTuNgayTQ.setPreferredSize(new Dimension(120, 25));
        panel.add(dateTuNgayTQ);

        panel.add(new JLabel("Đến ngày:"));
        dateDenNgayTQ = new JDateChooser();
        dateDenNgayTQ.setDate(new java.util.Date());
        dateDenNgayTQ.setPreferredSize(new Dimension(120, 25));
        panel.add(dateDenNgayTQ);

        btnXemBaoCao = createStyledButton("Xem báo cáo", COLOR_BUTTON);
        panel.add(btnXemBaoCao);

        return panel;
    }

    private java.util.Date getFirstDayOfMonth() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return java.sql.Date.valueOf(firstDay);
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
    public JTable getTblThuNhap() {
        return tblThuNhap;
    }

    public JTable getTblChiTieu() {
        return tblChiTieu;
    }

    public DefaultTableModel getModelThuNhap() {
        return modelThuNhap;
    }

    public DefaultTableModel getModelChiTieu() {
        return modelChiTieu;
    }

    public JTextField getTxtSoTienThu() {
        return txtSoTienThu;
    }

    public JTextField getTxtNoiDungThu() {
        return txtNoiDungThu;
    }

    public JTextField getTxtSoTienChi() {
        return txtSoTienChi;
    }

    public JTextField getTxtMucDichChi() {
        return txtMucDichChi;
    }

    public JDateChooser getDateNgayThu() {
        return dateNgayThu;
    }

    public JDateChooser getDateNgayChi() {
        return dateNgayChi;
    }

    public JDateChooser getDateTuNgayThu() {
        return dateTuNgayThu;
    }

    public JDateChooser getDateDenNgayThu() {
        return dateDenNgayThu;
    }

    public JDateChooser getDateTuNgayChi() {
        return dateTuNgayChi;
    }

    public JDateChooser getDateDenNgayChi() {
        return dateDenNgayChi;
    }

    public JDateChooser getDateTuNgayTQ() {
        return dateTuNgayTQ;
    }

    public JDateChooser getDateDenNgayTQ() {
        return dateDenNgayTQ;
    }

    public JButton getBtnThemThu() {
        return btnThemThu;
    }

    public JButton getBtnSuaThu() {
        return btnSuaThu;
    }

    public JButton getBtnXoaThu() {
        return btnXoaThu;
    }

    public JButton getBtnLamMoiThu() {
        return btnLamMoiThu;
    }

    public JButton getBtnTimKiemThu() {
        return btnTimKiemThu;
    }

    public JButton getBtnThemChi() {
        return btnThemChi;
    }

    public JButton getBtnSuaChi() {
        return btnSuaChi;
    }

    public JButton getBtnXoaChi() {
        return btnXoaChi;
    }

    public JButton getBtnLamMoiChi() {
        return btnLamMoiChi;
    }

    public JButton getBtnTimKiemChi() {
        return btnTimKiemChi;
    }

    public JButton getBtnXemBaoCao() {
        return btnXemBaoCao;
    }

    public JLabel getLblTongThu() {
        return lblTongThu;
    }

    public JLabel getLblTongThuHoaDon() {
        return lblTongThuHoaDon;
    }

    public JLabel getLblTongChi() {
        return lblTongChi;
    }

    public JLabel getLblTongChiNguyenLieu() {
        return lblTongChiNguyenLieu;
    }

    public JLabel getLblTongThuTongQuan() {
        return lblTongThuTongQuan;
    }

    public JLabel getLblTongChiTongQuan() {
        return lblTongChiTongQuan;
    }

    public JLabel getLblLoiNhuanTongQuan() {
        return lblLoiNhuanTongQuan;
    }
}
