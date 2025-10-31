package Controller;

import Model.*;
import Service.*;
import View.DatDichVuView;
import View.KhachHangDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Desktop;

// iText imports
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Font;
import java.util.HashSet;
import java.util.Set;

public class DatDichVuController {

    private DatDichVuView view;
    private KhachHangService khachHangService;
    private DichVuService dichVuService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;

    private NumberFormat currencyFormat;
    private KhachHang khachHangHienTai;
    private BigDecimal tongTien;

    public DatDichVuController(DatDichVuView view) {
        this.view = view;
        this.khachHangService = new KhachHangService();
        this.dichVuService = new DichVuService();
        this.nhanVienService = new NhanVienService();
        this.hoaDonService = new HoaDonService();

        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.tongTien = BigDecimal.ZERO;

        initController();
        loadDuLieuBanDau();
    }

    private void initController() {
        // Sự kiện cho combobox khách hàng
        view.getCboKhachHang().addActionListener(e -> handleChonKhachHang());

        // Sự kiện cho combobox dịch vụ
        view.getCboDichVu().addActionListener(e -> handleChonDichVu());

        // Sự kiện cho nút thêm khách hàng
        view.getBtnThemKhachHang().addActionListener(e -> handleThemKhachHang());

        // Sự kiện cho nút thêm dịch vụ vào danh sách
        view.getBtnThemDichVu().addActionListener(e -> handleThemDichVuVaoDanhSach());

        // Sự kiện cho nút xóa dịch vụ
        view.getBtnXoaDichVu().addActionListener(e -> handleXoaDichVu());

        // Sự kiện cho nút đổi điểm
        view.getBtnDoiDiem().addActionListener(e -> handleDoiDiem());

        // Sự kiện cho nút in hóa đơn
        view.getBtnInHoaDon().addActionListener(e -> handleInHoaDon());

        // Sự kiện cho nút làm mới
        view.getBtnLamMoi().addActionListener(e -> handleLamMoi());

        // Sự kiện double-click để xóa dịch vụ từ bảng
        view.getTblDichVuDaChon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleXoaDichVu();
                }
            }
        });
    }

    private void loadDuLieuBanDau() {
        loadKhachHang();
        loadDichVu();
        loadNhanVien();
        capNhatTongTien();
    }

    private void loadKhachHang() {
        try {
            List<KhachHang> dsKhachHang = khachHangService.getAllKhachHang();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn khách hàng --");

            for (KhachHang kh : dsKhachHang) {
                model.addElement(kh.getHoTen() + " - " + kh.getSoDienThoai());
            }

            view.getCboKhachHang().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách khách hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDichVu() {
        try {
            List<DichVu> dsDichVu = dichVuService.getAllDichVu();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn dịch vụ --");

            for (DichVu dv : dsDichVu) {
                model.addElement(dv.getTenDichVu() + " - " + currencyFormat.format(dv.getGia()));
            }

            view.getCboDichVu().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNhanVien() {
        try {
            List<NhanVien> dsNhanVien = nhanVienService.getAllNhanVien();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn nhân viên --");

            for (NhanVien nv : dsNhanVien) {
                model.addElement(nv.getHoTen() + " - " + nv.getChucVu());
            }

            view.getCboNhanVien().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChonKhachHang() {
        int selectedIndex = view.getCboKhachHang().getSelectedIndex();
        if (selectedIndex > 0) {
            try {
                String selected = (String) view.getCboKhachHang().getSelectedItem();
                String soDienThoai = selected.split(" - ")[1];

                khachHangHienTai = khachHangService.getKhachHangBySoDienThoai(soDienThoai);
                if (khachHangHienTai != null) {
                    view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lấy thông tin khách hàng: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleChonDichVu() {
        int selectedIndex = view.getCboDichVu().getSelectedIndex();
        if (selectedIndex > 0) {
            try {
                String selected = (String) view.getCboDichVu().getSelectedItem();
                String tenDichVu = selected.split(" - ")[0];

                List<DichVu> dsDichVu = dichVuService.getAllDichVu();
                for (DichVu dv : dsDichVu) {
                    if (dv.getTenDichVu().equals(tenDichVu)) {
                        view.getTxtThoiGian().setText(dv.getThoiGian() + " phút");
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lấy thông tin dịch vụ: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleThemKhachHang() {
        KhachHangDialog dialog = new KhachHangDialog((Frame) SwingUtilities.getWindowAncestor(view));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (validateKhachHangForm(dialog.getHoTen(), dialog.getSoDienThoai())) {
                try {
                    KhachHang khachHangMoi = new KhachHang();
                    khachHangMoi.setHoTen(dialog.getHoTen());
                    khachHangMoi.setSoDienThoai(dialog.getSoDienThoai());
                    khachHangMoi.setLoaiKhach(dialog.getLoaiKhach());
                    khachHangMoi.setGhiChu(dialog.getGhiChu());
                    khachHangMoi.setDiemTichLuy(0);
                    khachHangMoi.setNgayTao(java.time.LocalDateTime.now());

                    boolean success = khachHangService.addKhachHang(khachHangMoi);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadKhachHang(); // Tải lại danh sách khách hàng

                        // Tự động chọn khách hàng vừa thêm
                        for (int i = 0; i < view.getCboKhachHang().getItemCount(); i++) {
                            String item = view.getCboKhachHang().getItemAt(i);
                            if (item.contains(dialog.getSoDienThoai())) {
                                view.getCboKhachHang().setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi khi thêm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private boolean validateKhachHangForm(String hoTen, String soDienThoai) {
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập họ tên khách hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (soDienThoai.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập số điện thoại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(view, "Số điện thoại không hợp lệ (10-11 số)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra số điện thoại đã tồn tại chưa
        try {
            KhachHang khachHang = khachHangService.getKhachHangBySoDienThoai(soDienThoai);
            if (khachHang != null) {
                JOptionPane.showMessageDialog(view, "Số điện thoại đã tồn tại trong hệ thống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            // Bỏ qua lỗi nếu không tìm thấy
        }

        return true;
    }

    private void handleThemDichVuVaoDanhSach() {
        if (!kiemTraDuLieuHopLe()) {
            return;
        }

        try {
            String selectedDichVu = (String) view.getCboDichVu().getSelectedItem();
            String selectedNhanVien = (String) view.getCboNhanVien().getSelectedItem();

            String tenDichVu = selectedDichVu.split(" - ")[0];
            String tenNhanVien = selectedNhanVien.split(" - ")[0];
            String thoiGian = view.getTxtThoiGian().getText();

            // Lấy giá từ dịch vụ
            BigDecimal donGia = BigDecimal.ZERO;
            List<DichVu> dsDichVu = dichVuService.getAllDichVu();
            for (DichVu dv : dsDichVu) {
                if (dv.getTenDichVu().equals(tenDichVu)) {
                    donGia = dv.getGia();
                    break;
                }
            }

            int soLuong = Integer.parseInt(view.getTxtSoLuongNguoi().getText());
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));

            // Thêm vào bảng
            DefaultTableModel model = view.getTableModel();
            int stt = model.getRowCount() + 1;
            model.addRow(new Object[]{
                stt,
                tenDichVu,
                thoiGian,
                currencyFormat.format(donGia),
                soLuong,
                tenNhanVien,
                currencyFormat.format(thanhTien)
            });

            // Cập nhật tổng tiền
            tongTien = tongTien.add(thanhTien);
            capNhatTongTien();

            // Reset các trường nhập
            view.getCboDichVu().setSelectedIndex(0);
            view.getTxtThoiGian().setText("");
            view.getTxtSoLuongNguoi().setText("1");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXoaDichVu() {
        int selectedRow = view.getTblDichVuDaChon().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần xóa",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy thành tiền từ dòng được chọn
            String thanhTienStr = (String) view.getTableModel().getValueAt(selectedRow, 6);
            BigDecimal thanhTien = new BigDecimal(thanhTienStr.replaceAll("[^\\d]", ""));

            // Xóa dòng
            view.getTableModel().removeRow(selectedRow);

            // Cập nhật tổng tiền
            tongTien = tongTien.subtract(thanhTien);
            capNhatTongTien();

            // Cập nhật lại STT
            capNhatSTT();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa dịch vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDoiDiem() {
        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng trước khi đổi điểm",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int diemHienCo = khachHangHienTai.getDiemTichLuy();
        if (diemHienCo == 0) {
            JOptionPane.showMessageDialog(view, "Khách hàng không có điểm tích lũy để đổi",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(
                view,
                "Số điểm hiện có: " + diemHienCo + " điểm\n"
                + "1 điểm = 1 vé gọi đầu\n"
                + "Nhập số điểm muốn đổi:",
                "Đổi điểm tích lũy",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                int diemMuonDoi = Integer.parseInt(input.trim());
                if (diemMuonDoi <= 0) {
                    JOptionPane.showMessageDialog(view, "Số điểm phải lớn hơn 0",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (diemMuonDoi > diemHienCo) {
                    JOptionPane.showMessageDialog(view, "Số điểm muốn đổi vượt quá điểm hiện có",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Thêm dịch vụ "Vé gọi đầu" vào danh sách
                DefaultTableModel model = view.getTableModel();
                int stt = model.getRowCount() + 1;
                model.addRow(new Object[]{
                    stt,
                    "Vé gọi đầu (đổi điểm)",
                    "0 phút",
                    currencyFormat.format(0),
                    diemMuonDoi,
                    getNhanVienInfoFromTable(),
                    currencyFormat.format(0)
                });

                // Cập nhật điểm tích lũy
                khachHangHienTai.setDiemTichLuy(diemHienCo - diemMuonDoi);
                view.getLblDiemTichLuy().setText(khachHangHienTai.getDiemTichLuy() + " điểm");

                JOptionPane.showMessageDialog(view, "Đổi điểm thành công! Đã thêm " + diemMuonDoi + " vé gọi đầu",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Số điểm không hợp lệ",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleInHoaDon() {
        if (view.getTableModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng thêm dịch vụ trước khi in hóa đơn",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng trước khi in hóa đơn",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Xác nhận in hóa đơn
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn có muốn in hóa đơn PDF?\n"
                    + "Khách hàng: " + getTenKhachHangHienTai() + "\n"
                    + "Tổng tiền: " + currencyFormat.format(tongTien) + "\n"
                    + "Số dịch vụ: " + view.getTableModel().getRowCount(),
                    "Xác nhận in hóa đơn PDF",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Tạo và in hóa đơn PDF
            boolean success = inHoaDonPDF();

            if (success) {
                // Cập nhật điểm tích lũy cho khách hàng (100.000 VND = 1 điểm)
                int diemThuong = tongTien.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();
                if (diemThuong > 0) {
                    int diemMoi = khachHangHienTai.getDiemTichLuy() + diemThuong;
                    khachHangHienTai.setDiemTichLuy(diemMoi);
                    khachHangService.updateKhachHang(khachHangHienTai);
                    view.getLblDiemTichLuy().setText(diemMoi + " điểm");

                    JOptionPane.showMessageDialog(view,
                            "In hóa đơn thành công!\n"
                            + "Khách hàng được thưởng " + diemThuong + " điểm tích lũy!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "In hóa đơn thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }

                // Reset form sau khi in hóa đơn
                handleLamMoi();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xử lý hóa đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức in hóa đơn PDF chi tiết
    private boolean inHoaDonPDF() {
        FileOutputStream fos = null;
        try {
            // Tạo tên file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "HoaDon_DichVu_" + sdf.format(new Date()) + ".pdf";
            fos = new FileOutputStream(fileName);

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            // Sử dụng font Unicode để hỗ trợ tiếng Việt
            BaseFont baseFont = createBaseFont();

            // Tạo fonts - sử dụng iText Font
            Font fontNormal = new Font(baseFont, 12, Font.NORMAL);
            Font fontBold = new Font(baseFont, 12, Font.BOLD);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD);
            Font fontHeader = new Font(baseFont, 10, Font.BOLD);
            Font fontSmall = new Font(baseFont, 10, Font.NORMAL);

            // Tiêu đề
            Paragraph title = new Paragraph("HOÁ ĐƠN DỊCH VỤ SPA", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            doc.add(new Paragraph(" "));

            // Thông tin cửa hàng
            Paragraph storeInfo = new Paragraph("SWEET HOME", fontBold);
            storeInfo.setAlignment(Element.ALIGN_CENTER);
            doc.add(storeInfo);

            Paragraph storeAddress = new Paragraph("43 Đ. Lý Tự Trọng, P, Ninh Kiều, Cần Thơ 94100, Việt Nam", fontSmall);
            storeAddress.setAlignment(Element.ALIGN_CENTER);
            doc.add(storeAddress);

            Paragraph storePhone = new Paragraph("Điện thoại: 097 3791 643", fontSmall);
            storePhone.setAlignment(Element.ALIGN_CENTER);
            doc.add(storePhone);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            // Thông tin hóa đơn
            doc.add(new Paragraph("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            // Lấy thông tin nhân viên từ dịch vụ đầu tiên
            String nhanVienInfo = getNhanVienInfoFromTable();
            doc.add(new Paragraph("Nhân viên phụ trách: " + nhanVienInfo, fontNormal));
            doc.add(new Paragraph("Khách hàng: " + getTenKhachHangHienTai(), fontNormal));

            if (khachHangHienTai.getSoDienThoai() != null && !khachHangHienTai.getSoDienThoai().isEmpty()) {
                doc.add(new Paragraph("SĐT: " + khachHangHienTai.getSoDienThoai(), fontNormal));
            }

            doc.add(new Paragraph("---------------------------------------------", fontNormal));
            doc.add(new Paragraph(" "));

            // Bảng dịch vụ
            PdfPTable table = createServiceTable(fontNormal, fontHeader);
            doc.add(table);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            // Tổng tiền
            Paragraph total = new Paragraph("TỔNG TIỀN: " + currencyFormat.format(tongTien), fontBold);
            total.setAlignment(Element.ALIGN_RIGHT);
            doc.add(total);

            // Điểm tích lũy
            int diemThuong = tongTien.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();
            if (diemThuong > 0) {
                doc.add(new Paragraph("Điểm tích lũy: +" + diemThuong + " điểm", fontNormal));
            }

            doc.add(new Paragraph(" "));

            // QR Code thanh toán (nếu có tổng tiền)
            if (tongTien.compareTo(BigDecimal.ZERO) > 0) {
                addQRCodeToDocument(doc, fontBold, fontSmall);
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Cảm ơn quý khách!", fontBold));
            doc.add(new Paragraph("Hẹn gặp lại!", fontNormal));

            doc.close();

            // Mở file PDF
            JOptionPane.showMessageDialog(view, "Đã in hóa đơn ra file: " + fileName);
            try {
                Desktop.getDesktop().open(new File(fileName));
            } catch (Exception e) {
                System.err.println("Không thể mở file PDF: " + e.getMessage());
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn PDF: " + e.getMessage());
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
// Phương thức lấy thông tin nhân viên từ bảng dịch vụ

    private String getNhanVienInfoFromTable() {
        DefaultTableModel model = view.getTableModel();
        if (model.getRowCount() > 0) {
            // Lấy nhân viên từ dòng đầu tiên
            Object nhanVienObj = model.getValueAt(0, 5); // Cột nhân viên là cột thứ 5
            if (nhanVienObj != null) {
                String nhanVienStr = nhanVienObj.toString();

                // Nếu có nhiều nhân viên khác nhau, liệt kê tất cả
                if (model.getRowCount() > 1) {
                    Set<String> nhanVienSet = new HashSet<>();
                    nhanVienSet.add(nhanVienStr);

                    for (int i = 1; i < model.getRowCount(); i++) {
                        Object nvObj = model.getValueAt(i, 5);
                        if (nvObj != null) {
                            nhanVienSet.add(nvObj.toString());
                        }
                    }

                    if (nhanVienSet.size() == 1) {
                        return nhanVienStr;
                    } else {
                        return String.join(", ", nhanVienSet);
                    }
                }

                return nhanVienStr;
            }
        }
        return "Chưa xác định";
    }

    // Phương thức hỗ trợ tạo BaseFont
    private BaseFont createBaseFont() throws Exception {
        try {
            return BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e1) {
            try {
                return BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e2) {
                try {
                    return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
                } catch (Exception e3) {
                    return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                }
            }
        }
    }

    // Phương thức hỗ trợ tạo bảng dịch vụ
// Phương thức hỗ trợ tạo bảng dịch vụ - ĐÃ SỬA LỖI
    private PdfPTable createServiceTable(Font fontNormal, Font fontHeader) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        float[] columnWidths = {1f, 3f, 1.5f, 1.5f, 2f};
        table.setWidths(columnWidths);

        // Header bảng
        table.addCell(new Phrase("STT", fontHeader));
        table.addCell(new Phrase("Tên dịch vụ", fontHeader));
        table.addCell(new Phrase("Số lượng", fontHeader));
        table.addCell(new Phrase("Đơn giá", fontHeader));
        table.addCell(new Phrase("Thành tiền", fontHeader));

        // Thêm dữ liệu dịch vụ - SỬA LỖI ÉP KIỂU
        DefaultTableModel model = view.getTableModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int stt = i + 1;

            // Lấy dữ liệu từ bảng và chuyển đổi an toàn sang String
            Object tenDichVuObj = model.getValueAt(i, 1);
            Object soLuongObj = model.getValueAt(i, 4);
            Object donGiaObj = model.getValueAt(i, 3);
            Object thanhTienObj = model.getValueAt(i, 6);

            // Chuyển đổi an toàn sang String
            String tenDichVu = convertToString(tenDichVuObj);
            String soLuong = convertToString(soLuongObj);
            String donGiaStr = convertToString(donGiaObj);
            String thanhTienStr = convertToString(thanhTienObj);

            table.addCell(new Phrase(String.valueOf(stt), fontNormal));
            table.addCell(new Phrase(tenDichVu, fontNormal));
            table.addCell(new Phrase(soLuong, fontNormal));
            table.addCell(new Phrase(donGiaStr, fontNormal));
            table.addCell(new Phrase(thanhTienStr, fontNormal));
        }

        return table;
    }

// Phương thức hỗ trợ chuyển đổi an toàn sang String
    private String convertToString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    // Phương thức hỗ trợ thêm QR Code
    private void addQRCodeToDocument(Document doc, Font fontBold, Font fontSmall) throws Exception {
        try {
            String bankBin = "970431";
            String accountNumber = "0973791643";
            String accountName = "NGUYEN DIEM THAO NGUYEN";
            String addInfo = "Thanh toán dịch vụ SPA - " + getTenKhachHangHienTai();

            String qrUrl = "https://img.vietqr.io/image/"
                    + bankBin + "-" + accountNumber
                    + "-compact.png?amount=" + tongTien
                    + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                    + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

            BufferedImage qrBufferedImage = ImageIO.read(new URL(qrUrl));
            String qrPath = "VietQR_DichVu_" + System.currentTimeMillis() + ".png";
            ImageIO.write(qrBufferedImage, "PNG", new File(qrPath));

            com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrPath);
            qrImage.scaleToFit(120, 120);
            qrImage.setAlignment(Element.ALIGN_CENTER);

            doc.add(new Paragraph("Mã QR thanh toán:", fontBold));
            doc.add(qrImage);

            doc.add(new Paragraph("Ngân hàng: EximBank", fontSmall));
            doc.add(new Paragraph("Chủ tài khoản: " + accountName, fontSmall));
            doc.add(new Paragraph("Số tài khoản: " + accountNumber, fontSmall));

            // Xóa file QR tạm
            new File(qrPath).delete();

        } catch (Exception e) {
            System.err.println("Không thể tạo QR thanh toán: " + e.getMessage());
            doc.add(new Paragraph("Quý khách vui lòng thanh toán trực tiếp tại quầy.", fontSmall));
        }
    }

    // Phương thức hỗ trợ - Lấy tên khách hàng hiện tại
    private String getTenKhachHangHienTai() {
        if (khachHangHienTai != null) {
            return khachHangHienTai.getHoTen();
        }
        return "Không xác định";
    }

    private void handleLamMoi() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn làm mới form? Tất cả dữ liệu chưa lưu sẽ bị mất.",
                "Xác nhận làm mới",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            view.getTableModel().setRowCount(0);
            view.getCboKhachHang().setSelectedIndex(0);
            view.getCboDichVu().setSelectedIndex(0);
            view.getCboNhanVien().setSelectedIndex(0);
            view.getTxtSoLuongNguoi().setText("1");
            view.getTxtThoiGian().setText("");
            tongTien = BigDecimal.ZERO;
            capNhatTongTien();
            khachHangHienTai = null;
            view.getLblDiemTichLuy().setText("0 điểm");
        }
    }

    private boolean kiemTraDuLieuHopLe() {
        if (view.getCboKhachHang().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (view.getCboDichVu().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (view.getCboNhanVien().getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhân viên thực hiện",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int soLuong = Integer.parseInt(view.getTxtSoLuongNguoi().getText());
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(view, "Số lượng người phải lớn hơn 0",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Số lượng người không hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void capNhatTongTien() {
        view.getLblTongTien().setText(currencyFormat.format(tongTien));
    }

    private void capNhatSTT() {
        DefaultTableModel model = view.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }
}
