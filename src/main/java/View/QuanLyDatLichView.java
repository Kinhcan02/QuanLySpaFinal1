package View;

import Model.DatLich;
import Model.DatLichChiTiet;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import Model.NhanVien;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import Service.NhanVienService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.List;
import java.util.*;

public class QuanLyDatLichView extends JPanel {

    private DatLichService datLichService;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private GiuongService giuongService;
    private NhanVienService nhanVienService;
    // Components
    private JPanel calendarPanel;
    private JPanel timelinePanel;
    private JLabel lblThangNam;
    private JButton btnThangTruoc, btnThangSau;
    private JButton btnHomNay;
    private JButton btnHoanThanh;
    private LocalDate currentDate;
    private LocalDate selectedDate;
    private JComboBox<NhanVien> cbNhanVienDichVu;
    private JButton btnPhanCongNV;

    // Form components
    private JComboBox<KhachHang> cbKhachHang;
    private JComboBox<DichVu> cbDichVu;
    private JComboBox<Giuong> cbGiuong;
    private JTextField txtNgayDat;
    private JTextField txtGioDat;
    private JTextArea txtGhiChu;
    private JButton btnThem, btnSua, btnXoa, btnXacNhan, btnHuy;

    // Components for multiple services
    private JList<DichVu> listDichVu;
    private DefaultListModel<DichVu> listModelDichVu;
    private JButton btnThemDichVu, btnXoaDichVu;
    private Map<DichVu, NhanVien> phanCongNhanVien;

    // Thêm spinner cho số lượng người
    private JSpinner spinnerSoLuongNguoi;

    // Selected appointment
    private DatLich selectedAppointment;
    private Integer maGiuongCu; // THÊM FIELD NÀY

    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_PRIMARY = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_SECONDARY = new Color(0x6c, 0x75, 0x7d);
    private final Color COLOR_TODAY = new Color(0x0d, 0x6e, 0xfd);
    private final Color COLOR_SELECTED = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_EVENT = new Color(0xff, 0xe6, 0xe6);
    private final Color COLOR_LIST_BG = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_LIST_FOREGROUND = Color.WHITE;

    public QuanLyDatLichView() {
        initServices();
        initUI();
        loadData();
        loadNhanVienChoDichVu();
        this.phanCongNhanVien = new HashMap<>();

    }

