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

    public QuanLyNhapNguyenLieuController(QuanLyNhapNguyenLieuView view) {
        this.view = view;
        this.nhapNguyenLieuService = new NhapNguyenLieuService();
        this.nguyenLieuService = new NguyenLieuService();
        this.loaiNguyenLieuService = new LoaiNguyenLieuService();
        this.model = view.getModel();
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

    private void loadData() {
        try {
            List<NhapNguyenLieu> list = nhapNguyenLieuService.getAllNhapNguyenLieu();
            model.setRowCount(0);
            for (NhapNguyenLieu nhap : list) {
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(view, "Lỗi khi tải loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themNhapNguyenLieu() {
        try {
            // Lấy danh sách nguyên liệu
            List<NguyenLieu> listNL = nguyenLieuService.getAllNguyenLieu();
            if (listNL.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Chưa có nguyên liệu nào. Vui lòng thêm nguyên liệu trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> cboNguyenLieu = new JComboBox<>();
            for (NguyenLieu nl : listNL) {
                cboNguyenLieu.addItem(nl.getMaNguyenLieu() + " - " + nl.getTenNguyenLieu());
            }

            JTextField txtSoLuong = new JTextField();
            JTextField txtDonGia = new JTextField();
            JTextField txtNguonNhap = new JTextField();

            Object[] message = {
                "Nguyên liệu:", cboNguyenLieu,
                "Số lượng:", txtSoLuong,
                "Đơn giá (VND):", txtDonGia,
                "Nguồn nhập:", txtNguonNhap
            };

            int option = JOptionPane.showConfirmDialog(view, message, "Thêm phiếu nhập", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String selectedNL = (String) cboNguyenLieu.getSelectedItem();
                String soLuongStr = txtSoLuong.getText().trim();
                String donGiaStr = txtDonGia.getText().trim();
                String nguonNhap = txtNguonNhap.getText().trim();

                if (selectedNL == null) {
                    JOptionPane.showMessageDialog(view, "Vui lòng chọn nguyên liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy mã nguyên liệu từ chuỗi selected
                int maNguyenLieu = Integer.parseInt(selectedNL.split(" - ")[0]);
                NguyenLieu nguyenLieu = nguyenLieuService.getNguyenLieuById(maNguyenLieu);

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong <= 0) {
                        JOptionPane.showMessageDialog(view, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal donGia;
                try {
                    donGia = new BigDecimal(donGiaStr.replaceAll("[^\\d.]", ""));
                    if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(view, "Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                NhapNguyenLieu nhapNL = new NhapNguyenLieu(
                    maNguyenLieu, 
                    LocalDate.now(),
                    nguyenLieu.getTenNguyenLieu(),
                    nguyenLieu.getDonViTinh(),
                    soLuong,
                    donGia,
                    nguonNhap.isEmpty() ? null : nguonNhap
                );

                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn thêm phiếu nhập này?\n" +
                    "Nguyên liệu: " + nguyenLieu.getTenNguyenLieu() + "\n" +
                    "Số lượng: " + soLuong + "\n" +
                    "Đơn giá: " + String.format("%,.0f VND", donGia) + "\n" +
                    "Thành tiền: " + String.format("%,.0f VND", nhapNL.getThanhTien()), 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = nhapNguyenLieuService.addNhapNguyenLieu(nhapNL);
                    if (success) {
                        // Cập nhật số lượng tồn kho
                        nguyenLieuService.updateSoLuongTon(maNguyenLieu, nguyenLieu.getSoLuongTon() + soLuong);
                        
                        JOptionPane.showMessageDialog(view, "Thêm phiếu nhập thành công!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhapNguyenLieu() {
        int selectedRow = view.getTblNhapNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phiếu nhập để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
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

            int option = JOptionPane.showConfirmDialog(view, message, "Sửa phiếu nhập", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String soLuongStr = txtSoLuong.getText().trim();
                String donGiaStr = txtDonGia.getText().trim();
                String nguonNhap = txtNguonNhap.getText().trim();

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong <= 0) {
                        JOptionPane.showMessageDialog(view, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal donGia;
                try {
                    donGia = new BigDecimal(donGiaStr.replaceAll("[^\\d.]", ""));
                    if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(view, "Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tính toán chênh lệch số lượng để cập nhật tồn kho
                int soLuongChenhLech = soLuong - nhapHienTai.getSoLuong();

                nhapHienTai.setSoLuong(soLuong);
                nhapHienTai.setDonGia(donGia);
                nhapHienTai.setNguonNhap(nguonNhap.isEmpty() ? null : nguonNhap);

                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn sửa phiếu nhập này?\n" +
                    "Nguyên liệu: " + nguyenLieuHienTai.getTenNguyenLieu() + "\n" +
                    "Số lượng: " + soLuong + " (" + (soLuongChenhLech >= 0 ? "+" : "") + soLuongChenhLech + ")\n" +
                    "Đơn giá: " + String.format("%,.0f VND", donGia) + "\n" +
                    "Thành tiền: " + String.format("%,.0f VND", nhapHienTai.getThanhTien()), 
                    "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = nhapNguyenLieuService.updateNhapNguyenLieu(nhapHienTai);
                    if (success) {
                        // Cập nhật số lượng tồn kho
                        if (soLuongChenhLech != 0) {
                            nguyenLieuService.updateSoLuongTon(
                                nguyenLieuHienTai.getMaNguyenLieu(), 
                                nguyenLieuHienTai.getSoLuongTon() + soLuongChenhLech
                            );
                        }
                        
                        JOptionPane.showMessageDialog(view, "Sửa phiếu nhập thành công!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Sửa phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhapNguyenLieu() {
        int selectedRow = view.getTblNhapNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phiếu nhập để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNhap = (Integer) model.getValueAt(selectedRow, 0);
            NhapNguyenLieu nhap = nhapNguyenLieuService.getNhapNguyenLieuById(maNhap);
            NguyenLieu nguyenLieu = nguyenLieuService.getNguyenLieuById(nhap.getMaNguyenLieu());

            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa phiếu nhập này?\n" +
                "Nguyên liệu: " + nhap.getTenNguyenLieu() + "\n" +
                "Số lượng: " + nhap.getSoLuong() + "\n" +
                "Đơn giá: " + String.format("%,.0f VND", nhap.getDonGia()) + "\n\n" +
                "Lưu ý: Số lượng tồn kho sẽ bị trừ đi " + nhap.getSoLuong() + " " + nhap.getDonViTinh(), 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = nhapNguyenLieuService.deleteNhapNguyenLieu(maNhap);
                if (success) {
                    // Cập nhật số lượng tồn kho (trừ đi số lượng đã nhập)
                    int soLuongTonMoi = nguyenLieu.getSoLuongTon() - nhap.getSoLuong();
                    if (soLuongTonMoi < 0) soLuongTonMoi = 0;
                    nguyenLieuService.updateSoLuongTon(nguyenLieu.getMaNguyenLieu(), soLuongTonMoi);
                    
                    JOptionPane.showMessageDialog(view, "Xóa phiếu nhập thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(view, "Lỗi khi lọc theo loại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer getMaLoaiFromTen(String tenLoai) {
        if (tenLoai.equals("Tất cả") || tenLoai.isEmpty()) return null;
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
            JOptionPane.showMessageDialog(view, "Lỗi khi mở quản lý loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}