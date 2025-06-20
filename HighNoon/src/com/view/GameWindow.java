package com.view;

import com.services.SoundManager;
import com.model.DatabaseManager;
import com.presenter.GamePresenter;
import com.presenter.MainMenuPresenter;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private GamePresenter gamePresenter;
    private MainMenuPresenter mainMenuPresenter;

    public GameWindow() {
        setTitle("High Noon");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi Model
        DatabaseManager dbManager = new DatabaseManager();

        // Inisialisasi Views
        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel();

        // Inisialisasi Presenter dan hubungkan ke View
        mainMenuPresenter = new MainMenuPresenter(mainMenuPanel, dbManager);
        mainMenuPanel.setPresenter(mainMenuPresenter);

        gamePresenter = new GamePresenter(gamePanel, dbManager, this);
        gamePanel.setPresenter(gamePresenter);

        mainPanel.add(mainMenuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
        setVisible(true);

        SoundManager.playMusic("res/sounds/MainTheme.wav");
    }

    public void showMenu() {
        gamePresenter.stopGame();
        cardLayout.show(mainPanel, "MENU");
        mainMenuPresenter.loadScores();
        SoundManager.playMusic("res/sounds/MainTheme.wav");
    }

    public void showGame(String username) {
        // SoundManager.stopMusic(); // Opsional
        gamePresenter.startGame(username);
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }
}