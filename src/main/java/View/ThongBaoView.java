package View;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ThongBaoView extends JPanel {
    private JList<String> listThongBao;
    private DefaultListModel<String> listModel;
    private JButton btnXemTatCa, btnDanhDauDaDoc;
    private JLabel lblBadge;

    // Màu sắc giống QuanLyCaLamView
    private final Color COLOR_BACKGROUND = new Color(0xF0, 0xF8, 0xF0);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_BORDER = new Color(0x4D, 0x8A, 0x57);

    public ThongBaoView() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel tiêu đề
        JPanel headerPanel = createTitlePanel();
        
        // Danh sách thông báo
        listModel = new DefaultListModel<>();
        listThongBao = new JList<>(listModel);
        listThongBao.setFont(new Font("Arial", Font.PLAIN, 12));
        listThongBao.setBackground(Color.WHITE);
        listThongBao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listThongBao.setSelectionBackground(COLOR_BUTTON);
        listThongBao.setSelectionForeground(COLOR_TEXT);

        JScrollPane scrollPane = new JScrollPane(listThongBao);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            "Thông báo hệ thống",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            COLOR_BORDER
        ));

        // Panel nút bấm
        JPanel buttonPanel = createButtonPanel();

        // Thêm components vào main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel(new BorderLayout());
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("HỆ THỐNG THÔNG BÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT);

        // Tạo badge
        lblBadge = new JLabel("0");
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(Color.RED);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        lblBadge.setFont(new Font("Arial", Font.BOLD, 12));
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
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnButton.setBackground(COLOR_BACKGROUND);
        pnButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnDanhDauDaDoc = createStyledButton("Đánh dấu đã đọc", COLOR_BUTTON);
        btnXemTatCa = createStyledButton("Xem chi tiết", COLOR_BUTTON);

        pnButton.add(btnDanhDauDaDoc);
        pnButton.add(btnXemTatCa);

        return pnButton;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
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
        // Các thiết lập giao diện khác nếu cần
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

    // GETTER METHODS
    public JButton getBtnXemTatCa() { return btnXemTatCa; }
    public JButton getBtnDanhDauDaDoc() { return btnDanhDauDaDoc; }
    public JList<String> getListThongBao() { return listThongBao; }
    public DefaultListModel<String> getListModel() { return listModel; }
}