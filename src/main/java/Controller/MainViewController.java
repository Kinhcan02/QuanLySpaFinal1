package Controller;

import View.MainView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class MainViewController implements ActionListener {

    private MainView mainView;

    public MainViewController(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == mainView.getBtnThongBao()) {
            hienThiThongBao("Tính năng Thông báo đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnQuanLyNguyenLieu()) {
            hienThiThongBao("Tính năng Quản lý nguyên liệu đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnDatDichVu()) {
            hienThiThongBao("Tính năng Đặt dịch vụ đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnQuanLyNhanVien()) {
            mainView.showQuanLyNhanVien();
        } 
        else if (source == mainView.getBtnQuanLyKhachHang()) {
            mainView.showQuanLyKhachHang();
        } 
        else if (source == mainView.getBtnQuanLyDichVu()) {
            mainView.showQuanLyDichVu();
        } 
        else if (source == mainView.getBtnThongKe()) {
            hienThiThongBao("Tính năng Thống kê đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnCaiDat()) {
            hienThiThongBao("Tính năng Cài đặt đang phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } 
        else if (source == mainView.getBtnThoat()) {
            mainView.xacNhanThoatChuongTrinh();
        }
    }

    private void hienThiThongBao(String message, String title, int messageType) {
        mainView.hienThiThongBao(message, title, messageType);
    }
}