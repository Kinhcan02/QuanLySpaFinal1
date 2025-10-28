package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyNhapNguyenLieuView extends JPanel {

    private JTable tblNhapNguyenLieu;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnLoaiNguyenLieu;
    private JComboBox<String> cboLoaiFilter;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyNhapNguyenLieuView() {
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

        // Bảng nhập nguyên liệu
        createTable();
        JScrollPane sp = new JScrollPane(tblNhapNguyenLieu);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách nhập nguyên liệu"));
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

        JLabel lblTitle = new JLabel("QUẢN LÝ NHẬP NGUYÊN LIỆU");
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
        lblTimKiem.setForeground(Color.BLACK);
        pnSearch.add(lblTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 30));
        pnSearch.add(txtTimKiem);

        JLabel lblLoai = new JLabel("Loại nguyên liệu:");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 12));
        lblLoai.setForeground(Color.BLACK);
        pnSearch.add(lblLoai);

        cboLoaiFilter = new JComboBox<>();
        cboLoaiFilter.setPreferredSize(new Dimension(150, 30));
        pnSearch.add(cboLoaiFilter);

        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        pnSearch.add(btnTimKiem);

        btnLoaiNguyenLieu = createStyledButton("Loại nguyên liệu", COLOR_BUTTON);
        btnLoaiNguyenLieu.setPreferredSize(new Dimension(140, 30));
        pnSearch.add(btnLoaiNguyenLieu);

        return pnSearch;
    }

    private void createTable() {
        String[] cols = {"Mã nhập", "Ngày nhập", "Tên nguyên liệu", "Số lượng", "Đơn vị tính", "Đơn giá", "Thành tiền", "Nguồn nhập", "Mã NL"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhapNguyenLieu = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                ((JComponent) c).setForeground(Color.BLACK);
                return c;
            }
        };

        // Cấu hình bảng
        tblNhapNguyenLieu.setRowHeight(35);
        tblNhapNguyenLieu.setSelectionBackground(COLOR_BUTTON);
        tblNhapNguyenLieu.setSelectionForeground(COLOR_TEXT);
        tblNhapNguyenLieu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblNhapNguyenLieu.setFont(new Font("Arial", Font.PLAIN, 12));
        tblNhapNguyenLieu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblNhapNguyenLieu.getTableHeader().setBackground(COLOR_BUTTON);
        tblNhapNguyenLieu.getTableHeader().setForeground(COLOR_TEXT);

        // Ẩn cột mã nguyên liệu
        tblNhapNguyenLieu.removeColumn(tblNhapNguyenLieu.getColumnModel().getColumn(8));

        // Đặt độ rộng cột
        tblNhapNguyenLieu.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblNhapNguyenLieu.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblNhapNguyenLieu.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblNhapNguyenLieu.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblNhapNguyenLieu.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblNhapNguyenLieu.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblNhapNguyenLieu.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblNhapNguyenLieu.getColumnModel().getColumn(7).setPreferredWidth(150);
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
    public JTable getTblNhapNguyenLieu() {
        return tblNhapNguyenLieu;
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

    public JButton getBtnLoaiNguyenLieu() {
        return btnLoaiNguyenLieu;
    }
}
