package View;

import Model.LuongNhanVien;
import Service.LuongNhanVienService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LichSuTinhLuongPanel extends JPanel {
    private LuongNhanVienService service;
    private JTable tblLichSu;
    private DefaultTableModel tableModel;
    private JLabel lblTitle;
    private JLabel lblTongSo;
    private JComboBox<Integer> cboThang;
    private JComboBox<Integer> cboNam;
    private JButton btnTimKiem;

    public LichSuTinhLuongPanel(LuongNhanVienService service) {
        this.service = service;
        initializeComponents();
        setupUI();
        setupComboBoxes();
        loadData(null, null);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0x8C, 0xC9, 0x80));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng lịch sử
        tableModel = new DefaultTableModel(new String[]{
            "Mã Lương", "Mã NV", "Tên Nhân Viên", "Tháng", "Năm", 
            "Lương Cơ Bản", "Tiền Dịch Vụ", "Tổng Lương", "Ngày Tính", "Trạng Thái", "Ghi Chú"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLichSu = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        };

        // Cấu hình bảng
        tblLichSu.setRowHeight(30);
        tblLichSu.setSelectionBackground(new Color(0x4D, 0x8A, 0x57));
        tblLichSu.setSelectionForeground(Color.WHITE);
        tblLichSu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblLichSu.getTableHeader().setBackground(new Color(0x4D, 0x8A, 0x57));
        tblLichSu.getTableHeader().setForeground(Color.WHITE);

        // Tạo combobox và nút
        cboThang = new JComboBox<>();
        cboNam = new JComboBox<>();
        btnTimKiem = createStyledButton("Tìm Kiếm", new Color(0x4D, 0x8A, 0x57));

        // Label tổng số
        lblTongSo = new JLabel("Tổng số: 0 bản ghi");
        lblTongSo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTongSo.setForeground(Color.BLACK);
    }

    private void setupComboBoxes() {
        // Thêm các tháng
        cboThang.addItem(null); // Cho phép chọn tất cả
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem(i);
        }
        
        // Thêm các năm
        cboNam.addItem(null); // Cho phép chọn tất cả
        for (int i = 2020; i <= 2030; i++) {
            cboNam.addItem(i);
        }
        
        // Chọn tháng và năm hiện tại
        java.time.LocalDate now = java.time.LocalDate.now();
        cboThang.setSelectedItem(now.getMonthValue());
        cboNam.setSelectedItem(now.getYear());
        
        // Thêm sự kiện tìm kiếm
        btnTimKiem.addActionListener(e -> timKiemLichSu());
    }

    private void setupUI() {
        // Panel tiêu đề
        JPanel titlePanel = createTitlePanel("LỊCH SỬ TÍNH LƯƠNG");

        // Panel filter - SỬA LẠI PHẦN NÀY
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm lịch sử"));
        filterPanel.setPreferredSize(new Dimension(1000, 60)); // Đặt chiều cao cố định
        
        // Thêm components vào filter panel
        filterPanel.add(new JLabel("Tháng:"));
        filterPanel.add(cboThang);
        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(cboNam);
        filterPanel.add(btnTimKiem);
        filterPanel.add(lblTongSo);

        // Panel chứa filter và bảng
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        contentPanel.add(filterPanel, BorderLayout.NORTH);

        // Panel bảng
        JScrollPane scrollPane = new JScrollPane(tblLichSu);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lịch sử tính lương"));
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm các component vào panel chính
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x4D, 0x8A, 0x57));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.setPreferredSize(new Dimension(1000, 50)); // Đặt chiều cao cố định
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
        button.setPreferredSize(new Dimension(100, 30)); // Đặt kích thước cố định

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

    private void timKiemLichSu() {
        Integer thang = (Integer) cboThang.getSelectedItem();
        Integer nam = (Integer) cboNam.getSelectedItem();
        loadData(thang, nam);
    }

    private void loadData(Integer thang, Integer nam) {
        try {
            List<LuongNhanVien> danhSachLichSu;
            
            if (thang != null && nam != null) {
                danhSachLichSu = service.getLichSuTinhLuong(thang, nam);
            } else if (thang != null) {
                // Chỉ tìm theo tháng (tất cả năm)
                danhSachLichSu = service.getAllLuong().stream()
                    .filter(luong -> luong.getThang().equals(thang))
                    .toList();
            } else if (nam != null) {
                // Chỉ tìm theo năm (tất cả tháng)
                danhSachLichSu = service.getAllLuong().stream()
                    .filter(luong -> luong.getNam().equals(nam))
                    .toList();
            } else {
                // Hiển thị tất cả
                danhSachLichSu = service.getAllLuong();
            }
            
            hienThiDuLieu(danhSachLichSu);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDuLieu(List<LuongNhanVien> danhSachLichSu) {
        tableModel.setRowCount(0);
        
        for (LuongNhanVien luong : danhSachLichSu) {
            Object[] row = {
                luong.getMaLuong(),
                luong.getMaNhanVien(),
                luong.getNhanVien() != null ? luong.getNhanVien().getHoTen() : "N/A",
                luong.getThang(),
                luong.getNam(),
                String.format("%,.0f VND", luong.getLuongCanBan()),
                String.format("%,.0f VND", luong.getTongTienDichVu()),
                String.format("%,.0f VND", luong.getTongLuong()),
                luong.getNgayTinhLuong() != null ? 
                    luong.getNgayTinhLuong().toString() : "N/A",
                luong.getTrangThai(),
                luong.getGhiChu() != null ? luong.getGhiChu() : ""
            };
            tableModel.addRow(row);
        }
        
        lblTongSo.setText("Tổng số: " + danhSachLichSu.size() + " bản ghi lịch sử");
    }

    // Getter methods
    public JTable getTblLichSu() { return tblLichSu; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JComboBox<Integer> getCboThang() { return cboThang; }
    public JComboBox<Integer> getCboNam() { return cboNam; }
    public JLabel getLblTongSo() { return lblTongSo; }
}