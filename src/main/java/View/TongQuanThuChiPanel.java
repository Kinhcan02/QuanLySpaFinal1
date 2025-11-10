package View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TongQuanThuChiPanel extends JPanel {
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_PANEL = new Color(0xF0, 0xF8, 0xF0);

    // Components
    private JLabel lblTongThu, lblTongChi, lblLoiNhuan;
    private JSpinner spnThangTongQuan, spnNamTongQuan;
    private JButton btnThongKeTongQuan;
    private JTextArea txtBaoCao;

    public TongQuanThuChiPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Filter panel
        JPanel pnFilter = createFilterPanel();
        add(pnFilter, BorderLayout.NORTH);

        // Main content
        JPanel pnContent = createContentPanel();
        add(pnContent, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("TỔNG QUAN THU CHI - SPA/BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Lọc dữ liệu"));

        // Tháng
        panel.add(new JLabel("Tháng:"));
        spnThangTongQuan = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnThangTongQuan.setPreferredSize(new Dimension(60, 25));
        panel.add(spnThangTongQuan);

        // Năm
        panel.add(new JLabel("Năm:"));
        int currentYear = LocalDate.now().getYear();
        spnNamTongQuan = new JSpinner(new SpinnerNumberModel(currentYear, 2020, currentYear + 5, 1));
        spnNamTongQuan.setPreferredSize(new Dimension(80, 25));
        panel.add(spnNamTongQuan);

        // Nút thống kê
        btnThongKeTongQuan = createStyledButton("Thống kê tổng quan", COLOR_BUTTON);
        panel.add(btnThongKeTongQuan);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);

        // Summary cards
        JPanel pnCards = createSummaryCards();
        panel.add(pnCards, BorderLayout.NORTH);

        // Report area
        JPanel pnReport = createReportPanel();
        panel.add(pnReport, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryCards() {
        JPanel pnCards = new JPanel(new GridLayout(1, 3, 20, 20));
        pnCards.setBackground(COLOR_BACKGROUND);
        pnCards.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Thu nhập card
        JPanel cardThu = createSummaryCard("TỔNG THU NHẬP", "0 VND", COLOR_BUTTON);
        lblTongThu = (JLabel) ((JPanel) cardThu.getComponent(1)).getComponent(0);

        // Chi tiêu card
        JPanel cardChi = createSummaryCard("TỔNG CHI TIÊU", "0 VND", new Color(0xE7, 0x4C, 0x3C));
        lblTongChi = (JLabel) ((JPanel) cardChi.getComponent(1)).getComponent(0);

        // Lợi nhuận card
        JPanel cardLoiNhuan = createSummaryCard("LỢI NHUẬN", "0 VND", new Color(0x2E, 0xCC, 0x71));
        lblLoiNhuan = (JLabel) ((JPanel) cardLoiNhuan.getComponent(1)).getComponent(0);

        pnCards.add(cardThu);
        pnCards.add(cardChi);
        pnCards.add(cardLoiNhuan);

        return pnCards;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(color);
        valuePanel.add(lblValue, BorderLayout.CENTER);

        card.add(valuePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder("Báo cáo chi tiết"));

        txtBaoCao = new JTextArea();
        txtBaoCao.setEditable(false);
        txtBaoCao.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBaoCao.setBackground(Color.WHITE);
        txtBaoCao.setLineWrap(true);
        txtBaoCao.setWrapStyleWord(true);
        txtBaoCao.setText("Chọn tháng/năm và nhấn 'Thống kê tổng quan' để xem báo cáo...");

        JScrollPane scrollPane = new JScrollPane(txtBaoCao);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

    // Getter methods
    public JLabel getLblTongThu() { return lblTongThu; }
    public JLabel getLblTongChi() { return lblTongChi; }
    public JLabel getLblLoiNhuan() { return lblLoiNhuan; }
    public JSpinner getSpnThangTongQuan() { return spnThangTongQuan; }
    public JSpinner getSpnNamTongQuan() { return spnNamTongQuan; }
    public JButton getBtnThongKeTongQuan() { return btnThongKeTongQuan; }
    public JTextArea getTxtBaoCao() { return txtBaoCao; }
}