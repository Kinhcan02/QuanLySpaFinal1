package Controller;

import Model.LoaiNguyenLieu;
import Model.NguyenLieu;
import Service.LoaiNguyenLieuService;
import Service.NguyenLieuService;
import View.QuanLyNguyenLieuView;
import View.QuanLyLoaiNguyenLieuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class QuanLyNguyenLieuController {
    private QuanLyNguyenLieuView view;
    private NguyenLieuService nguyenLieuService;
    private LoaiNguyenLieuService loaiNguyenLieuService;
    private DefaultTableModel model;

    public QuanLyNguyenLieuController(QuanLyNguyenLieuView view) {
        this.view = view;
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
                themNguyenLieu();
            }
        });

        view.getBtnSua().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suaNguyenLieu();
            }
        });

        view.getBtnXoa().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xoaNguyenLieu();
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
            List<NguyenLieu> list = nguyenLieuService.getAllNguyenLieu();
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                model.addRow(new Object[]{
                    nl.getMaNguyenLieu(),
                    nl.getTenNguyenLieu(),
                    nl.getSoLuongTon(),
                    nl.getDonViTinh(),
                    tenLoai,
                    nl.getMaLoaiNL()
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

    private String getTenLoaiNguyenLieu(Integer maLoaiNL) {
        if (maLoaiNL == null) return "";
        try {
            LoaiNguyenLieu loaiNL = loaiNguyenLieuService.getLoaiNguyenLieuById(maLoaiNL);
            return loaiNL.getTenLoaiNL();
        } catch (Exception e) {
            return "";
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

    private void themNguyenLieu() {
        try {
            JTextField txtTen = new JTextField();
            JTextField txtSoLuong = new JTextField("0");
            JTextField txtDonVi = new JTextField();
            
            JComboBox<String> cboLoai = new JComboBox<>();
            cboLoai.addItem(""); // Trống
            List<LoaiNguyenLieu> listLoai = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : listLoai) {
                cboLoai.addItem(loaiNL.getTenLoaiNL());
            }

            Object[] message = {
                "Tên nguyên liệu:", txtTen,
                "Số lượng tồn:", txtSoLuong,
                "Đơn vị tính:", txtDonVi,
                "Loại nguyên liệu:", cboLoai
            };

            int option = JOptionPane.showConfirmDialog(view, message, "Thêm nguyên liệu", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String ten = txtTen.getText().trim();
                String soLuongStr = txtSoLuong.getText().trim();
                String donVi = txtDonVi.getText().trim();
                String tenLoai = (String) cboLoai.getSelectedItem();

                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (donVi.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong < 0) {
                        JOptionPane.showMessageDialog(view, "Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer maLoai = getMaLoaiFromTen(tenLoai);
                NguyenLieu nguyenLieu = new NguyenLieu(ten, soLuong, donVi, maLoai);
                
                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn thêm nguyên liệu này?", "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = nguyenLieuService.addNguyenLieu(nguyenLieu);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Thêm nguyên liệu thành công!");
                        loadData();
                        loadLoaiNguyenLieu();
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNguyenLieu() {
        int selectedRow = view.getTblNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nguyên liệu để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNguyenLieu = (Integer) model.getValueAt(selectedRow, 0);
            String tenHienTai = (String) model.getValueAt(selectedRow, 1);
            Integer soLuongHienTai = (Integer) model.getValueAt(selectedRow, 2);
            String donViHienTai = (String) model.getValueAt(selectedRow, 3);
            String loaiHienTai = (String) model.getValueAt(selectedRow, 4);

            JTextField txtTen = new JTextField(tenHienTai);
            JTextField txtSoLuong = new JTextField(soLuongHienTai.toString());
            JTextField txtDonVi = new JTextField(donViHienTai);
            
            JComboBox<String> cboLoai = new JComboBox<>();
            cboLoai.addItem(""); // Trống
            List<LoaiNguyenLieu> listLoai = loaiNguyenLieuService.getAllLoaiNguyenLieu();
            for (LoaiNguyenLieu loaiNL : listLoai) {
                cboLoai.addItem(loaiNL.getTenLoaiNL());
                if (loaiNL.getTenLoaiNL().equals(loaiHienTai)) {
                    cboLoai.setSelectedItem(loaiNL.getTenLoaiNL());
                }
            }

            Object[] message = {
                "Tên nguyên liệu:", txtTen,
                "Số lượng tồn:", txtSoLuong,
                "Đơn vị tính:", txtDonVi,
                "Loại nguyên liệu:", cboLoai
            };

            int option = JOptionPane.showConfirmDialog(view, message, "Sửa nguyên liệu", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String ten = txtTen.getText().trim();
                String soLuongStr = txtSoLuong.getText().trim();
                String donVi = txtDonVi.getText().trim();
                String tenLoai = (String) cboLoai.getSelectedItem();

                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (donVi.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongStr);
                    if (soLuong < 0) {
                        JOptionPane.showMessageDialog(view, "Số lượng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view, "Số lượng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer maLoai = getMaLoaiFromTen(tenLoai);
                NguyenLieu nguyenLieu = new NguyenLieu(maNguyenLieu, ten, soLuong, donVi, maLoai);
                
                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn sửa nguyên liệu này?", "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = nguyenLieuService.updateNguyenLieu(nguyenLieu);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Sửa nguyên liệu thành công!");
                        loadData();
                        loadLoaiNguyenLieu();
                    } else {
                        JOptionPane.showMessageDialog(view, "Sửa nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNguyenLieu() {
        int selectedRow = view.getTblNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một nguyên liệu để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maNguyenLieu = (Integer) model.getValueAt(selectedRow, 0);
            String tenNguyenLieu = (String) model.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa nguyên liệu '" + tenNguyenLieu + "'?", "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = nguyenLieuService.deleteNguyenLieu(maNguyenLieu);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa nguyên liệu thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa nguyên liệu thất bại! Có thể đang có dữ liệu liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<NguyenLieu> list = nguyenLieuService.getAllNguyenLieu();
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                if (nl.getTenNguyenLieu().toLowerCase().contains(keyword.toLowerCase()) ||
                    (nl.getDonViTinh() != null && nl.getDonViTinh().toLowerCase().contains(keyword.toLowerCase()))) {
                    String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                    model.addRow(new Object[]{
                        nl.getMaNguyenLieu(),
                        nl.getTenNguyenLieu(),
                        nl.getSoLuongTon(),
                        nl.getDonViTinh(),
                        tenLoai,
                        nl.getMaLoaiNL()
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
            Integer maLoai = getMaLoaiFromTen(selectedLoai);
            if (maLoai == null) {
                loadData();
                return;
            }

            List<NguyenLieu> list = nguyenLieuService.getNguyenLieuByMaLoaiNL(maLoai);
            model.setRowCount(0);
            for (NguyenLieu nl : list) {
                String tenLoai = getTenLoaiNguyenLieu(nl.getMaLoaiNL());
                model.addRow(new Object[]{
                    nl.getMaNguyenLieu(),
                    nl.getTenNguyenLieu(),
                    nl.getSoLuongTon(),
                    nl.getDonViTinh(),
                    tenLoai,
                    nl.getMaLoaiNL()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi lọc theo loại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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