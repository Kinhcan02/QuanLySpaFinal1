package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PhanTramDichVuView extends JPanel {
    private JTable tblPhanTram;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTextField txtNhanVien; // Đổi từ JComboBox thành JTextField
    private JComboBox<String> cboLoaiDichVu;
    private JTextField txtTiLePhanTram;
    private JTextField txtMaPhanTram;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public PhanTramDichVuView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBackground(COLOR_BACKGROUND);
        setPreferredSize(new Dimension(800, 500));

        // Panel tiêu đề
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Panel chính
        JPanel pnMain = new JPanel(new BorderLayout(5, 5));
        pnMain.setBackground(COLOR_BACKGROUND);
        pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel form nhập liệu
        JPanel pnForm = createFormPanel();
        pnMain.add(pnForm, BorderLayout.NORTH);

        // Panel tìm kiếm
        JPanel pnSearch = createSearchPanel();
        pnMain.add(pnSearch, BorderLayout.CENTER);

        // Bảng phần trăm dịch vụ
        createTable();
        JScrollPane sp = new JScrollPane(tblPhanTram);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                "Danh sách phần trăm dịch vụ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                COLOR_BUTTON
        ));
        pnMain.add(sp, BorderLayout.CENTER);

        add(pnMain, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnButton = createButtonPanel();
        add(pnButton, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ PHẦN TRĂM DỊCH VỤ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createFormPanel() {
        JPanel pnForm = new JPanel(new GridBagLayout());
        pnForm.setBackground(COLOR_BACKGROUND);
        pnForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                "Thông tin phần trăm dịch vụ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                COLOR_BUTTON
        ));
        pnForm.setPreferredSize(new Dimension(getWidth(), 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Mã phần trăm và Nhân viên
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnForm.add(createStyledLabel("Mã phần trăm:"), gbc);

        gbc.gridx = 1;
        txtMaPhanTram = createStyledTextField(8);
        txtMaPhanTram.setEditable(false);
        pnForm.add(txtMaPhanTram, gbc);

        gbc.gridx = 2;
        pnForm.add(createStyledLabel("Nhân viên:"), gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        txtNhanVien = createStyledTextField(20); // Đổi thành JTextField
        txtNhanVien.setEditable(false); // Không cho chỉnh sửa vì chỉ hiển thị
        pnForm.add(txtNhanVien, gbc);

        // Dòng 2: Loại dịch vụ và Tỉ lệ phần trăm
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnForm.add(createStyledLabel("Loại dịch vụ:"), gbc);

        gbc.gridx = 1;
        cboLoaiDichVu = new JComboBox<>();
        styleComboBox(cboLoaiDichVu);
        pnForm.add(cboLoaiDichVu, gbc);

        gbc.gridx = 2;
        pnForm.add(createStyledLabel("Tỉ lệ phần trăm (%):"), gbc);

        gbc.gridx = 3;
        txtTiLePhanTram = createStyledTextField(10);
        pnForm.add(txtTiLePhanTram, gbc);

        return pnForm;
    }

    private JPanel createSearchPanel() {
        JPanel pnSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnSearch.setBackground(COLOR_BACKGROUND);
        pnSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel lblTimKiem = createStyledLabel("Tìm kiếm:");
        pnSearch.add(lblTimKiem);

        txtTimKiem = createStyledTextField(20);
        pnSearch.add(txtTimKiem);

        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTimKiem.setPreferredSize(new Dimension(90, 25));
        pnSearch.add(btnTimKiem);

        return pnSearch;
    }

    private void createTable() {
        String[] cols = {"Mã phần trăm", "Nhân viên", "Loại dịch vụ", "Tỉ lệ phần trăm (%)"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhanTram = new JTable(model) {
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
        tblPhanTram.setRowHeight(30);
        tblPhanTram.setSelectionBackground(COLOR_BUTTON);
        tblPhanTram.setSelectionForeground(COLOR_TEXT);
        tblPhanTram.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPhanTram.setFont(new Font("Arial", Font.PLAIN, 11));
        tblPhanTram.setForeground(Color.BLACK);
        tblPhanTram.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tblPhanTram.getTableHeader().setBackground(COLOR_BUTTON);
        tblPhanTram.getTableHeader().setForeground(COLOR_TEXT);

        // Đặt độ rộng cột
        tblPhanTram.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblPhanTram.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPhanTram.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblPhanTram.getColumnModel().getColumn(3).setPreferredWidth(100);
    }

    private JPanel createButtonPanel() {
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnButton.setBackground(COLOR_BACKGROUND);
        pnButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

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

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(COLOR_BUTTON));
        comboBox.setPreferredSize(new Dimension(200, 25));
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

    // Getter methods - Cập nhật getter cho txtNhanVien
    public JTable getTblPhanTram() { return tblPhanTram; }
    public DefaultTableModel getModel() { return model; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JTextField getTxtNhanVien() { return txtNhanVien; } // Đổi từ getCboNhanVien()
    public JComboBox<String> getCboLoaiDichVu() { return cboLoaiDichVu; }
    public JTextField getTxtTiLePhanTram() { return txtTiLePhanTram; }
    public JTextField getTxtMaPhanTram() { return txtMaPhanTram; }
}