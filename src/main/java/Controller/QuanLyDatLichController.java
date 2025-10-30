package Controller;

import View.QuanLyDatLichView;
import Service.DatLichService;
import Service.KhachHangService;
import Service.DichVuService;
import Service.GiuongService;
import Model.DatLich;
import Model.DatLichChiTiet;
import Model.KhachHang;
import Model.DichVu;
import Model.Giuong;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        this.view = view;
        this.datLichService = new DatLichService();
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.giuongService = new GiuongService();

        setupEventListeners();
    }

    private void setupEventListeners() {
        view.getBtnThem().addActionListener(this);
        view.getBtnSua().addActionListener(this);
        view.getBtnXoa().addActionListener(this);
        view.getBtnXacNhan().addActionListener(this);
        view.getBtnHuy().addActionListener(this);
        view.getBtnThemDichVu().addActionListener(this);
        view.getBtnXoaDichVu().addActionListener(this);
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
        } else if (source == view.getBtnThemDichVu()) {
            handleThemDichVu();
        } else if (source == view.getBtnXoaDichVu()) {
            handleXoaDichVu();
        }
    }

    private void handleThemMoi() {
        try {
            DatLich datLich = validateAndGetFormData();
            if (datLich == null) return;

            boolean success = datLichService.addDatLich(datLich);
            if (success) {
                // Cập nhật trạng thái giường thành "Đã đặt" ngay khi thêm lịch
                if (datLich.getMaGiuong() != null) {
                    giuongService.updateTrangThaiGiuong(datLich.getMaGiuong(), "Đã đặt");
                }
                
                JOptionPane.showMessageDialog(view, "Thêm lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                view.updateTimeline();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSua() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Chuyển sang chế độ sửa
            isEditMode = true;
            currentEditId = selectedAppointment.getMaLich();
            
            // Điền dữ liệu vào form
            fillFormData(selectedAppointment);
            
            JOptionPane.showMessageDialog(view, "Đã chuyển sang chế độ sửa. Vui lòng cập nhật thông tin và nhấn 'Thêm mới' để lưu thay đổi.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXoa() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa lịch hẹn này?\nKhách hàng: " + 
                khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen() +
                "\nThời gian: " + selectedAppointment.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + 
                selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.deleteDatLich(selectedAppointment.getMaLich());
                
                if (success && selectedAppointment.getMaGiuong() != null) {
                    // Cập nhật trạng thái giường thành "Trống" khi xóa lịch
                    giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Trống");
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXacNhan() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để xác nhận", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(view, 
                "Xác nhận lịch hẹn này?\nKhách hàng: " + 
                khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen() +
                "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                "Xác nhận lịch hẹn", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật trạng thái lịch hẹn
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã xác nhận");
                
                if (success && selectedAppointment.getMaGiuong() != null) {
                    // Cập nhật trạng thái giường thành "Đang phục vụ" khi xác nhận
                    giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Đang phục vụ");
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xác nhận lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Xác nhận lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xác nhận lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHuyLich() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để hủy", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(view, 
                "Hủy lịch hẹn này?\nKhách hàng: " + 
                khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen() +
                "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                "Xác nhận hủy lịch", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã hủy");
                
                if (success && selectedAppointment.getMaGiuong() != null) {
                    // Cập nhật trạng thái giường thành "Trống" khi hủy lịch
                    giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Trống");
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Hủy lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.updateTimeline();
                } else {
                    JOptionPane.showMessageDialog(view, "Hủy lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi hủy lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemDichVu() {
        DichVu selectedDichVu = (DichVu) view.getCbDichVu().getSelectedItem();
        if (selectedDichVu != null && selectedDichVu.getMaDichVu() != null) {
            // Kiểm tra trùng
            for (int i = 0; i < view.getListModelDichVu().size(); i++) {
                DichVu dv = view.getListModelDichVu().getElementAt(i);
                if (dv.getMaDichVu().equals(selectedDichVu.getMaDichVu())) {
                    JOptionPane.showMessageDialog(view, "Dịch vụ này đã được thêm", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            view.getListModelDichVu().addElement(selectedDichVu);
        }
    }

    private void handleXoaDichVu() {
        int selectedIndex = view.getListDichVu().getSelectedIndex();
        if (selectedIndex != -1) {
            view.getListModelDichVu().remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

private DatLich validateAndGetFormData() {
    // Validate khách hàng
    KhachHang selectedKhachHang = (KhachHang) view.getCbKhachHang().getSelectedItem();
    if (selectedKhachHang == null || selectedKhachHang.getMaKhachHang() == null) {
        JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    // Validate ngày đặt
    String ngayDatStr = view.getTxtNgayDat().getText().trim();
    LocalDate ngayDat;
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ngayDat = LocalDate.parse(ngayDatStr, formatter);
        
        // Kiểm tra ngày không được trong quá khứ
        if (ngayDat.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(view, "Ngày đặt không được trong quá khứ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(view, "Ngày đặt không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    // Validate giờ đặt
    String gioDatStr = view.getTxtGioDat().getText().trim();
    LocalTime gioDat;
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        gioDat = LocalTime.parse(gioDatStr, formatter);
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(view, "Giờ đặt không hợp lệ. Vui lòng nhập theo định dạng HH:mm", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    // Lấy thông tin giường (không kiểm tra trùng lịch)
    Giuong selectedGiuong = (Giuong) view.getCbGiuong().getSelectedItem();
    Integer maGiuong = (selectedGiuong != null && selectedGiuong.getMaGiuong() != null) 
            ? selectedGiuong.getMaGiuong() : null;

    String ghiChu = view.getTxtGhiChu().getText().trim();

    // Tạo đối tượng DatLich
    DatLich datLich = new DatLich();
    
    if (isEditMode && currentEditId != -1) {
        datLich.setMaLich(currentEditId);
    }
    
    datLich.setMaKhachHang(selectedKhachHang.getMaKhachHang());
    datLich.setNgayDat(ngayDat);
    datLich.setGioDat(gioDat);
    datLich.setTrangThai("Chờ xác nhận");
    datLich.setMaGiuong(maGiuong);
    datLich.setGhiChu(ghiChu);

    // Thêm TẤT CẢ dịch vụ từ danh sách
    List<DatLichChiTiet> danhSachDichVu = new ArrayList<>();
    for (int i = 0; i < view.getListModelDichVu().size(); i++) {
        DichVu dichVu = view.getListModelDichVu().getElementAt(i);
        if (dichVu != null && dichVu.getMaDichVu() != null) {
            DatLichChiTiet chiTiet = new DatLichChiTiet();
            chiTiet.setMaDichVu(dichVu.getMaDichVu());
            chiTiet.setDichVu(dichVu);
            danhSachDichVu.add(chiTiet);
        }
    }
    datLich.setDanhSachDichVu(danhSachDichVu);

    return datLich;
}

    private void fillFormData(DatLich datLich) {
        // Điền thông tin khách hàng
        for (int i = 0; i < view.getCbKhachHang().getItemCount(); i++) {
            KhachHang kh = view.getCbKhachHang().getItemAt(i);
            if (kh.getMaKhachHang().equals(datLich.getMaKhachHang())) {
                view.getCbKhachHang().setSelectedIndex(i);
                break;
            }
        }

        // Điền thông tin giường
        if (datLich.getMaGiuong() != null) {
            for (int i = 0; i < view.getCbGiuong().getItemCount(); i++) {
                Giuong g = view.getCbGiuong().getItemAt(i);
                if (g.getMaGiuong() != null && g.getMaGiuong().equals(datLich.getMaGiuong())) {
                    view.getCbGiuong().setSelectedIndex(i);
                    break;
                }
            }
        } else {
            view.getCbGiuong().setSelectedIndex(0);
        }

        // Điền ngày giờ
        view.getTxtNgayDat().setText(datLich.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        view.getTxtGioDat().setText(datLich.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")));
        view.getTxtGhiChu().setText(datLich.getGhiChu() != null ? datLich.getGhiChu() : "");

        // Điền danh sách dịch vụ
        view.getListModelDichVu().clear();
        if (datLich.hasDichVu()) {
            for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                if (chiTiet.getDichVu() != null) {
                    view.getListModelDichVu().addElement(chiTiet.getDichVu());
                }
            }
        }
    }

    private void clearForm() {
        view.getCbKhachHang().setSelectedIndex(0);
        view.getCbDichVu().setSelectedIndex(0);
        view.getCbGiuong().setSelectedIndex(0);
        view.getTxtGioDat().setText("");
        view.getTxtGhiChu().setText("");
        view.getListModelDichVu().clear();
        
        // Reset edit mode
        isEditMode = false;
        currentEditId = -1;
        
        // Giữ nguyên ngày đặt (ngày đang chọn trên lịch)
        view.getTxtNgayDat().setText(view.getSelectedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}