package Controller;

import Model.ChiTietHoaDon;
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
import Model.HoaDon;
import Service.HoaDonService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

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
        view.getBtnHoanThanh().addActionListener(this);
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
        } else if (source == view.getBtnHoanThanh()) {
            handleHoanThanh();
        }
    }

    private void handleThemMoi() {
        try {
            DatLich datLich = validateAndGetFormData();
            if (datLich == null) {
                return;
            }

            boolean success;
            if (isEditMode && currentEditId != -1) {
                // Chế độ sửa
                success = datLichService.updateDatLich(datLich);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Cập nhật lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    isEditMode = false;
                    currentEditId = -1;
                }
            } else {
                // Chế độ thêm mới
                success = datLichService.addDatLich(datLich);
                if (success) {
                    // Cập nhật trạng thái giường thành "Đã đặt" ngay khi thêm lịch
                    if (datLich.getMaGiuong() != null) {
                        giuongService.updateTrangThaiGiuong(datLich.getMaGiuong(), "Đã đặt");
                    }
                    JOptionPane.showMessageDialog(view, "Thêm lịch hẹn thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            if (success) {
                clearForm();
                view.updateTimeline();
            } else {
                JOptionPane.showMessageDialog(view,
                        isEditMode ? "Cập nhật lịch hẹn thất bại" : "Thêm lịch hẹn thất bại",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                    "Bạn có chắc chắn muốn xóa lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " "
                    + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
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

    private void handleHoanThanh() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để hoàn thành", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra nếu lịch hẹn chưa được xác nhận
            if (!selectedAppointment.isDaXacNhan() && !selectedAppointment.isDangThucHien()) {
                JOptionPane.showMessageDialog(view, "Chỉ có thể hoàn thành lịch hẹn đã được xác nhận hoặc đang thực hiện", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tính tổng tiền hóa đơn (KHÔNG tính phí giường)
            BigDecimal tongTien = tinhTongTienHoaDon(selectedAppointment);

            // Tính điểm tích lũy (100.000 VND = 1 điểm)
            int diemThuong = tongTien.divideToIntegralValue(BigDecimal.valueOf(100000)).intValue();

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Hoàn thành lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm"))
                    + "\nTổng tiền: " + String.format("%,.0f", tongTien) + " VND"
                    + (diemThuong > 0 ? "\nĐiểm tích lũy: +" + diemThuong + " điểm" : "")
                    + "\n\nSau khi hoàn thành sẽ:\n- Lưu hóa đơn\n- In PDF hóa đơn\n- Xóa form",
                    "Xác nhận hoàn thành", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật trạng thái lịch hẹn thành "Hoàn thành"
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Hoàn thành");

                if (success) {
                    // LƯU HÓA ĐƠN VÀO DATABASE
                    boolean luuHoaDonThanhCong = luuHoaDon(selectedAppointment);

                    if (luuHoaDonThanhCong) {
                        // CẬP NHẬT ĐIỂM TÍCH LŨY CHO KHÁCH HÀNG
                        if (diemThuong > 0) {
                            capNhatDiemTichLuy(selectedAppointment.getMaKhachHang(), diemThuong);
                        }

                        // In hóa đơn PDF
                        inHoaDonPDF(selectedAppointment);

                        // Cập nhật trạng thái giường
                        if (selectedAppointment.getMaGiuong() != null) {
                            giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Trống");
                            view.refreshGiuongComboBox();
                        }

                        clearForm();

                        JOptionPane.showMessageDialog(view,
                                "Hoàn thành lịch hẹn thành công!\n"
                                + "- Đã lưu hóa đơn vào database\n"
                                + (diemThuong > 0 ? "- Đã thưởng " + diemThuong + " điểm tích lũy cho khách hàng\n" : "")
                                + "- Đã in PDF\n"
                                + "- Đã xóa form",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        view.updateTimeline();
                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Hoàn thành lịch hẹn nhưng LỖI khi lưu hóa đơn!",
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Cập nhật trạng thái lịch hẹn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi hoàn thành lịch hẹn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

// Thêm phương thức cập nhật điểm tích lũy
    private void capNhatDiemTichLuy(Integer maKhachHang, int diemThuong) {
        try {
            KhachHang khachHang = khachHangService.getKhachHangById(maKhachHang);
            if (khachHang != null) {
                int diemHienTai = khachHang.getDiemTichLuy();
                int diemMoi = diemHienTai + diemThuong;
                khachHang.setDiemTichLuy(diemMoi);
                khachHangService.updateKhachHang(khachHang);

                System.out.println("Đã cập nhật điểm tích lũy cho khách hàng " + khachHang.getHoTen()
                        + ": " + diemHienTai + " + " + diemThuong + " = " + diemMoi + " điểm");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật điểm tích lũy: " + e.getMessage());
        }
    }

// Sửa đổi phương thức luuHoaDon - BỎ phần phí giường
    private boolean luuHoaDon(DatLich datLich) {
        try {
            // Lấy thông tin cần thiết
            KhachHang khachHang = khachHangService.getKhachHangById(datLich.getMaKhachHang());

            // Tính tổng tiền (KHÔNG tính phí giường)
            BigDecimal tongTien = tinhTongTienHoaDon(datLich);

            // Tạo đối tượng HoaDon
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaKhachHang(datLich.getMaKhachHang());
            hoaDon.setNgayLap(java.time.LocalDateTime.now());
            hoaDon.setTongTien(tongTien);
            hoaDon.setMaNhanVienLap(1); // TODO: Lấy từ session đăng nhập thực tế
            hoaDon.setGhiChu("Hóa đơn từ lịch hẹn #" + datLich.getMaLich());

            // Tạo danh sách chi tiết hóa đơn
            List<ChiTietHoaDon> chiTietList = new ArrayList<>();

            // Thêm dịch vụ vào chi tiết hóa đơn
            if (datLich.hasDichVu()) {
                for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                    if (chiTiet.getDichVu() != null) {
                        ChiTietHoaDon cthd = new ChiTietHoaDon();
                        cthd.setMaDichVu(chiTiet.getMaDichVu());
                        cthd.setSoLuong(1);
                        cthd.setDonGia(chiTiet.getDichVu().getGia());
                        cthd.recalculateThanhTien(); // Tính thành tiền
                        chiTietList.add(cthd);
                    }
                }
            }

            // ĐÃ BỎ phần thêm phí giường
            // Gán danh sách chi tiết vào hóa đơn
            hoaDon.setChiTietHoaDon(chiTietList);

            // Lưu hóa đơn vào database
            HoaDonService hoaDonService = new HoaDonService();
            boolean success = hoaDonService.addHoaDon(hoaDon);

            if (success) {
                System.out.println("Đã lưu hóa đơn thành công: " + hoaDon.getMaHoaDon());
                return true;
            } else {
                System.err.println("Lỗi khi lưu hóa đơn");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Phương thức tính tổng tiền hóa đơn - ĐÃ SỬA để trả về BigDecimal
    private BigDecimal tinhTongTienHoaDon(DatLich datLich) {
        BigDecimal tongTien = BigDecimal.ZERO;

        // Tính tiền dịch vụ
        if (datLich.hasDichVu()) {
            for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                if (chiTiet.getDichVu() != null && chiTiet.getDichVu().getGia() != null) {
                    tongTien = tongTien.add(chiTiet.getDichVu().getGia());
                }
            }
        }

        return tongTien;
    }

    // Phương thức in hóa đơn PDF
    private void inHoaDonPDF(DatLich datLich) {
        try {
            // Lấy thông tin cần thiết
            KhachHang khachHang = khachHangService.getKhachHangById(datLich.getMaKhachHang());
            Giuong giuong = datLich.getMaGiuong() != null ? giuongService.getGiuongById(datLich.getMaGiuong()) : null;

            BigDecimal tongTienBigDecimal = tinhTongTienHoaDon(datLich);
            double tongTien = tongTienBigDecimal.doubleValue();

            // Xác nhận in hóa đơn
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn có muốn in hóa đơn PDF?\n"
                    + "Khách hàng: " + khachHang.getHoTen() + "\n"
                    + "Tổng tiền: " + String.format("%,.0f", tongTien) + " VND\n"
                    + "Số dịch vụ: " + (datLich.hasDichVu() ? datLich.getDanhSachDichVu().size() : 0),
                    "Xác nhận in hóa đơn PDF",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Tạo hóa đơn tạm để in
            inHoaDonPDFDetail(datLich, khachHang, giuong, tongTien, "Nhân viên lễ tân");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn!");
        }
    }

// Sửa đổi phương thức inHoaDonPDFDetail - Thêm hiển thị điểm tích lũy
    public void inHoaDonPDFDetail(DatLich datLich, KhachHang khachHang, Giuong giuong, double tongTien, String tenNV) {
        FileOutputStream fos = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "HoaDon_DatLich_" + datLich.getMaLich() + "_" + sdf.format(new Date()) + ".pdf";
            fos = new FileOutputStream(fileName);

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            // Sử dụng font Unicode để hỗ trợ tiếng Việt
            BaseFont baseFont;
            try {
                baseFont = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e1) {
                try {
                    baseFont = BaseFont.createFont("c:/windows/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception e2) {
                    try {
                        baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
                    } catch (Exception e3) {
                        baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    }
                }
            }

            Font fontNormal = new Font(baseFont, 12);
            Font fontBold = new Font(baseFont, 12, Font.BOLD);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD);
            Font fontHeader = new Font(baseFont, 10, Font.BOLD);
            Font fontSmall = new Font(baseFont, 10, Font.NORMAL);

            // Tiêu đề
            Paragraph title = new Paragraph("HOÁ ĐƠN DỊCH VỤ SPA", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
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
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            doc.add(new Paragraph("Nhân viên lập: " + tenNV, fontNormal));
            doc.add(new Paragraph("Khách hàng: " + khachHang.getHoTen(), fontNormal));

            // Thêm thông tin liên hệ khách hàng
            if (khachHang.getSoDienThoai() != null && !khachHang.getSoDienThoai().isEmpty()) {
                doc.add(new Paragraph("SĐT: " + khachHang.getSoDienThoai(), fontNormal));
            }

            // Hiển thị điểm tích lũy hiện tại
            doc.add(new Paragraph("Điểm tích lũy hiện tại: " + khachHang.getDiemTichLuy() + " điểm", fontNormal));

            doc.add(new Paragraph("---------------------------------------------", fontNormal));

            // Bảng dịch vụ
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            float[] columnWidths = {0.8f, 3f, 2f, 1.5f, 2f, 2f};
            table.setWidths(columnWidths);

            // Header bảng
            table.addCell(new Phrase("STT", fontHeader));
            table.addCell(new Phrase("Tên dịch vụ", fontHeader));
            table.addCell(new Phrase("Thời gian", fontHeader));
            table.addCell(new Phrase("Số lượng", fontHeader));
            table.addCell(new Phrase("Đơn giá", fontHeader));
            table.addCell(new Phrase("Thành tiền", fontHeader));

            // Thêm dữ liệu với số thứ tự
            int stt = 1;
            double tongCong = 0.0;

            if (datLich.hasDichVu()) {
                for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                    DichVu dichVu = chiTiet.getDichVu();
                    if (dichVu != null) {
                        String tenDV = dichVu.getTenDichVu();
                        BigDecimal donGia = dichVu.getGia();
                        double thanhTien = donGia != null ? donGia.doubleValue() : 0.0;
                        tongCong += thanhTien;

                        // Thêm các cell với STT
                        table.addCell(new Phrase(String.valueOf(stt++), fontNormal));
                        table.addCell(new Phrase(tenDV, fontNormal));
                        table.addCell(new Phrase("60 phút", fontNormal)); // Mặc định
                        table.addCell(new Phrase("1", fontNormal)); // Mặc định số lượng 1
                        table.addCell(new Phrase(String.format("%,.0f", donGia) + " VND", fontNormal));
                        table.addCell(new Phrase(String.format("%,.0f", thanhTien) + " VND", fontNormal));
                    }
                }
            }

            // ĐÃ BỎ phần thêm thông tin giường
            doc.add(table);
            doc.add(new Paragraph("---------------------------------------------", fontNormal));
            doc.add(new Paragraph(String.format("Tổng cộng: %s VND", String.format("%,.0f", tongCong)), fontBold));

            // Hiển thị điểm tích lũy được thưởng
            int diemThuong = (int) (tongCong / 100000);
            if (diemThuong > 0) {
                doc.add(new Paragraph("Điểm tích lũy được thưởng: +" + diemThuong + " điểm", fontBold));
            }

            // Thêm QR Code thanh toán
            if (tongCong > 0) {
                try {
                    String bankBin = "970431";
                    String accountNumber = "0973791643";
                    String accountName = "NGUYEN DIEM THAO NGUYEN";
                    String addInfo = "Thanh toán đặt lịch #" + datLich.getMaLich();

                    String qrUrl = "https://img.vietqr.io/image/"
                            + bankBin + "-" + accountNumber
                            + "-compact.png?amount=" + tongCong
                            + "&addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
                            + "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8);

                    BufferedImage qrBufferedImage = ImageIO.read(new URL(qrUrl));
                    String qrPath = "VietQR_DatLich_" + System.currentTimeMillis() + ".png";
                    ImageIO.write(qrBufferedImage, "PNG", new File(qrPath));

                    com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrPath);
                    qrImage.scaleToFit(120, 120);
                    qrImage.setAlignment(Element.ALIGN_CENTER);

                    doc.add(new Paragraph("\nMã QR thanh toán:", fontBold));
                    doc.add(qrImage);

                    doc.add(new Paragraph("Ngân hàng: MBBank", fontNormal));
                    doc.add(new Paragraph("Chủ tài khoản: " + accountName, fontNormal));
                    doc.add(new Paragraph("Số tài khoản: " + accountNumber, fontNormal));

                    // Xóa file QR tạm
                    new File(qrPath).delete();
                } catch (Exception e) {
                    System.err.println("Không thể tạo QR thanh toán: " + e.getMessage());
                    doc.add(new Paragraph("\nQuý khách vui lòng thanh toán trực tiếp tại quầy.", fontNormal));
                }
            }

            // Thêm ghi chú nếu có
            if (datLich.getGhiChu() != null && !datLich.getGhiChu().trim().isEmpty()) {
                doc.add(new Paragraph("\nGhi chú: " + datLich.getGhiChu(), fontNormal));
            }

            doc.add(new Paragraph("\nCảm ơn quý khách!", fontBold));
            doc.add(new Paragraph("Hẹn gặp lại!", fontNormal));

            doc.close();

            // Mở file PDF
            JOptionPane.showMessageDialog(view, "Đã in hóa đơn ra file: " + fileName);
            try {
                Desktop.getDesktop().open(new File(fileName));
            } catch (Exception e) {
                System.err.println("Không thể mở file PDF: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi in hóa đơn PDF: " + e.getMessage());
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

    // Phương thức gửi thông tin đến hóa đơn
    private void sendToHoaDon(DatLich datLich) {
        try {
            // Lấy thông tin giường
            Giuong giuong = giuongService.getGiuongById(datLich.getMaGiuong());
            KhachHang khachHang = khachHangService.getKhachHangById(datLich.getMaKhachHang());

            // Tạo map chứa thông tin cho hóa đơn
            Map<String, Object> hoaDonInfo = new HashMap<>();
            hoaDonInfo.put("maGiuong", datLich.getMaGiuong());
            hoaDonInfo.put("soHieuGiuong", giuong != null ? giuong.getSoHieu() : "Không xác định");
            hoaDonInfo.put("maKhachHang", datLich.getMaKhachHang());
            hoaDonInfo.put("tenKhachHang", khachHang != null ? khachHang.getHoTen() : "Không xác định");
            hoaDonInfo.put("soLuongNguoi", datLich.getSoLuongNguoi());

            // Thêm thông tin dịch vụ
            List<Map<String, Object>> dichVuList = new ArrayList<>();
            if (datLich.hasDichVu()) {
                for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                    Map<String, Object> dichVuInfo = new HashMap<>();
                    dichVuInfo.put("maDichVu", chiTiet.getMaDichVu());
                    dichVuInfo.put("tenDichVu", chiTiet.getDichVu().getTenDichVu());
                    dichVuInfo.put("gia", chiTiet.getDichVu().getGia());
                    dichVuList.add(dichVuInfo);
                }
            }
            hoaDonInfo.put("dichVu", dichVuList);

            // Ở đây bạn có thể gọi service hóa đơn để lưu thông tin
            // hoaDonService.createHoaDon(hoaDonInfo);
            System.out.println("Thông tin gửi đến hóa đơn: " + hoaDonInfo);

        } catch (Exception e) {
            System.err.println("Lỗi khi gửi thông tin đến hóa đơn: " + e.getMessage());
        }
    }

    // Cập nhật phương thức handleXacNhan() để kiểm tra giường "Đang sử dụng"
    private void handleXacNhan() {
        DatLich selectedAppointment = view.getSelectedAppointment();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lịch hẹn để xác nhận", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra nếu có giường được chọn và giường đang "Đang sử dụng"
            if (selectedAppointment.getMaGiuong() != null) {
                Giuong giuong = giuongService.getGiuongById(selectedAppointment.getMaGiuong());
                if (giuong != null && giuong.isDangSuDung()) {
                    JOptionPane.showMessageDialog(view,
                            "Không thể xác nhận lịch hẹn. Giường " + giuong.getSoHieu() + " đang được sử dụng.\nVui lòng chọn giường khác hoặc đợi giường trống.",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Xác nhận lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                    "Xác nhận lịch hẹn", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật trạng thái lịch hẹn
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã xác nhận");

                if (success && selectedAppointment.getMaGiuong() != null) {
                    // Cập nhật trạng thái giường thành "Đang sử dụng" khi xác nhận
                    giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Đang sử dụng");

                    // Refresh combobox giường để ẩn giường đang sử dụng
                    view.refreshGiuongComboBox();
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
                    "Hủy lịch hẹn này?\nKhách hàng: "
                    + khachHangService.getKhachHangById(selectedAppointment.getMaKhachHang()).getHoTen()
                    + "\nThời gian: " + selectedAppointment.getGioDat().format(DateTimeFormatter.ofPattern("HH:mm")),
                    "Xác nhận hủy lịch", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = datLichService.updateTrangThai(selectedAppointment.getMaLich(), "Đã hủy");

                if (success && selectedAppointment.getMaGiuong() != null) {
                    // Cập nhật trạng thái giường thành "Trống" khi hủy lịch
                    giuongService.updateTrangThaiGiuong(selectedAppointment.getMaGiuong(), "Trống");

                    // Refresh combobox giường để hiển thị lại giường
                    view.refreshGiuongComboBox();
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

        // Lấy số lượng người từ spinner
        Integer soLuongNguoi = (Integer) view.getSpinnerSoLuongNguoi().getValue();
        if (soLuongNguoi == null || soLuongNguoi < 1) {
            soLuongNguoi = 1; // Mặc định 1 người
        }

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
        datLich.setSoLuongNguoi(soLuongNguoi);

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

        // Điền số lượng người
        if (datLich.getSoLuongNguoi() != null) {
            view.getSpinnerSoLuongNguoi().setValue(datLich.getSoLuongNguoi());
        } else {
            view.getSpinnerSoLuongNguoi().setValue(1);
        }

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
        view.getSpinnerSoLuongNguoi().setValue(1); // Reset về 1 người

        // Reset edit mode
        isEditMode = false;
        currentEditId = -1;

        // Giữ nguyên ngày đặt (ngày đang chọn trên lịch)
        view.getTxtNgayDat().setText(view.getSelectedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Reset selected appointment
        view.setSelectedAppointment(null);
    }
}
