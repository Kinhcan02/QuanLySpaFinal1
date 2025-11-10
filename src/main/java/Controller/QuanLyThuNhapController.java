package Controller;

import View.QuanLyThuNhapPanel;
import Service.ThuNhapService;
import Model.ThuNhap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class QuanLyThuNhapController {

    private QuanLyThuNhapPanel view;
    private ThuNhapService thuNhapService;

    public QuanLyThuNhapController(QuanLyThuNhapPanel view) {
        this.view = view;
        this.thuNhapService = new ThuNhapService();
        initController();
        loadData();
    }

    private void initController() {
        // Button events
        view.getBtnThem().addActionListener(e -> themThuNhap());
        view.getBtnSua().addActionListener(e -> suaThuNhap());
        view.getBtnXoa().addActionListener(e -> xoaThuNhap());
        view.getBtnLamMoi().addActionListener(e -> clearFields());
        view.getBtnTinhToan().addActionListener(e -> tinhToanThuNhap());

        // Table selection event
        view.getTblThuNhap().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectThuNhapRow();
                }
            }
        });

        // Thay đổi tháng năm để cập nhật thống kê
        view.getSpnThang().addChangeListener(e -> capNhatThongKe());
        view.getSpnNam().addChangeListener(e -> capNhatThongKe());
    }

    private void loadData() {
        DefaultTableModel model = view.getModelThuNhap();
        model.setRowCount(0);

        java.util.List<ThuNhap> list = thuNhapService.getAllThuNhap();
        for (ThuNhap tn : list) {
            model.addRow(new Object[]{
                tn.getMaThu(),
                tn.getThang(),
                tn.getNam(),
                formatTienTe(tn.getTongDoanhThuDichVu()),
                formatTienTe(tn.getTongLuongNhanVien()),
                formatTienTe(tn.getThuNhapThuc()), // Hiển thị cả âm nếu có
                tn.getNgayTinhThuNhap(),
                tn.getGhiChu()
            });
        }

        // Cập nhật thống kê sau khi load data
        capNhatThongKe();
    }

    private void themThuNhap() {
        try {
            int thang = (int) view.getSpnThang().getValue();
            int nam = (int) view.getSpnNam().getValue();

            String doanhThuText = view.getTxtTongDoanhThu().getText().replaceAll("[^\\d]", "");
            String luongText = view.getTxtTongLuong().getText().replaceAll("[^\\d]", "");

            if (doanhThuText.isEmpty() || luongText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin doanh thu và lương!");
                return;
            }

            BigDecimal tongDoanhThu = new BigDecimal(doanhThuText);
            BigDecimal tongLuong = new BigDecimal(luongText);
            String ghiChu = view.getTxtGhiChu().getText();

            if (tongDoanhThu.compareTo(BigDecimal.ZERO) < 0 || tongLuong.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(view, "Số tiền không được âm!");
                return;
            }

            // TÍNH TOÁN THU NHẬP THỰC - CHO PHÉP ÂM
            BigDecimal thuNhapThuc = tongDoanhThu.subtract(tongLuong);

            // Tạo đối tượng với thu nhập thực (có thể âm)
            ThuNhap thuNhap = new ThuNhap(0, thang, nam, tongDoanhThu, tongLuong,
                    thuNhapThuc, LocalDate.now(), ghiChu);

            if (thuNhapService.themThuNhap(thuNhap)) {
                JOptionPane.showMessageDialog(view, "Thêm thu nhập thành công!");
                loadData();
                clearFields();
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
            int thang = (int) view.getSpnThang().getValue();
            int nam = (int) view.getSpnNam().getValue();

            String doanhThuText = view.getTxtTongDoanhThu().getText().replaceAll("[^\\d]", "");
            String luongText = view.getTxtTongLuong().getText().replaceAll("[^\\d]", "");

            if (doanhThuText.isEmpty() || luongText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin doanh thu và lương!");
                return;
            }

            BigDecimal tongDoanhThu = new BigDecimal(doanhThuText);
            BigDecimal tongLuong = new BigDecimal(luongText);
            String ghiChu = view.getTxtGhiChu().getText();

            // Tính thu nhập thực - CHO PHÉP ÂM
            BigDecimal thuNhapThuc = tongDoanhThu.subtract(tongLuong);

            ThuNhap thuNhap = new ThuNhap(maThu, thang, nam, tongDoanhThu, tongLuong,
                    thuNhapThuc, LocalDate.now(), ghiChu);

            if (thuNhapService.suaThuNhap(thuNhap)) {
                JOptionPane.showMessageDialog(view, "Sửa thu nhập thành công!");
                loadData();
                clearFields();
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
        int thang = (int) view.getModelThuNhap().getValueAt(selectedRow, 1);
        int nam = (int) view.getModelThuNhap().getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa thu nhập tháng " + thang + "/" + nam + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (thuNhapService.xoaThuNhap(maThu)) {
                JOptionPane.showMessageDialog(view, "Xóa thu nhập thành công!");
                loadData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thu nhập thất bại!");
            }
        }
    }

    private void tinhToanThuNhap() {
        try {
            int thang = (int) view.getSpnThang().getValue();
            int nam = (int) view.getSpnNam().getValue();

            // Tính toán thu nhập từ service
            ThuNhap thuNhapTinhToan = thuNhapService.tinhToanThuNhapThang(thang, nam);

            // Hiển thị kết quả tính toán trên giao diện - CHO PHÉP HIỂN THỊ ÂM
            view.getLblTongDoanhThu().setText(formatTienTe(thuNhapTinhToan.getTongDoanhThuDichVu()));
            view.getLblTongLuong().setText(formatTienTe(thuNhapTinhToan.getTongLuongNhanVien()));

            // SỬA Ở ĐÂY: Điền vào form nhập liệu - DÙNG PHƯƠNG THỨC CHUYỂN ĐỔI ĐÚNG
            view.getTxtTongDoanhThu().setText(chuyenBigDecimalThanhChuoi(thuNhapTinhToan.getTongDoanhThuDichVu()));
            view.getTxtTongLuong().setText(chuyenBigDecimalThanhChuoi(thuNhapTinhToan.getTongLuongNhanVien()));
            view.getTxtGhiChu().setText("Tính toán tự động từ dữ liệu thực tế");

            // Hiển thị kết quả cho người dùng
            JOptionPane.showMessageDialog(view,
                    "ĐÃ TÍNH TOÁN THU NHẬP THÀNH CÔNG!\n\n"
                    + "Tháng: " + thang + "/" + nam + "\n"
                    + "Tổng doanh thu: " + formatTienTe(thuNhapTinhToan.getTongDoanhThuDichVu()) + "\n"
                    + "Tổng lương: " + formatTienTe(thuNhapTinhToan.getTongLuongNhanVien()) + "\n"
                    + "Dữ liệu đã được điền vào form. Bấm 'Thêm' để lưu vào hệ thống.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tính toán: " + e.getMessage());
            e.printStackTrace();
        }
    }

// THÊM PHƯƠNG THỨC CHUYỂN BIGDECIMAL THÀNH CHUỖI SỐ NGUYÊN
    private String chuyenBigDecimalThanhChuoi(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }

        // Làm tròn đến số nguyên
        BigDecimal rounded = amount.setScale(0, java.math.RoundingMode.HALF_UP);

        // Chuyển thành số nguyên và trả về chuỗi
        return rounded.toBigInteger().toString();
    }

    private void capNhatThongKe() {
        try {
            int thang = (int) view.getSpnThang().getValue();
            int nam = (int) view.getSpnNam().getValue();

            java.util.List<ThuNhap> list = thuNhapService.getThuNhapByThangNam(thang, nam);
            BigDecimal tongDoanhThu = BigDecimal.ZERO;
            BigDecimal tongLuong = BigDecimal.ZERO;
            BigDecimal tongThuNhapThuc = BigDecimal.ZERO;

            for (ThuNhap tn : list) {
                tongDoanhThu = tongDoanhThu.add(tn.getTongDoanhThuDichVu());
                tongLuong = tongLuong.add(tn.getTongLuongNhanVien());
                tongThuNhapThuc = tongThuNhapThuc.add(tn.getThuNhapThuc());
            }

            // Cập nhật giao diện - CHO PHÉP HIỂN THỊ ÂM
            view.getLblTongDoanhThu().setText(formatTienTe(tongDoanhThu));
            view.getLblTongLuong().setText(formatTienTe(tongLuong));
            view.getLblThuNhapThuc().setText(formatTienTe(tongThuNhapThuc));

        } catch (Exception e) {
            e.printStackTrace();
            // Hiển thị giá trị mặc định khi có lỗi
            view.getLblTongDoanhThu().setText("0 VND");
            view.getLblTongLuong().setText("0 VND");
            view.getLblThuNhapThuc().setText("0 VND");
        }
    }

    private void selectThuNhapRow() {
        int selectedRow = view.getTblThuNhap().getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = view.getModelThuNhap();

            view.getSpnThang().setValue(model.getValueAt(selectedRow, 1));
            view.getSpnNam().setValue(model.getValueAt(selectedRow, 2));

            String doanhThuText = model.getValueAt(selectedRow, 3).toString().replaceAll("[^\\d-]", "");
            view.getTxtTongDoanhThu().setText(doanhThuText.replace("-", ""));

            String luongText = model.getValueAt(selectedRow, 4).toString().replaceAll("[^\\d-]", "");
            view.getTxtTongLuong().setText(luongText.replace("-", ""));

            view.getTxtGhiChu().setText(model.getValueAt(selectedRow, 7).toString());

            // Cập nhật thống kê khi chọn row
            capNhatThongKe();
        }
    }

    private void clearFields() {
        view.getTxtTongDoanhThu().setText("");
        view.getTxtTongLuong().setText("");
        view.getTxtGhiChu().setText("");
        view.getTblThuNhap().clearSelection();

        // Cập nhật thống kê sau khi clear
        capNhatThongKe();
    }

    // PHƯƠNG THỨC FORMAT TIỀN TỆ - CHO PHÉP HIỂN THỊ ÂM
// PHƯƠNG THỨC FORMAT TIỀN TỆ - SỬA LẠI ĐỂ KHÔNG CHIA 1000
// PHƯƠNG THỨC FORMAT TIỀN TỆ - SỬA LẠI HOÀN TOÀN
    private String formatTienTe(BigDecimal amount) {
        if (amount == null) {
            return "0 VND";
        }

        try {
            // Làm tròn đến số nguyên
            BigDecimal rounded = amount.setScale(0, java.math.RoundingMode.HALF_UP);

            // Chuyển thành số nguyên
            java.math.BigInteger bigInt = rounded.toBigInteger();

            // Format số nguyên không có phần thập phân
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
            formatter.setNegativePrefix("-");

            String result = formatter.format(bigInt) + " VND";

            // Debug: in ra để kiểm tra
            System.out.println("Format tiền: " + amount + " -> " + result);

            return result;

        } catch (Exception e) {
            // Fallback đơn giản
            String result = amount.toBigInteger().toString() + " VND";
            System.out.println("Format lỗi, dùng fallback: " + result);
            return result;
        }
    }
}
