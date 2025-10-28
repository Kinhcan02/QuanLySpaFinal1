package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyDichVuView extends JPanel {

    private JTable tblDichVu;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnLoaiDichVu;
    private JComboBox<String> cboLoaiFilter;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyDichVuView() {
        initUI();
    }

    private void initUI() {
        setSize(1200, 750);
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);

        // Panel tiêu đề
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Panel tìm kiếm
        JPanel pnSearch = createSearchPanel();
        add(pnSearch, BorderLayout.NORTH);

        // Bảng dịch vụ
        createTable();
        JScrollPane sp = new JScrollPane(tblDichVu);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách dịch vụ"));
        sp.setBackground(COLOR_BACKGROUND);
        add(sp, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnButton = createButtonPanel();
        add(pnButton, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createSearchPanel() {
        JPanel pnSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnSearch.setBackground(COLOR_BACKGROUND);
        pnSearch.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(new Font("Arial", Font.BOLD, 12));
        lblTimKiem.setForeground(COLOR_TEXT);
        pnSearch.add(lblTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 30));
        pnSearch.add(txtTimKiem);

        JLabel lblLoai = new JLabel("Loại dịch vụ:");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 12));
        lblLoai.setForeground(COLOR_TEXT);
        pnSearch.add(lblLoai);

        // ComboBox sẽ được load dữ liệu thực tế từ controller
        cboLoaiFilter = new JComboBox<>();
        cboLoaiFilter.setPreferredSize(new Dimension(150, 30));
        pnSearch.add(cboLoaiFilter);

        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        pnSearch.add(btnTimKiem);

        // Thêm nút Loại dịch vụ
        btnLoaiDichVu = createStyledButton("Loại dịch vụ", COLOR_BUTTON);
        btnLoaiDichVu.setPreferredSize(new Dimension(120, 30));
        pnSearch.add(btnLoaiDichVu);

        return pnSearch;
    }

 private void createTable() {
    String[] cols = {"Mã DV", "Tên dịch vụ", "Đơn giá", "Thời gian (phút)", "Loại DV", "Mô tả"}; // Sửa tên cột
    model = new DefaultTableModel(cols, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tblDichVu = new JTable(model) {
        @Override
        public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);

            if (!isRowSelected(row)) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }

            // Đặt màu chữ cho tất cả các ô
            if (c instanceof JComponent) {
                ((JComponent) c).setForeground(Color.BLACK);
            }

            return c;
        }
    };

    // Cấu hình bảng
    tblDichVu.setRowHeight(35);
    tblDichVu.setSelectionBackground(COLOR_BUTTON);
    tblDichVu.setSelectionForeground(COLOR_TEXT);
    tblDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tblDichVu.setFont(new Font("Arial", Font.PLAIN, 12));
    tblDichVu.setForeground(Color.BLACK);
    tblDichVu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    tblDichVu.getTableHeader().setBackground(COLOR_BUTTON);
    tblDichVu.getTableHeader().setForeground(COLOR_TEXT);

    // Đặt độ rộng cột
    tblDichVu.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã DV
    tblDichVu.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên dịch vụ
    tblDichVu.getColumnModel().getColumn(2).setPreferredWidth(120); // Đơn giá
    tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(120); // Thời gian (phút)
    tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(120); // Loại DV
    tblDichVu.getColumnModel().getColumn(5).setPreferredWidth(300); // Mô tả
}

    private JPanel createButtonPanel() {
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnButton.setBackground(COLOR_BACKGROUND);
        pnButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnThem = createStyledButton("Thêm mới", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", COLOR_BUTTON);
        btnLamMoi = createStyledButton("Làm mới", COLOR_BUTTON);

        pnButton.add(btnThem);
        pnButton.add(btnSua);
        pnButton.add(btnXoa);
        pnButton.add(btnLamMoi);

        return pnButton;
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
    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCboLoaiFilter() {
        return cboLoaiFilter;
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

    public JButton getBtnLoaiDichVu() {
        return btnLoaiDichVu;
    }
}
