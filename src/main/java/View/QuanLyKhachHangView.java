package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class QuanLyKhachHangView extends JPanel {

    private JTable tblKhachHang;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JComboBox<String> cboLoaiFilter;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyKhachHangView() {
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

        // Bảng khách hàng
        createTable();
        JScrollPane sp = new JScrollPane(tblKhachHang);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));
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

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
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

        JLabel lblLoai = new JLabel("Loại khách:");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 12));
        lblLoai.setForeground(COLOR_TEXT);
        pnSearch.add(lblLoai);

        cboLoaiFilter = new JComboBox<>(new String[]{"Tất cả", "Thân thiết", "Thường xuyên", "Mới"});
        cboLoaiFilter.setPreferredSize(new Dimension(150, 30));
        pnSearch.add(cboLoaiFilter);

        btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        pnSearch.add(btnTimKiem);

        return pnSearch;
    }

    private void createTable() {
        String[] cols = {"Mã KH", "Họ tên", "Ngày sinh", "Loại khách", "Số điện thoại", "Ghi chú", "Ngày tạo", "Điểm tích lũy"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(model) {
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
        tblKhachHang.setRowHeight(35);
        tblKhachHang.setSelectionBackground(COLOR_BUTTON);
        tblKhachHang.setSelectionForeground(COLOR_TEXT);
        tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKhachHang.setFont(new Font("Arial", Font.PLAIN, 12));
        tblKhachHang.setForeground(Color.BLACK);
        tblKhachHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblKhachHang.getTableHeader().setBackground(COLOR_BUTTON);
        tblKhachHang.getTableHeader().setForeground(COLOR_TEXT);

        // Đặt độ rộng cột
        tblKhachHang.getColumnModel().getColumn(0).setPreferredWidth(80);   // Mã KH
        tblKhachHang.getColumnModel().getColumn(1).setPreferredWidth(200);  // Họ tên
        tblKhachHang.getColumnModel().getColumn(2).setPreferredWidth(100);  // Ngày sinh
        tblKhachHang.getColumnModel().getColumn(3).setPreferredWidth(120);  // Loại khách
        tblKhachHang.getColumnModel().getColumn(4).setPreferredWidth(120);  // Số điện thoại
        tblKhachHang.getColumnModel().getColumn(5).setPreferredWidth(200);  // Ghi chú
        tblKhachHang.getColumnModel().getColumn(6).setPreferredWidth(150);  // Ngày tạo
        tblKhachHang.getColumnModel().getColumn(7).setPreferredWidth(100);  // Điểm tích lũy
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

    // Phương thức tạo DateChooser với style phù hợp
    public JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(150, 30));
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Customize the calendar colors to match our theme
        try {
            // Set background color for the date editor
            if (dateChooser.getDateEditor() != null) {
                dateChooser.getDateEditor().getUiComponent().setBackground(Color.WHITE);
            }
            
            // Customize calendar colors if possible
            java.util.Properties props = new java.util.Properties();
            props.put("JCalendar.background", COLOR_BACKGROUND);
            props.put("JCalendar.foreground", Color.BLACK);
            props.put("JCalendar.selectedBackground", COLOR_BUTTON);
            props.put("JCalendar.selectedForeground", COLOR_TEXT);
            props.put("JCalendar.todayBackground", COLOR_BUTTON.brighter());
            props.put("JCalendar.todayForeground", COLOR_TEXT);
            
            // Apply properties to JCalendar component
            com.toedter.calendar.JCalendar calendar = dateChooser.getJCalendar();
            if (calendar != null) {
                calendar.setBackground(COLOR_BACKGROUND);
                calendar.setDecorationBackgroundColor(COLOR_BACKGROUND);
                calendar.setDecorationBackgroundColor(COLOR_BUTTON);
                calendar.setSundayForeground(new Color(200, 0, 0)); // Red for Sunday
                calendar.setWeekdayForeground(Color.BLACK);
            }
        } catch (Exception e) {
            // Ignore if customization fails
        }
        
        return dateChooser;
    }

    // Getter methods
    public JTable getTblKhachHang() {
        return tblKhachHang;
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
}