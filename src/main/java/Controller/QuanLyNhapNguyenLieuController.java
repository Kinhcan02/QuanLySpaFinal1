package Controller;

import Model.LoaiNguyenLieu;
import Model.NguyenLieu;
import Model.NhapNguyenLieu;
import Service.LoaiNguyenLieuService;
import Service.NguyenLieuService;
import Service.NhapNguyenLieuService;
import View.QuanLyNhapNguyenLieuView;
import View.QuanLyLoaiNguyenLieuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyNhapNguyenLieuController {

    private QuanLyNhapNguyenLieuView view;
    private NhapNguyenLieuService nhapNguyenLieuService;
    private NguyenLieuService nguyenLieuService;
    private LoaiNguyenLieuService loaiNguyenLieuService;
    private DefaultTableModel model;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    // Biến để lưu kết quả từ dialog
    private boolean dialogResult;

    public QuanLyNhapNguyenLieuController(QuanLyNhapNguyenLieuView view) {
        this.view = view;
        this.nhapNguyenLieuService = new NhapNguyenLieuService();
        this.nguyenLieuService = new NguyenLieuService();
        this.loaiNguyenLieuService = new LoaiNguyenLieuService();
        this.model = view.getModel();
        
        // Cập nhật combobox năm khi khởi tạo
        view.updateYearComboBox();
        
        initController();
        loadData();
        loadLoaiNguyenLieu();
    }

    private void initController() {
        view.getBtnThem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                themNhapNguyenLieu();
            }
        });

        view.getBtnSua().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suaNhapNguyenLieu();
            }
        });
        
        view.getBtnLietKe().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lietKeTheoThangNam();
            }
        });
        
        view.getBtnXoa().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xoaNhapNguyenLieu();
            }
        });

        view.getBtnLamMoi().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lamMoi();
            }
        });

        view.getBtnTimKiem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timKiem();
            }
        });

        view.getBtnLoaiNguyenLieu().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moQuanLyLoaiNguyenLieu();
            }
        });

        view.getCboLoaiFilter().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                locTheoLoai();
            }
        });
    }

    private void lietKeTheoThangNam() {
        try {
            String thangStr = (String) view.getCboThang().getSelectedItem();
            String namStr = (String) view.getCboNam().getSelectedItem();
            
            if (thangStr == null || namStr == null) {
                hienThiThongBao("Vui lòng chọn tháng và năm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int thang = Integer.parseInt(thangStr);
            int nam = Integer.parseInt(namStr);
            
            // Kiểm tra tháng hợp lệ
            if (thang < 1 || thang > 12) {
                hienThiThongBao("Tháng phải từ 1 đến 12!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Gọi service để lấy dữ liệu theo tháng năm
            List<NhapNguyenLieu> list = nhapNguyenLieuService.getNhapNguyenLieuByThangNam(thang, nam);
            model.setRowCount(0);
            
            if (list.isEmpty()) {
                hienThiThongBao("Không có phiếu nhập nào trong tháng " + thang + "/" + nam, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            BigDecimal tongTien = BigDecimal.ZERO;
            int tongSoLuong = 0;
            
            for (NhapNguyenLieu nhap : list) {
                BigDecimal thanhTien = nhap.getThanhTien();
                tongTien = tongTien.add(thanhTien);
                tongSoLuong += nhap.getSoLuong();
                
                // ĐÁNH DẤU PHIẾU NHẬP KHÔNG CÓ NGUYÊN LIỆU (CHỈ ĐỂ HIỂN THỊ)
                String tenNguyenLieuHienThi = nhap.getTenNguyenLieu();
                if (nhap.getMaNguyenLieu() == null || nhap.getMaNguyenLieu() == 0) {
                    tenNguyenLieuHienThi = "[ĐÃ XÓA] " + tenNguyenLieuHienThi;
                }
                
                model.addRow(new Object[]{
                    nhap.getMaNhap(),
                    nhap.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    tenNguyenLieuHienThi,
                    nhap.getSoLuong(),
                    nhap.getDonViTinh(),
                    String.format("%,.0f VND", nhap.getDonGia()),
                    String.format("%,.0f VND", thanhTien),
                    nhap.getNguonNhap(),
                    nhap.getMaNguyenLieu()
                });
            }
            
            // Hiển thị tổng kết
            if (!list.isEmpty()) {
                String thongBaoTongKet = String.format(
                    "Thống kê tháng %d/%d:\n" +
                    "- Tổng số phiếu nhập: %d\n" +
                    "- Tổng số lượng: %,d\n" +
                    "- Tổng thành tiền: %,d VND",
                    thang, nam, list.size(), tongSoLuong, tongTien.intValue()
                );
                
                hienThiThongBao(thongBaoTongKet, "Kết quả liệt kê", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            hienThiThongBao("Tháng và năm phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi liệt kê dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        try {
            List<NhapNguyenLieu> list = nhapNguyenLieuService.getAllNhapNguyenLieu();
            model.setRowCount(0);
            for (NhapNguyenLieu nhap : list) {
                BigDecimal thanhTien = nhap.getThanhTien();

                // ĐÁNH DẤU PHIẾU NHẬP KHÔNG CÓ NGUYÊN LIỆU (CHỈ ĐỂ HIỂN THỊ)
                String tenNguyenLieuHienThi = nhap.getTenNguyenLieu();
                if (nhap.getMaNguyenLieu() == null || nhap.getMaNguyenLieu() == 0) {
                    tenNguyenLieuHienThi = "[ĐÃ XÓA] " + tenNguyenLieuHienThi;
                }

                model.addRow(new Object[]{
                    nhap.getMaNhap(),
                    nhap.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    tenNguyenLieuHienThi,
                    nhap.getSoLuong(),
                    nhap.getDonViTinh(),
                    String.format("%,.0f VND", nhap.getDonGia()),
                    String.format("%,.0f VND", thanhTien),
                    nhap.getNguonNhap(),
                    nhap.getMaNguyenLieu()
                });
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLoaiNguyenLieu() {
        try {
            view.getCboLoaiFilter().removeAllItems();
            view.getCboLoaiFilter().addItem("Tất cả");

            List<LoaiNguyenLieu> list = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : list) {
                view.getCboLoaiFilter().addItem(loaiNL.getTenLoaiNL());
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themNhapNguyenLieu() {
        try {
            // Tạo dialog với 2 lựa chọn
            JPanel panel = new JPanel(new GridLayout(0, 1));
            
            JRadioButton rbDaCo = new JRadioButton("Nguyên liệu đã có");
            JRadioButton rbChuaCo = new JRadioButton("Nguyên liệu chưa có");
            ButtonGroup group = new ButtonGroup();
            group.add(rbDaCo);
            group.add(rbChuaCo);
            panel.add(new JLabel("Chọn loại nguyên liệu:"));
            panel.add(rbDaCo);
            panel.add(rbChuaCo);
            rbDaCo.setBackground(COLOR_BACKGROUND);
            rbChuaCo.setBackground(COLOR_BACKGROUND);
            Object[] message = {"Chọn loại nguyên liệu:", panel};
            boolean confirmed = showCustomInputDialog(message, "Chọn loại nguyên liệu");
            
            if (confirmed) {
                if (rbDaCo.isSelected()) {
                    themNguyenLieuDaCo();
                } else if (rbChuaCo.isSelected()) {
                    themNguyenLieuChuaCo();
                } else {
                    hienThiThongBao("Vui lòng chọn một loại nguyên liệu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themNguyenLieuDaCo() {
        try {
            // Panel cho nguyên liệu đã có
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

            JComboBox<String> cboNguyenLieu = new JComboBox<>();
            JTextField txtSoLuong = new JTextField();
            JTextField txtDonGia = new JTextField();
            JTextField txtNguonNhap = new JTextField();

            // Load danh sách nguyên liệu hiện có
            List<NguyenLieu> listNL = nguyenLieuService.getAllNguyenLieu();
            if (listNL.isEmpty()) {
                hienThiThongBao("Chưa có nguyên liệu nào. Vui lòng thêm nguyên liệu mới trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (NguyenLieu nl : listNL) {
                cboNguyenLieu.addItem(nl.getMaNguyenLieu() + " - " + nl.getTenNguyenLieu() + " (Tồn: " + nl.getSoLuongTon() + " " + nl.getDonViTinh() + ")");
            }

            panel.add(new JLabel("Chọn nguyên liệu:"));
            panel.add(cboNguyenLieu);
            panel.add(new JLabel("Số lượng nhập:"));
            panel.add(txtSoLuong);
            panel.add(new JLabel("Đơn giá (VND):"));
            panel.add(txtDonGia);
            panel.add(new JLabel("Nguồn nhập:"));
            panel.add(txtNguonNhap);

            Object[] message = {"Nhập nguyên liệu đã có:", panel};
            boolean confirmed = showCustomInputDialog(message, "Nhập nguyên liệu đã có");

            if (confirmed) {
                String selectedNL = (String) cboNguyenLieu.getSelectedItem();
                String soLuongStr = txtSoLuong.getText().trim();
                String donGiaStr = txtDonGia.getText().trim();
                String nguonNhap = txtNguonNhap.getText().trim();

                if (selectedNL == null) {
                    hienThiThongBao("Vui lòng chọn nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy mã nguyên liệu từ chuỗi selected
                int maNguyenLieu = Integer.parseInt(selectedNL.split(" - ")[0]);
                NguyenLieu nguyenLieu = nguyenLieuService.getNguyenLieuById(maNguyenLieu);

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong <= 0) {
                        hienThiThongBao("Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal donGia;
                try {
                    donGia = new BigDecimal(donGiaStr.replaceAll("[^\\d.]", ""));
                    if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
                        hienThiThongBao("Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo phiếu nhập mới
                NhapNguyenLieu nhapNL = new NhapNguyenLieu(
                        maNguyenLieu,
                        LocalDate.now(),
                        nguyenLieu.getTenNguyenLieu(),
                        nguyenLieu.getDonViTinh(),
                        soLuong,
                        donGia,
                        nguonNhap.isEmpty() ? null : nguonNhap
                );

                boolean xacNhan = hienThiXacNhan(
                        "Bạn có chắc chắn muốn thêm phiếu nhập này?\n" +
                        "Nguyên liệu: " + nguyenLieu.getTenNguyenLieu() + "\n" +
                        "Số lượng nhập: " + soLuong + " " + nguyenLieu.getDonViTinh() + "\n" +
                        "Số lượng tồn mới: " + (nguyenLieu.getSoLuongTon() + soLuong) + " " + nguyenLieu.getDonViTinh() + "\n" +
                        "Đơn giá: " + String.format("%,.0f VND", donGia) + "\n" +
                        "Thành tiền: " + String.format("%,.0f VND", nhapNL.getThanhTien())
                );

                if (xacNhan) {
                    boolean success = nhapNguyenLieuService.addNhapNguyenLieu(nhapNL);
                    if (success) {
                        // Cập nhật số lượng tồn kho
                        nguyenLieuService.updateSoLuongTon(maNguyenLieu, nguyenLieu.getSoLuongTon() + soLuong);

                        hienThiThongBao("Thêm phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        hienThiThongBao("Thêm phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm nguyên liệu đã có: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themNguyenLieuChuaCo() {
        try {
            // Panel cho nguyên liệu chưa có
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

            JTextField txtTenNguyenLieu = new JTextField();
            JTextField txtDonViTinh = new JTextField();
            JComboBox<String> cboLoaiNL = new JComboBox<>();
            JTextField txtSoLuong = new JTextField();
            JTextField txtDonGia = new JTextField();
            JTextField txtNguonNhap = new JTextField();

            // Load loại nguyên liệu
            List<LoaiNguyenLieu> listLoai = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loai : listLoai) {
                cboLoaiNL.addItem(loai.getTenLoaiNL());
            }

            panel.add(new JLabel("Tên nguyên liệu mới:"));
            panel.add(txtTenNguyenLieu);
            panel.add(new JLabel("Đơn vị tính:"));
            panel.add(txtDonViTinh);
            panel.add(new JLabel("Loại nguyên liệu:"));
            panel.add(cboLoaiNL);
            panel.add(new JLabel("Số lượng nhập:"));
            panel.add(txtSoLuong);
            panel.add(new JLabel("Đơn giá (VND):"));
            panel.add(txtDonGia);
            panel.add(new JLabel("Nguồn nhập:"));
            panel.add(txtNguonNhap);

            Object[] message = {"Thêm nguyên liệu mới và nhập:", panel};
            boolean confirmed = showCustomInputDialog(message, "Thêm nguyên liệu mới và nhập");

            if (confirmed) {
                String tenNguyenLieu = txtTenNguyenLieu.getText().trim();
                String donViTinh = txtDonViTinh.getText().trim();
                String tenLoai = (String) cboLoaiNL.getSelectedItem();
                String soLuongStr = txtSoLuong.getText().trim();
                String donGiaStr = txtDonGia.getText().trim();
                String nguonNhap = txtNguonNhap.getText().trim();

                // Validate dữ liệu
                if (tenNguyenLieu.isEmpty()) {
                    hienThiThongBao("Tên nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (donViTinh.isEmpty()) {
                    hienThiThongBao("Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra xem tên nguyên liệu đã tồn tại chưa
                try {
                    NguyenLieu nlExist = nguyenLieuService.getNguyenLieuByTen(tenNguyenLieu);
                    if (nlExist != null) {
                        boolean choice = hienThiXacNhan(
                                "Nguyên liệu '" + tenNguyenLieu + "' đã tồn tại!\nBạn có muốn chuyển sang nhập nguyên liệu đã có không?"
                        );

                        if (choice) {
                            themNguyenLieuDaCo();
                            return;
                        } else {
                            return;
                        }
                    }
                } catch (Exception ex) {
                    // Nếu hàm getNguyenLieuByTen chưa có, bỏ qua kiểm tra
                    System.out.println("Chưa có hàm getNguyenLieuByTen, bỏ qua kiểm tra trùng tên");
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong <= 0) {
                        hienThiThongBao("Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal donGia;
                try {
                    donGia = new BigDecimal(donGiaStr.replaceAll("[^\\d.]", ""));
                    if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
                        hienThiThongBao("Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy mã loại từ tên loại
                Integer maLoai = getMaLoaiFromTen(tenLoai);

                // Tạo nguyên liệu mới
                NguyenLieu nlMoi = new NguyenLieu(
                        tenNguyenLieu,
                        soLuong, // Số lượng tồn = số lượng nhập
                        donViTinh,
                        maLoai
                );

                // Thêm nguyên liệu mới vào database
                boolean successNL = nguyenLieuService.addNguyenLieu(nlMoi);
                if (!successNL) {
                    hienThiThongBao("Thêm nguyên liệu mới thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy mã nguyên liệu vừa tạo (tìm lại theo tên)
                NguyenLieu nlVuaTao = null;
                try {
                    nlVuaTao = nguyenLieuService.getNguyenLieuByTen(tenNguyenLieu);
                } catch (Exception ex) {
                    // Nếu không lấy được theo tên, thử lấy theo danh sách
                    List<NguyenLieu> allNL = nguyenLieuService.getAllNguyenLieu();
                    for (NguyenLieu nl : allNL) {
                        if (nl.getTenNguyenLieu().equals(tenNguyenLieu)) {
                            nlVuaTao = nl;
                            break;
                        }
                    }
                }

                if (nlVuaTao == null) {
                    hienThiThongBao("Không thể lấy thông tin nguyên liệu vừa tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo phiếu nhập cho nguyên liệu mới
                NhapNguyenLieu nhapNL = new NhapNguyenLieu(
                        nlVuaTao.getMaNguyenLieu(),
                        LocalDate.now(),
                        tenNguyenLieu,
                        donViTinh,
                        soLuong,
                        donGia,
                        nguonNhap.isEmpty() ? null : nguonNhap
                );

                boolean successNhap = nhapNguyenLieuService.addNhapNguyenLieu(nhapNL);
                if (successNhap) {
                    hienThiThongBao("Thêm nguyên liệu mới và phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    hienThiThongBao("Thêm phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm nguyên liệu mới: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhapNguyenLieu() {
        int selectedRow = view.getTblNhapNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một phiếu nhập để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNhap = (Integer) model.getValueAt(selectedRow, 0);
            NhapNguyenLieu nhapHienTai = nhapNguyenLieuService.getNhapNguyenLieuById(maNhap);
            NguyenLieu nguyenLieuHienTai = nguyenLieuService.getNguyenLieuById(nhapHienTai.getMaNguyenLieu());

            JTextField txtSoLuong = new JTextField(nhapHienTai.getSoLuong().toString());
            JTextField txtDonGia = new JTextField(nhapHienTai.getDonGia().toString());
            JTextField txtNguonNhap = new JTextField(nhapHienTai.getNguonNhap() != null ? nhapHienTai.getNguonNhap() : "");

            Object[] message = {
                "Nguyên liệu: " + nguyenLieuHienTai.getTenNguyenLieu(),
                "Số lượng:", txtSoLuong,
                "Đơn giá (VND):", txtDonGia,
                "Nguồn nhập:", txtNguonNhap
            };

            boolean confirmed = showCustomInputDialog(message, "Sửa phiếu nhập");

            if (confirmed) {
                String soLuongStr = txtSoLuong.getText().trim();
                String donGiaStr = txtDonGia.getText().trim();
                String nguonNhap = txtNguonNhap.getText().trim();

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong <= 0) {
                        hienThiThongBao("Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal donGia;
                try {
                    donGia = new BigDecimal(donGiaStr.replaceAll("[^\\d.]", ""));
                    if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
                        hienThiThongBao("Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    hienThiThongBao("Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tính toán chênh lệch số lượng để cập nhật tồn kho
                int soLuongChenhLech = soLuong - nhapHienTai.getSoLuong();

                nhapHienTai.setSoLuong(soLuong);
                nhapHienTai.setDonGia(donGia);
                nhapHienTai.setNguonNhap(nguonNhap.isEmpty() ? null : nguonNhap);

                boolean xacNhan = hienThiXacNhan(
                        "Bạn có chắc chắn muốn sửa phiếu nhập này?\n" +
                        "Nguyên liệu: " + nguyenLieuHienTai.getTenNguyenLieu() + "\n" +
                        "Số lượng: " + soLuong + " (" + (soLuongChenhLech >= 0 ? "+" : "") + soLuongChenhLech + ")\n" +
                        "Đơn giá: " + String.format("%,.0f VND", donGia) + "\n" +
                        "Thành tiền: " + String.format("%,.0f VND", nhapHienTai.getThanhTien())
                );

                if (xacNhan) {
                    boolean success = nhapNguyenLieuService.updateNhapNguyenLieu(nhapHienTai);
                    if (success) {
                        // Cập nhật số lượng tồn kho
                        if (soLuongChenhLech != 0) {
                            nguyenLieuService.updateSoLuongTon(
                                    nguyenLieuHienTai.getMaNguyenLieu(),
                                    nguyenLieuHienTai.getSoLuongTon() + soLuongChenhLech
                            );
                        }

                        hienThiThongBao("Sửa phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        hienThiThongBao("Sửa phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhapNguyenLieu() {
        int selectedRow = view.getTblNhapNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một phiếu nhập để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNhap = (Integer) model.getValueAt(selectedRow, 0);
            NhapNguyenLieu nhap = nhapNguyenLieuService.getNhapNguyenLieuById(maNhap);

            // CHO PHÉP XÓA CẢ PHIẾU NHẬP CÓ MaNguyenLieu = NULL
            String message;

            if (nhap.getMaNguyenLieu() == null || nhap.getMaNguyenLieu() == 0) {
                // PHIẾU NHẬP KHÔNG CÓ NGUYÊN LIỆU
                message = "Bạn có chắc chắn muốn xóa phiếu nhập này?\n" +
                         "Nguyên liệu: " + nhap.getTenNguyenLieu() + " (ĐÃ XÓA)\n" +
                         "Số lượng: " + nhap.getSoLuong() + "\n" +
                         "Đơn giá: " + String.format("%,.0f VND", nhap.getDonGia()) + "\n\n" +
                         "Lưu ý: Phiếu nhập này không ảnh hưởng đến tồn kho.";
            } else {
                // PHIẾU NHẬP CÓ NGUYÊN LIỆU HỢP LỆ
                NguyenLieu nguyenLieu = nguyenLieuService.getNguyenLieuById(nhap.getMaNguyenLieu());
                message = "Bạn có chắc chắn muốn xóa phiếu nhập này?\n" +
                         "Nguyên liệu: " + nhap.getTenNguyenLieu() + "\n" +
                         "Số lượng: " + nhap.getSoLuong() + "\n" +
                         "Đơn giá: " + String.format("%,.0f VND", nhap.getDonGia()) + "\n\n" +
                         "Lưu ý: Số lượng tồn kho sẽ bị trừ đi " + nhap.getSoLuong() + " " + nhap.getDonViTinh();
            }

            boolean xacNhan = hienThiXacNhan(message);

            if (xacNhan) {
                boolean success = nhapNguyenLieuService.deleteNhapNguyenLieu(maNhap);
                if (success) {
                    // CHỈ CẬP NHẬT TỒN KHO NẾU CÓ MaNguyenLieu HỢP LỆ
                    if (nhap.getMaNguyenLieu() != null && nhap.getMaNguyenLieu() != 0) {
                        NguyenLieu nguyenLieu = nguyenLieuService.getNguyenLieuById(nhap.getMaNguyenLieu());
                        int soLuongTonMoi = nguyenLieu.getSoLuongTon() - nhap.getSoLuong();
                        if (soLuongTonMoi < 0) {
                            soLuongTonMoi = 0;
                        }
                        nguyenLieuService.updateSoLuongTon(nguyenLieu.getMaNguyenLieu(), soLuongTonMoi);
                    }

                    hienThiThongBao("Xóa phiếu nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    hienThiThongBao("Xóa phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<NhapNguyenLieu> list = nhapNguyenLieuService.getAllNhapNguyenLieu();
            model.setRowCount(0);
            for (NhapNguyenLieu nhap : list) {
                if (nhap.getTenNguyenLieu().toLowerCase().contains(keyword.toLowerCase()) ||
                    (nhap.getNguonNhap() != null && nhap.getNguonNhap().toLowerCase().contains(keyword.toLowerCase()))) {
                    BigDecimal thanhTien = nhap.getThanhTien();
                    model.addRow(new Object[]{
                        nhap.getMaNhap(),
                        nhap.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        nhap.getTenNguyenLieu(),
                        nhap.getSoLuong(),
                        nhap.getDonViTinh(),
                        String.format("%,.0f VND", nhap.getDonGia()),
                        String.format("%,.0f VND", thanhTien),
                        nhap.getNguonNhap(),
                        nhap.getMaNguyenLieu()
                    });
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void locTheoLoai() {
        String selectedLoai = (String) view.getCboLoaiFilter().getSelectedItem();
        if (selectedLoai == null || selectedLoai.equals("Tất cả")) {
            loadData();
            return;
        }

        try {
            // Lấy mã loại từ tên loại
            Integer maLoai = getMaLoaiFromTen(selectedLoai);
            if (maLoai == null) {
                loadData();
                return;
            }

            // Lấy danh sách nguyên liệu thuộc loại này
            List<NguyenLieu> listNL = nguyenLieuService.getNguyenLieuByMaLoaiNL(maLoai);
            if (listNL.isEmpty()) {
                model.setRowCount(0);
                return;
            }

            // Lấy tất cả phiếu nhập và lọc
            List<NhapNguyenLieu> listNhap = nhapNguyenLieuService.getAllNhapNguyenLieu();
            model.setRowCount(0);

            for (NhapNguyenLieu nhap : listNhap) {
                for (NguyenLieu nl : listNL) {
                    if (nhap.getMaNguyenLieu().equals(nl.getMaNguyenLieu())) {
                        BigDecimal thanhTien = nhap.getThanhTien();
                        model.addRow(new Object[]{
                            nhap.getMaNhap(),
                            nhap.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            nhap.getTenNguyenLieu(),
                            nhap.getSoLuong(),
                            nhap.getDonViTinh(),
                            String.format("%,.0f VND", nhap.getDonGia()),
                            String.format("%,.0f VND", thanhTien),
                            nhap.getNguonNhap(),
                            nhap.getMaNguyenLieu()
                        });
                        break;
                    }
                }
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi lọc theo loại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer getMaLoaiFromTen(String tenLoai) {
        if (tenLoai.equals("Tất cả") || tenLoai.isEmpty()) {
            return null;
        }
        try {
            List<LoaiNguyenLieu> list = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : list) {
                if (loaiNL.getTenLoaiNL().equals(tenLoai)) {
                    return loaiNL.getMaLoaiNL();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void lamMoi() {
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
        loadData();
    }

    private void moQuanLyLoaiNguyenLieu() {
        try {
            JFrame frame = new JFrame("Quản lý loại nguyên liệu");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            QuanLyLoaiNguyenLieuView loaiNLView = new QuanLyLoaiNguyenLieuView();
            new QuanLyLoaiNguyenLieuController(loaiNLView);

            frame.add(loaiNLView);
            frame.setVisible(true);

            // Reload lại danh sách loại nguyên liệu khi đóng cửa sổ
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadLoaiNguyenLieu();
                    loadData();
                }
            });
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi mở quản lý loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    private boolean hienThiXacNhan(String message) {
        JDialog dialog = createConfirmationDialog(message);
        dialog.setVisible(true);
        return dialogResult;
    }

    private boolean showCustomInputDialog(Object[] message, String title) {
        JDialog dialog = createInputDialog(message, title);
        dialog.setVisible(true);
        return dialogResult;
    }

    private JDialog createCustomDialog(String message, String title, int messageType) {
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Icon icon = null;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                icon = UIManager.getIcon("OptionPane.errorIcon");
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.informationIcon");
                break;
            case JOptionPane.WARNING_MESSAGE:
                icon = UIManager.getIcon("OptionPane.warningIcon");
                break;
            case JOptionPane.QUESTION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.questionIcon");
                break;
        }

        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel, BorderLayout.WEST);
        }
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        return dialog;
    }

    private JDialog createConfirmationDialog(String message) {
        JButton yesButton = createStyledButton("Có", COLOR_BUTTON);
        JButton noButton = createStyledButton("Không", new Color(0xCC, 0x66, 0x66));

        yesButton.addActionListener(e -> {
            dialogResult = true;
            Window window = SwingUtilities.getWindowAncestor(yesButton);
            if (window != null) {
                window.dispose();
            }
        });

        noButton.addActionListener(e -> {
            dialogResult = false;
            Window window = SwingUtilities.getWindowAncestor(noButton);
            if (window != null) {
                window.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel, BorderLayout.WEST);
        }
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) null, "Xác nhận", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        return dialog;
    }

    private JDialog createInputDialog(Object[] message, String title) {
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        JButton cancelButton = createStyledButton("Hủy", new Color(0xCC, 0x66, 0x66));

        // Tạo panel chứa các component input
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        inputPanel.setBackground(COLOR_BACKGROUND);

        // Tìm tất cả các JTextField và JComboBox trong message
        java.util.List<JTextField> textFields = new java.util.ArrayList<>();
        java.util.List<JComboBox<?>> comboBoxes = new java.util.ArrayList<>();

        for (Object obj : message) {
            if (obj instanceof JTextField) {
                textFields.add((JTextField) obj);
                inputPanel.add((JTextField) obj);
            } else if (obj instanceof JComboBox) {
                comboBoxes.add((JComboBox<?>) obj);
                inputPanel.add((JComboBox<?>) obj);
            } else if (obj instanceof JPanel) {
                inputPanel.add((JPanel) obj);
            } else if (obj instanceof String) {
                JLabel label = new JLabel((String) obj);
                label.setForeground(COLOR_TEXT);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                inputPanel.add(label);
            }
        }

        okButton.addActionListener(e -> {
            dialogResult = true;
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            Window window = SwingUtilities.getWindowAncestor(cancelButton);
            if (window != null) {
                window.dispose();
            }
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        return dialog;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}