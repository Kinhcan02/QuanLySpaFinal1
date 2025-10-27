package Controller;

import View.QuanLyDichVuView;
import Model.DichVu;
import Model.LoaiDichVu;
import Service.DichVuService;
import Service.LoaiDichVuService;
import View.QuanLyDichVuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class QuanLyDichVuController {
    private QuanLyDichVuView view;
    private DichVuService dichVuService;
    private LoaiDichVuService loaiDichVuService;

    public QuanLyDichVuController(QuanLyDichVuView view) {
        this.view = view;
        this.dichVuService = new DichVuService();
        this.loaiDichVuService = new LoaiDichVuService();
        
        initController();
        loadAllDichVu();
    }

    private void initController() {
        // Sự kiện nút Thêm
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themDichVu();
            }
        });

        // Sự kiện nút Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaDichVu();
            }
        });

        // Sự kiện nút Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaDichVu();
            }
        });

        // Sự kiện nút Làm mới
        view.getBtnLamMoi().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllDichVu();
                view.getTxtTimKiem().setText("");
                view.getCboLoaiFilter().setSelectedIndex(0);
            }
        });

        // Sự kiện nút Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemDichVu();
            }
        });
    }

    private void loadAllDichVu() {
        try {
            List<DichVu> listDichVu = dichVuService.getAllDichVu();
            displayDichVuOnTable(listDichVu);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải danh sách dịch vụ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayDichVuOnTable(List<DichVu> listDichVu) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (DichVu dv : listDichVu) {
            Object[] rowData = {
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                formatCurrency(dv.getGia()),
                "60 phút", // Giả định thời gian mặc định
                getTenLoaiDichVu(dv.getMaLoaiDV()),
                dv.getGhiChu()
            };
            model.addRow(rowData);
        }
    }

    private String getTenLoaiDichVu(Integer maLoaiDV) {
        if (maLoaiDV == null) return "Chưa phân loại";
        
        try {
            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            return loaiDV.getTenLoaiDV();
        } catch (Exception e) {
            return "Không xác định";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,d VNĐ", amount.longValue());
    }

    private void themDichVu() {
        try {
            // Hiển thị dialog thêm dịch vụ
            JTextField txtTenDV = new JTextField();
            JTextField txtGia = new JTextField();
            JComboBox<String> cboLoaiDV = new JComboBox<>();
            JTextArea txtGhiChu = new JTextArea(3, 20);
            
            // Load loại dịch vụ vào combobox
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");
            for (LoaiDichVu loai : listLoaiDV) {
                cboLoaiDV.addItem(loai.getTenLoaiDV());
            }

            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            
            Object[] message = {
                "Tên dịch vụ:", txtTenDV,
                "Giá dịch vụ:", txtGia,
                "Loại dịch vụ:", cboLoaiDV,
                "Ghi chú:", scrollGhiChu
            };

            int option = JOptionPane.showConfirmDialog(view, message, 
                "Thêm dịch vụ mới", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                // Validation
                if (txtTenDV.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên dịch vụ không được để trống");
                    return;
                }
                
                BigDecimal gia;
                try {
                    gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Giá dịch vụ không hợp lệ");
                    return;
                }

                Integer maLoaiDV = null;
                if (cboLoaiDV.getSelectedIndex() > 0) {
                    maLoaiDV = listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV();
                }

                DichVu dichVu = new DichVu(
                    txtTenDV.getText().trim(),
                    gia,
                    maLoaiDV,
                    txtGhiChu.getText().trim()
                );

                boolean success = dichVuService.addDichVu(dichVu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Thêm dịch vụ thành công");
                    loadAllDichVu();
                } else {
                    JOptionPane.showMessageDialog(view, "Thêm dịch vụ thất bại");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm dịch vụ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một dịch vụ để sửa");
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            DichVu dichVu = dichVuService.getDichVuById(maDichVu);

            // Hiển thị dialog sửa dịch vụ
            JTextField txtTenDV = new JTextField(dichVu.getTenDichVu());
            JTextField txtGia = new JTextField(dichVu.getGia().toString());
            JComboBox<String> cboLoaiDV = new JComboBox<>();
            JTextArea txtGhiChu = new JTextArea(dichVu.getGhiChu(), 3, 20);
            
            // Load loại dịch vụ vào combobox
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");
            int selectedIndex = 0;
            for (int i = 0; i < listLoaiDV.size(); i++) {
                LoaiDichVu loai = listLoaiDV.get(i);
                cboLoaiDV.addItem(loai.getTenLoaiDV());
                if (dichVu.getMaLoaiDV() != null && dichVu.getMaLoaiDV().equals(loai.getMaLoaiDV())) {
                    selectedIndex = i + 1;
                }
            }
            cboLoaiDV.setSelectedIndex(selectedIndex);

            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            
            Object[] message = {
                "Tên dịch vụ:", txtTenDV,
                "Giá dịch vụ:", txtGia,
                "Loại dịch vụ:", cboLoaiDV,
                "Ghi chú:", scrollGhiChu
            };

            int option = JOptionPane.showConfirmDialog(view, message, 
                "Sửa dịch vụ", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                // Validation
                if (txtTenDV.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên dịch vụ không được để trống");
                    return;
                }
                
                BigDecimal gia;
                try {
                    gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Giá dịch vụ không hợp lệ");
                    return;
                }

                Integer maLoaiDV = null;
                if (cboLoaiDV.getSelectedIndex() > 0) {
                    maLoaiDV = listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV();
                }

                dichVu.setTenDichVu(txtTenDV.getText().trim());
                dichVu.setGia(gia);
                dichVu.setMaLoaiDV(maLoaiDV);
                dichVu.setGhiChu(txtGhiChu.getText().trim());

                boolean success = dichVuService.updateDichVu(dichVu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Cập nhật dịch vụ thành công");
                    loadAllDichVu();
                } else {
                    JOptionPane.showMessageDialog(view, "Cập nhật dịch vụ thất bại");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa dịch vụ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một dịch vụ để xóa");
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa dịch vụ: " + tenDichVu + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dichVuService.deleteDichVu(maDichVu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa dịch vụ thành công");
                    loadAllDichVu();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa dịch vụ thất bại");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa dịch vụ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemDichVu() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String loaiFilter = (String) view.getCboLoaiFilter().getSelectedItem();
            
            List<DichVu> ketQua;
            
            if (!tuKhoa.isEmpty()) {
                ketQua = dichVuService.searchDichVuByTen(tuKhoa);
            } else {
                ketQua = dichVuService.getAllDichVu();
            }
            
            // Lọc theo loại dịch vụ nếu không chọn "Tất cả"
            if (!"Tất cả".equals(loaiFilter)) {
                ketQua.removeIf(dv -> {
                    String tenLoai = getTenLoaiDichVu(dv.getMaLoaiDV());
                    return !loaiFilter.equals(tenLoai);
                });
            }
            
            displayDichVuOnTable(ketQua);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}