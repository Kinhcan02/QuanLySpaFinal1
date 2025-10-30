package View;

import Model.DatLich;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;

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

    // Components
    private JPanel calendarPanel;
    private JPanel timelinePanel;
    private JLabel lblThangNam;
    private JButton btnThangTruoc, btnThangSau;
    private JButton btnHomNay;
    private LocalDate currentDate;
    private LocalDate selectedDate;

    // Form components
    private JComboBox<KhachHang> cbKhachHang;
    private JComboBox<DichVu> cbDichVu;
    private JComboBox<Giuong> cbGiuong;
    private JTextField txtNgayDat;
    private JTextField txtGioDat;
    private JTextArea txtGhiChu;
    private JButton btnThem, btnSua, btnXoa, btnXacNhan, btnHuy;

    private final Color COLOR_BACKGROUND = new Color(0xf8, 0xf9, 0xfa);
    private final Color COLOR_PRIMARY = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_SECONDARY = new Color(0x6c, 0x75, 0x7d);
    private final Color COLOR_TODAY = new Color(0x0d, 0x6e, 0xfd);
    private final Color COLOR_SELECTED = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_EVENT = new Color(0xff, 0xe6, 0xe6);

    public QuanLyDatLichView() {
        initServices();
        initUI();
        loadData();
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

        currentDate = LocalDate.now();
        selectedDate = LocalDate.now();

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
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
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_PRIMARY);

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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Lịch tháng"));
        panel.setBackground(Color.WHITE);

        // Month navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);

        btnThangTruoc = createStyledButton("◀", COLOR_SECONDARY);
        btnThangTruoc.setPreferredSize(new Dimension(40, 30));
        btnThangTruoc.addActionListener(e -> previousMonth());

        btnThangSau = createStyledButton("▶", COLOR_SECONDARY);
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
        panel.setBackground(Color.WHITE);

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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Khách hàng *:"), gbc);

        gbc.gridx = 1;
        cbKhachHang = new JComboBox<>();
        cbKhachHang.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbKhachHang, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Dịch vụ:"), gbc);

        gbc.gridx = 1;
        cbDichVu = new JComboBox<>();
        cbDichVu.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbDichVu, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giường:"), gbc);

        gbc.gridx = 1;
        cbGiuong = new JComboBox<>();
        cbGiuong.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(cbGiuong, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày đặt *:"), gbc);

        gbc.gridx = 1;
        txtNgayDat = new JTextField();
        txtNgayDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtNgayDat, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Giờ đặt *:"), gbc);

        gbc.gridx = 1;
        txtGioDat = new JTextField();
        txtGioDat.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtGioDat, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Ghi chú:"), gbc);

        gbc.gridx = 1;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 14));
        txtGhiChu.setLineWrap(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        formPanel.add(scrollGhiChu, gbc);

        // Button row
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.setBackground(COLOR_BACKGROUND);

        btnThem = createStyledButton("Thêm mới", COLOR_PRIMARY);
        btnSua = createStyledButton("Sửa", COLOR_PRIMARY);
        btnXoa = createStyledButton("Xóa", new Color(0xE7, 0x4C, 0x3C));
        btnXacNhan = createStyledButton("Xác nhận", new Color(0x2E, 0xCC, 0x71));
        btnHuy = createStyledButton("Hủy lịch", new Color(0xE6, 0x7E, 0x22));

        formButtonPanel.add(btnThem);
        formButtonPanel.add(btnSua);
        formButtonPanel.add(btnXoa);
        formButtonPanel.add(btnXacNhan);
        formButtonPanel.add(btnHuy);

        formPanel.add(formButtonPanel, gbc);

        return formPanel;
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
                btn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
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

    private JPanel createAppointmentPanel(DatLich appointment) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(COLOR_EVENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Time and status
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(COLOR_EVENT);

        JLabel lblTime = new JLabel(appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblTime.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblStatus = new JLabel(appointment.getTrangThai());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Color code status
        switch (appointment.getTrangThai()) {
            case "Đã xác nhận":
                lblStatus.setForeground(new Color(0x28, 0xa7, 0x45));
                break;
            case "Đã hủy":
                lblStatus.setForeground(Color.RED);
                break;
            case "Hoàn thành":
                lblStatus.setForeground(new Color(0x00, 0x7b, 0xff));
                break;
            case "Đang thực hiện":
                lblStatus.setForeground(new Color(0xff, 0xc1, 0x07));
                break;
            default:
                lblStatus.setForeground(COLOR_SECONDARY);
        }

        leftPanel.add(lblTime);
        leftPanel.add(lblStatus);

        // Customer and service info
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COLOR_EVENT);

        String customerName = getTenKhachHang(appointment.getMaKhachHang());
        JLabel lblCustomer = new JLabel(customerName);
        lblCustomer.setFont(new Font("Arial", Font.BOLD, 12));

        // Service names
        StringBuilder services = new StringBuilder();
        if (appointment.hasDichVu()) {
            for (int i = 0; i < Math.min(appointment.getDanhSachDichVu().size(), 2); i++) {
                if (i > 0) services.append(", ");
                services.append(appointment.getDanhSachDichVu().get(i).getDichVu().getTenDichVu());
            }
            if (appointment.getDanhSachDichVu().size() > 2) {
                services.append("...");
            }
        } else {
            services.append("Không có dịch vụ");
        }

        JLabel lblServices = new JLabel(services.toString());
        lblServices.setFont(new Font("Arial", Font.PLAIN, 11));
        lblServices.setForeground(COLOR_SECONDARY);

        rightPanel.add(lblCustomer, BorderLayout.NORTH);
        rightPanel.add(lblServices, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        // Add click listener
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAppointmentDetails(appointment);
            }
        });

        return panel;
    }

    private void showAppointmentDetails(DatLich appointment) {
        // TODO: Implement appointment details dialog
        JOptionPane.showMessageDialog(this, 
            "Chi tiết lịch hẹn:\n" +
            "Khách hàng: " + getTenKhachHang(appointment.getMaKhachHang()) + "\n" +
            "Thời gian: " + appointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
            "Trạng thái: " + appointment.getTrangThai() + "\n" +
            "Ghi chú: " + (appointment.getGhiChu() != null ? appointment.getGhiChu() : "Không có"),
            "Chi tiết lịch hẹn",
            JOptionPane.INFORMATION_MESSAGE
        );
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

            // Load giường
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
        if (maKhachHang == null) return "Không xác định";
        try {
            KhachHang kh = khachHangService.getKhachHangById(maKhachHang);
            return kh != null ? kh.getHoTen() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    // Getter methods for controller
    public JComboBox<KhachHang> getCbKhachHang() { return cbKhachHang; }
    public JComboBox<DichVu> getCbDichVu() { return cbDichVu; }
    public JComboBox<Giuong> getCbGiuong() { return cbGiuong; }
    public JTextField getTxtNgayDat() { return txtNgayDat; }
    public JTextField getTxtGioDat() { return txtGioDat; }
    public JTextArea getTxtGhiChu() { return txtGhiChu; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnXacNhan() { return btnXacNhan; }
    public JButton getBtnHuy() { return btnHuy; }
    public LocalDate getSelectedDate() { return selectedDate; }
}