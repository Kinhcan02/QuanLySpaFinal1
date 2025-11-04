package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class DatDichVuView extends JPanel {

    // Màu sắc mới - thân thiện và chuyên nghiệp
    private final Color COLOR_PRIMARY = new Color(74, 138, 87);     // Xanh lá chính #4A8A57
    private final Color COLOR_SECONDARY = new Color(108, 187, 126); // Xanh lá nhạt #6CBB7E
    private final Color COLOR_ACCENT = new Color(255, 107, 107);    // Đỏ cam #FF6B6B
    private final Color COLOR_BACKGROUND = new Color(248, 250, 252); // Nền trắng xám #F8FAFC
    private final Color COLOR_CARD = Color.WHITE;                   // Nền card trắng
    private final Color COLOR_TEXT_PRIMARY = new Color(51, 51, 51);  // Chữ đậm #333333
    private final Color COLOR_TEXT_SECONDARY = new Color(102, 102, 102); // Chữ nhạt #666666
    private final Color COLOR_BORDER = new Color(222, 226, 230);    // Viền xám #DEE2E6

    // Các thành phần giao diện
    private JComboBox<String> cboKhachHang;
    private JComboBox<String> cboDichVu;
    private JComboBox<String> cboNhanVien;
    private JTextField txtSoLuongNguoi;
    private JTextField txtThoiGian;
    private JLabel lblDiemTichLuy;
    private JLabel lblTongTien;
    private JTable tblDichVuDaChon;
    private DefaultTableModel tableModel;

    // Các nút
    private JButton btnThemKhachHang;
    private JButton btnThemDichVu;
    private JButton btnXoaDichVu;
    private JButton btnDoiDiem;
    private JButton btnInHoaDon;
    private JButton btnLamMoi;

    public DatDichVuView() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BACKGROUND);

        // Panel chính với padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(COLOR_BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel tiêu đề
        JPanel titlePanel = createTitlePanel();
        mainContainer.add(titlePanel, BorderLayout.NORTH);

        // Panel nội dung chính
        JPanel contentPanel = createContentPanel();
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("ĐẶT DỊCH VỤ SPA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubtitle = new JLabel("Quản lý đặt dịch vụ và tích điểm khách hàng");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(COLOR_TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(COLOR_BACKGROUND);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND);

        // Panel thông tin đặt dịch vụ
        JPanel infoPanel = createInfoPanel();
        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel danh sách dịch vụ đã chọn
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        // Panel thông tin thanh toán và nút
        JPanel bottomPanel = createBottomPanel();
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setBackground(COLOR_BACKGROUND);

        // Hàng 1: Khách hàng và Dịch vụ
        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 0));
        row1.setBackground(COLOR_BACKGROUND);
        row1.add(createKhachHangPanel());
        row1.add(createDichVuPanel());

        // Hàng 2: Thông tin chi tiết
        JPanel row2 = new JPanel(new GridLayout(1, 4, 15, 0));
        row2.setBackground(COLOR_BACKGROUND);
        row2.add(createSoLuongPanel());
        row2.add(createThoiGianPanel());
        row2.add(createNhanVienPanel());
        row2.add(createDiemTichLuyPanel());

        panel.add(row1);
        panel.add(row2);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        cboKhachHang = new JComboBox<>();
        styleComboBox(cboKhachHang);

        btnThemKhachHang = createIconButton("+", "Thêm khách hàng mới");
        btnThemKhachHang.setBackground(COLOR_SECONDARY);

        JPanel comboPanel = new JPanel(new BorderLayout(10, 0));
        comboPanel.setBackground(COLOR_CARD);
        comboPanel.add(cboKhachHang, BorderLayout.CENTER);
        comboPanel.add(btnThemKhachHang, BorderLayout.EAST);

        panel.add(lblTitle);
        panel.add(comboPanel);

        return panel;
    }

    private JPanel createDichVuPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        cboDichVu = new JComboBox<>();
        styleComboBox(cboDichVu);

        btnThemDichVu = createPrimaryButton("THÊM VÀO DANH SÁCH");

        JPanel comboPanel = new JPanel(new BorderLayout(10, 0));
        comboPanel.setBackground(COLOR_CARD);
        comboPanel.add(cboDichVu, BorderLayout.CENTER);
        comboPanel.add(btnThemDichVu, BorderLayout.EAST);

        panel.add(lblTitle);
        panel.add(comboPanel);

        return panel;
    }

    private JPanel createSoLuongPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("SỐ LƯỢNG NGƯỜI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        txtSoLuongNguoi = new JTextField("1");
        styleTextField(txtSoLuongNguoi);
        txtSoLuongNguoi.setHorizontalAlignment(JTextField.CENTER);

        panel.add(lblTitle);
        panel.add(txtSoLuongNguoi);

        return panel;
    }

    private JPanel createThoiGianPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("THỜI GIAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        txtThoiGian = new JTextField();
        styleTextField(txtThoiGian);
        txtThoiGian.setEditable(false);
        txtThoiGian.setBackground(new Color(245, 245, 245));

        panel.add(lblTitle);
        panel.add(txtThoiGian);

        return panel;
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("NHÂN VIÊN THỰC HIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        cboNhanVien = new JComboBox<>();
        styleComboBox(cboNhanVien);

        panel.add(lblTitle);
        panel.add(cboNhanVien);

        return panel;
    }

    private JPanel createDiemTichLuyPanel() {
        JPanel panel = createCardPanel();

        JLabel lblTitle = new JLabel("ĐIỂM TÍCH LŨY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        lblDiemTichLuy = new JLabel("0 điểm", JLabel.CENTER);
        lblDiemTichLuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDiemTichLuy.setForeground(COLOR_ACCENT);
        lblDiemTichLuy.setOpaque(true);
        lblDiemTichLuy.setBackground(new Color(255, 245, 245));
        lblDiemTichLuy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));

        panel.add(lblTitle);
        panel.add(lblDiemTichLuy);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("DANH SÁCH DỊCH VỤ ĐÃ CHỌN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Tạo bảng
        String[] columns = {"STT", "Tên dịch vụ", "Thời gian", "Đơn giá", "Số lượng", "Nhân viên", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        tblDichVuDaChon = new JTable(tableModel);
        styleTable(tblDichVuDaChon);

        JScrollPane scrollPane = new JScrollPane(tblDichVuDaChon);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);

        // Panel tổng tiền
        JPanel totalPanel = createTotalPanel();

        // Panel nút chức năng
        JPanel buttonPanel = createButtonPanel();

        panel.add(totalPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("TỔNG TIỀN:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        lblTongTien = new JLabel("0 VND");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTongTien.setForeground(COLOR_ACCENT);

        JLabel lblHuongDan = new JLabel("• Double-click để xóa dịch vụ •");
        lblHuongDan.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHuongDan.setForeground(COLOR_TEXT_SECONDARY);
        lblHuongDan.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(lblTongTien);
        panel.add(Box.createRigidArea(new Dimension(30, 0)));
        panel.add(lblHuongDan);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(COLOR_BACKGROUND);

        btnXoaDichVu = createSecondaryButton("XÓA DỊCH VỤ");
        btnDoiDiem = createSecondaryButton("ĐỔI ĐIỂM");
        btnInHoaDon = createPrimaryButton("IN HÓA ĐƠN PDF");
        btnLamMoi = createSecondaryButton("LÀM MỚI");

        panel.add(btnXoaDichVu);
        panel.add(btnDoiDiem);
        panel.add(btnInHoaDon);
        panel.add(btnLamMoi);

        return panel;
    }

    // Các phương thức tạo component với style
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        // Đặt kích thước cột
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên dịch vụ
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Thời gian
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Số lượng
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Nhân viên
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Thành tiền
    }

    private JButton createPrimaryButton(String text) {
        return createButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return createButton(text, new Color(108, 117, 125), Color.WHITE);
    }

    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_SECONDARY);
            }
        });

        return button;
    }

    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }

    private void setupUI() {
        setPreferredSize(new Dimension(1200, 800));
    }

    // Getter methods
    public JComboBox<String> getCboKhachHang() {
        return cboKhachHang;
    }

    public JComboBox<String> getCboDichVu() {
        return cboDichVu;
    }

    public JComboBox<String> getCboNhanVien() {
        return cboNhanVien;
    }

    public JTextField getTxtSoLuongNguoi() {
        return txtSoLuongNguoi;
    }

    public JTextField getTxtThoiGian() {
        return txtThoiGian;
    }

    public JLabel getLblDiemTichLuy() {
        return lblDiemTichLuy;
    }

    public JLabel getLblTongTien() {
        return lblTongTien;
    }

    public JTable getTblDichVuDaChon() {
        return tblDichVuDaChon;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnThemKhachHang() {
        return btnThemKhachHang;
    }

    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }

    public JButton getBtnXoaDichVu() {
        return btnXoaDichVu;
    }

    public JButton getBtnDoiDiem() {
        return btnDoiDiem;
    }

    public JButton getBtnInHoaDon() {
        return btnInHoaDon;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    // Phương thức hỗ trợ
    public void themKhachHangVaoComboBox(String khachHangInfo) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cboKhachHang.getModel();
        model.addElement(khachHangInfo);
    }

