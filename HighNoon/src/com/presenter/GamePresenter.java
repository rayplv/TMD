package com.presenter;

import com.services.SoundManager;
import com.model.DatabaseManager;
import com.view.GameWindow;
import com.view.IGameView;

import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePresenter implements ActionListener {
    private IGameView view;
    private DatabaseManager dbManager;
    private GameWindow gameWindow;
    private Timer gameTimer;
    private String username;
    private boolean isRunning = false;

    // Game State
    private int score = 0;
    private int count = 0;
    private int playerX, playerY;
    private final int PLAYER_WIDTH = 60;
    private final int PLAYER_HEIGHT = 80; // Sesuaikan dengan aset Anda
    private final int PLAYER_SPEED = 4;
    private boolean moveLeft, moveRight, moveUp, moveDown;
    private boolean isFacingRight = true;

    // Player Animation
    private int currentPlayerFrame = 0;
    private final int totalPlayerFrames = 8;
    private int playerAnimationCounter = 0;
    private final int playerAnimationDelay = 5;

    // Game Objects
    private List<GameObjectPresenter> gameObjects;
    private Random random = new Random();
    private int spawnTimer = 0;
    private final int SPAWN_DELAY = 90;

    // Lasso
    private Line2D.Float lassoLine;
    private boolean isLassoVisible = false;
    private int lassoTimer = 0;
    private final int LASSO_VISIBLE_DURATION = 15;

    public GamePresenter(IGameView view, DatabaseManager dbManager, GameWindow gameWindow) {
        this.view = view;
        this.dbManager = dbManager;
        this.gameWindow = gameWindow;
        this.gameTimer = new Timer(16, this); // ~60 FPS
    }

    public void startGame(String username) {
        this.username = username;
        this.score = 0;
        this.count = 0;
        this.isRunning = true;
        this.playerX = (view.getGamePanelWidth() - PLAYER_WIDTH) / 2;
        this.playerY = (view.getGamePanelHeight() - PLAYER_HEIGHT) / 2;
        this.gameObjects = new ArrayList<>();
        this.moveLeft = this.moveRight = this.moveUp = this.moveDown = false;
        gameTimer.start();
    }

    public void stopGame() {
        isRunning = false;
        gameTimer.stop();
        if (score > 0 || count > 0) {
            dbManager.saveOrUpdateScore(username, score, count);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isRunning) return;
        updatePlayerMovement();
        updateGameObjects();
        updateLasso();
        spawnNewObject();
        view.setHUDInfo(username, score, count);
        view.repaintView();
    }

    private void updatePlayerMovement() {
        boolean isMoving = moveLeft || moveRight || moveUp || moveDown;
        if (isMoving) {
            playerAnimationCounter++;
            if (playerAnimationCounter > playerAnimationDelay) {
                playerAnimationCounter = 0;
                currentPlayerFrame = (currentPlayerFrame + 1) % totalPlayerFrames;
            }
        } else {
            currentPlayerFrame = 0;
        }

        if (moveLeft) { playerX -= PLAYER_SPEED; isFacingRight = false; }
        if (moveRight) { playerX += PLAYER_SPEED; isFacingRight = true; }
        if (moveUp) playerY -= PLAYER_SPEED;
        if (moveDown) playerY += PLAYER_SPEED;

        playerX = Math.max(0, Math.min(playerX, view.getGamePanelWidth() - PLAYER_WIDTH));
        playerY = Math.max(0, Math.min(playerY, view.getGamePanelHeight() - PLAYER_HEIGHT));
    }

    private void updateGameObjects() {
        Iterator<GameObjectPresenter> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObjectPresenter obj = iterator.next();
            obj.update(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

            if ((obj.speedX > 0 && obj.x > view.getGamePanelWidth()) || (obj.speedX < 0 && obj.x < -obj.width)) {
                iterator.remove();
            } else if (obj.isCaught && new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT).intersects(obj.getBounds())) {
                score += obj.scoreValue;
                count++;
                if (obj.isOutlaw) SoundManager.playSoundEffect("res/sounds/Scream.wav");
                else SoundManager.playSoundEffect("res/sounds/Boom.wav");
                iterator.remove();
            }
        }
    }

    private void updateLasso() {
        if (isLassoVisible) {
            lassoTimer++;
            if (lassoTimer > LASSO_VISIBLE_DURATION) {
                isLassoVisible = false;
                lassoLine = null;
                view.clearLasso();
            }
        }
    }

    private void spawnNewObject() {
        spawnTimer++;
        if (spawnTimer > SPAWN_DELAY) {
            spawnTimer = 0;
            boolean spawnOnTopLane = random.nextBoolean();
            int y;
            if (spawnOnTopLane) {
                y = random.nextInt(view.getGamePanelHeight() / 4);
            } else {
                y = view.getGamePanelHeight() - (view.getGamePanelHeight() / 4) + random.nextInt(view.getGamePanelHeight() / 4 - 60);
            }
            boolean startsLeft = random.nextBoolean();
            boolean isOutlaw = random.nextBoolean();
            gameObjects.add(new GameObjectPresenter(y, startsLeft, isOutlaw, view.getGamePanelWidth()));
        }
    }

    private void checkLassoCollision() {
        for (GameObjectPresenter obj : gameObjects) {
            if (!obj.isCaught && lassoLine.intersects(obj.getBounds())) {
                obj.isCaught = true;
                break;
            }
        }
    }

    // Methods to be called by View
    public void onKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_SPACE) {
            stopGame();
            gameWindow.showMenu();
            return;
        }
        if (keyCode == KeyEvent.VK_LEFT) moveLeft = true;
        if (keyCode == KeyEvent.VK_RIGHT) moveRight = true;
        if (keyCode == KeyEvent.VK_UP) moveUp = true;
        if (keyCode == KeyEvent.VK_DOWN) moveDown = true;
    }

    public void onKeyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) moveLeft = false;
        if (keyCode == KeyEvent.VK_RIGHT) moveRight = false;
        if (keyCode == KeyEvent.VK_UP) moveUp = false;
        if (keyCode == KeyEvent.VK_DOWN) moveDown = false;
    }

    public void onMousePressed(int x, int y) {
        if (isRunning && !isLassoVisible) {
            lassoLine = new Line2D.Float(playerX + PLAYER_WIDTH / 2, playerY + PLAYER_HEIGHT / 2, x, y);
            isLassoVisible = true;
            lassoTimer = 0;
            checkLassoCollision();
            view.drawLasso((int)lassoLine.x1, (int)lassoLine.y1, (int)lassoLine.x2, (int)lassoLine.y2);
        }
    }

    // Getters for the View to render
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getCurrentPlayerFrame() { return currentPlayerFrame; }
    public boolean isPlayerFacingRight() { return isFacingRight; }
    public List<GameObjectPresenter> getGameObjects() { return gameObjects; }
}

