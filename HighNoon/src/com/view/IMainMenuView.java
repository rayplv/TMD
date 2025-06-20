package com.view;

import com.model.ScoreData;
import java.util.List;

public interface IMainMenuView {
    void showUsernameWarning(String message);
    void setScores(List<ScoreData> scores);
    GameWindow getGameWindow(); // Untuk navigasi
}