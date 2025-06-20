package com.presenter;

import com.model.DatabaseManager;
import com.model.ScoreData;
import com.view.IMainMenuView;
import javax.swing.JOptionPane;
import java.util.List;

public class MainMenuPresenter {
    private IMainMenuView view;
    private DatabaseManager dbManager;

    public MainMenuPresenter(IMainMenuView view, DatabaseManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
    }

    public void onPlayClicked(String username) {
        if (username == null || username.trim().isEmpty()) {
            view.showUsernameWarning("Username tidak boleh kosong!");
        } else {
            view.getGameWindow().showGame(username);
        }
    }

    public void onQuitClicked() {
        System.exit(0);
    }

    public void loadScores() {
        List<ScoreData> scores = dbManager.getAllScores();
        view.setScores(scores);
    }
}