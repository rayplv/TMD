package com.view;

import com.model.ScoreData;
import com.presenter.MainMenuPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainMenuPanel extends JPanel implements IMainMenuView {
    private GameWindow gameWindow;
    private MainMenuPresenter presenter;
    private JTextField usernameField;
    private JTable scoreTable;
    private DefaultTableModel tableModel;

    public MainMenuPanel(GameWindow window) {
        this.gameWindow = window;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(210, 180, 140));

        // Panel Judul
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(210, 180, 140));
        JLabel titleLabel = new JLabel("High Noon");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panel Kontrol
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        controlPanel.setBackground(new Color(210, 180, 140));
        usernameField = new JTextField(15);
        JButton playButton = new JButton("Play");
        JButton quitButton = new JButton("Quit");
        controlPanel.add(new JLabel("Username:"));
        controlPanel.add(usernameField);
        controlPanel.add(playButton);
        controlPanel.add(quitButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Tabel Skor
        String[] columnNames = {"Rank", "Username", "Score", "Caught"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        add(scrollPane, BorderLayout.CENTER);

        // Aksi Tombol -> Delegasi ke Presenter
        playButton.addActionListener(e -> presenter.onPlayClicked(usernameField.getText()));
        quitButton.addActionListener(e -> presenter.onQuitClicked());
    }

    public void setPresenter(MainMenuPresenter presenter) {
        this.presenter = presenter;
        presenter.loadScores();
    }

    @Override
    public void showUsernameWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void setScores(List<ScoreData> scores) {
        tableModel.setRowCount(0); // Kosongkan tabel
        int rank = 1;
        for (ScoreData score : scores) {
            tableModel.addRow(new Object[]{rank++, score.getUsername(), score.getScore(), score.getCount()});
        }
    }

    @Override
    public GameWindow getGameWindow() {
        return this.gameWindow;
    }
}