    private void initServices() {
        this.datLichService = new DatLichService();
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.giuongService = new GiuongService();
        this.nhanVienService = new NhanVienService(); // THÊM SERVICE
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        currentDate = LocalDate.now();
        selectedDate = LocalDate.now();

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setLeftComponent(createCalendarPanel());
        splitPane.setRightComponent(createTimelinePanel());

        add(splitPane, BorderLayout.CENTER);

        // Form panel
        add(createFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("LỊCH HẸN THEO NGÀY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(COLOR_BACKGROUND);

        btnHomNay = createStyledButton("Hôm nay", COLOR_PRIMARY);
        btnHomNay.addActionListener(e -> selectToday());

        navPanel.add(btnHomNay);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch tháng"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 400));

        // Month navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);

        btnThangTruoc = createStyledButton("<<", COLOR_PRIMARY);
        btnThangTruoc.setPreferredSize(new Dimension(40, 30));
        btnThangTruoc.addActionListener(e -> previousMonth());

        btnThangSau = createStyledButton(">>", COLOR_PRIMARY);
        btnThangSau.setPreferredSize(new Dimension(40, 30));
        btnThangSau.addActionListener(e -> nextMonth());

        lblThangNam = new JLabel("", JLabel.CENTER);
        lblThangNam.setFont(new Font("Arial", Font.BOLD, 16));

        navPanel.add(btnThangTruoc, BorderLayout.WEST);
        navPanel.add(lblThangNam, BorderLayout.CENTER);
        navPanel.add(btnThangSau, BorderLayout.EAST);

        // Calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarPanel.setBackground(Color.WHITE);

        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();

        return panel;
    }

    private JPanel createTimelinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch hẹn ngày " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        panel.setBackground(COLOR_BACKGROUND);

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        updateTimeline();

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt lịch"));
        formPanel.setVisible(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 - Khách hàng
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblKhachHang = new JLabel("Khách hàng *:");
        lblKhachHang.setForeground(Color.WHITE);
        formPanel.add(lblKhachHang, gbc);

        gbc.gridx = 1;
        cbKhachHang = new JComboBox<>();
        cbKhachHang.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbKhachHang, gbc);

        // Row 1 - Số lượng người
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblSoLuongNguoi = new JLabel("Số lượng người:");
        lblSoLuongNguoi.setForeground(Color.WHITE);
        formPanel.add(lblSoLuongNguoi, gbc);

        gbc.gridx = 1;
        spinnerSoLuongNguoi = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spinnerSoLuongNguoi.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(spinnerSoLuongNguoi, gbc);

        // Row 2 - Dịch vụ (combobox + nút thêm)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblDichVu = new JLabel("Dịch vụ:");
        lblDichVu.setForeground(Color.WHITE);
        formPanel.add(lblDichVu, gbc);

        gbc.gridx = 1;
        JPanel dichVuPanel = new JPanel(new BorderLayout(5, 0));
        cbDichVu = new JComboBox<>();
        cbDichVu.setFont(new Font("Arial", Font.PLAIN, 14));
        dichVuPanel.add(cbDichVu, BorderLayout.CENTER);

        btnThemDichVu = createStyledButton("+", COLOR_PRIMARY);
        btnThemDichVu.setPreferredSize(new Dimension(40, 25));
        dichVuPanel.add(btnThemDichVu, BorderLayout.EAST);

        formPanel.add(dichVuPanel, gbc);

        // Row 3 - Danh sách dịch vụ đã chọn
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblDSDichVu = new JLabel("DS dịch vụ:");
        lblDSDichVu.setForeground(Color.WHITE);
        formPanel.add(lblDSDichVu, gbc);

        gbc.gridx = 1;
        JPanel listPanel = new JPanel(new BorderLayout());
        listModelDichVu = new DefaultListModel<>();
        listDichVu = new JList<>(listModelDichVu);
        listDichVu.setFont(new Font("Arial", Font.PLAIN, 12));
        listDichVu.setBackground(COLOR_LIST_BG);
        listDichVu.setForeground(COLOR_LIST_FOREGROUND);
        listDichVu.setSelectionBackground(COLOR_LIST_BG.darker());
        listDichVu.setSelectionForeground(Color.WHITE);
        listDichVu.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollList = new JScrollPane(listDichVu);
        scrollList.setPreferredSize(new Dimension(200, 80));
        scrollList.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 1));
        listPanel.add(scrollList, BorderLayout.CENTER);

        btnXoaDichVu = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXoaDichVu.setPreferredSize(new Dimension(60, 25));
        listPanel.add(btnXoaDichVu, BorderLayout.EAST);

        formPanel.add(listPanel, gbc);

        // THÊM DÒNG MỚI: Nhân viên thực hiện dịch vụ
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblNhanVienDV = new JLabel("NV thực hiện:");
        lblNhanVienDV.setForeground(Color.WHITE);
        formPanel.add(lblNhanVienDV, gbc);

        gbc.gridx = 1;
        JPanel nhanVienPanel = new JPanel(new BorderLayout(5, 0));
        cbNhanVienDichVu = new JComboBox<>();
        cbNhanVienDichVu.setFont(new Font("Arial", Font.PLAIN, 14));
        nhanVienPanel.add(cbNhanVienDichVu, BorderLayout.CENTER);

        btnPhanCongNV = createStyledButton("Phân công", COLOR_PRIMARY);
        btnPhanCongNV.setPreferredSize(new Dimension(100, 25));
        nhanVienPanel.add(btnPhanCongNV, BorderLayout.EAST);

        formPanel.add(nhanVienPanel, gbc);

        // Row 5 - Giường (tăng số thứ tự lên)
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblGiuong = new JLabel("Giường:");
        lblGiuong.setForeground(Color.WHITE);
        formPanel.add(lblGiuong, gbc);

