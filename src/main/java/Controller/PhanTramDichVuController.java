package Controller;

import Model.PhanTramDichVu;
import Model.NhanVien;
import Model.LoaiDichVu;
import Service.PhanTramDichVuService;
import Service.NhanVienService;
import Service.LoaiDichVuService;
import View.PhanTramDichVuView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PhanTramDichVuController {

    private final PhanTramDichVuView view;
    private final PhanTramDichVuService phanTramService;
    private final NhanVienService nhanVienService;
    private final LoaiDichVuService loaiDichVuService;
    private Integer currentMaNhanVien;

    // Map để lưu trữ mapping giữa tên và mã
    private Map<String, Integer> nhanVienMap;
    private Map<String, Integer> loaiDichVuMap;

    public PhanTramDichVuController(PhanTramDichVuView view) {
        this(view, null);
    }

    public PhanTramDichVuController(PhanTramDichVuView view, Integer maNhanVien) {
        this.view = view;
        this.currentMaNhanVien = maNhanVien;
        this.phanTramService = new PhanTramDichVuService();
        this.nhanVienService = new NhanVienService();
        this.loaiDichVuService = new LoaiDichVuService();
        this.nhanVienMap = new HashMap<>();
        this.loaiDichVuMap = new HashMap<>();

        initController();
        loadComboBoxData();
        if (maNhanVien != null) {
            loadPhanTramByNhanVien(maNhanVien);
        } else {
            loadAllPhanTramDichVu();
        }
    }

    private void initController() {
        // Sự kiện cho nút Thêm mới
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themPhanTramDichVu();
            }
        });

        // Sự kiện cho nút Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaPhanTramDichVu();
            }
        });

        // Sự kiện cho nút Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaPhanTramDichVu();
            }
        });

        // Sự kiện cho nút Làm mới
        view.getBtnLamMoi().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                if (currentMaNhanVien != null) {
                    loadPhanTramByNhanVien(currentMaNhanVien);
                } else {
                    loadAllPhanTramDichVu();
                }
            }
        });

        // Sự kiện cho nút Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemPhanTramDichVu();
            }
        });

        // Sự kiện click trên bảng
        view.getTblPhanTram().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiThongTinPhanTram();
            }
        });
    }

    private void loadComboBoxData() {
        try {
            // Load danh sách nhân viên - sử dụng Map với key là "Mã NV - Tên NV"
            List<NhanVien> nhanViens = nhanVienService.getAllNhanVien();
            nhanVienMap.clear();

            for (NhanVien nv : nhanViens) {
                // Sử dụng format "Mã NV - Tên NV" để đảm bảo duy nhất
                String key = nv.getMaNhanVien() + " - " + nv.getHoTen();
                nhanVienMap.put(key, nv.getMaNhanVien());
            }

            // Load danh sách loại dịch vụ
            List<LoaiDichVu> loaiDichVus = loaiDichVuService.getAllLoaiDichVu();
            view.getCboLoaiDichVu().removeAllItems();
            loaiDichVuMap.clear();

            for (LoaiDichVu ldv : loaiDichVus) {
                String tenLoaiDichVu = ldv.getTenLoaiDV();
                view.getCboLoaiDichVu().addItem(tenLoaiDichVu);
                loaiDichVuMap.put(tenLoaiDichVu, ldv.getMaLoaiDV());
            }

            // Nếu có currentMaNhanVien, tự động hiển thị "Mã NV - Tên NV"
            if (currentMaNhanVien != null) {
                String tenNhanVien = getTenNhanVien(currentMaNhanVien);
                if (tenNhanVien != null && !tenNhanVien.equals("N/A")) {
                    String displayText = currentMaNhanVien + " - " + tenNhanVien;
                    view.getTxtNhanVien().setText(displayText);
                }
            }

        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu combobox: " + e.getMessage());
        }
    }

    private void loadAllPhanTramDichVu() {
        try {
            List<PhanTramDichVu> list = phanTramService.getAllPhanTramDichVu();
            hienThiDanhSachPhanTramDichVu(list);
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách phần trăm dịch vụ: " + e.getMessage());
        }
    }

    private void loadPhanTramByNhanVien(int maNhanVien) {
        try {
            List<PhanTramDichVu> list = phanTramService.getPhanTramByNhanVien(maNhanVien);
            hienThiDanhSachPhanTramDichVu(list);
        } catch (Exception e) {
            showError("Lỗi khi tải phần trăm dịch vụ theo nhân viên: " + e.getMessage());
        }
    }

    private void hienThiDanhSachPhanTramDichVu(List<PhanTramDichVu> list) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        for (PhanTramDichVu pt : list) {
            // Lấy thông tin tên nhân viên và tên loại dịch vụ
            String tenNhanVien = getTenNhanVien(pt.getMaNhanVien());
            String tenLoaiDichVu = getTenLoaiDichVu(pt.getMaLoaiDV());

            Object[] row = {
                pt.getMaPhanTram(),
                pt.getMaNhanVien() + " - " + tenNhanVien, // Hiển thị cả mã và tên
                tenLoaiDichVu,
                String.format("%.1f%%", pt.getTiLePhanTram())
            };
            model.addRow(row);
        }
    }

    private String getTenNhanVien(Integer maNhanVien) {
        try {
            if (maNhanVien != null) {
                NhanVien nv = nhanVienService.getNhanVienById(maNhanVien);
                return nv != null ? nv.getHoTen() : "N/A";
            }
        } catch (Exception e) {
            // Không hiển thị lỗi để tránh làm gián đoạn
        }
        return "N/A";
    }

    private String getTenLoaiDichVu(Integer maLoaiDV) {
        try {
            if (maLoaiDV != null) {
                LoaiDichVu ldv = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
                return ldv != null ? ldv.getTenLoaiDV() : "N/A";
            }
        } catch (Exception e) {
            // Không hiển thị lỗi để tránh làm gián đoạn
        }
        return "N/A";
    }

    private void themPhanTramDichVu() {
        try {
            PhanTramDichVu pt = layThongTinPhanTramTuForm();
            if (pt == null) {
                return;
            }

            if (phanTramService.addPhanTramDichVu(pt)) {
                showSuccess("Thêm phần trăm dịch vụ thành công!");
                lamMoiForm();
                if (currentMaNhanVien != null) {
                    loadPhanTramByNhanVien(currentMaNhanVien);
                } else {
                    loadAllPhanTramDichVu();
                }
            } else {
                showError("Thêm phần trăm dịch vụ thất bại!");
            }
        } catch (Exception e) {
            showError("Lỗi khi thêm phần trăm dịch vụ: " + e.getMessage());
        }
    }

    private void suaPhanTramDichVu() {
        try {
            String maPTStr = view.getTxtMaPhanTram().getText().trim();
            if (maPTStr.isEmpty()) {
                showError("Vui lòng chọn phần trăm dịch vụ cần sửa!");
                return;
            }

            int maPhanTram = Integer.parseInt(maPTStr);
            PhanTramDichVu pt = layThongTinPhanTramTuForm();
            if (pt == null) {
                return;
            }

            pt.setMaPhanTram(maPhanTram);

            if (phanTramService.updatePhanTramDichVu(pt)) {
                showSuccess("Cập nhật phần trăm dịch vụ thành công!");
                lamMoiForm();
                if (currentMaNhanVien != null) {
                    loadPhanTramByNhanVien(currentMaNhanVien);
                } else {
                    loadAllPhanTramDichVu();
                }
            } else {
                showError("Cập nhật phần trăm dịch vụ thất bại!");
            }
        } catch (Exception e) {
            showError("Lỗi khi cập nhật phần trăm dịch vụ: " + e.getMessage());
        }
    }

    private void xoaPhanTramDichVu() {
        try {
            String maPTStr = view.getTxtMaPhanTram().getText().trim();
            if (maPTStr.isEmpty()) {
                showError("Vui lòng chọn phần trăm dịch vụ cần xóa!");
                return;
            }

            int maPhanTram = Integer.parseInt(maPTStr);
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Bạn có chắc chắn muốn xóa phần trăm dịch vụ này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (phanTramService.deletePhanTramDichVu(maPhanTram)) {
                    showSuccess("Xóa phần trăm dịch vụ thành công!");
                    lamMoiForm();
                    if (currentMaNhanVien != null) {
                        loadPhanTramByNhanVien(currentMaNhanVien);
                    } else {
                        loadAllPhanTramDichVu();
                    }
                } else {
                    showError("Xóa phần trăm dịch vụ thất bại!");
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi xóa phần trăm dịch vụ: " + e.getMessage());
        }
    }

    private void timKiemPhanTramDichVu() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim().toLowerCase();
            List<PhanTramDichVu> ketQua;

            if (currentMaNhanVien != null) {
                ketQua = phanTramService.getPhanTramByNhanVien(currentMaNhanVien);
            } else {
                ketQua = phanTramService.getAllPhanTramDichVu();
            }

            if (!tuKhoa.isEmpty()) {
                final String keyword = tuKhoa;
                ketQua = ketQua.stream()
                        .filter(pt -> {
                            String tenNV = getTenNhanVien(pt.getMaNhanVien()).toLowerCase();
                            String maVaTen = pt.getMaNhanVien() + " - " + tenNV;
                            String tenLoaiDV = getTenLoaiDichVu(pt.getMaLoaiDV()).toLowerCase();
                            String tiLe = String.format("%.1f", pt.getTiLePhanTram());
                            return maVaTen.toLowerCase().contains(keyword)
                                    || tenLoaiDV.contains(keyword)
                                    || tiLe.contains(keyword);
                        })
                        .toList();
            }

            hienThiDanhSachPhanTramDichVu(ketQua);
            if (ketQua.isEmpty()) {
                showInfo("Không tìm thấy phần trăm dịch vụ phù hợp!");
            }
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void hienThiThongTinPhanTram() {
        int selectedRow = view.getTblPhanTram().getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = view.getModel();
            view.getTxtMaPhanTram().setText(model.getValueAt(selectedRow, 0).toString());

            // Lấy thông tin từ bảng
            String maVaTenNhanVien = model.getValueAt(selectedRow, 1).toString();
            String tenLoaiDichVu = model.getValueAt(selectedRow, 2).toString();
            String tiLeStr = model.getValueAt(selectedRow, 3).toString();

            // Set selected item cho textfield và combobox
            view.getTxtNhanVien().setText(maVaTenNhanVien);
            view.getCboLoaiDichVu().setSelectedItem(tenLoaiDichVu);

            // Xử lý tỉ lệ phần trăm (bỏ ký tự %)
            if (tiLeStr.endsWith("%")) {
                tiLeStr = tiLeStr.substring(0, tiLeStr.length() - 1);
            }
            view.getTxtTiLePhanTram().setText(tiLeStr.trim());
        }
    }

    private PhanTramDichVu layThongTinPhanTramTuForm() {
        try {
            String nhanVienText = view.getTxtNhanVien().getText().trim();
            String tenLoaiDichVu = (String) view.getCboLoaiDichVu().getSelectedItem();
            String tiLePhanTramStr = view.getTxtTiLePhanTram().getText().trim();

            // Validation
            if (nhanVienText == null || nhanVienText.isEmpty()) {
                showError("Vui lòng chọn nhân viên!");
                return null;
            }

            if (tenLoaiDichVu == null || tenLoaiDichVu.isEmpty()) {
                showError("Vui lòng chọn loại dịch vụ!");
                return null;
            }

            if (tiLePhanTramStr.isEmpty()) {
                showError("Vui lòng nhập tỉ lệ phần trăm!");
                return null;
            }

            // Lấy mã nhân viên từ text (format: "Mã NV - Tên NV")
            Integer maNhanVien = null;
            if (currentMaNhanVien != null) {
                // Nếu đang filter theo nhân viên cụ thể, sử dụng currentMaNhanVien
                maNhanVien = currentMaNhanVien;
            } else {
                // Nếu không filter, phân tích từ text field
                if (nhanVienText.contains("-")) {
                    try {
                        String maStr = nhanVienText.split("-")[0].trim();
                        maNhanVien = Integer.parseInt(maStr);
                    } catch (Exception e) {
                        showError("Định dạng nhân viên không hợp lệ!");
                        return null;
                    }
                } else {
                    // Fallback: tìm trong map (có thể không chính xác nếu trùng tên)
                    maNhanVien = nhanVienMap.get(nhanVienText);
                }
            }

            Integer maLoaiDV = loaiDichVuMap.get(tenLoaiDichVu);

            if (maNhanVien == null) {
                showError("Không tìm thấy mã nhân viên từ: " + nhanVienText);
                return null;
            }

            if (maLoaiDV == null) {
                showError("Không tìm thấy loại dịch vụ: " + tenLoaiDichVu);
                return null;
            }

            // Parse và validate tỉ lệ phần trăm
            double tiLePhanTram;
            try {
                tiLePhanTram = Double.parseDouble(tiLePhanTramStr);
            } catch (NumberFormatException e) {
                showError("Tỉ lệ phần trăm phải là số hợp lệ!");
                return null;
            }

            if (tiLePhanTram < 0 || tiLePhanTram > 100) {
                showError("Tỉ lệ phần trăm phải từ 0 đến 100!");
                return null;
            }

            // Kiểm tra xem đã tồn tại phần trăm dịch vụ cho nhân viên và loại dịch vụ này chưa
            try {
                PhanTramDichVu existing = phanTramService.getPhanTramByNhanVienAndLoaiDV(maNhanVien, maLoaiDV);
                if (existing != null) {
                    String maPTStr = view.getTxtMaPhanTram().getText().trim();
                    // Nếu đang thêm mới hoặc sửa bản ghi khác
                    if (maPTStr.isEmpty() || existing.getMaPhanTram() != Integer.parseInt(maPTStr)) {
                        String tenNhanVienChinhXac = getTenNhanVien(maNhanVien);
                        showError("Đã tồn tại phần trăm dịch vụ cho nhân viên '" + tenNhanVienChinhXac
                                + "' (Mã NV: " + maNhanVien + ") và loại dịch vụ '" + tenLoaiDichVu + "'");
                        return null;
                    }
                }
            } catch (Exception e) {
                // Bỏ qua lỗi kiểm tra trùng
            }

            return new PhanTramDichVu(null, maLoaiDV, maNhanVien, tiLePhanTram);

        } catch (Exception e) {
            showError("Lỗi dữ liệu: " + e.getMessage());
            return null;
        }
    }

    private void lamMoiForm() {
        view.getTxtMaPhanTram().setText("");
        view.getTxtTiLePhanTram().setText("");
        view.getTxtTimKiem().setText("");

        // Reset combobox nhưng vẫn giữ nhân viên nếu đang filter
        if (view.getCboLoaiDichVu().getItemCount() > 0) {
            view.getCboLoaiDichVu().setSelectedIndex(0);
        }

        // Chỉ reset textfield nhân viên nếu không đang filter theo nhân viên cụ thể
        if (currentMaNhanVien == null) {
            view.getTxtNhanVien().setText("");
        } else {
            // Nếu đang filter theo nhân viên, vẫn giữ nhân viên đó được hiển thị
            String tenNhanVien = getTenNhanVien(currentMaNhanVien);
            if (tenNhanVien != null && !tenNhanVien.equals("N/A")) {
                String displayText = currentMaNhanVien + " - " + tenNhanVien;
                view.getTxtNhanVien().setText(displayText);
            }
        }

        view.getTblPhanTram().clearSelection();
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getter để có thể truy cập từ bên ngoài nếu cần
    public Integer getCurrentMaNhanVien() {
        return currentMaNhanVien;
    }

    public void setCurrentMaNhanVien(Integer maNhanVien) {
        this.currentMaNhanVien = maNhanVien;
        if (maNhanVien != null) {
            loadPhanTramByNhanVien(maNhanVien);
            loadComboBoxData(); // Reload để cập nhật combobox
        } else {
            loadAllPhanTramDichVu();
            loadComboBoxData(); // Reload để cập nhật combobox
        }
    }
}