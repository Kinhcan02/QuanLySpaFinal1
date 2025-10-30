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
            DatLich datLich = validateAndGetFormData();
            if (datLich == null) return;

            boolean success = datLichService.addDatLich(datLich);
            if (success) {
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
        // Implementation for edit
        JOptionPane.showMessageDialog(view, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleXoa() {
        // Implementation for delete
        JOptionPane.showMessageDialog(view, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleXacNhan() {
        // Implementation for confirm
        JOptionPane.showMessageDialog(view, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleHuyLich() {
        // Implementation for cancel
        JOptionPane.showMessageDialog(view, "Chức năng đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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

        // Lấy thông tin dịch vụ và giường
        DichVu selectedDichVu = (DichVu) view.getCbDichVu().getSelectedItem();
        Giuong selectedGiuong = (Giuong) view.getCbGiuong().getSelectedItem();

        Integer maGiuong = (selectedGiuong != null && selectedGiuong.getMaGiuong() != null) 
                ? selectedGiuong.getMaGiuong() : null;

        String ghiChu = view.getTxtGhiChu().getText().trim();

        // Tạo đối tượng DatLich
        DatLich datLich = new DatLich();
        datLich.setMaKhachHang(selectedKhachHang.getMaKhachHang());
        datLich.setNgayDat(ngayDat);
        datLich.setGioDat(gioDat);
        datLich.setTrangThai("Chờ xác nhận");
        datLich.setMaGiuong(maGiuong);
        datLich.setGhiChu(ghiChu);

        // Thêm dịch vụ nếu có
        if (selectedDichVu != null && selectedDichVu.getMaDichVu() != null) {
            DatLichChiTiet chiTiet = new DatLichChiTiet();
            chiTiet.setMaDichVu(selectedDichVu.getMaDichVu());
            chiTiet.setDichVu(selectedDichVu);
            
            List<DatLichChiTiet> danhSachDichVu = new ArrayList<>();
            danhSachDichVu.add(chiTiet);
            datLich.setDanhSachDichVu(danhSachDichVu);
        }

        return datLich;
    }

    private void clearForm() {
        view.getCbKhachHang().setSelectedIndex(0);
        view.getCbDichVu().setSelectedIndex(0);
        view.getCbGiuong().setSelectedIndex(0);
        view.getTxtGioDat().setText("");
        view.getTxtGhiChu().setText("");
        // Giữ nguyên ngày đặt (ngày đang chọn trên lịch)
    }
}