// Thêm vào DatDichVuView.java
    public void setCheDoChinhSua(boolean cheDoChinhSua, Integer maHoaDon) {
        if (cheDoChinhSua && maHoaDon != null) {
            // Tìm và cập nhật tiêu đề
            JPanel mainContainer = (JPanel) getComponent(0);
            JPanel titlePanel = (JPanel) mainContainer.getComponent(0);
            JPanel textPanel = (JPanel) titlePanel.getComponent(0);
            JLabel lblTitle = (JLabel) textPanel.getComponent(0);

            lblTitle.setText("SỬA HÓA ĐƠN DỊCH VỤ #" + maHoaDon);
            lblTitle.setForeground(new Color(255, 107, 107)); // Màu đỏ để phân biệt

            // Có thể cập nhật subtitle nếu muốn
            JLabel lblSubtitle = (JLabel) textPanel.getComponent(1);
            lblSubtitle.setText("Chế độ chỉnh sửa - Cẩn thận khi thay đổi thông tin");
        } else {
            // Khôi phục về trạng thái ban đầu
            JPanel mainContainer = (JPanel) getComponent(0);
            JPanel titlePanel = (JPanel) mainContainer.getComponent(0);
            JPanel textPanel = (JPanel) titlePanel.getComponent(0);
            JLabel lblTitle = (JLabel) textPanel.getComponent(0);

            lblTitle.setText("ĐẶT DỊCH VỤ SPA");
            lblTitle.setForeground(new Color(74, 138, 87)); // Màu xanh ban đầu

            JLabel lblSubtitle = (JLabel) textPanel.getComponent(1);
            lblSubtitle.setText("Quản lý đặt dịch vụ và tích điểm khách hàng");
        }

        revalidate();
        repaint();
    }

    public void capNhatComboBoxKhachHang() {
        cboKhachHang.revalidate();
        cboKhachHang.repaint();
    }

    public void xoaTatCaDichVu() {
        tableModel.setRowCount(0);
    }

    public int getSoLuongDichVuDaChon() {
        return tableModel.getRowCount();
    }
}
