package Controller;

import Model.LoaiNguyenLieu;
import Service.LoaiNguyenLieuService;
import View.QuanLyLoaiNguyenLieuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class QuanLyLoaiNguyenLieuController {
    private QuanLyLoaiNguyenLieuView view;
    private LoaiNguyenLieuService service;
    private DefaultTableModel model;

    public QuanLyLoaiNguyenLieuController(QuanLyLoaiNguyenLieuView view) {
        this.view = view;
        this.service = new LoaiNguyenLieuService();
        this.model = view.getModel();
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnThem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                themLoaiNguyenLieu();
            }
        });

        view.getBtnSua().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suaLoaiNguyenLieu();
            }
        });

        view.getBtnXoa().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xoaLoaiNguyenLieu();
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

        view.getBtnDong().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dong();
            }
        });
    }

    private void loadData() {
        try {
            List<LoaiNguyenLieu> list = service.getAllLoaiNguyenLieu();
            model.setRowCount(0);
            for (LoaiNguyenLieu loaiNL : list) {
                model.addRow(new Object[]{
                    loaiNL.getMaLoaiNL(),
                    loaiNL.getTenLoaiNL(),
                    loaiNL.getMoTa()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themLoaiNguyenLieu() {
        try {
            JTextField txtTen = new JTextField();
            JTextArea txtMoTa = new JTextArea(3, 20);
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);

            Object[] message = {
                "Tên loại nguyên liệu:", txtTen,
                "Mô tả:", scrollMoTa
            };

            int option = JOptionPane.showConfirmDialog(view, message, "Thêm loại nguyên liệu", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String ten = txtTen.getText().trim();
                String moTa = txtMoTa.getText().trim();

                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên loại nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LoaiNguyenLieu loaiNL = new LoaiNguyenLieu(ten, moTa);
                
                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn thêm loại nguyên liệu này?", "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = service.addLoaiNguyenLieu(loaiNL);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Thêm loại nguyên liệu thành công!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Thêm loại nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaLoaiNguyenLieu() {
        int selectedRow = view.getTblLoaiNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một loại nguyên liệu để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maLoaiNL = (Integer) model.getValueAt(selectedRow, 0);
            String tenHienTai = (String) model.getValueAt(selectedRow, 1);
            String moTaHienTai = (String) model.getValueAt(selectedRow, 2);

            JTextField txtTen = new JTextField(tenHienTai);
            JTextArea txtMoTa = new JTextArea(moTaHienTai, 3, 20);
            JScrollPane scrollMoTa = new JScrollPane(txtMoTa);

            Object[] message = {
                "Tên loại nguyên liệu:", txtTen,
                "Mô tả:", scrollMoTa
            };

            int option = JOptionPane.showConfirmDialog(view, message, "Sửa loại nguyên liệu", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String ten = txtTen.getText().trim();
                String moTa = txtMoTa.getText().trim();

                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên loại nguyên liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LoaiNguyenLieu loaiNL = new LoaiNguyenLieu(maLoaiNL, ten, moTa);
                
                int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn có chắc chắn muốn sửa loại nguyên liệu này?", "Xác nhận", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = service.updateLoaiNguyenLieu(loaiNL);
                    if (success) {
                        JOptionPane.showMessageDialog(view, "Sửa loại nguyên liệu thành công!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Sửa loại nguyên liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLoaiNguyenLieu() {
        int selectedRow = view.getTblLoaiNguyenLieu().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một loại nguyên liệu để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer maLoaiNL = (Integer) model.getValueAt(selectedRow, 0);
            String tenLoaiNL = (String) model.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa loại nguyên liệu '" + tenLoaiNL + "'?", "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = service.deleteLoaiNguyenLieu(maLoaiNL);
                if (success) {
                    JOptionPane.showMessageDialog(view, "Xóa loại nguyên liệu thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa loại nguyên liệu thất bại! Có thể đang có nguyên liệu thuộc loại này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xóa loại nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<LoaiNguyenLieu> list = service.getAllLoaiNguyenLieu();
            model.setRowCount(0);
            for (LoaiNguyenLieu loaiNL : list) {
                if (loaiNL.getTenLoaiNL().toLowerCase().contains(keyword.toLowerCase()) ||
                    (loaiNL.getMoTa() != null && loaiNL.getMoTa().toLowerCase().contains(keyword.toLowerCase()))) {
                    model.addRow(new Object[]{
                        loaiNL.getMaLoaiNL(),
                        loaiNL.getTenLoaiNL(),
                        loaiNL.getMoTa()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        view.getTxtTimKiem().setText("");
        loadData();
    }

    private void dong() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
        if (frame != null) {
            frame.dispose();
        }
    }
}