package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ThongBaoView extends JInternalFrame {
    private JList<String> listThongBao;
    private DefaultListModel<String> listModel;
    private JButton btnDong, btnXemTatCa, btnDanhDauDaDoc;
    private JLabel lblBadge;
    private JPanel mainPanel;

    // Màu sắc giống QuanLyTaiKhoanView
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public ThongBaoView() {
        super("Thông Báo", true, true, true, true);
        initComponents();
        setupUI();
    }

    private void initComponents() {
        // Kích thước bằng QuanLyTaiKhoanView
        setSize(1200, 750);
        
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel tiêu đề giống QuanLyTaiKhoanView
        JPanel headerPanel = createTitlePanel();

        // Danh sách thông báo với kích thước lớn hơn
        listModel = new DefaultListModel<>();
        listThongBao = new JList<>(listModel);
        listThongBao.setFont(new Font("Arial", Font.PLAIN, 14));
        listThongBao.setBackground(Color.WHITE);
        listThongBao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(listThongBao);
        scrollPane.setPreferredSize(new Dimension(1150, 600)); // Kích thước lớn
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BUTTON, 2), 
            "Thông báo hệ thống",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            COLOR_BUTTON
        ));

        // Panel nút bấm
        JPanel buttonPanel = createButtonPanel();

        // Thêm components vào main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel(new BorderLayout());
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG THÔNG BÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);

        // Tạo badge và thêm vào góc phải
        lblBadge = new JLabel("0");
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(Color.RED);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblBadge.setFont(new Font("Arial", Font.BOLD, 14));
        lblBadge.setVisible(false);
        lblBadge.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgePanel.setBackground(COLOR_BUTTON);
        badgePanel.add(lblBadge);

        pnTitle.add(lblTitle, BorderLayout.WEST);
        pnTitle.add(badgePanel, BorderLayout.EAST);

        return pnTitle;
    }

    private JPanel createButtonPanel() {
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnButton.setBackground(COLOR_BACKGROUND);
        pnButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnDanhDauDaDoc = createStyledButton("Đánh dấu đã đọc", COLOR_BUTTON);
        btnXemTatCa = createStyledButton("Xem chi tiết", COLOR_BUTTON);
        btnDong = createStyledButton("Đóng", COLOR_BUTTON);

        pnButton.add(btnDanhDauDaDoc);
        pnButton.add(btnXemTatCa);
        pnButton.add(btnDong);

        return pnButton;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void setupUI() {
        setFrameIcon(null);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        
        // Thêm listener để xử lý khi đóng
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                // Chỉ ẩn đi thay vì đóng hoàn toàn
                setVisible(false);
            }
        });
    }

    public void hienThi() {
        setVisible(true);
        toFront();
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public void anDi() {
        setVisible(false);
    }

    public void capNhatDanhSachThongBao(String[] thongBao) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String tb : thongBao) {
                listModel.addElement("• " + tb);
            }
        });
    }

    public void xoaTatCaThongBao() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            anBadge();
        });
    }

    public void hienThiThongBao(String thongBao) {
        SwingUtilities.invokeLater(() -> {
            listModel.addElement("• " + thongBao);
        });
    }

    public void hienThiBadge(int soLuong) {
        SwingUtilities.invokeLater(() -> {
            if (soLuong > 0) {
                lblBadge.setText(String.valueOf(soLuong));
                lblBadge.setVisible(true);
            } else {
                anBadge();
            }
        });
    }

    public void anBadge() {
        SwingUtilities.invokeLater(() -> {
            lblBadge.setVisible(false);
        });
    }

    public void showInternalFrame(JInternalFrame internalFrame) {
        Container parent = getParent();
        if (parent instanceof JDesktopPane) {
            JDesktopPane desktopPane = (JDesktopPane) parent;
            desktopPane.add(internalFrame);
            internalFrame.setVisible(true);
            try {
                internalFrame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDangHienThi() {
        return isVisible();
    }

    // GETTER METHODS
    public JButton getBtnDong() { return btnDong; }
    public JButton getBtnXemTatCa() { return btnXemTatCa; }
    public JButton getBtnDanhDauDaDoc() { return btnDanhDauDaDoc; }
    public JList<String> getListThongBao() { return listThongBao; }
    public DefaultListModel<String> getListModel() { return listModel; }
}