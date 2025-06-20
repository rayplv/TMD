package com.controller;

import com.view.GamePanel;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLogic implements ActionListener {
    private GamePanel gamePanel;
    private Timer gameTimer;
    private final int DELAY = 16; // Delay untuk sekitar 60 FPS (1000/60)

    public GameLogic(GamePanel panel) {
        this.gamePanel = panel;
        gameTimer = new Timer(DELAY, this);
    }

    public void startGame() {
        gameTimer.start();
    }

    public void stopGame() {
        gameTimer.stop();
    }

    // Metode ini akan dipanggil setiap kali timer berdetak
    @Override
    public void actionPerformed(ActionEvent e) {
        gamePanel.updateGame(); // Perbarui logika game
        gamePanel.repaint();    // Gambar ulang panel game
    }
}