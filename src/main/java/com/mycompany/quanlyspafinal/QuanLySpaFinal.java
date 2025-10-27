/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quanlyspafinal;

import Controller.AuthController;
import View.LoginView;
import javax.swing.SwingUtilities;

/**
 *
 * @author MINH MAN
 */
public class QuanLySpaFinal {

    public static void main(String[] args) {
        try {
            // Set system look and feel
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AuthController authController = new AuthController();
                authController.khoiDong();
            }
        });
    }
}
