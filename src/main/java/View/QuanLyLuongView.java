package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyLuongView extends JPanel {
    private JTable tblLuong;
    private JButton btnTinhLuong;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnDong;
    private JComboBox<Integer> cboThang;
    private JComboBox<Integer> cboNam;
    private JComboBox<String> cboNhanVien;
    private DefaultTableModel tableModel;

    public QuanLyLuongView() {
        initializeComponents();
        setupUI();
        setupComboBoxes();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0x8C, 0xC9, 0x80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng
        tableModel = new DefaultTableModel(new String[]{
            "Mã Lương", "Mã NV", "Tên Nhân Viên", "Tháng", "Năm", 
            "Tổng Lương", "Ngày Tính", "Trạng Thái"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 1, 3, 4 -> Integer.class;
                    case 5, 6, 7 -> String.class;
                    default -> String.class;
                };
            }
        };

        tblLuong = new JTable(tableModel) {
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
        tblLuong.setRowHeight(35);
        tblLuong.setSelectionBackground(new Color(0x4D, 0x8A, 0x57));
        tblLuong.setSelectionForeground(Color.WHITE);
        tblLuong.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLuong.setFont(new Font("Arial", Font.PLAIN, 12));
        tblLuong.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblLuong.getTableHeader().setBackground(new Color(0x4D, 0x8A, 0x57));
        tblLuong.getTableHeader().setForeground(Color.WHITE);

        // Tạo các nút
        btnTinhLuong = createStyledButton("Tính Lương", new Color(0x4D, 0x8A, 0x57));
        btnCapNhat = createStyledButton("Cập Nhật TT", new Color(0x4D, 0x8A, 0x57));
        btnXoa = createStyledButton("Xóa", new Color(0xD3, 0x53, 0x53)); // Màu đỏ cho nút xóa
        btnLamMoi = createStyledButton("Làm Mới", new Color(0x4D, 0x8A, 0x57));
        btnDong = createStyledButton("Đóng", new Color(0x8C, 0x8C, 0x8C));

        // Tạo combobox
        cboThang = new JComboBox<>();
        cboNam = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
    }

    private void setupComboBoxes() {
        // Thêm các tháng
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem(i);
        }
        
        // Thêm các năm (từ 2020 đến 2030)
        for (int i = 2020; i <= 2030; i++) {
            cboNam.addItem(i);
        }
        
        // Chọn tháng và năm hiện tại
        java.time.LocalDate now = java.time.LocalDate.now();
        cboThang.setSelectedItem(now.getMonthValue());
        cboNam.setSelectedItem(now.getYear());
    }

    private void setupUI() {
        // Tiêu đề
        JPanel titlePanel = createTitlePanel("QUẢN LÝ LƯƠNG NHÂN VIÊN");

        // Panel filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Lọc dữ liệu"));
        
        filterPanel.add(new JLabel("Tháng:"));
        filterPanel.add(cboThang);
        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(cboNam);
        filterPanel.add(new JLabel("Nhân viên:"));
        filterPanel.add(cboNhanVien);

        // Panel chứa bảng
        JScrollPane scrollPane = new JScrollPane(tblLuong);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lương nhân viên"));

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        buttonPanel.add(btnTinhLuong);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnDong);

        // Panel content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thêm vào main panel
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x4D, 0x8A, 0x57));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
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
    public JTable getTblLuong() { return tblLuong; }
    public JButton getBtnTinhLuong() { return btnTinhLuong; }
    public JButton getBtnCapNhat() { return btnCapNhat; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnDong() { return btnDong; }
    public JComboBox<Integer> getCboThang() { return cboThang; }
    public JComboBox<Integer> getCboNam() { return cboNam; }
    public JComboBox<String> getCboNhanVien() { return cboNhanVien; }
    public DefaultTableModel getTableModel() { return tableModel; }
}