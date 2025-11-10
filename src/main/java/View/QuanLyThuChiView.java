package View;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class QuanLyThuChiView extends JPanel {

    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    private JTabbedPane tabbedPane;
    private QuanLyThuNhapPanel thuNhapPanel;
    private QuanLyChiTieuPanel chiTieuPanel;
    private TongQuanThuChiPanel tongQuanPanel;

    public QuanLyThuChiView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);

        // Title panel
        JPanel pnTitle = createTitlePanel();
        add(pnTitle, BorderLayout.NORTH);

        // Main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BACKGROUND);
        tabbedPane.setForeground(COLOR_TEXT);

        // Tabs
        thuNhapPanel = new QuanLyThuNhapPanel();
        chiTieuPanel = new QuanLyChiTieuPanel();
        tongQuanPanel = new TongQuanThuChiPanel();

        tabbedPane.addTab("QUẢN LÝ THU NHẬP", thuNhapPanel);
        tabbedPane.addTab("QUẢN LÝ CHI TIÊU", chiTieuPanel);
        tabbedPane.addTab("TỔNG QUAN THU CHI", tongQuanPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(COLOR_BUTTON);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ THU CHI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT);
        pnTitle.add(lblTitle);

        return pnTitle;
    }

    // Getter methods
    public QuanLyThuNhapPanel getThuNhapPanel() {
        return thuNhapPanel;
    }

    public QuanLyChiTieuPanel getChiTieuPanel() {
        return chiTieuPanel;
    }

    public TongQuanThuChiPanel getTongQuanPanel() {
        return tongQuanPanel;
    }
}