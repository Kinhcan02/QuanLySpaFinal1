package Controller;

import View.QuanLyDatLichView;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import Model.DatLich;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;
import java.awt.Component;
import java.awt.Container;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyDatLichController implements ActionListener {

    private QuanLyDatLichView view;
    private DatLichService datLichService;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private GiuongService giuongService;

    private boolean isEditMode = false;
    private int currentEditId = -1;

    public QuanLyDatLichController(QuanLyDatLichView view) {
        try {
            this.view = view;
            this.datLichService = new DatLichService();
            this.khachHangService = new KhachHangService();
            this.dichVuService = new DichVuService();
            this.giuongService = new GiuongService();

            setupEventListeners();
            loadInitialData();
            setupThongBaoListener();
            setupGiuongStatusTimer();

        } catch (Exception e) {
            showError("Lỗi khi khởi tạo controller: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Main buttons
        view.getBtnThem().addActionListener(this);
        view.getBtnSua().addActionListener(this);
        view.getBtnXoa().addActionListener(this);
        view.getBtnXacNhan().addActionListener(this);
        view.getBtnHuy().addActionListener(this);

        // Tìm nút Lưu và Hủy trong form
        setupFormButtons();
        // Table selection listener
        view.getTableDatLich().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    handleTableSelection();
                }
            }
        });

        // Combobox change listeners
        view.getCbDichVu().addActionListener(e -> handleDichVuChange());
        view.getCbGiuong().addActionListener(e -> handleGiuongChange());
    }

    private void setupFormButtons() {
        // Tìm nút Lưu trong form
        JButton btnLuu = findButtonInForm("Lưu");
        if (btnLuu != null) {
            btnLuu.addActionListener(e -> handleLuu());
        }

        // Tìm nút Hủy trong form
        JButton btnHuyForm = findButtonInForm("Hủy");
        if (btnHuyForm != null) {
            btnHuyForm.addActionListener(e -> handleHuyForm());
        }
    }

    private JButton findButtonInForm(String buttonText) {
        JPanel formPanel = view.getFormPanel();
        return findButtonInContainer(formPanel, buttonText);
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

    private void setupThongBaoListener() {
        datLichService.addThongBaoListener(new DatLichService.ThongBaoListener() {
            @Override
            public void onThongBaoSapToiGio(DatLich datLich) {
                SwingUtilities.invokeLater(() -> {
                    String message = String.format(
                            "Lịch hẹn sắp đến giờ!\nKhách hàng: %s\nGiờ: %s\nDịch vụ: %s\nGiường: %s",
                            getTenKhachHang(datLich.getMaKhachHang()),
                            datLich.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                            getTenDichVu(datLich.getMaDichVu()),
                            getTenGiuong(datLich.getMaGiuong())
                    );

                    JOptionPane.showMessageDialog(view, message, "Thông báo lịch hẹn",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            }
        });
    }

    private void loadTableData() {
        try {
            List<DatLich> danhSachDatLich = datLichService.getAllDatLich();
            updateTableData(danhSachDatLich);
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void updateTableData(List<DatLich> danhSachDatLich) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

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
            model.addRow(row);
        }
    }

    private void loadComboboxData() {
        try {
            // Load khách hàng
            view.getCbKhachHang().removeAllItems();
            List<KhachHang> khachHangs = khachHangService.getAllKhachHang();

            // Thêm item mặc định nếu không có khách hàng
            if (khachHangs.isEmpty()) {
                view.getCbKhachHang().addItem(new KhachHang());
            } else {
                for (KhachHang kh : khachHangs) {
                    view.getCbKhachHang().addItem(kh);
                }
            }

            // Load dịch vụ
            view.getCbDichVu().removeAllItems();
            view.getCbDichVu().addItem(new DichVu()); // Item trống

            List<DichVu> dichVus = dichVuService.getAllDichVu();
            for (DichVu dv : dichVus) {
                view.getCbDichVu().addItem(dv);
            }

            // Load giường
            loadGiuongData();

        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu combobox: " + e.getMessage());

            // Đảm bảo combobox luôn có ít nhất 1 item
            ensureComboBoxHasItems();
        }
    }

    private void loadGiuongData() {
        try {
            view.getCbGiuong().removeAllItems();
            view.getCbGiuong().addItem(new Giuong()); // Item trống

            // Lấy danh sách giường với trạng thái cập nhật
            List<Giuong> giuongs = giuongService.getAllGiuongWithStatus();

            for (Giuong g : giuongs) {
                // Chỉ hiển thị giường trống hoặc có thể đặt được
                if (g.isTrong() || g.isDaDat()) {
                    view.getCbGiuong().addItem(g);
                }
            }

            // Nếu không có giường nào, đảm bảo vẫn có item trống
            if (view.getCbGiuong().getItemCount() == 1) { // Chỉ có item trống
                // Có thể thêm thông báo hoặc giữ nguyên
            }

        } catch (Exception e) {
            showError("Lỗi khi tải danh sách giường: " + e.getMessage());

            // Đảm bảo combobox giường có ít nhất item trống
            if (view.getCbGiuong().getItemCount() == 0) {
                view.getCbGiuong().addItem(new Giuong());
            }
        }
    }

    private void ensureComboBoxHasItems() {
        // Đảm bảo các combobox luôn có ít nhất 1 item
        if (view.getCbKhachHang().getItemCount() == 0) {
            view.getCbKhachHang().addItem(new KhachHang());
        }

        if (view.getCbDichVu().getItemCount() == 0) {
            view.getCbDichVu().addItem(new DichVu());
        }

        if (view.getCbGiuong().getItemCount() == 0) {
            view.getCbGiuong().addItem(new Giuong());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.getBtnThem()) {
            handleThemMoi();
        } else if (source == view.getBtnSua()) {
            handleSua();
        } else if (source == view.getBtnXoa()) {
            handleXoa();
        } else if (source == view.getBtnXacNhan()) {
            handleXacNhan();
        } else if (source == view.getBtnHuy()) {
            handleHuyLich();
        }
    }

    private void handleThemMoi() {
        try {
            isEditMode = false;
            currentEditId = -1;
            view.setFormMode(false); // Chế độ thêm mới

            // Kiểm tra xem có dữ liệu trong combobox không
            if (view.getCbKhachHang().getItemCount() <= 1) {
                showWarning("Không có khách hàng nào. Vui lòng thêm khách hàng trước khi đặt lịch.");
                return;
            }

            clearForm();
            // KHÔNG ẨN FORM - LUÔN HIỂN THỊ

            // Set focus vào field đầu tiên
            view.getCbKhachHang().requestFocusInWindow();

        } catch (Exception ex) {
            showError("Lỗi khi mở form thêm mới: " + ex.getMessage());
        }
    }

    private void handleSua() {
        int selectedRow = view.getTableDatLich().getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn một đặt lịch để sửa");
            return;
        }

        try {
            int maLich = (int) view.getTableModel().getValueAt(selectedRow, 0);
            DatLich datLich = datLichService.getDatLichById(maLich);

            if (datLich.isDaHuy() || datLich.isHoanThanh()) {
                showWarning("Không thể sửa lịch đã hủy hoặc hoàn thành");
                return;
            }

            isEditMode = true;
            currentEditId = maLich;
            view.setFormMode(true); // Chế độ chỉnh sửa

            fillFormWithData(datLich);
            // KHÔNG ẨN FORM - LUÔN HIỂN THỊ

        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu để sửa: " + e.getMessage());
        }
    }

    private void handleTableSelection() {
        int selectedRow = view.getTableDatLich().getSelectedRow();
        if (selectedRow != -1) {
            String trangThai = (String) view.getTableModel().getValueAt(selectedRow, 6);

            // Enable/disable buttons based on status
            view.getBtnXacNhan().setEnabled("Chờ xác nhận".equals(trangThai));
            view.getBtnHuy().setEnabled(!"Đã hủy".equals(trangThai) && !"Hoàn thành".equals(trangThai));
            view.getBtnSua().setEnabled(!"Đã hủy".equals(trangThai) && !"Hoàn thành".equals(trangThai));

            // TỰ ĐỘNG HIỂN THỊ DỮ LIỆU KHI CHỌN DÒNG
            try {
                int maLich = (int) view.getTableModel().getValueAt(selectedRow, 0);
                DatLich datLich = datLichService.getDatLichById(maLich);

                if (!datLich.isDaHuy() && !datLich.isHoanThanh()) {
                    isEditMode = true;
                    currentEditId = maLich;
                    view.setFormMode(true);
                    fillFormWithData(datLich);
                } else {
                    // Nếu là lịch đã hủy hoặc hoàn thành, chỉ hiển thị thông tin
                    isEditMode = false;
                    view.setFormMode(false);
                    fillFormWithData(datLich);
                    disableFormForViewOnly();
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải dữ liệu đặt lịch: " + e.getMessage());
            }
        }
    }

    private void disableFormForViewOnly() {
        // Vô hiệu hóa các control khi chỉ xem thông tin
        view.getCbKhachHang().setEnabled(false);
        view.getCbDichVu().setEnabled(false);
        view.getCbGiuong().setEnabled(false);
        view.getTxtNgayDat().setEnabled(false);
        view.getTxtGioDat().setEnabled(false);
        view.getTxtGhiChu().setEnabled(false);

        // Ẩn nút Lưu
        JButton btnLuu = findButtonInForm("Lưu");
        if (btnLuu != null) {
            btnLuu.setVisible(false);
        }
    }

    private void enableFormForEditing() {
        // Kích hoạt lại các control khi chỉnh sửa
        view.getCbKhachHang().setEnabled(true);
        view.getCbDichVu().setEnabled(true);
        view.getCbGiuong().setEnabled(true);
        view.getTxtNgayDat().setEnabled(true);
        view.getTxtGioDat().setEnabled(true);
        view.getTxtGhiChu().setEnabled(true);

        // Hiện nút Lưu
        JButton btnLuu = findButtonInForm("Lưu");
        if (btnLuu != null) {
            btnLuu.setVisible(true);
        }
    }

    private void handleLuu() {
        try {
            DatLich datLich = validateAndGetFormData();
            if (datLich == null) {
                return;
            }

            boolean success;
            if (isEditMode) {
                datLich.setMaLich(currentEditId);
                success = datLichService.updateDatLich(datLich);
            } else {
                success = datLichService.addDatLich(datLich);
            }

            if (success) {
                showSuccess(isEditMode ? "Cập nhật đặt lịch thành công" : "Thêm đặt lịch thành công");

                // Cập nhật lại trạng thái giường
                if (datLich.getMaGiuong() != null) {
                    Giuong giuong = giuongService.getGiuongById(datLich.getMaGiuong());
                    if (giuong != null) {
                        giuongService.updateGiuongStatus(giuong);
                    }
                }

                loadTableData();
                // KHÔNG ẨN FORM SAU KHI LƯU
                clearForm(); // Chỉ clear form để chuẩn bị cho lần nhập tiếp theo
                isEditMode = false;
                view.setFormMode(false);
                enableFormForEditing();

            } else {
                showError(isEditMode ? "Cập nhật đặt lịch thất bại" : "Thêm đặt lịch thất bại");
            }

        } catch (Exception e) {
            showError("Lỗi khi lưu đặt lịch: " + e.getMessage());
        }
    }

    private void handleHuyForm() {
        // Khi hủy form, chỉ clear dữ liệu chứ không ẩn form
        clearForm();
        isEditMode = false;
        view.setFormMode(false);
        enableFormForEditing();

        // Bỏ chọn dòng trong table
        view.getTableDatLich().clearSelection();
    }

    private void loadInitialData() {
        try {
            loadTableData();
            loadComboboxData();

            // TỰ ĐỘNG HIỂN THỊ DỮ LIỆU ĐẦU TIÊN NẾU CÓ
            if (view.getTableModel().getRowCount() > 0) {
                view.getTableDatLich().setRowSelectionInterval(0, 0);
                handleTableSelection();
            } else {
                // Nếu không có dữ liệu, hiển thị form trống để thêm mới
                clearForm();
            }

        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu ban đầu: " + e.getMessage());
            ensureComboBoxHasItems();
        }
    }

    private void handleXoa() {
        int selectedRow = view.getTableDatLich().getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn một đặt lịch để xóa");
            return;
        }

        try {
            int maLich = (int) view.getTableModel().getValueAt(selectedRow, 0);
            DatLich datLich = datLichService.getDatLichById(maLich);

            if (datLich.isDaXacNhan() && !datLich.isQuaGio()) {
                showWarning("Không thể xóa lịch đã xác nhận. Vui lòng hủy lịch trước.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa đặt lịch này?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.deleteDatLich(maLich);
                if (success) {
                    showSuccess("Xóa đặt lịch thành công");
                    loadTableData();
                } else {
                    showError("Xóa đặt lịch thất bại");
                }
            }

        } catch (Exception e) {
            showError("Lỗi khi xóa đặt lịch: " + e.getMessage());
        }
    }

    private void handleXacNhan() {
        int selectedRow = view.getTableDatLich().getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn một đặt lịch để xác nhận");
            return;
        }

        try {
            int maLich = (int) view.getTableModel().getValueAt(selectedRow, 0);
            DatLich datLich = datLichService.getDatLichById(maLich);

            if (!datLich.isCho()) {
                showWarning("Chỉ có thể xác nhận lịch đang ở trạng thái chờ");
                return;
            }

            // Kiểm tra xung đột giường
            if (datLich.getMaGiuong() != null) {
                boolean giuongTrong = datLichService.isGiuongTrong(
                        datLich.getMaGiuong(),
                        datLich.getNgayDat(),
                        datLich.getGioDat(),
                        datLich.getMaDichVu() != null ? getThoiGianDichVu(datLich.getMaDichVu()) : 60
                );

                if (!giuongTrong) {
                    showWarning("Giường đã được đặt trong khoảng thời gian này. Vui lòng chọn giường khác.");
                    return;
                }
            }

            boolean success = datLichService.updateTrangThai(maLich, "Đã xác nhận");
            if (success) {
                showSuccess("Xác nhận đặt lịch thành công");
                loadTableData();
            } else {
                showError("Xác nhận đặt lịch thất bại");
            }

        } catch (Exception e) {
            showError("Lỗi khi xác nhận đặt lịch: " + e.getMessage());
        }
    }

    private void handleHuyLich() {
        int selectedRow = view.getTableDatLich().getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn một đặt lịch để hủy");
            return;
        }

        try {
            int maLich = (int) view.getTableModel().getValueAt(selectedRow, 0);
            DatLich datLich = datLichService.getDatLichById(maLich);

            if (datLich.isDaHuy()) {
                showWarning("Lịch này đã được hủy trước đó");
                return;
            }

            if (datLich.isHoanThanh()) {
                showWarning("Không thể hủy lịch đã hoàn thành");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn hủy đặt lịch này?", "Xác nhận hủy",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(maLich, "Đã hủy");
                if (success) {
                    showSuccess("Hủy đặt lịch thành công");
                    loadTableData();
                } else {
                    showError("Hủy đặt lịch thất bại");
                }
            }

        } catch (Exception e) {
            showError("Lỗi khi hủy đặt lịch: " + e.getMessage());
        }
    }

    private void handleTimKiem() {
        String tuKhoa = view.getTxtTimKiem().getText().trim();
        String trangThai = (String) view.getCbTrangThai().getSelectedItem();

        try {
            List<DatLich> ketQua = datLichService.getAllDatLich();

            // Lọc theo từ khóa (tên khách hàng)
            if (!tuKhoa.isEmpty()) {
                ketQua.removeIf(datLich -> {
                    String tenKhachHang = getTenKhachHang(datLich.getMaKhachHang());
                    return !tenKhachHang.toLowerCase().contains(tuKhoa.toLowerCase());
                });
            }

            // Lọc theo trạng thái
            if (!trangThai.equals("Tất cả")) {
                ketQua.removeIf(datLich -> !datLich.getTrangThai().equals(trangThai));
            }

            updateTableData(ketQua);

        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void setupGiuongStatusTimer() {
        Timer timer = new Timer(60000, e -> {
            // Cập nhật lại danh sách giường
            loadGiuongData();
        });
        timer.start();
    }

    private void handleDichVuChange() {
        // Có thể thêm logic xử lý khi dịch vụ thay đổi
        // Ví dụ: tự động đề xuất thời gian kết thúc
    }

    private void handleGiuongChange() {
        // Có thể thêm logic kiểm tra giường trống
    }

    private boolean validateBeforeShowForm() {
        // Kiểm tra xem có khách hàng không
        if (view.getCbKhachHang().getItemCount() <= 1) { // Chỉ có item trống
            showWarning("Không có khách hàng nào. Vui lòng thêm khách hàng trước khi đặt lịch.");
            return false;
        }

        // Kiểm tra xem có dịch vụ không
        if (view.getCbDichVu().getItemCount() <= 1) { // Chỉ có item trống
            showWarning("Không có dịch vụ nào. Vui lòng thêm dịch vụ trước khi đặt lịch.");
            return false;
        }

        return true;
    }

    private DatLich validateAndGetFormData() {
        // Validate khách hàng
        KhachHang selectedKhachHang = (KhachHang) view.getCbKhachHang().getSelectedItem();
        if (selectedKhachHang == null || selectedKhachHang.getMaKhachHang() == null) {
            showWarning("Vui lòng chọn khách hàng");
            return null;
        }

        // Validate ngày đặt
        String ngayDatStr = view.getTxtNgayDat().getText().trim();
        LocalDate ngayDat;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            ngayDat = LocalDate.parse(ngayDatStr, formatter);
        } catch (DateTimeParseException e) {
            showWarning("Ngày đặt không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy");
            return null;
        }

        // Validate giờ đặt
        String gioDatStr = view.getTxtGioDat().getText().trim();
        LocalTime gioDat;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            gioDat = LocalTime.parse(gioDatStr, formatter);
        } catch (DateTimeParseException e) {
            showWarning("Giờ đặt không hợp lệ. Vui lòng nhập theo định dạng HH:mm");
            return null;
        }

        // Lấy thông tin dịch vụ và giường (có thể null)
        DichVu selectedDichVu = (DichVu) view.getCbDichVu().getSelectedItem();
        Giuong selectedGiuong = (Giuong) view.getCbGiuong().getSelectedItem();

        Integer maDichVu = (selectedDichVu != null && selectedDichVu.getMaDichVu() != null)
                ? selectedDichVu.getMaDichVu() : null;
        Integer maGiuong = (selectedGiuong != null && selectedGiuong.getMaGiuong() != null)
                ? selectedGiuong.getMaGiuong() : null;

        // Kiểm tra giường trống (nếu có chọn giường)
        if (maGiuong != null) {
            int thoiGianDichVu = maDichVu != null ? getThoiGianDichVu(maDichVu) : 60;
            boolean giuongTrong = datLichService.isGiuongTrong(maGiuong, ngayDat, gioDat, thoiGianDichVu);

            if (!giuongTrong && (!isEditMode || isGiuongChanged(maGiuong))) {
                showWarning("Giường đã được đặt trong khoảng thời gian này. Vui lòng chọn giường khác.");
                return null;
            }
        }

        String ghiChu = view.getTxtGhiChu().getText().trim();

        return new DatLich(
                selectedKhachHang.getMaKhachHang(),
                ngayDat,
                gioDat,
                maDichVu,
                "Chờ xác nhận",
                maGiuong,
                ghiChu
        );
    }

    private boolean isGiuongChanged(Integer newMaGiuong) {
        if (!isEditMode) {
            return true;
        }

        try {
            DatLich current = datLichService.getDatLichById(currentEditId);
            return !newMaGiuong.equals(current.getMaGiuong());
        } catch (Exception e) {
            return true;
        }
    }

    private void fillFormWithData(DatLich datLich) {
        // Chọn khách hàng
        for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
            KhachHang kh = (KhachHang) view.getCbKhachHang().getItemAt(i);
            if (kh.getMaKhachHang() != null && kh.getMaKhachHang().equals(datLich.getMaKhachHang())) {
                view.getCbKhachHang().setSelectedIndex(i);
                break;
            }
        }

        // Chọn dịch vụ
        if (datLich.getMaDichVu() != null) {
            for (int i = 0; i < view.getCbDichVu().getItemCount(); i++) {
                DichVu dv = (DichVu) view.getCbDichVu().getItemAt(i);
                if (dv.getMaDichVu() != null && dv.getMaDichVu().equals(datLich.getMaDichVu())) {
                    view.getCbDichVu().setSelectedIndex(i);
                    break;
                }
            }
        } else {
            view.getCbDichVu().setSelectedIndex(0); // Chọn item đầu tiên (item trống)
        }

        // Chọn giường
        if (datLich.getMaGiuong() != null) {
            for (int i = 0; i < view.getCbGiuong().getItemCount(); i++) {
                Giuong g = (Giuong) view.getCbGiuong().getItemAt(i);
                if (g.getMaGiuong() != null && g.getMaGiuong().equals(datLich.getMaGiuong())) {
                    view.getCbGiuong().setSelectedIndex(i);
                    break;
                }
            }
        } else {
            view.getCbGiuong().setSelectedIndex(0); // Chọn item đầu tiên (item trống)
        }

        // Điền thông tin khác
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        view.getTxtNgayDat().setText(datLich.getNgayDat().format(dateFormatter));
        view.getTxtGioDat().setText(datLich.getGioDat().format(timeFormatter));
        view.getTxtGhiChu().setText(datLich.getGhiChu() != null ? datLich.getGhiChu() : "");
    }

    private void clearForm() {
        try {
            // Kiểm tra combobox có item không trước khi set selected index
            if (view.getCbKhachHang().getItemCount() > 0) {
                view.getCbKhachHang().setSelectedIndex(0);
            }

            if (view.getCbDichVu().getItemCount() > 0) {
                view.getCbDichVu().setSelectedIndex(0);
            }

            if (view.getCbGiuong().getItemCount() > 0) {
                view.getCbGiuong().setSelectedIndex(0);
            }

            // Set ngày mặc định là hôm nay
            view.getTxtNgayDat().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            view.getTxtGioDat().setText("");
            view.getTxtGhiChu().setText("");

        } catch (Exception e) {
            System.err.println("Lỗi khi clear form: " + e.getMessage());
            // Không cần hiển thị thông báo lỗi cho người dùng
        }
    }

    private void showForm() {
        view.getFormPanel().setVisible(true);
        view.revalidate();
        view.repaint();
    }

    private void hideForm() {
        view.getFormPanel().setVisible(false);
        view.revalidate();
        view.repaint();
    }

    // Helper methods
    private String getTenKhachHang(Integer maKhachHang) {
        if (maKhachHang == null) {
            return "Chưa chọn";
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
            return "Chưa chọn";
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
            return "Chưa chọn";
        }
        try {
            Giuong g = giuongService.getGiuongById(maGiuong);
            return g != null ? g.getSoHieu() : "Không xác định";
        } catch (Exception e) {
            return "Lỗi";
        }
    }

    private int getThoiGianDichVu(Integer maDichVu) {
        if (maDichVu == null) {
            return 60;
        }
        try {
            DichVu dv = dichVuService.getDichVuById(maDichVu);
            return dv != null && dv.getThoiGian() != null ? dv.getThoiGian() : 60;
        } catch (Exception e) {
            return 60;
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(view, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
