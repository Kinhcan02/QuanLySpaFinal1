package Controller;

import Service.ThongKeService;
import View.ThongKeView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ThongKeController {
    private final ThongKeView view;
    private final ThongKeService service;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Biến lưu trữ dữ liệu hiện tại
    private Map<String, Object> currentTongQuan;
    private List<Map<String, Object>> currentKhachHang;
    private List<Map<String, Object>> currentDoanhThu;
    private List<Map<String, Object>> currentDichVu;
    private Date currentFromDate;
    private Date currentToDate;
    private int currentYear;

    public ThongKeController(ThongKeView view) {
        this.view = view;
        this.service = new ThongKeService();
        initController();
    }

    private void initController() {
        view.getBtnThongKe().addActionListener(e -> thongKe());
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());
        
        // Load danh sách năm
        loadDanhSachNam();
        
        // Thực hiện thống kê tự động khi mở form
        thongKe();
    }
    
    private void loadDanhSachNam() {
        try {
            List<Integer> years = service.getDanhSachNam();
            view.getCboNam().removeAllItems();
            
            if (years.isEmpty()) {
                // Nếu không có năm nào, thêm năm hiện tại
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                view.getCboNam().addItem(currentYear);
            } else {
                for (Integer year : years) {
                    view.getCboNam().addItem(year);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void thongKe() {
        try {
            String fromDateStr = view.getTxtDateFrom().getText().trim();
            String toDateStr = view.getTxtDateTo().getText().trim();

            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!");
                return;
            }

            Date fromDate = dateFormat.parse(fromDateStr);
            Date toDate = dateFormat.parse(toDateStr);
            currentYear = (Integer) view.getCboNam().getSelectedItem();
            
            // Lưu trữ ngày hiện tại
            currentFromDate = fromDate;
            currentToDate = toDate;

            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(view, "Ngày bắt đầu phải trước ngày kết thúc!");
                return;
            }

            // Thực hiện các thống kê
            thucHienThongKe(fromDate, toDate, currentYear);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Lỗi định dạng ngày! Vui lòng nhập đúng định dạng dd/MM/yyyy\n" + ex.getMessage());
        }
    }

    private void thucHienThongKe(Date fromDate, Date toDate, int year) {
        try {
            // Thống kê tổng quan
            currentTongQuan = service.thongKeTongQuan(fromDate, toDate);
            hienThiTongQuan(currentTongQuan, fromDate, toDate);

            // Thống kê khách hàng
            currentKhachHang = service.thongKeKhachHangNhieuDichVu(fromDate, toDate, 10);
            hienThiKhachHang(currentKhachHang);

            // Thống kê doanh thu
            currentDoanhThu = service.thongKeDoanhThuTheoThang(year);
            hienThiDoanhThu(currentDoanhThu);

            // Thống kê dịch vụ
            currentDichVu = service.thongKeDichVuBanChay(fromDate, toDate, 10);
            hienThiDichVu(currentDichVu);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi thực hiện thống kê: " + ex.getMessage());
        }
    }

    private void hienThiTongQuan(Map<String, Object> tongQuan, Date fromDate, Date toDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("THỐNG KÊ TỔNG QUAN\n");
        sb.append("Khoảng thời gian: ").append(dateFormat.format(fromDate))
          .append(" - ").append(dateFormat.format(toDate)).append("\n\n");

        int tongHoaDon = ((Number) tongQuan.getOrDefault("TongHoaDon", 0)).intValue();
        double tongDoanhThu = ((Number) tongQuan.getOrDefault("TongDoanhThu", 0.0)).doubleValue();
        int tongKhachHang = ((Number) tongQuan.getOrDefault("TongKhachHang", 0)).intValue();
        double donGiaTrungBinh = ((Number) tongQuan.getOrDefault("DonGiaTrungBinh", 0.0)).doubleValue();

        sb.append("┌────────────────────────────────────────┐\n");
        sb.append(String.format("│ %-30s %10s │\n", "Tổng số hóa đơn:", String.format("%,d", tongHoaDon)));
        sb.append(String.format("│ %-30s %10s │\n", "Tổng doanh thu:", String.format("%,.0f VND", tongDoanhThu)));
        sb.append(String.format("│ %-30s %10s │\n", "Tổng khách hàng:", String.format("%,d", tongKhachHang)));
        sb.append(String.format("│ %-30s %10s │\n", "Đơn giá trung bình:", String.format("%,.0f VND", donGiaTrungBinh)));
        sb.append("└────────────────────────────────────────┘\n");

        view.getTxtTongQuan().setText(sb.toString());
    }

    private void hienThiKhachHang(List<Map<String, Object>> khachHangList) {
        DefaultTableModel model = (DefaultTableModel) view.getTblKhachHang().getModel();
        model.setRowCount(0);

        int stt = 1;
        for (Map<String, Object> kh : khachHangList) {
            model.addRow(new Object[]{
                stt++,
                kh.get("MaKhachHang"),
                kh.get("HoTen"),
                kh.get("SoDienThoai"),
                kh.get("LoaiKhach"),
                String.format("%,d", kh.get("SoDichVuDaDung")),
                String.format("%,.0f VND", kh.get("TongChiTieu"))
            });
        }
    }

    private void hienThiDoanhThu(List<Map<String, Object>> doanhThuList) {
        DefaultTableModel model = (DefaultTableModel) view.getTblDoanhThu().getModel();
        model.setRowCount(0);

        for (Map<String, Object> dt : doanhThuList) {
            double doanhThu = ((Number) dt.get("DoanhThu")).doubleValue();
            int soHoaDon = ((Number) dt.get("SoHoaDon")).intValue();
            double trungBinh = soHoaDon > 0 ? doanhThu / soHoaDon : 0;

            model.addRow(new Object[]{
                "Tháng " + dt.get("Thang"),
                String.format("%,.0f VND", doanhThu),
                String.format("%,d", soHoaDon),
                String.format("%,.0f VND", trungBinh)
            });
        }
    }

    private void hienThiDichVu(List<Map<String, Object>> dichVuList) {
        DefaultTableModel model = (DefaultTableModel) view.getTblDichVu().getModel();
        model.setRowCount(0);

        int stt = 1;
        for (Map<String, Object> dv : dichVuList) {
            model.addRow(new Object[]{
                stt++,
                dv.get("MaDichVu"),
                dv.get("TenDichVu"),
                dv.get("TenLoaiDV"),
                String.format("%,.0f VND", dv.get("Gia")),
                String.format("%,d", dv.get("SoLuongBan")),
                String.format("%,.0f VND", dv.get("DoanhThu"))
            });
        }
    }

    private void xuatExcel() {
        // Kiểm tra xem có dữ liệu để xuất không
        if (currentTongQuan == null || currentKhachHang == null || currentDoanhThu == null || currentDichVu == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng thực hiện thống kê trước khi xuất Excel!");
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo Excel");
            fileChooser.setSelectedFile(new File("BaoCaoThongKe_" + 
                new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".xlsx"));

            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Excel Files (*.xlsx)", "xlsx"));

            if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                    file = new File(file.getAbsolutePath() + ".xlsx");
                }

                Workbook workbook = new XSSFWorkbook();
                createExcelSheets(workbook);
                
                FileOutputStream outputStream = new FileOutputStream(file);
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

                // Mở file Excel
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }

                JOptionPane.showMessageDialog(view,
                        "Đã xuất báo cáo Excel thành công!\n" +
                        "File: " + file.getName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi xuất báo cáo Excel: " + ex.getMessage());
        }
    }

    private void createExcelSheets(Workbook workbook) {
        // Tạo các style
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Sheet tổng quan
        createTongQuanSheet(workbook, headerStyle, titleStyle, currencyStyle);
        
        // Sheet khách hàng
        createKhachHangSheet(workbook, headerStyle, titleStyle, currencyStyle);
        
        // Sheet doanh thu
        createDoanhThuSheet(workbook, headerStyle, titleStyle, currencyStyle);
        
        // Sheet dịch vụ
        createDichVuSheet(workbook, headerStyle, titleStyle, currencyStyle);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private void createTongQuanSheet(Workbook workbook, CellStyle headerStyle, CellStyle titleStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Tổng quan");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO THỐNG KÊ TỔNG QUAN");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // Thời gian
        Row timeRow = sheet.createRow(1);
        timeRow.createCell(0).setCellValue("Thời gian: " + 
            dateFormat.format(currentFromDate) + " - " + dateFormat.format(currentToDate));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
        
        // Header
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Chỉ tiêu", "Giá trị", "Đơn vị"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dữ liệu
        int rowNum = 4;
        String[][] data = {
            {"Tổng số hóa đơn", String.valueOf(currentTongQuan.get("TongHoaDon")), "hóa đơn"},
            {"Tổng doanh thu", String.format("%,.0f", currentTongQuan.get("TongDoanhThu")), "VND"},
            {"Tổng khách hàng", String.valueOf(currentTongQuan.get("TongKhachHang")), "khách hàng"},
            {"Đơn giá trung bình", String.format("%,.0f", currentTongQuan.get("DonGiaTrungBinh")), "VND"}
        };
        
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowData[0]);
            row.createCell(1).setCellValue(rowData[1]);
            row.createCell(2).setCellValue(rowData[2]);
        }
        
        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createKhachHangSheet(Workbook workbook, CellStyle headerStyle, CellStyle titleStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Khách hàng");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("TOP KHÁCH HÀNG SỬ DỤNG NHIỀU DỊCH VỤ NHẤT");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        
        // Header
        Row headerRow = sheet.createRow(2);
        String[] headers = {"STT", "Mã KH", "Họ tên", "Số điện thoại", "Loại KH", "Số DV đã dùng", "Tổng chi tiêu (VND)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dữ liệu
        int rowNum = 3;
        int stt = 1;
        for (Map<String, Object> kh : currentKhachHang) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(((Number) kh.get("MaKhachHang")).intValue());
            row.createCell(2).setCellValue((String) kh.get("HoTen"));
            row.createCell(3).setCellValue((String) kh.get("SoDienThoai"));
            row.createCell(4).setCellValue((String) kh.get("LoaiKhach"));
            row.createCell(5).setCellValue(((Number) kh.get("SoDichVuDaDung")).intValue());
            
            Cell doanhThuCell = row.createCell(6);
            doanhThuCell.setCellValue(((Number) kh.get("TongChiTieu")).doubleValue());
            doanhThuCell.setCellStyle(currencyStyle);
        }
        
        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createDoanhThuSheet(Workbook workbook, CellStyle headerStyle, CellStyle titleStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Doanh thu");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DOANH THU THEO THÁNG NĂM " + currentYear);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // Header
        Row headerRow = sheet.createRow(2);
        String[] headers = {"Tháng", "Doanh thu (VND)", "Số hóa đơn", "Doanh thu trung bình (VND)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dữ liệu
        int rowNum = 3;
        double tongDoanhThu = 0;
        int tongHoaDon = 0;
        
        for (Map<String, Object> dt : currentDoanhThu) {
            Row row = sheet.createRow(rowNum++);
            double doanhThu = ((Number) dt.get("DoanhThu")).doubleValue();
            int soHoaDon = ((Number) dt.get("SoHoaDon")).intValue();
            double trungBinh = soHoaDon > 0 ? doanhThu / soHoaDon : 0;
            
            tongDoanhThu += doanhThu;
            tongHoaDon += soHoaDon;
            
            row.createCell(0).setCellValue("Tháng " + dt.get("Thang"));
            
            Cell doanhThuCell = row.createCell(1);
            doanhThuCell.setCellValue(doanhThu);
            doanhThuCell.setCellStyle(currencyStyle);
            
            row.createCell(2).setCellValue(soHoaDon);
            
            Cell trungBinhCell = row.createCell(3);
            trungBinhCell.setCellValue(trungBinh);
            trungBinhCell.setCellStyle(currencyStyle);
        }
        
        // Dòng tổng
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("TỔNG CỘNG");
        
        Cell totalDoanhThuCell = totalRow.createCell(1);
        totalDoanhThuCell.setCellValue(tongDoanhThu);
        totalDoanhThuCell.setCellStyle(currencyStyle);
        
        totalRow.createCell(2).setCellValue(tongHoaDon);
        
        Cell totalTrungBinhCell = totalRow.createCell(3);
        totalTrungBinhCell.setCellValue(tongHoaDon > 0 ? tongDoanhThu / tongHoaDon : 0);
        totalTrungBinhCell.setCellStyle(currencyStyle);
        
        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createDichVuSheet(Workbook workbook, CellStyle headerStyle, CellStyle titleStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Dịch vụ");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("TOP DỊCH VỤ BÁN CHẠY");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        
        // Header
        Row headerRow = sheet.createRow(2);
        String[] headers = {"STT", "Mã DV", "Tên dịch vụ", "Loại DV", "Đơn giá (VND)", "Số lượng bán", "Doanh thu (VND)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dữ liệu
        int rowNum = 3;
        int stt = 1;
        double tongDoanhThu = 0;
        
        for (Map<String, Object> dv : currentDichVu) {
            Row row = sheet.createRow(rowNum++);
            double doanhThu = ((Number) dv.get("DoanhThu")).doubleValue();
            tongDoanhThu += doanhThu;
            
            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(((Number) dv.get("MaDichVu")).intValue());
            row.createCell(2).setCellValue((String) dv.get("TenDichVu"));
            row.createCell(3).setCellValue((String) dv.get("TenLoaiDV"));
            
            Cell giaCell = row.createCell(4);
            giaCell.setCellValue(((Number) dv.get("Gia")).doubleValue());
            giaCell.setCellStyle(currencyStyle);
            
            row.createCell(5).setCellValue(((Number) dv.get("SoLuongBan")).intValue());
            
            Cell doanhThuCell = row.createCell(6);
            doanhThuCell.setCellValue(doanhThu);
            doanhThuCell.setCellStyle(currencyStyle);
        }
        
        // Dòng tổng
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(5).setCellValue("TỔNG CỘNG");
        Cell totalDoanhThuCell = totalRow.createCell(6);
        totalDoanhThuCell.setCellValue(tongDoanhThu);
        totalDoanhThuCell.setCellStyle(currencyStyle);
        
        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}