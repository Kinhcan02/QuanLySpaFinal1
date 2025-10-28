package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyLoaiDichVuView extends JDialog {
    private JPanel mainPanel;
    private JTable tblLoaiDichVu;
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnDong;
    private DefaultTableModel tableModel;

    public QuanLyLoaiDichVuView(JFrame parent) {
        super(parent, "Quản Lý Loại Dịch Vụ", true);
        initializeComponents();
        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng
        tableModel = new DefaultTableModel(new String[]{"Mã loại", "Tên loại dịch vụ", "Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLoaiDichVu = new JTable(tableModel) {
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
        tblLoaiDichVu.setRowHeight(35);
        tblLoaiDichVu.setSelectionBackground(new Color(0x4D, 0x8A, 0x57));
        tblLoaiDichVu.setSelectionForeground(Color.WHITE);
        tblLoaiDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoaiDichVu.setFont(new Font("Arial", Font.PLAIN, 12));
        tblLoaiDichVu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblLoaiDichVu.getTableHeader().setBackground(new Color(0x4D, 0x8A, 0x57));
        tblLoaiDichVu.getTableHeader().setForeground(Color.WHITE);

        // Tạo các nút
        btnThem = createStyledButton("Thêm mới", new Color(0x4D, 0x8A, 0x57));
        btnSua = createStyledButton("Sửa", new Color(0x4D, 0x8A, 0x57));
        btnXoa = createStyledButton("Xóa", new Color(0x4D, 0x8A, 0x57));
        btnLamMoi = createStyledButton("Làm mới", new Color(0x4D, 0x8A, 0x57));
        btnDong = createStyledButton("Đóng", new Color(0x8C, 0x8C, 0x8C));
    }

    private void setupUI() {
        // Tiêu đề
        JPanel titlePanel = createTitlePanel("QUẢN LÝ LOẠI DỊCH VỤ");

        // Panel chứa bảng
        JScrollPane scrollPane = new JScrollPane(tblLoaiDichVu);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách loại dịch vụ"));

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnDong);

        // Panel content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thêm vào main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setSize(800, 600);
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
    public JTable getTblLoaiDichVu() {
        return tblLoaiDichVu;
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

    public JButton getBtnDong() {
        return btnDong;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}