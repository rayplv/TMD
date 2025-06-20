package com.view;

import com.model.DatabaseManager;
import com.model.ScoreData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainMenuPanel extends JPanel {
    private GameWindow gameWindow;
    private JTextField usernameField;
    private JTable scoreTable;
    private DefaultTableModel tableModel;

    public MainMenuPanel(GameWindow window) {
        this.gameWindow = window;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(210, 180, 140)); // Warna tema gurun

        // Panel Judul
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(210, 180, 140));
        JLabel titleLabel = new JLabel("High Noon");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panel Input dan Tombol
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        controlPanel.setBackground(new Color(210, 180, 140));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameField = new JTextField(15);
        JButton playButton = new JButton("Play");
        JButton quitButton = new JButton("Quit");
        controlPanel.add(usernameLabel);
        controlPanel.add(usernameField);
        controlPanel.add(playButton);
        controlPanel.add(quitButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Tabel Skor
        String[] columnNames = {"Rank", "Username", "Score", "Caught"};
        tableModel = new DefaultTableModel(columnNames, 0);
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        add(scrollPane, BorderLayout.CENTER);

        // Aksi Tombol
        playButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                gameWindow.showGame(username);
            }
        });

        quitButton.addActionListener(e -> System.exit(0));

        refreshScores();
    }

    // Metode untuk memuat ulang data skor dari database
    public void refreshScores() {
        tableModel.setRowCount(0); // Kosongkan tabel
        DatabaseManager dbManager = new DatabaseManager();
        List<ScoreData> scores = dbManager.getAllScores();
        int rank = 1;
        for (ScoreData score : scores) {
            tableModel.addRow(new Object[]{rank++, score.getUsername(), score.getScore(), score.getCount()});
        }
    }
}