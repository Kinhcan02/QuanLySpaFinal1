package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class QuanLyNhanVienView extends JPanel {

    private JTable tblNhanVien;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnPhanTramDichVu;
    private JComboBox<String> cboChucVuFilter;

    // Form fields
    private JTextField txtMaNhanVien;
    private JTextField txtHoTen;
    private JDateChooser dateChooserNgaySinh;
    private JTextField txtSoDienThoai;
    private JTextArea txtDiaChi;
    private JComboBox<String> cboChucVu;
    private JDateChooser dateChooserNgayVaoLam;
    private JTextField txtLuongCanBan;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyNhanVienView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBackground(COLOR_BACKGROUND);

        // Panel tiêu đề
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Panel chính chứa tất cả nội dung
        JPanel pnMain = new JPanel(new BorderLayout(5, 5));
        pnMain.setBackground(COLOR_BACKGROUND);
        pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel trên cùng chứa form và tìm kiếm
        JPanel pnTop = new JPanel(new BorderLayout(5, 5));
        pnTop.setBackground(COLOR_BACKGROUND);

        // Panel form nhập liệu
        JPanel pnForm = createFormPanel();
        pnTop.add(pnForm, BorderLayout.NORTH);

        // Panel tìm kiếm
        JPanel pnSearch = createSearchPanel();
        pnTop.add(pnSearch, BorderLayout.SOUTH);

        pnMain.add(pnTop, BorderLayout.NORTH);

        // Bảng nhân viên - chiếm không gian chính
        createTable();
        JScrollPane sp = new JScrollPane(tblNhanVien);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                "Danh sách nhân viên",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                COLOR_BUTTON
        ));
        sp.setBackground(COLOR_BACKGROUND);
        sp.setPreferredSize(new Dimension(800, 300));
        pnMain.add(sp, BorderLayout.CENTER);

        add(pnMain, BorderLayout.CENTER);

        // Panel nút bấm ở dưới cùng
        JPanel pnButton = createButtonPanel();
        add(pnButton, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        pnTitle.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createFormPanel() {
        JPanel pnForm = new JPanel(new GridBagLayout());
        pnForm.setBackground(COLOR_BACKGROUND);
        pnForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                "Thông tin nhân viên",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                COLOR_BUTTON
        ));
        pnForm.setPreferredSize(new Dimension(getWidth(), 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Mã NV và Họ tên
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnForm.add(createStyledLabel("Mã NV:"), gbc);

        gbc.gridx = 1;
        txtMaNhanVien = createStyledTextField(8);
        txtMaNhanVien.setEditable(false);
        pnForm.add(txtMaNhanVien, gbc);

        gbc.gridx = 2;
        pnForm.add(createStyledLabel("Họ tên:"), gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        txtHoTen = createStyledTextField(20);
        pnForm.add(txtHoTen, gbc);

        // Dòng 2: Ngày sinh và Số điện thoại
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnForm.add(createStyledLabel("Ngày sinh:"), gbc);

        gbc.gridx = 1;
        dateChooserNgaySinh = createStyledDateChooser();
        pnForm.add(dateChooserNgaySinh, gbc);

        gbc.gridx = 2;
        pnForm.add(createStyledLabel("Số điện thoại:"), gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        txtSoDienThoai = createStyledTextField(12);
        pnForm.add(txtSoDienThoai, gbc);

        // Dòng 3: Chức vụ và Ngày vào làm
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        pnForm.add(createStyledLabel("Chức vụ:"), gbc);

        gbc.gridx = 1;
        cboChucVu = new JComboBox<>(new String[]{"Quản lý", "Nhân viên"});
        styleComboBox(cboChucVu);
        pnForm.add(cboChucVu, gbc);

        gbc.gridx = 2;
        pnForm.add(createStyledLabel("Ngày vào làm:"), gbc);

        gbc.gridx = 3;
        dateChooserNgayVaoLam = createStyledDateChooser();
        pnForm.add(dateChooserNgayVaoLam, gbc);

        gbc.gridx = 4;
        pnForm.add(createStyledLabel("Lương cơ bản:"), gbc);

        gbc.gridx = 5;
        txtLuongCanBan = createStyledTextField(8);
        txtLuongCanBan.setText("0.0");
        pnForm.add(txtLuongCanBan, gbc);

        // Dòng 4: Địa chỉ và nút phần trăm dịch vụ
        gbc.gridx = 0;
        gbc.gridy = 3;
        pnForm.add(createStyledLabel("Địa chỉ:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        txtDiaChi = new JTextArea(2, 20);
        styleTextArea(txtDiaChi);
        JScrollPane scrollDiaChi = new JScrollPane(txtDiaChi);
        scrollDiaChi.setPreferredSize(new Dimension(100, 50));
        scrollDiaChi.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        pnForm.add(scrollDiaChi, gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnPhanTramDichVu = createStyledButton("Phần trăm dịch vụ", COLOR_BUTTON);
        btnPhanTramDichVu.setPreferredSize(new Dimension(120, 30)); // Bé hơn
        pnForm.add(btnPhanTramDichVu, gbc);

        return pnForm;
    }

    private JPanel createSearchPanel() {
        JPanel pnSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnSearch.setBackground(COLOR_BACKGROUND);
        pnSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnSearch.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel lblTimKiem = createStyledLabel("Tìm kiếm:");
        pnSearch.add(lblTimKiem);

        txtTimKiem = createStyledTextField(15);
        pnSearch.add(txtTimKiem);

        JLabel lblChucVu = createStyledLabel("Chức vụ:");
        pnSearch.add(lblChucVu);

        cboChucVuFilter = new JComboBox<>(new String[]{"Tất cả", "Quản lý", "Nhân viên", "Kỹ thuật viên", "Lễ tân", "Thu ngân"});
        styleComboBox(cboChucVuFilter);
        pnSearch.add(cboChucVuFilter);

        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTimKiem.setPreferredSize(new Dimension(90, 25));
        pnSearch.add(btnTimKiem);

        return pnSearch;
    }

    private void createTable() {
        String[] cols = {"Mã NV", "Họ tên", "Ngày sinh", "Số điện thoại", "Địa chỉ", "Chức vụ", "Ngày vào làm", "Lương cơ bản", "Thâm niên"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhanVien = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                if (c instanceof JComponent) {
                    ((JComponent) c).setForeground(Color.BLACK);
                }

                return c;
            }
        };

        // Cấu hình bảng
        tblNhanVien.setRowHeight(30);
        tblNhanVien.setSelectionBackground(COLOR_BUTTON);
        tblNhanVien.setSelectionForeground(COLOR_TEXT);
        tblNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblNhanVien.setFont(new Font("Arial", Font.PLAIN, 11));
        tblNhanVien.setForeground(Color.BLACK);
        tblNhanVien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tblNhanVien.getTableHeader().setBackground(COLOR_BUTTON);
        tblNhanVien.getTableHeader().setForeground(COLOR_TEXT);

        // Đặt độ rộng cột
        tblNhanVien.getColumnModel().getColumn(0).setPreferredWidth(60);   // Mã NV
        tblNhanVien.getColumnModel().getColumn(1).setPreferredWidth(150);  // Họ tên
        tblNhanVien.getColumnModel().getColumn(2).setPreferredWidth(80);   // Ngày sinh
        tblNhanVien.getColumnModel().getColumn(3).setPreferredWidth(100);  // Số điện thoại
        tblNhanVien.getColumnModel().getColumn(4).setPreferredWidth(200);  // Địa chỉ
        tblNhanVien.getColumnModel().getColumn(5).setPreferredWidth(100);  // Chức vụ
        tblNhanVien.getColumnModel().getColumn(6).setPreferredWidth(80);   // Ngày vào làm
        tblNhanVien.getColumnModel().getColumn(7).setPreferredWidth(90);   // Lương cơ bản
        tblNhanVien.getColumnModel().getColumn(8).setPreferredWidth(60);   // Thâm niên
    }

    private JPanel createButtonPanel() {
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnButton.setBackground(COLOR_BACKGROUND);
        pnButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        pnButton.setPreferredSize(new Dimension(getWidth(), 45));

        btnThem = createStyledButton("Thêm mới", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm mới", COLOR_BUTTON);

        Dimension buttonSize = new Dimension(90, 30);
        btnThem.setPreferredSize(buttonSize);
        btnSua.setPreferredSize(buttonSize);
        btnXoa.setPreferredSize(buttonSize);
        btnLamMoi.setPreferredSize(buttonSize);

        pnButton.add(btnThem);
        pnButton.add(btnSua);
        pnButton.add(btnXoa);
        pnButton.add(btnLamMoi);

        return pnButton;
    }

    // Utility methods for styling
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(COLOR_BUTTON);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 11));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        return textField;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 11));
        dateChooser.getCalendarButton().setPreferredSize(new Dimension(25, 25));
        dateChooser.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        return dateChooser;
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Arial", Font.PLAIN, 11));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        comboBox.setPreferredSize(new Dimension(120, 25));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
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

    // Getter methods for controller
    public JTable getTblNhanVien() {
        return tblNhanVien;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
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

    public JButton getBtnTimKiem() {
        return btnTimKiem;
    }

    public JButton getBtnPhanTramDichVu() {
        return btnPhanTramDichVu;
    }

    public JComboBox<String> getCboChucVuFilter() {
        return cboChucVuFilter;
    }

    public JTextField getTxtMaNhanVien() {
        return txtMaNhanVien;
    }

    public JTextField getTxtHoTen() {
        return txtHoTen;
    }

    public JDateChooser getDateChooserNgaySinh() {
        return dateChooserNgaySinh;
    }

    public JTextField getTxtSoDienThoai() {
        return txtSoDienThoai;
    }

    public JTextArea getTxtDiaChi() {
        return txtDiaChi;
    }

    public JComboBox<String> getCboChucVu() {
        return cboChucVu;
    }

    public JDateChooser getDateChooserNgayVaoLam() {
        return dateChooserNgayVaoLam;
    }

    public JTextField getTxtLuongCanBan() {
        return txtLuongCanBan;
    }
}