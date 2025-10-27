package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyDichVuView extends JPanel {

    private JTable tblDichVu;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JComboBox<String> cboLoaiFilter;

    public QuanLyDichVuView() {
        initUI();
    }

    private void initUI() {
        setSize(1200, 750);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0x8C, 0xC9, 0x80)); // Màu nền #8cc980

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
        sp.setBackground(new Color(0x8C, 0xC9, 0x80));
        add(sp, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnButton = createButtonPanel();
        add(pnButton, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(new Color(0x4D, 0x8A, 0x57)); // Màu nút #4d8a57
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE); // Màu chữ #ffffff
        pnTitle.add(lblTitle);
        
        return pnTitle;
    }

    private JPanel createSearchPanel() {
        JPanel pnSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnSearch.setBackground(new Color(0x8C, 0xC9, 0x80)); // Màu nền #8cc980
        pnSearch.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(new Font("Arial", Font.BOLD, 12));
        lblTimKiem.setForeground(Color.WHITE); // Màu chữ #ffffff
        pnSearch.add(lblTimKiem);
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(200, 30));
        pnSearch.add(txtTimKiem);

        JLabel lblLoai = new JLabel("Loại dịch vụ:");
        lblLoai.setFont(new Font("Arial", Font.BOLD, 12));
        lblLoai.setForeground(Color.WHITE); // Màu chữ #ffffff
        pnSearch.add(lblLoai);
        
        cboLoaiFilter = new JComboBox<>(new String[]{"Tất cả", "Massage", "Chăm sóc da", "Làm đẹp", "Thẩm mỹ", "Thư giãn", "Khác"});
        cboLoaiFilter.setPreferredSize(new Dimension(120, 30));
        pnSearch.add(cboLoaiFilter);

        btnTimKiem = createStyledButton("Tìm kiếm", new Color(0x4D, 0x8A, 0x57));
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        pnSearch.add(btnTimKiem);

        return pnSearch;
    }

    private void createTable() {
        String[] cols = {"Mã DV", "Tên dịch vụ", "Đơn giá", "Thời gian", "Loại DV", "Mô tả"};
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
        tblDichVu.setSelectionBackground(new Color(0x4D, 0x8A, 0x57)); // Màu nút khi chọn
        tblDichVu.setSelectionForeground(Color.WHITE); // Màu chữ khi chọn
        tblDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDichVu.setFont(new Font("Arial", Font.PLAIN, 12));
        tblDichVu.setForeground(Color.BLACK); // Màu chữ cho nội dung bảng
        tblDichVu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblDichVu.getTableHeader().setBackground(new Color(0x4D, 0x8A, 0x57)); // Màu nút cho header
        tblDichVu.getTableHeader().setForeground(Color.WHITE); // Màu chữ header
        
        // Đặt độ rộng cột
        tblDichVu.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã DV
        tblDichVu.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên dịch vụ
        tblDichVu.getColumnModel().getColumn(2).setPreferredWidth(120); // Đơn giá
        tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(100); // Thời gian
        tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(120); // Loại DV
        tblDichVu.getColumnModel().getColumn(5).setPreferredWidth(300); // Mô tả
    }

    private JPanel createButtonPanel() {
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnButton.setBackground(new Color(0x8C, 0xC9, 0x80)); // Màu nền #8cc980
        pnButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnThem = createStyledButton("Thêm mới", new Color(0x4D, 0x8A, 0x57));
        btnSua = createStyledButton("Sửa", new Color(0x4D, 0x8A, 0x57));
        btnXoa = createStyledButton("Xóa", new Color(0x4D, 0x8A, 0x57));
        btnLamMoi = createStyledButton("Làm mới", new Color(0x4D, 0x8A, 0x57));

        pnButton.add(btnThem);
        pnButton.add(btnSua);
        pnButton.add(btnXoa);
        pnButton.add(btnLamMoi);

        return pnButton;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE); // Màu chữ #ffffff
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
    public JTable getTblDichVu() { return tblDichVu; }
    public DefaultTableModel getModel() { return model; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JComboBox<String> getCboLoaiFilter() { return cboLoaiFilter; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTimKiem() { return btnTimKiem; }

    // Hàm main để test
    public static void main(String[] args) {
        // Sử dụng SwingUtilities.invokeLater để đảm bảo thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            // Set Look and Feel để giao diện đẹp hơn
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tạo frame chính
        JFrame frame = new JFrame("Quản Lý Dịch Vụ - Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setLocationRelativeTo(null); // Hiển thị giữa màn hình

        // Tạo và thêm view vào frame
        QuanLyDichVuView view = new QuanLyDichVuView();
        frame.add(view);

        // Thêm dữ liệu mẫu để test
        addSampleData(view);

        // Hiển thị frame
        frame.setVisible(true);

        // Hiển thị thông báo khi chạy thành công
        System.out.println("Ứng dụng Quản Lý Dịch Vụ đã khởi chạy thành công!");
        System.out.println("Các tính năng có thể test:");
        System.out.println("- Giao diện với màu sắc: nền #8cc980, nút #4d8a57");
        System.out.println("- Các nút chức năng: Thêm, Sửa, Xóa, Làm mới, Tìm kiếm");
        System.out.println("- Bảng hiển thị danh sách dịch vụ");
        System.out.println("- ComboBox lọc theo loại dịch vụ");
        System.out.println("- Ô tìm kiếm dịch vụ");
    }

    // Thêm dữ liệu mẫu để test giao diện
    private static void addSampleData(QuanLyDichVuView view) {
        DefaultTableModel model = view.getModel();
        
        // Dữ liệu mẫu
        Object[][] sampleData = {
            {1, "Massage thư giãn", "250,000 VNĐ", "60 phút", "Massage", "Massage toàn thân giúp thư giãn"},
            {2, "Chăm sóc da mặt", "350,000 VNĐ", "90 phút", "Chăm sóc da", "Làm sạch và dưỡng ẩm da mặt"},
            {3, "Tắm trắng", "500,000 VNĐ", "120 phút", "Làm đẹp", "Tắm trắng toàn thân"},
            {4, "Phun xăm thẩm mỹ", "2,000,000 VNĐ", "180 phút", "Thẩm mỹ", "Phun xăm chân mày, môi"},
            {5, "Xông hơi", "150,000 VNĐ", "30 phút", "Thư giãn", "Xông hơi thảo dược"}
        };

        // Thêm dữ liệu mẫu vào bảng
        for (Object[] row : sampleData) {
            model.addRow(row);
        }

        // Hiển thị thông báo trong console
        System.out.println("Đã thêm " + sampleData.length + " dịch vụ mẫu vào bảng");
    }
}