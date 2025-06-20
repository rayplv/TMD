package com.view;

import com.controller.GameLogic;
import com.controller.SoundManager;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private GameLogic gameLogic;

    public GameWindow() {
        // Pengaturan dasar JFrame
        setTitle("High Noon");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Tampilkan di tengah layar
        setResizable(false);

        // Menggunakan CardLayout untuk beralih antara menu dan game
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi panel-panel
        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        gameLogic = new GameLogic(gamePanel); // GameLogic mengontrol GamePanel

        // Menambahkan panel ke CardLayout
        mainPanel.add(mainMenuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
        setVisible(true);

        // Mulai musik di menu
        SoundManager.playMusic("res/sounds/MainTheme.wav");
    }

    // Metode untuk beralih ke panel menu
    public void showMenu() {
        gameLogic.stopGame();
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.refreshScores(); // Refresh tabel skor setiap kembali ke menu
        SoundManager.playMusic("res/sounds/MainTheme.wav");
    }

    // Metode untuk beralih ke panel game
    public void showGame(String username) {
        // SoundManager.stopMusic();
        gamePanel.startGame(username);
        gameLogic.startGame();
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow(); // Agar input keyboard langsung aktif
    }
}