package Controller;

import View.QuanLyThuChiView;
import Service.ThuNhapService;
import Service.ChiTieuService;
import Model.ThuNhap;
import Model.ChiTieu;
import Repository.ThuNhapRepository;
import Repository.ChiTieuRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class QuanLyThuChiController {
    private QuanLyThuChiView view;
    private ThuNhapService thuNhapService;
    private ChiTieuService chiTieuService;

    public QuanLyThuChiController(QuanLyThuChiView view) {
        this.view = view;
        this.thuNhapService = new ThuNhapService(new ThuNhapRepository());
        this.chiTieuService = new ChiTieuService(new ChiTieuRepository());
        
        initController();
        loadData();
    }

    private void initController() {
        // Thu nhập events
        view.getBtnThemThu().addActionListener(e -> themThuNhap());
        view.getBtnSuaThu().addActionListener(e -> suaThuNhap());
        view.getBtnXoaThu().addActionListener(e -> xoaThuNhap());
        view.getBtnLamMoiThu().addActionListener(e -> clearThuNhapFields());
        view.getBtnTimKiemThu().addActionListener(e -> timKiemThuNhap());
        
        // Chi tiêu events
        view.getBtnThemChi().addActionListener(e -> themChiTieu());
        view.getBtnSuaChi().addActionListener(e -> suaChiTieu());
        view.getBtnXoaChi().addActionListener(e -> xoaChiTieu());
        view.getBtnLamMoiChi().addActionListener(e -> clearChiTieuFields());
        view.getBtnTimKiemChi().addActionListener(e -> timKiemChiTieu());
        
        // Tổng quan events
        view.getBtnXemBaoCao().addActionListener(e -> xemBaoCao());
        
        // Table selection events
        view.getTblThuNhap().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectThuNhapRow();
            }
        });
        
        view.getTblChiTieu().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectChiTieuRow();
            }
        });
    }

    private void loadData() {
        loadThuNhap();
        loadChiTieu();
        updateTongQuan();
    }

    private void loadThuNhap() {
        DefaultTableModel model = view.getModelThuNhap();
        model.setRowCount(0);
        
        java.util.List<ThuNhap> list = thuNhapService.getAllThuNhap();
        for (ThuNhap tn : list) {
            model.addRow(new Object[]{
                tn.getMaThu(),
                tn.getNgayThu(),
                String.format("%,.0f VND", tn.getSoTien()),
                tn.getNoiDung(),
                "Phát sinh"
            });
        }
        
        // Update total - SỬA LỖI Ở ĐÂY
        BigDecimal tongThu = thuNhapService.tinhTongThuNhap(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        view.getLblTongThu().setText(String.format("%,.0f VND", tongThu));
        
        // Update từ hóa đơn
        BigDecimal tongThuHoaDon = thuNhapService.tinhTongThuNhapTuHoaDon(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        view.getLblTongThuHoaDon().setText(String.format("%,.0f VND", tongThuHoaDon));
    }

    private void loadChiTieu() {
        DefaultTableModel model = view.getModelChiTieu();
        model.setRowCount(0);
        
        java.util.List<ChiTieu> list = chiTieuService.getAllChiTieu();
        for (ChiTieu ct : list) {
            model.addRow(new Object[]{
                ct.getMaChi(),
                ct.getNgayChi(),
                String.format("%,.0f VND", ct.getSoTien()),
                ct.getMucDich(),
                "Phát sinh"
            });
        }
        
        // Update total - SỬA LỖI Ở ĐÂY
        BigDecimal tongChi = chiTieuService.tinhTongChiTieu(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        view.getLblTongChi().setText(String.format("%,.0f VND", tongChi));
        
        // Update từ nguyên liệu
        BigDecimal tongChiNguyenLieu = chiTieuService.tinhTongChiTuNguyenLieu(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        view.getLblTongChiNguyenLieu().setText(String.format("%,.0f VND", tongChiNguyenLieu));
    }

    private void themThuNhap() {
        try {
            // Chuyển đổi từ java.util.Date sang LocalDate
            Date selectedDate = view.getDateNgayThu().getDate();
            LocalDate ngayThu = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            String soTienText = view.getTxtSoTienThu().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền!");
                return;
            }
            
            BigDecimal soTien = new BigDecimal(soTienText);
            String noiDung = view.getTxtNoiDungThu().getText();

            if (noiDung.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập nội dung!");
                return;
            }

            ThuNhap thuNhap = new ThuNhap(ngayThu, soTien, noiDung);
            
            if (thuNhapService.themThuNhap(thuNhap)) {
                JOptionPane.showMessageDialog(view, "Thêm thu nhập thành công!");
                loadThuNhap();
                updateTongQuan();
                clearThuNhapFields();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thu nhập thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void suaThuNhap() {
        int selectedRow = view.getTblThuNhap().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thu nhập cần sửa!");
            return;
        }

        try {
            int maThu = (int) view.getModelThuNhap().getValueAt(selectedRow, 0);
            Date selectedDate = view.getDateNgayThu().getDate();
            LocalDate ngayThu = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            String soTienText = view.getTxtSoTienThu().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền!");
                return;
            }
            
            BigDecimal soTien = new BigDecimal(soTienText);
            String noiDung = view.getTxtNoiDungThu().getText();

            ThuNhap thuNhap = new ThuNhap(maThu, ngayThu, soTien, noiDung);
            
            if (thuNhapService.suaThuNhap(thuNhap)) {
                JOptionPane.showMessageDialog(view, "Sửa thu nhập thành công!");
                loadThuNhap();
                updateTongQuan();
                clearThuNhapFields();
            } else {
                JOptionPane.showMessageDialog(view, "Sửa thu nhập thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage());
        }
    }

    private void xoaThuNhap() {
        int selectedRow = view.getTblThuNhap().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thu nhập cần xóa!");
            return;
        }

        int maThu = (int) view.getModelThuNhap().getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa thu nhập này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (thuNhapService.xoaThuNhap(maThu)) {
                JOptionPane.showMessageDialog(view, "Xóa thu nhập thành công!");
                loadThuNhap();
                updateTongQuan();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thu nhập thất bại!");
            }
        }
    }

    private void themChiTieu() {
        try {
            // Chuyển đổi từ java.util.Date sang LocalDate
            Date selectedDate = view.getDateNgayChi().getDate();
            LocalDate ngayChi = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            String soTienText = view.getTxtSoTienChi().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền!");
                return;
            }
            
            BigDecimal soTien = new BigDecimal(soTienText);
            String mucDich = view.getTxtMucDichChi().getText();

            if (mucDich.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập mục đích!");
                return;
            }

            ChiTieu chiTieu = new ChiTieu(ngayChi, mucDich, soTien);
            
            if (chiTieuService.themChiTieu(chiTieu)) {
                JOptionPane.showMessageDialog(view, "Thêm chi tiêu thành công!");
                loadChiTieu();
                updateTongQuan();
                clearChiTieuFields();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm chi tiêu thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void suaChiTieu() {
        int selectedRow = view.getTblChiTieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn chi tiêu cần sửa!");
            return;
        }

        try {
            int maChi = (int) view.getModelChiTieu().getValueAt(selectedRow, 0);
            Date selectedDate = view.getDateNgayChi().getDate();
            LocalDate ngayChi = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            String soTienText = view.getTxtSoTienChi().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền!");
                return;
            }
            
            BigDecimal soTien = new BigDecimal(soTienText);
            String mucDich = view.getTxtMucDichChi().getText();

            ChiTieu chiTieu = new ChiTieu(maChi, ngayChi, mucDich, soTien);
            
            if (chiTieuService.suaChiTieu(chiTieu)) {
                JOptionPane.showMessageDialog(view, "Sửa chi tiêu thành công!");
                loadChiTieu();
                updateTongQuan();
                clearChiTieuFields();
            } else {
                JOptionPane.showMessageDialog(view, "Sửa chi tiêu thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage());
        }
    }

    private void xoaChiTieu() {
        int selectedRow = view.getTblChiTieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn chi tiêu cần xóa!");
            return;
        }

        int maChi = (int) view.getModelChiTieu().getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa chi tiêu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (chiTieuService.xoaChiTieu(maChi)) {
                JOptionPane.showMessageDialog(view, "Xóa chi tiêu thành công!");
                loadChiTieu();
                updateTongQuan();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa chi tiêu thất bại!");
            }
        }
    }

    private void timKiemThuNhap() {
        try {
            Date tuNgay = view.getDateTuNgayThu().getDate();
            Date denNgay = view.getDateDenNgayThu().getDate();
            
            if (tuNgay == null || denNgay == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn khoảng thời gian!");
                return;
            }
            
            LocalDate fromDate = tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate toDate = denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            java.util.List<ThuNhap> list = thuNhapService.getThuNhapByDateRange(fromDate, toDate);
            
            DefaultTableModel model = view.getModelThuNhap();
            model.setRowCount(0);
            
            for (ThuNhap tn : list) {
                model.addRow(new Object[]{
                    tn.getMaThu(),
                    tn.getNgayThu(),
                    String.format("%,.0f VND", tn.getSoTien()),
                    tn.getNoiDung(),
                    "Phát sinh"
                });
            }
            
            // Update totals
            BigDecimal tongThu = thuNhapService.tinhTongThuNhap(fromDate, toDate);
            BigDecimal tongThuHoaDon = thuNhapService.tinhTongThuNhapTuHoaDon(fromDate, toDate);
            
            view.getLblTongThu().setText(String.format("%,.0f VND", tongThu));
            view.getLblTongThuHoaDon().setText(String.format("%,.0f VND", tongThuHoaDon));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    private void timKiemChiTieu() {
        try {
            Date tuNgay = view.getDateTuNgayChi().getDate();
            Date denNgay = view.getDateDenNgayChi().getDate();
            
            if (tuNgay == null || denNgay == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn khoảng thời gian!");
                return;
            }
            
            LocalDate fromDate = tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate toDate = denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            java.util.List<ChiTieu> list = chiTieuService.getChiTieuByDateRange(fromDate, toDate);
            
            DefaultTableModel model = view.getModelChiTieu();
            model.setRowCount(0);
            
            for (ChiTieu ct : list) {
                model.addRow(new Object[]{
                    ct.getMaChi(),
                    ct.getNgayChi(),
                    String.format("%,.0f VND", ct.getSoTien()),
                    ct.getMucDich(),
                    "Phát sinh"
                });
            }
            
            // Update totals
            BigDecimal tongChi = chiTieuService.tinhTongChiTieu(fromDate, toDate);
            BigDecimal tongChiNguyenLieu = chiTieuService.tinhTongChiTuNguyenLieu(fromDate, toDate);
            
            view.getLblTongChi().setText(String.format("%,.0f VND", tongChi));
            view.getLblTongChiNguyenLieu().setText(String.format("%,.0f VND", tongChiNguyenLieu));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    private void xemBaoCao() {
        try {
            Date tuNgay = view.getDateTuNgayTQ().getDate();
            Date denNgay = view.getDateDenNgayTQ().getDate();
            
            if (tuNgay == null || denNgay == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn khoảng thời gian!");
                return;
            }
            
            LocalDate fromDate = tuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate toDate = denNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            BigDecimal tongThu = thuNhapService.tinhTongThuNhap(fromDate, toDate)
                                .add(thuNhapService.tinhTongThuNhapTuHoaDon(fromDate, toDate));
            BigDecimal tongChi = chiTieuService.tinhTongChiTieu(fromDate, toDate)
                                .add(chiTieuService.tinhTongChiTuNguyenLieu(fromDate, toDate));
            BigDecimal loiNhuan = tongThu.subtract(tongChi);
            
            view.getLblTongThuTongQuan().setText(String.format("%,.0f VND", tongThu));
            view.getLblTongChiTongQuan().setText(String.format("%,.0f VND", tongChi));
            view.getLblLoiNhuanTongQuan().setText(String.format("%,.0f VND", loiNhuan));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi xem báo cáo: " + e.getMessage());
        }
    }

    private void selectThuNhapRow() {
        int selectedRow = view.getTblThuNhap().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModelThuNhap();
            LocalDate ngayThu = (LocalDate) model.getValueAt(selectedRow, 1);
            
            // Chuyển đổi LocalDate sang java.util.Date
            Date date = java.sql.Date.valueOf(ngayThu);
            view.getDateNgayThu().setDate(date);
            
            String soTienText = model.getValueAt(selectedRow, 2).toString().replaceAll("[^\\d]", "");
            view.getTxtSoTienThu().setText(soTienText);
            view.getTxtNoiDungThu().setText(model.getValueAt(selectedRow, 3).toString());
        }
    }

    private void selectChiTieuRow() {
        int selectedRow = view.getTblChiTieu().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModelChiTieu();
            LocalDate ngayChi = (LocalDate) model.getValueAt(selectedRow, 1);
            
            // Chuyển đổi LocalDate sang java.util.Date
            Date date = java.sql.Date.valueOf(ngayChi);
            view.getDateNgayChi().setDate(date);
            
            String soTienText = model.getValueAt(selectedRow, 2).toString().replaceAll("[^\\d]", "");
            view.getTxtSoTienChi().setText(soTienText);
            view.getTxtMucDichChi().setText(model.getValueAt(selectedRow, 3).toString());
        }
    }

    private void updateTongQuan() {
        LocalDate fromDate = getStartOfMonth();
        LocalDate toDate = getEndOfMonth();
        
        BigDecimal tongThu = thuNhapService.tinhTongThuNhap(fromDate, toDate)
                          .add(thuNhapService.tinhTongThuNhapTuHoaDon(fromDate, toDate));
        BigDecimal tongChi = chiTieuService.tinhTongChiTieu(fromDate, toDate)
                          .add(chiTieuService.tinhTongChiTuNguyenLieu(fromDate, toDate));
        BigDecimal loiNhuan = tongThu.subtract(tongChi);
        
        view.getLblTongThuTongQuan().setText(String.format("%,.0f VND", tongThu));
        view.getLblTongChiTongQuan().setText(String.format("%,.0f VND", tongChi));
        view.getLblLoiNhuanTongQuan().setText(String.format("%,.0f VND", loiNhuan));
    }

    private LocalDate getStartOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate getEndOfMonth() {
        return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
    }

    private void clearThuNhapFields() {
        view.getTxtSoTienThu().setText("");
        view.getTxtNoiDungThu().setText("");
        view.getDateNgayThu().setDate(new Date());
        view.getTblThuNhap().clearSelection();
    }

    private void clearChiTieuFields() {
        view.getTxtSoTienChi().setText("");
        view.getTxtMucDichChi().setText("");
        view.getDateNgayChi().setDate(new Date());
        view.getTblChiTieu().clearSelection();
    }
}