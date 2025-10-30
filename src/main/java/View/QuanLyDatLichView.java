package View;

import Model.DatLich;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.border.Border;

public class QuanLyDatLichView extends JPanel {

    private DatLichService datLichService;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private GiuongService giuongService;

    // Components
    private JTable tableDatLich;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JDateChooser dateChooser;
    private JButton btnThem, btnSua, btnXoa, btnXacNhan, btnHuy;

    // Form components
    private JComboBox<KhachHang> cbKhachHang;
    private JComboBox<DichVu> cbDichVu;
    private JComboBox<Giuong> cbGiuong;
    private JTextField txtNgayDat;
    private JTextField txtGioDat;
    private JTextArea txtGhiChu;

    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyDatLichView() {
        initServices();
        initUI();
        loadData();
        setupEvents();
    }

    private void initServices() {
        this.datLichService = new DatLichService();
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.giuongService = new GiuongService();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content
        add(createMainContentPanel(), BorderLayout.CENTER);

        // Form panel (HIỂN THỊ LUÔN thay vì ẩn ban đầu)
        add(createFormPanel(), BorderLayout.SOUTH);
        setupGiuongComboBoxRenderer();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT LỊCH");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(COLOR_BACKGROUND);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đã hủy", "Hoàn thành"});
        cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnTimKiem = createStyledButton("Tìm kiếm", COLOR_BUTTON);

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(cbTrangThai);
        searchPanel.add(btnTimKiem);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BACKGROUND);

        // Table
        String[] columns = {"Mã lịch", "Khách hàng", "Ngày đặt", "Giờ đặt", "Dịch vụ", "Giường", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDatLich = new JTable(tableModel);
        tableDatLich.setFont(new Font("Arial", Font.PLAIN, 14));
        tableDatLich.setRowHeight(25);
        tableDatLich.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tableDatLich);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách đặt lịch"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(COLOR_BACKGROUND);

        btnThem = createStyledButton("Thêm mới", COLOR_BUTTON);
        btnSua = createStyledButton("Sửa", COLOR_BUTTON);
        btnXoa = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXacNhan = createStyledButton("Xác nhận", new Color(0x2E, 0xCC, 0x71));
        btnHuy = createStyledButton("Hủy lịch", new Color(0xE6, 0x7E, 0x22));

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Thông tin đặt lịch"
        ));
        formPanel.setVisible(true); // Ẩn ban đầu

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Khách hàng *:"), gbc);

        gbc.gridx = 1;
        cbKhachHang = new JComboBox<>();
        cbKhachHang.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbKhachHang, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Dịch vụ:"), gbc);

        gbc.gridx = 1;
        cbDichVu = new JComboBox<>();
        cbDichVu.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbDichVu, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Giường:"), gbc);

        gbc.gridx = 1;
        cbGiuong = new JComboBox<>();
        cbGiuong.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbGiuong, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày đặt *:"), gbc);

        gbc.gridx = 1;
        txtNgayDat = new JTextField();
        txtNgayDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtNgayDat, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Giờ đặt *:"), gbc);

        gbc.gridx = 1;
        txtGioDat = new JTextField();
        txtGioDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtGioDat, gbc);

        // Row 5
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 1;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 14));
        txtGhiChu.setLineWrap(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        formPanel.add(scrollGhiChu, gbc);

        // Button row
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.setBackground(COLOR_BACKGROUND);

        JButton btnLuu = createStyledButton("Lưu", COLOR_BUTTON);
        JButton btnHuyForm = createStyledButton("Hủy", new Color(0x95, 0xA5, 0xA6));

        formButtonPanel.add(btnLuu);
        formButtonPanel.add(btnHuyForm);

        formPanel.add(formButtonPanel, gbc);

        return formPanel;
    }

    public void setFormVisible(boolean visible) {
        JPanel formPanel = getFormPanel();
        if (formPanel != null) {
            formPanel.setVisible(visible);
            revalidate();
            repaint();
        }
    }

    public void setFormEnabled(boolean enabled) {
        cbKhachHang.setEnabled(enabled);
        cbDichVu.setEnabled(enabled);
        cbGiuong.setEnabled(enabled);
        txtNgayDat.setEnabled(enabled);
        txtGioDat.setEnabled(enabled);
        txtGhiChu.setEnabled(enabled);
    }

    public void setFormState(boolean isEditMode, boolean hasSelection) {
        if (isEditMode && hasSelection) {
            setFormEnabled(true);
            // Có thể thay đổi màu sắc hoặc style để chỉ ra đang chỉnh sửa
            setBackground(new Color(0xE8, 0xF5, 0xE8)); // Màu xanh nhạt
        } else {
            setFormEnabled(true);
            setBackground(COLOR_BACKGROUND); // Màu nền mặc định
        }
    }
    // Phương thức để lấy nút Lưu từ form

    public JButton getBtnLuu() {
        return findButtonInContainer(this, "Lưu");
    }

    // Phương thức để lấy nút Hủy từ form
    public JButton getBtnHuyForm() {
        return findButtonInContainer(this, "Hủy");
    }

    private JButton findButtonInContainer(Container container, String buttonText) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (buttonText.equals(button.getText())) {
                    return button;
                }
            } else if (comp instanceof Container) {
                JButton found = findButtonInContainer((Container) comp, buttonText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public void setFormMode(boolean isEditMode) {
        if (isEditMode) {
            // Có thể thay đổi tiêu đề hoặc style khi ở chế độ chỉnh sửa
            Border border = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.BLUE),
                    "Chỉnh sửa đặt lịch"
            );
            getFormPanel().setBorder(border);
        } else {
            Border border = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    "Thông tin đặt lịch"
            );
            getFormPanel().setBorder(border);
        }
    }

    private void setupGiuongComboBoxRenderer() {
        cbGiuong.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Giuong) {
                    Giuong giuong = (Giuong) value;

                    // Đổi màu theo trạng thái
                    if (giuong.getMaGiuong() != null) {
                        switch (giuong.getTrangThai()) {
                            case "Trống":
                                setForeground(new Color(0, 128, 0)); // Xanh lá
                                setText(giuong.getSoHieu() + " - " + giuong.getTrangThai());
                                break;
                            case "Đã đặt":
                                setForeground(Color.ORANGE);
                                setText(giuong.getSoHieu() + " - " + giuong.getTrangThai());
                                break;
                            case "Đang sử dụng":
                                setForeground(Color.RED);
                                setText(giuong.getSoHieu() + " - " + giuong.getTrangThai());
                                break;
                            case "Bảo trì":
                                setForeground(Color.GRAY);
                                setText(giuong.getSoHieu() + " - " + giuong.getTrangThai());
                                break;
                            default:
                                setForeground(Color.BLACK);
                                setText(giuong.getSoHieu() + " - " + giuong.getTrangThai());
                        }
                    } else {
                        setText("-- Chọn giường --");
                    }
                }

                return c;
            }
        });
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

    private void loadData() {
        // Load danh sách đặt lịch
        loadTableData();

        // Load combobox data
        loadComboboxData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        try {
            List<DatLich> danhSachDatLich = datLichService.getAllDatLich();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (DatLich datLich : danhSachDatLich) {
                String tenKhachHang = getTenKhachHang(datLich.getMaKhachHang());
                String tenDichVu = getTenDichVu(datLich.getMaDichVu());
                String tenGiuong = getTenGiuong(datLich.getMaGiuong());

                Object[] row = {
                    datLich.getMaLich(),
                    tenKhachHang,
                    datLich.getNgayDat().format(dateFormatter),
                    datLich.getGioDat().format(timeFormatter),
                    tenDichVu,
                    tenGiuong,
                    datLich.getTrangThai(),
                    datLich.getGhiChu()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadComboboxData() {
        try {
            // Load khách hàng
            cbKhachHang.removeAllItems();
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            for (KhachHang kh : khachHangs) {
                cbKhachHang.addItem(kh);
            }

            // Load dịch vụ - thêm item mặc định
            cbDichVu.removeAllItems();
            cbDichVu.addItem(new DichVu()); // Item trống
            List<DichVu> dichVus = dichVuService.getAllDichVu();
            for (DichVu dv : dichVus) {
                cbDichVu.addItem(dv);
            }

            // Load giường - thêm item mặc định
            cbGiuong.removeAllItems();
            cbGiuong.addItem(new Giuong()); // Item trống
            List<Giuong> giuongs = giuongService.getAllGiuong();
            for (Giuong g : giuongs) {
                cbGiuong.addItem(g);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu combobox: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTenKhachHang(Integer maKhachHang) {
        if (maKhachHang == null) {
            return "";
        }
        try {
            KhachHang kh = khachHangService.getKhachHangById(maKhachHang);
            return kh != null ? kh.getHoTen() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private String getTenDichVu(Integer maDichVu) {
        if (maDichVu == null) {
            return "";
        }
        try {
            DichVu dv = dichVuService.getDichVuById(maDichVu);
            return dv != null ? dv.getTenDichVu() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private String getTenGiuong(Integer maGiuong) {
        if (maGiuong == null) {
            return "";
        }
        try {
            Giuong g = giuongService.getGiuongById(maGiuong);
            return g != null ? g.getSoHieu() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private void setupEvents() {
        btnThem.addActionListener(e -> showFormThemMoi());
        // Thêm các event listeners khác...
    }

    private void showFormThemMoi() {
        clearForm();
        JPanel formPanel = (JPanel) getComponent(2); // Lấy form panel
        formPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void clearForm() {
        cbKhachHang.setSelectedIndex(0);
        cbDichVu.setSelectedIndex(0);
        cbGiuong.setSelectedIndex(0);
        txtNgayDat.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtGioDat.setText("");
        txtGhiChu.setText("");
    }
    // Thêm các phương thức getter này vào class QuanLyDatLichView

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnXacNhan() {
        return btnXacNhan;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    public JTable getTableDatLich() {
        return tableDatLich;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCbTrangThai() {
        return cbTrangThai;
    }

    public JComboBox<KhachHang> getCbKhachHang() {
        return cbKhachHang;
    }

    public JComboBox<DichVu> getCbDichVu() {
        return cbDichVu;
    }

    public JComboBox<Giuong> getCbGiuong() {
        return cbGiuong;
    }

    public JTextField getTxtNgayDat() {
        return txtNgayDat;
    }

    public JTextField getTxtGioDat() {
        return txtGioDat;
    }

    public JTextArea getTxtGhiChu() {
        return txtGhiChu;
    }

    public JPanel getFormPanel() {
        // Giả sử form panel là component thứ 2 trong BorderLayout.SOUTH
        return (JPanel) getComponent(2);
    }
    // Các phương thức khác cho CRUD operations...
}
