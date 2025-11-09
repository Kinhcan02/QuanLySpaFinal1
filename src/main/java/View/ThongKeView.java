package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class ThongKeView extends JPanel {

    // Màu sắc giống QuanLyDichVuView
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    private JComboBox<Integer> cboNam;
    private JButton btnThongKe, btnXuatExcel;
    private JDateChooser dateChooserFrom, dateChooserTo;
    private JTabbedPane tabbedPane;
    private JTable tblKhachHang, tblDoanhThu, tblDichVu;
    private JTextArea txtTongQuan;
    private JTable tblHoaDon;

    public ThongKeView() {
        initUI();
    }

    private void initUI() {
        setSize(1200, 750);
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);

        // Panel tiêu đề
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Panel điều khiển
        JPanel pnControl = createControlPanel();
        add(pnControl, BorderLayout.NORTH);

        // TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(COLOR_BUTTON);

        createTabs();
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("THỐNG KÊ & BÁO CÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createControlPanel() {
        JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnControl.setBackground(COLOR_BACKGROUND);
        pnControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ngày bắt đầu
        JLabel lblFrom = new JLabel("Từ ngày:");
        lblFrom.setFont(new Font("Arial", Font.BOLD, 12));
        lblFrom.setForeground(COLOR_BUTTON);
        pnControl.add(lblFrom);

        dateChooserFrom = new JDateChooser();
        styleDateChooser(dateChooserFrom);

        // Đặt ngày mặc định là 01/01/2024
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date defaultFromDate = sdf.parse("01/01/2024");
            dateChooserFrom.setDate(defaultFromDate);
        } catch (Exception e) {
            // Nếu có lỗi, sử dụng ngày đầu năm hiện tại
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            dateChooserFrom.setDate(cal.getTime());
        }
        pnControl.add(dateChooserFrom);

        // Ngày kết thúc
        JLabel lblTo = new JLabel("Đến ngày:");
        lblTo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTo.setForeground(COLOR_BUTTON);
        pnControl.add(lblTo);

        dateChooserTo = new JDateChooser();
        styleDateChooser(dateChooserTo);

        // Đặt ngày mặc định là ngày hiện tại
        dateChooserTo.setDate(new Date());
        pnControl.add(dateChooserTo);

        // Năm
        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Arial", Font.BOLD, 12));
        lblNam.setForeground(COLOR_BUTTON);
        pnControl.add(lblNam);

        cboNam = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear - 5; year <= currentYear; year++) {
            cboNam.addItem(year);
        }
        cboNam.setSelectedItem(currentYear);
        cboNam.setPreferredSize(new Dimension(90, 30));
        cboNam.setBackground(Color.WHITE);
        cboNam.setForeground(COLOR_BUTTON);
        pnControl.add(cboNam);

        // Nút thống kê
        btnThongKe = createStyledButton("Thống kê", COLOR_BUTTON);
        pnControl.add(btnThongKe);

        // Nút xuất Excel
        btnXuatExcel = createStyledButton("Xuất Excel", new Color(0x3e9c70));
        pnControl.add(btnXuatExcel);

        return pnControl;
    }

    private void createTabs() {
        // Tab tổng quan
        JPanel pnTongQuan = createTongQuanPanel();
        tabbedPane.addTab("Tổng quan", pnTongQuan);

        // Tab khách hàng
        JPanel pnKhachHang = createKhachHangPanel();
        tabbedPane.addTab("Khách hàng", pnKhachHang);

        // Tab doanh thu
        JPanel pnDoanhThu = createDoanhThuPanel();
        tabbedPane.addTab("Doanh thu", pnDoanhThu);

        // Tab dịch vụ
        JPanel pnDichVu = createDichVuPanel();
        tabbedPane.addTab("Dịch vụ", pnDichVu);

        // TAB MỚI: Hóa đơn
        JPanel pnHoaDon = createHoaDonPanel();
        tabbedPane.addTab("Hóa đơn", pnHoaDon);
    }
// Thêm phương thức tạo panel hóa đơn

    private JPanel createHoaDonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] cols = {"Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền", "Số DV", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = createStyledTable(model);

        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn theo thời gian"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

// Thêm getter cho tblHoaDon
    public JTable getTblHoaDon() {
        return tblHoaDon;
    }

    private JPanel createTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        txtTongQuan = new JTextArea();
        txtTongQuan.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtTongQuan.setEditable(false);
        txtTongQuan.setText("Chọn khoảng thời gian và nhấn 'Thống kê' để xem báo cáo tổng quan...\n\n");
        txtTongQuan.setBackground(Color.WHITE);
        txtTongQuan.setForeground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(txtTongQuan);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Thống kê tổng quan"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] cols = {"STT", "Mã KH", "Họ tên", "Số điện thoại", "Loại KH", "Số DV đã dùng", "Tổng chi tiêu"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = createStyledTable(model);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Top khách hàng sử dụng nhiều dịch vụ nhất"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDoanhThuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] cols = {"Tháng", "Doanh thu", "Số hóa đơn", "Doanh thu trung bình"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDoanhThu = createStyledTable(model);

        JScrollPane scrollPane = new JScrollPane(tblDoanhThu);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Doanh thu theo tháng"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDichVuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] cols = {"STT", "Mã DV", "Tên dịch vụ", "Loại DV", "Đơn giá", "Số lượng bán", "Doanh thu"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVu = createStyledTable(model);

        JScrollPane scrollPane = new JScrollPane(tblDichVu);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Top dịch vụ bán chạy"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
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

        table.setRowHeight(35);
        table.setSelectionBackground(COLOR_BUTTON);
        table.setSelectionForeground(COLOR_TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_BUTTON);
        table.getTableHeader().setForeground(COLOR_TEXT);

        return table;
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

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setPreferredSize(new Dimension(120, 30));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.getCalendarButton().setBackground(COLOR_BUTTON);
        dateChooser.getCalendarButton().setForeground(COLOR_TEXT);
        dateChooser.getCalendarButton().setFocusPainted(false);
        dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Sửa lỗi: Ép kiểu an toàn về JTextField
        Component editorComponent = dateChooser.getDateEditor().getUiComponent();
        if (editorComponent instanceof JTextField) {
            JTextField dateTextField = (JTextField) editorComponent;
            dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BUTTON, 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            dateTextField.setBackground(Color.WHITE);
            dateTextField.setForeground(COLOR_BUTTON);
            dateTextField.setFont(new Font("Arial", Font.PLAIN, 12));
            dateTextField.setCaretColor(COLOR_BUTTON);
        }
    }

    // Getter methods
    public JComboBox<Integer> getCboNam() {
        return cboNam;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JButton getBtnXuatExcel() {
        return btnXuatExcel;
    }

    public Date getDateFrom() {
        return dateChooserFrom.getDate();
    }

    public Date getDateTo() {
        return dateChooserTo.getDate();
    }

    public JDateChooser getDateChooserFrom() {
        return dateChooserFrom;
    }

    public JDateChooser getDateChooserTo() {
        return dateChooserTo;
    }

    public JTable getTblKhachHang() {
        return tblKhachHang;
    }

    public JTable getTblDoanhThu() {
        return tblDoanhThu;
    }

    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public JTextArea getTxtTongQuan() {
        return txtTongQuan;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