        gbc.gridx = 1;
        cbGiuong = new JComboBox<>();
        cbGiuong.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbGiuong, gbc);

        // Row 6 - Ngày đặt (tăng số thứ tự lên)
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblNgayDat = new JLabel("Ngày đặt *:");
        lblNgayDat.setForeground(Color.WHITE);
        formPanel.add(lblNgayDat, gbc);

        gbc.gridx = 1;
        txtNgayDat = new JTextField();
        txtNgayDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtNgayDat, gbc);

        // Row 7 - Giờ đặt (tăng số thứ tự lên)
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel lblGioDat = new JLabel("Giờ đặt *:");
        lblGioDat.setForeground(Color.WHITE);
        formPanel.add(lblGioDat, gbc);

        gbc.gridx = 1;
        txtGioDat = new JTextField();
        txtGioDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtGioDat, gbc);

        // Row 8 - Ghi chú (tăng số thứ tự lên)
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel lblGhiChu = new JLabel("Ghi chú:");
        lblGhiChu.setForeground(Color.WHITE);
        formPanel.add(lblGhiChu, gbc);

        gbc.gridx = 1;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 14));
        txtGhiChu.setLineWrap(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        formPanel.add(scrollGhiChu, gbc);

        // Button row (tăng số thứ tự lên)
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.setBackground(COLOR_BACKGROUND);

        btnThem = createStyledButton("Thêm mới", COLOR_PRIMARY);
        btnSua = createStyledButton("Sửa", COLOR_PRIMARY);
        btnXoa = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXacNhan = createStyledButton("Xác nhận", new Color(0x2E, 0xCC, 0x71));
        btnHuy = createStyledButton("Hủy lịch", new Color(0xE6, 0x7E, 0x22));
        btnHoanThanh = createStyledButton("Hoàn thành", new Color(0x34, 0x98, 0xDB));

        formButtonPanel.add(btnThem);
        formButtonPanel.add(btnSua);
        formButtonPanel.add(btnXoa);
        formButtonPanel.add(btnXacNhan);
        formButtonPanel.add(btnHuy);
        formButtonPanel.add(btnHoanThanh);
        formPanel.add(formButtonPanel, gbc);

        return formPanel;
    }

    // THÊM PHƯƠNG THỨC LOAD NHÂN VIÊN
    public void loadNhanVienChoDichVu() {
        try {
            List<NhanVien> dsNhanVien = nhanVienService.getAllNhanVien();

            cbNhanVienDichVu.removeAllItems();
            cbNhanVienDichVu.addItem(new NhanVien()); // Item trống

            for (NhanVien nv : dsNhanVien) {
                cbNhanVienDichVu.addItem(nv);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // THÊM PHƯƠNG THỨC ĐỂ LẤY VÀ THIẾT LẬP PHÂN CÔNG NHÂN VIÊN
    public Map<DichVu, NhanVien> getPhanCongNhanVien() {
        return phanCongNhanVien;
    }

    public void setPhanCongNhanVien(Map<DichVu, NhanVien> phanCongNhanVien) {
        this.phanCongNhanVien = phanCongNhanVien;
    }

    public void themPhanCongNhanVien(DichVu dichVu, NhanVien nhanVien) {
        this.phanCongNhanVien.put(dichVu, nhanVien);
    }

    public void xoaPhanCongNhanVien(DichVu dichVu) {
        this.phanCongNhanVien.remove(dichVu);
    }

    public void clearPhanCongNhanVien() {
        this.phanCongNhanVien.clear();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate lastOfMonth = yearMonth.atEndOfMonth();

        // Update month year label
        lblThangNam.setText(yearMonth.getMonth().toString() + " " + yearMonth.getYear());

        // Day headers
        String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String dayName : dayNames) {
            JLabel lblDay = new JLabel(dayName, JLabel.CENTER);
            lblDay.setFont(new Font("Arial", Font.BOLD, 12));
            lblDay.setForeground(COLOR_SECONDARY);
            lblDay.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            calendarPanel.add(lblDay);
        }

        // Fill in days before first of month
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        for (int i = 0; i < firstDayOfWeek.getValue() % 7; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // Add days of month
        for (int day = 1; day <= lastOfMonth.getDayOfMonth(); day++) {
            final LocalDate date = yearMonth.atDay(day);
            JButton btnDay = createDayButton(date);
            calendarPanel.add(btnDay);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JButton createDayButton(LocalDate date) {
        JButton btn = new JButton(String.valueOf(date.getDayOfMonth()));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Style based on date
        if (date.equals(LocalDate.now())) {
            btn.setBackground(COLOR_TODAY);
            btn.setForeground(Color.WHITE);
        } else if (date.equals(selectedDate)) {
            btn.setBackground(COLOR_SELECTED);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        }

        // Add event indicator if there are appointments
        try {
            List<DatLich> appointments = datLichService.getDatLichTheoNgay(date);
            if (!appointments.isEmpty()) {
                btn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2));
            }
        } catch (Exception e) {
            // Ignore errors for event indicators
        }

        btn.addActionListener(e -> selectDate(date));

        return btn;
    }

    public void updateTimeline() {
        timelinePanel.removeAll();

        try {
            List<DatLich> appointments = datLichService.getDatLichTheoNgay(selectedDate);

            if (appointments.isEmpty()) {
                JLabel lblEmpty = new JLabel("Không có lịch hẹn nào cho ngày này", JLabel.CENTER);
                lblEmpty.setFont(new Font("Arial", Font.ITALIC, 14));
                lblEmpty.setForeground(COLOR_SECONDARY);
                timelinePanel.add(lblEmpty);
            } else {
                // Sort appointments by time
                appointments.sort((a, b) -> a.getGioDat().compareTo(b.getGioDat()));

                for (DatLich appointment : appointments) {
                    timelinePanel.add(createAppointmentPanel(appointment));
                    timelinePanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        } catch (Exception e) {
            JLabel lblError = new JLabel("Lỗi khi tải lịch hẹn: " + e.getMessage(), JLabel.CENTER);
            lblError.setForeground(Color.RED);
            timelinePanel.add(lblError);
        }

        timelinePanel.revalidate();
        timelinePanel.repaint();

        // Update panel title
        Container parent = timelinePanel.getParent();
        while (parent != null && !(parent instanceof JPanel)) {
            parent = parent.getParent();
        }
        if (parent instanceof JPanel) {
            ((JPanel) parent).setBorder(BorderFactory.createTitledBorder(
                    "Lịch hẹn ngày " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
        }
    }

    private void showAppointmentDetails(DatLich appointment) {
        StringBuilder details = new StringBuilder();
        details.append("Chi tiết lịch hẹn:\n");
        details.append("Khách hàng: ").append(getTenKhachHang(appointment.getMaKhachHang())).append("\n");
        KhachHang kh = khachHangService.getKhachHangById(appointment.getMaKhachHang());
        if (kh != null) {
            details.append("Điểm tích lũy: ").append(kh.getDiemTichLuy()).append(" điểm\n");
        }
        details.append("Số lượng người: ").append(appointment.getSoLuongNguoi()).append("\n");
        details.append("Thời gian: ").append(appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
        details.append("Trạng thái: ").append(appointment.getTrangThai()).append("\n");

        if (appointment.getMaGiuong() != null) {
            details.append("Giường: ").append(getTenGiuong(appointment.getMaGiuong())).append("\n");
        }

        // THÊM THÔNG TIN NHÂN VIÊN THỰC HIỆN DỊCH VỤ
        if (appointment.hasDichVu()) {
            details.append("\nDịch vụ và nhân viên thực hiện:\n");
            for (int i = 0; i < appointment.getDanhSachDichVu().size(); i++) {
                DatLichChiTiet chiTiet = appointment.getDanhSachDichVu().get(i);
                details.append("• ").append(chiTiet.getDichVu().getTenDichVu());
                if (chiTiet.getNhanVien() != null) {
                    details.append(" - NV: ").append(chiTiet.getNhanVien().getHoTen());
                } else {
                    details.append(" - Chưa phân công NV");
                }
                details.append("\n");
            }
        }

        details.append("\nGhi chú: ").append(appointment.getGhiChu() != null ? appointment.getGhiChu() : "Không có");

        JOptionPane.showMessageDialog(this, details.toString(),
                "Chi tiết lịch hẹn",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // CẬP NHẬT PHƯƠNG THỨC HIỂN THỊ TRONG TIMELINE
    private JPanel createAppointmentPanel(DatLich appointment) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Time and status panel - LEFT
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setBackground(new Color(0x8C, 0xC9, 0x80));

        JLabel lblTime = new JLabel(appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblTime.setFont(new Font("Arial", Font.BOLD, 16));
        lblTime.setForeground(Color.WHITE);
        lblTime.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblStatus = new JLabel(appointment.getTrangThai());
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        // Color code status với màu sắc đẹp hơn
        switch (appointment.getTrangThai()) {
            case "Đã xác nhận":
                lblStatus.setForeground(new Color(0x2E, 0xCC, 0x71)); // Xanh lá sáng
                break;
            case "Đã hủy":
                lblStatus.setForeground(new Color(0xE7, 0x4C, 0x3C)); // Đỏ
                break;
            case "Hoàn thành":
                lblStatus.setForeground(new Color(0x34, 0x98, 0xDB)); // Xanh dương
                break;
            case "Đang thực hiện":
                lblStatus.setForeground(new Color(0xF3, 0x9C, 0x12)); // Vàng cam
                break;
            default: // Chờ xác nhận
                lblStatus.setForeground(new Color(0xF1, 0xC4, 0x0F)); // Vàng
        }

        leftPanel.add(lblTime);
        leftPanel.add(lblStatus);

        // Customer and service info - CENTER
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(new Color(0x8C, 0xC9, 0x80));

        String customerName = getTenKhachHang(appointment.getMaKhachHang());
        JLabel lblCustomer = new JLabel(customerName);
        lblCustomer.setFont(new Font("Arial", Font.BOLD, 14));
        lblCustomer.setForeground(Color.WHITE);

        // Service names với hiển thị thông tin nhân viên
        StringBuilder services = new StringBuilder();
        if (appointment.hasDichVu()) {
            for (int i = 0; i < Math.min(appointment.getDanhSachDichVu().size(), 2); i++) {
                if (i > 0) {
                    services.append(" • ");
                }
                DatLichChiTiet chiTiet = appointment.getDanhSachDichVu().get(i);
                services.append(chiTiet.getDichVu().getTenDichVu());
                if (chiTiet.getNhanVien() != null) {
                    services.append(" (NV: ").append(chiTiet.getNhanVien().getHoTen()).append(")");
                }
            }
            if (appointment.getDanhSachDichVu().size() > 2) {
                services.append(" • ...");
            }
        } else {
            services.append("Không có dịch vụ");
        }

        JLabel lblServices = new JLabel("<html><div style='width: 200px;'>" + services.toString() + "</div></html>");
        lblServices.setFont(new Font("Arial", Font.PLAIN, 12));
        lblServices.setForeground(new Color(0xEC, 0xF0, 0xF1));

        centerPanel.add(lblCustomer, BorderLayout.NORTH);
        centerPanel.add(lblServices, BorderLayout.CENTER);

        // Bed info - RIGHT (nếu có)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(0x8C, 0xC9, 0x80));

        if (appointment.getMaGiuong() != null) {
            String tenGiuong = getTenGiuong(appointment.getMaGiuong());
            JLabel lblGiuong = new JLabel("Giường: " + tenGiuong);
            lblGiuong.setFont(new Font("Arial", Font.ITALIC, 11));
            lblGiuong.setForeground(new Color(0xEC, 0xF0, 0xF1));
            rightPanel.add(lblGiuong, BorderLayout.NORTH);
        }

        // Thêm hiển thị số lượng người vào panel bên phải
        JLabel lblSoLuongNguoi = new JLabel("Số người: " + appointment.getSoLuongNguoi());
        lblSoLuongNguoi.setFont(new Font("Arial", Font.ITALIC, 11));
        lblSoLuongNguoi.setForeground(new Color(0xEC, 0xF0, 0xF1));
        rightPanel.add(lblSoLuongNguoi, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(COLOR_PRIMARY);
                leftPanel.setBackground(COLOR_PRIMARY);
                centerPanel.setBackground(COLOR_PRIMARY);
                rightPanel.setBackground(COLOR_PRIMARY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(0x8C, 0xC9, 0x80));
                leftPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
                centerPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
                rightPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setSelectedAppointment(appointment);
                showAppointmentDetails(appointment);
            }
        });

        return panel;
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        updateCalendar();
        updateTimeline();

        // Auto-fill date in form
        txtNgayDat.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void selectToday() {
        selectDate(LocalDate.now());
    }

    private void previousMonth() {
        currentDate = currentDate.minusMonths(1);
        updateCalendar();
    }

    private void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateCalendar();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Giảm padding
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
        loadComboboxData();
        updateTimeline();

        // Auto-fill today's date
        txtNgayDat.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void loadComboboxData() {
        try {
            // Load khách hàng
            cbKhachHang.removeAllItems();
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
            for (KhachHang kh : khachHangs) {
                cbKhachHang.addItem(kh);
            }

            // Load dịch vụ
            cbDichVu.removeAllItems();
            cbDichVu.addItem(new DichVu()); // Item trống
            List<DichVu> dichVus = dichVuService.getAllDichVu();
            for (DichVu dv : dichVus) {
                cbDichVu.addItem(dv);
            }

            // Load giường - CHỈ HIỂN THỊ GIƯỜNG CÓ THỂ ĐẶT
            cbGiuong.removeAllItems();
            cbGiuong.addItem(new Giuong()); // Item trống

            // Sử dụng phương thức mới để lấy giường khả dụng
            List<Giuong> availableGiuongs = giuongService.getGiuongAvailableForBooking();
            for (Giuong g : availableGiuongs) {
                cbGiuong.addItem(g);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu combobox: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshGiuongComboBox() {
        loadComboboxData(); // Tải lại toàn bộ dữ liệu combobox
    }

    private String getTenKhachHang(Integer maKhachHang) {
        if (maKhachHang == null) {
            return "Không xác định";
        }
        try {
            KhachHang kh = khachHangService.getKhachHangById(maKhachHang);
            return kh != null ? kh.getHoTen() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private String getTenGiuong(Integer maGiuong) {
        if (maGiuong == null) {
            return "Không xác định";
        }
        try {
            Giuong g = giuongService.getGiuongById(maGiuong);
            return g != null ? g.getSoHieu() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    // Getter methods for controller
    public JComboBox<KhachHang> getCbKhachHang() {
        return cbKhachHang;
    }

    public JComboBox<DichVu> getCbDichVu() {
        return cbDichVu;
    }
    // THÊM GETTER VÀ SETTER CHO maGiuongCu

    public Integer getMaGiuongCu() {
        return maGiuongCu;
    }

    public void setMaGiuongCu(Integer maGiuongCu) {
        this.maGiuongCu = maGiuongCu;
    }
    

    public JComboBox<Giuong> getCbGiuong() {
        return cbGiuong;
    }

    public JComboBox<NhanVien> getCbNhanVienDichVu() {
        return cbNhanVienDichVu;
    }

    public JButton getBtnPhanCongNV() {
        return btnPhanCongNV;
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

    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }

    public JButton getBtnXoaDichVu() {
        return btnXoaDichVu;
    }

    public JButton getBtnHoanThanh() {
        return btnHoanThanh;
    }

    public JList<DichVu> getListDichVu() {
        return listDichVu;
    }

    public DefaultListModel<DichVu> getListModelDichVu() {
        return listModelDichVu;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public DatLich getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(DatLich appointment) {
        this.selectedAppointment = appointment;
    }

    // Thêm getter cho spinner số lượng người
    public JSpinner getSpinnerSoLuongNguoi() {
        return spinnerSoLuongNguoi;
    }
}
