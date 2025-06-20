package com.main;

import com.view.GameWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow(); // Membuat dan menampilkan jendela utama
        });
    }
}