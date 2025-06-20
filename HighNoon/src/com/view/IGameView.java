package com.view;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public interface IGameView {
    void repaintView();
    // Deklarasi drawPlayer() dihapus dari sini
    void drawGameObject(Graphics2D g, int x, int y, int width, int height, int frame, int frameWidth, int frameHeight, boolean isFacingRight, boolean isOutlaw, ImageObserver observer);
    void drawLasso(int startX, int startY, int endX, int endY);
    void clearLasso();
    void setHUDInfo(String username, int score, int count);
    void requestViewFocus();
    int getGamePanelWidth();
    int getGamePanelHeight();
}