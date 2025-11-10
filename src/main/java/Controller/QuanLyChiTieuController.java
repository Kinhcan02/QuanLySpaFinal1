package Controller;

import View.QuanLyChiTieuPanel;
import Service.ChiTieuService;
import Model.ChiTieu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class QuanLyChiTieuController {

    private QuanLyChiTieuPanel view;
    private ChiTieuService chiTieuService;

    public QuanLyChiTieuController(QuanLyChiTieuPanel view) {
        this.view = view;
        this.chiTieuService = new ChiTieuService();
        initController();
        loadData();
    }

    private void initController() {
        // Button events
        view.getBtnThem().addActionListener(e -> themChiTieu());
        view.getBtnSua().addActionListener(e -> suaChiTieu());
        view.getBtnXoa().addActionListener(e -> xoaChiTieu());
        view.getBtnLamMoi().addActionListener(e -> clearFields());
        view.getBtnThongKe().addActionListener(e -> thongKeNguyenLieu());

        // Table selection event
        view.getTblChiTieu().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectChiTieuRow();
                }
            }
        });

        // Thay đổi tháng năm để cập nhật thống kê
        view.getSpnThang().addChangeListener(e -> loadData());
        view.getSpnNam().addChangeListener(e -> loadData());
    }

    private void loadData() {
        DefaultTableModel model = view.getModelChiTieu();
        model.setRowCount(0);

        int thang = (int) view.getSpnThang().getValue();
        int nam = (int) view.getSpnNam().getValue();

        java.util.List<ChiTieu> list = chiTieuService.getChiTieuByThangNam(thang, nam);
        for (ChiTieu ct : list) {
            model.addRow(new Object[]{
                ct.getMaChi(),
                ct.getThang(),
                ct.getNam(),
                ct.getNgayChi(),
                String.format("%,.0f VND", ct.getSoTien()),
                ct.getMucDich(),
                ct.getLoaiChi(),
                ct.getNgayTao()
            });
        }

        capNhatThongKe(thang, nam);
    }

    private void thongKeNguyenLieu() {
        try {
            int thang = (int) view.getSpnThang().getValue();
            int nam = (int) view.getSpnNam().getValue();

            // Tính tổng chi phí nguyên liệu từ bảng NhapNguyenLieu
            BigDecimal tienNguyenLieu = chiTieuService.tinhTongNhapNguyenLieuThang(thang, nam);
            
            if (tienNguyenLieu.compareTo(BigDecimal.ZERO) > 0) {
                // Kiểm tra xem đã có chi phí nguyên liệu trong database chưa
                BigDecimal daCoTrongDB = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Nguyên liệu");
                
                if (daCoTrongDB.compareTo(BigDecimal.ZERO) > 0) {
                    JOptionPane.showMessageDialog(view, 
                            "Đã có chi phí nguyên liệu trong hệ thống: " + 
                            String.format("%,.0f VND", daCoTrongDB) +
                            "\nBạn có thể sửa/xóa trong bảng bên dưới.",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Hiển thị thông báo với số tiền từ bảng nhập nguyên liệu
                int option = JOptionPane.showConfirmDialog(view,
                        "Tổng chi phí nguyên liệu tháng " + thang + "/" + nam + " là: " + 
                        String.format("%,.0f VND", tienNguyenLieu) + 
                        "\n\nBạn có muốn thêm chi tiêu nguyên liệu này vào hệ thống?",
                        "Thống kê nguyên liệu",
                        JOptionPane.YES_NO_OPTION);
                
                if (option == JOptionPane.YES_OPTION) {
                    // Điền thông tin vào form
                    view.getSpnThangInput().setValue(thang);
                    view.getSpnNamInput().setValue(nam);
                    view.getTxtSoTien().setText(tienNguyenLieu.toBigInteger().toString());
                    view.getTxtMucDich().setText("Nhập nguyên liệu tháng " + thang + "/" + nam);
                    view.getCboLoaiChi().setSelectedItem("Nguyên liệu");
                    
                    JOptionPane.showMessageDialog(view, 
                            "Đã điền thông tin vào form!\nVui lòng kiểm tra và bấm 'Thêm' để lưu.",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(view, 
                        "Không có chi phí nguyên liệu trong tháng " + thang + "/" + nam,
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi thống kê: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void themChiTieu() {
        try {
            int thang = (int) view.getSpnThangInput().getValue();
            int nam = (int) view.getSpnNamInput().getValue();
            String mucDich = view.getTxtMucDich().getText().trim();
            String loaiChi = (String) view.getCboLoaiChi().getSelectedItem();

            String soTienText = view.getTxtSoTien().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty() || mucDich.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            BigDecimal soTien = new BigDecimal(soTienText);
            if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(view, "Số tiền phải lớn hơn 0!");
                return;
            }

            ChiTieu chiTieu = new ChiTieu(thang, nam, mucDich, soTien, loaiChi);

            if (chiTieuService.themChiTieu(chiTieu)) {
                JOptionPane.showMessageDialog(view, "Thêm chi tiêu thành công!");
                loadData();
                clearFields();
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
            int thang = (int) view.getSpnThangInput().getValue();
            int nam = (int) view.getSpnNamInput().getValue();
            String mucDich = view.getTxtMucDich().getText().trim();
            String loaiChi = (String) view.getCboLoaiChi().getSelectedItem();

            String soTienText = view.getTxtSoTien().getText().replaceAll("[^\\d]", "");
            if (soTienText.isEmpty() || mucDich.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            BigDecimal soTien = new BigDecimal(soTienText);
            if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(view, "Số tiền phải lớn hơn 0!");
                return;
            }

            // Lấy thông tin hiện tại để giữ nguyên ngày tạo
            ChiTieu chiTieuHienTai = chiTieuService.getChiTieuById(maChi);
            ChiTieu chiTieu = new ChiTieu(maChi, thang, nam, mucDich, soTien, loaiChi, chiTieuHienTai.getNgayTao());

            if (chiTieuService.suaChiTieu(chiTieu)) {
                JOptionPane.showMessageDialog(view, "Sửa chi tiêu thành công!");
                loadData();
                clearFields();
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
        String mucDich = view.getModelChiTieu().getValueAt(selectedRow, 5).toString();

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa chi tiêu: " + mucDich + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (chiTieuService.xoaChiTieu(maChi)) {
                JOptionPane.showMessageDialog(view, "Xóa chi tiêu thành công!");
                loadData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa chi tiêu thất bại!");
            }
        }
    }

    private void capNhatThongKe(int thang, int nam) {
        try {
            // CHỈ tính các chi phí ĐÃ CÓ TRONG DATABASE
            BigDecimal chiNguyenLieu = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Nguyên liệu");
            BigDecimal chiDien = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Điện");
            BigDecimal chiNuoc = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Nước");
            BigDecimal chiVeSinh = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Vệ sinh");
            BigDecimal chiWifi = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Wifi");
            BigDecimal chiKhac = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Khác");

            // TÍNH TỔNG CHI BẰNG TỔNG TẤT CẢ CÁC CHI PHÍ ĐÃ LƯU
            BigDecimal tongChi = chiNguyenLieu
                    .add(chiDien)
                    .add(chiNuoc)
                    .add(chiVeSinh)
                    .add(chiWifi)
                    .add(chiKhac);

            // Cập nhật giao diện - CHỈ HIỂN THỊ DỮ LIỆU ĐÃ LƯU TRONG DB
            view.getLblTongChi().setText(String.format("%,.0f VND", tongChi));
            view.getLblChiNguyenLieu().setText(String.format("%,.0f VND", chiNguyenLieu));
            view.getLblChiDien().setText(String.format("%,.0f VND", chiDien));
            view.getLblChiNuoc().setText(String.format("%,.0f VND", chiNuoc));
            view.getLblChiVeSinh().setText(String.format("%,.0f VND", chiVeSinh));
            view.getLblChiWifi().setText(String.format("%,.0f VND", chiWifi));
            view.getLblChiKhac().setText(String.format("%,.0f VND", chiKhac));

        } catch (Exception e) {
            e.printStackTrace();
            // Hiển thị giá trị mặc định khi có lỗi
            resetThongKe();
        }
    }

    private void resetThongKe() {
        view.getLblTongChi().setText("0 VND");
        view.getLblChiNguyenLieu().setText("0 VND");
        view.getLblChiDien().setText("0 VND");
        view.getLblChiNuoc().setText("0 VND");
        view.getLblChiVeSinh().setText("0 VND");
        view.getLblChiWifi().setText("0 VND");
        view.getLblChiKhac().setText("0 VND");
    }

    private void selectChiTieuRow() {
        int selectedRow = view.getTblChiTieu().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModelChiTieu();

            try {
                // SỬA LỖI Ở ĐÂY: Kiểm tra giá trị hợp lệ trước khi set cho Spinner
                Object thangValue = model.getValueAt(selectedRow, 1);
                Object namValue = model.getValueAt(selectedRow, 2);
                
                if (thangValue instanceof Integer) {
                    int thang = (Integer) thangValue;
                    if (thang >= 1 && thang <= 12) {
                        view.getSpnThangInput().setValue(thang);
                    }
                }
                
                if (namValue instanceof Integer) {
                    int nam = (Integer) namValue;
                    int currentYear = LocalDate.now().getYear();
                    if (nam >= 2020 && nam <= currentYear + 5) {
                        view.getSpnNamInput().setValue(nam);
                    }
                }

                String soTienText = model.getValueAt(selectedRow, 4).toString().replaceAll("[^\\d]", "");
                view.getTxtSoTien().setText(soTienText);

                view.getTxtMucDich().setText(model.getValueAt(selectedRow, 5).toString());
                view.getCboLoaiChi().setSelectedItem(model.getValueAt(selectedRow, 6));
                
            } catch (Exception e) {
                System.err.println("Lỗi khi chọn hàng: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        try {
            view.getSpnThangInput().setValue(LocalDate.now().getMonthValue());
            view.getSpnNamInput().setValue(LocalDate.now().getYear());
            view.getTxtSoTien().setText("");
            view.getTxtMucDich().setText("");
            view.getCboLoaiChi().setSelectedIndex(0);
            view.getTblChiTieu().clearSelection();
        } catch (Exception e) {
            System.err.println("Lỗi khi clear fields: " + e.getMessage());
        }
    }

    public BigDecimal getTongChiTheoNam(int nam) {
        try {
            return chiTieuService.tinhTongChiTieuNam(nam);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTongChiTheoThangNam(int thang, int nam) {
        try {
            return chiTieuService.tinhTongChiTieuThang(thang, nam);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}