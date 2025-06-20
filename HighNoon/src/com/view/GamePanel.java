package com.view; // Ganti dengan package Anda yang benar

import com.model.DatabaseManager; // Ganti dengan package Anda yang benar
import com.controller.SoundManager; // Pastikan import SoundManager ada
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private GameWindow gameWindow;
    private String username;

    // Aset Gambar
    private Image backgroundImg;
    private BufferedImage playerSpriteSheet;
    private BufferedImage outlawSpriteSheet;
    private BufferedImage zombieSpriteSheet;
    private Image nameSignImage;
    private Image scoreSignImage;

    // Aset Font Kustom
    private Font customFont;

    // Status Game
    private boolean isRunning = false;
    private int score = 0;
    private int count = 0;

    // Objek Pemain
    private int playerX, playerY;
    private int PLAYER_WIDTH = 60;
    private int PLAYER_HEIGHT;
    private final int PLAYER_SPEED = 4;

    // Kontrol Gerakan
    private boolean moveLeft, moveRight, moveUp, moveDown;
    private boolean isFacingRight = true;

    // Variabel Animasi Pemain
    private int playerFrameWidth, playerFrameHeight;
    private int currentPlayerFrame = 0;
    private final int totalPlayerFrames = 8;
    private int playerAnimationCounter = 0;
    private final int playerAnimationDelay = 5;

    // Objek Target
    private List<GameObject> gameObjects;
    private Random random = new Random();
    private int spawnTimer = 0;
    private final int SPAWN_DELAY = 90;

    // Mekanisme Lasso
    private Line2D.Float lassoLine;
    private boolean isLassoVisible = false;
    private int lassoTimer = 0;
    private final int LASSO_VISIBLE_DURATION = 15;

    public GamePanel(GameWindow window) {
        this.gameWindow = window;
        loadAssets();
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    endGame();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight = true;
                if (e.getKeyCode() == KeyEvent.VK_UP) moveUp = true;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) moveDown = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft = false;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight = false;
                if (e.getKeyCode() == KeyEvent.VK_UP) moveUp = false;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) moveDown = false;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isRunning && !isLassoVisible) {
                    lassoLine = new Line2D.Float(playerX + PLAYER_WIDTH / 2, playerY + PLAYER_HEIGHT / 2, e.getX(), e.getY());
                    isLassoVisible = true;
                    lassoTimer = 0;
                    checkLassoCollision();
                }
            }
        });
    }

    private void loadAssets() {
        try {
            backgroundImg = ImageIO.read(new File("res/images/background.png"));
            playerSpriteSheet = ImageIO.read(new File("res/images/CowBoyWalking.png"));
            outlawSpriteSheet = ImageIO.read(new File("res/images/CowboyAttackAnimation.png"));
            zombieSpriteSheet = ImageIO.read(new File("res/images/FunnelZombieWalk.png"));
            nameSignImage = ImageIO.read(new File("res/images/ForName.png"));
            scoreSignImage = ImageIO.read(new File("res/images/score_sign.png"));

            playerFrameWidth = playerSpriteSheet.getWidth() / totalPlayerFrames;
            playerFrameHeight = playerSpriteSheet.getHeight();
            PLAYER_HEIGHT = (int) ((double) playerFrameHeight / playerFrameWidth * PLAYER_WIDTH);

            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/VCR_OSD_MONO_1.001.ttf")).deriveFont(20f);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Monospaced", Font.BOLD, 20);
        }
    }

    public void startGame(String username) {
        this.username = username;
        this.score = 0;
        this.count = 0;
        this.isRunning = true;
        playerX = (getWidth() - PLAYER_WIDTH) / 2;
        playerY = (getHeight() - PLAYER_HEIGHT) / 2;
        gameObjects = new ArrayList<>();
    }

    public void endGame() {
        if (!isRunning) return;
        isRunning = false;
        if (score != 0 || count != 0) {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.saveOrUpdateScore(username, score, count);
        }
        gameWindow.showMenu();
    }

    public void updateGame() {
        if (!isRunning) return;

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

        if (moveLeft) {
            playerX -= PLAYER_SPEED;
            isFacingRight = false;
        }
        if (moveRight) {
            playerX += PLAYER_SPEED;
            isFacingRight = true;
        }
        if (moveUp) playerY -= PLAYER_SPEED;
        if (moveDown) playerY += PLAYER_SPEED;

        playerX = Math.max(0, Math.min(playerX, getWidth() - PLAYER_WIDTH));
        playerY = Math.max(0, Math.min(playerY, getHeight() - PLAYER_HEIGHT));

        if (isLassoVisible) {
            lassoTimer++;
            if (lassoTimer > LASSO_VISIBLE_DURATION) {
                isLassoVisible = false;
                lassoLine = null;
            }
        }

        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            obj.update(playerX, playerY);

            if ((obj.speedX > 0 && obj.x > getWidth()) || (obj.speedX < 0 && obj.x < -obj.width)) {
                iterator.remove();
            } else if (obj.isCaught && obj.getBounds().intersects(new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT))) {
                score += obj.scoreValue;
                count++;

                // Memainkan efek suara berdasarkan jenis objek
                if (obj.isOutlaw) {
                    SoundManager.playSoundEffect("res/sounds/Scream.wav");
                } else {
                    SoundManager.playSoundEffect("res/sounds/Boom.wav");
                }

                iterator.remove();
            }
        }

        spawnTimer++;
        if (spawnTimer > SPAWN_DELAY) {
            spawnTimer = 0;
            spawnNewObject();
        }
    }

    private void spawnNewObject() {
        boolean spawnOnTopLane = random.nextBoolean();
        int y;
        if (spawnOnTopLane) {
            y = random.nextInt(getHeight() / 4);
        } else {
            y = getHeight() - (getHeight() / 4) + random.nextInt(getHeight() / 4 - 60);
        }

        boolean startsLeft = random.nextBoolean();
        boolean isOutlaw = random.nextBoolean();
        gameObjects.add(new GameObject(y, startsLeft, isOutlaw));
    }

    private void checkLassoCollision() {
        for (GameObject obj : gameObjects) {
            if (!obj.isCaught && lassoLine.intersects(obj.getBounds())) {
                obj.isCaught = true;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);

        for (GameObject obj : gameObjects) {
            obj.draw(g2d, this);
        }

        if (playerSpriteSheet != null) {
            int sx1 = currentPlayerFrame * playerFrameWidth;
            int sy1 = 0;
            int sx2 = sx1 + playerFrameWidth;
            int sy2 = playerFrameHeight;

            if (isFacingRight) {
                g.drawImage(playerSpriteSheet, playerX, playerY, playerX + PLAYER_WIDTH, playerY + PLAYER_HEIGHT, sx1, sy1, sx2, sy2, this);
            } else {
                g.drawImage(playerSpriteSheet, playerX + PLAYER_WIDTH, playerY, playerX, playerY + PLAYER_HEIGHT, sx1, sy1, sx2, sy2, this);
            }
        }

        if (isLassoVisible && lassoLine != null) {
            g2d.setColor(new Color(139, 69, 19, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(lassoLine);
        }

        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        if (nameSignImage != null) {
            int nameSignWidth = 150;
            int nameSignHeight = 75;
            g2d.drawImage(nameSignImage, 10, 10, nameSignWidth, nameSignHeight, this);

            g2d.setFont(customFont.deriveFont(16f));
            g2d.setColor(Color.WHITE);

            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(username);
            g2d.drawString(username, 10 + (nameSignWidth - stringWidth) / 2, 57);
        }

        if (scoreSignImage != null) {
            int scoreSignWidth = 180;
            double aspectRatio = (double) scoreSignImage.getHeight(null) / scoreSignImage.getWidth(null);
            int scoreSignHeight = (int) (scoreSignWidth * aspectRatio);
            int scoreSignX = getWidth() - scoreSignWidth - 20;
            int scoreSignY = (getHeight() - scoreSignHeight) / 2;
            g2d.drawImage(scoreSignImage, scoreSignX, scoreSignY, scoreSignWidth, scoreSignHeight, this);

            g2d.setFont(customFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Score: " + score, scoreSignX + 40, scoreSignY + (int)(scoreSignHeight * 0.45));
            g2d.drawString("Caught: " + count, scoreSignX + 40, scoreSignY + (int)(scoreSignHeight * 0.6));
        }
    }

    private class GameObject {
        int x, y, width, height, scoreValue;
        double speedX;
        BufferedImage spriteSheet;
        int currentFrame = 0, totalFrames, frameWidth, frameHeight;
        int animationCounter = 0, animationDelay;
        boolean isCaught = false;
        boolean isOutlaw;

        GameObject(int y, boolean startsLeft, boolean isOutlaw) {
            this.y = y;
            this.isOutlaw = isOutlaw;

            if (isOutlaw) {
                this.spriteSheet = outlawSpriteSheet;
                this.scoreValue = 10;
                this.totalFrames = 5;
                this.animationDelay = 10;
                this.width = 80;
                this.height = 80;
            } else {
                this.spriteSheet = zombieSpriteSheet;
                this.scoreValue = -5;
                this.animationDelay = 8;
                this.totalFrames = 5;
                this.width = 70;
                this.height = 70;
            }

            if (startsLeft) {
                this.x = -this.width;
                this.speedX = random.nextDouble() * 2 + 1;
            } else {
                this.x = getWidth();
                this.speedX = -(random.nextDouble() * 2 + 1);
            }

            this.frameWidth = this.spriteSheet.getWidth() / this.totalFrames;
            this.frameHeight = this.spriteSheet.getHeight();
        }

        void update(int playerX, int playerY) {
            if (isCaught) {
                double targetX = playerX + PLAYER_WIDTH / 2.0;
                double targetY = playerY + PLAYER_HEIGHT / 2.0;
                double dx = targetX - (this.x + this.width / 2.0);
                double dy = targetY - (this.y + this.height / 2.0);
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > 5) {
                    this.x += (dx / distance) * 4;
                    this.y += (dy / distance) * 4;
                }
            } else {
                this.x += this.speedX;
            }

            animationCounter++;
            if (animationCounter > animationDelay) {
                animationCounter = 0;
                currentFrame = (currentFrame + 1) % totalFrames;
            }
        }

        void draw(Graphics2D g, ImageObserver observer) {
            if (spriteSheet != null) {
                int sx1 = currentFrame * frameWidth;
                int sy1 = 0;
                int sx2 = sx1 + frameWidth;
                int sy2 = frameHeight;

                // Arah hadap ditentukan oleh kecepatan horizontalnya
                boolean facingRight = speedX > 0;
                if(facingRight) {
                    g.drawImage(spriteSheet, x, y, x + width, y + height, sx1, sy1, sx2, sy2, observer);
                } else {
                    g.drawImage(spriteSheet, x + width, y, x, y + height, sx1, sy1, sx2, sy2, observer);
                }
            }
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }
}