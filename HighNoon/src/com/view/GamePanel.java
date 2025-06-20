package com.view;

import com.presenter.GamePresenter;
import com.presenter.GameObjectPresenter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements IGameView {
    private GamePresenter presenter;
    private String hudUsername = "";
    private int hudScore = 0, hudCount = 0;

    // Aset Gambar (tetap di View)
    private Image backgroundImg;
    private BufferedImage playerSpriteSheet;
    private BufferedImage outlawSpriteSheet;
    private BufferedImage zombieSpriteSheet;
    private Image nameSignImage;
    private Image scoreSignImage;
    private Font customFont;

    // Komponen visual
    private Line2D.Float lassoLine;

    public GamePanel() {
        loadAssets();
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (presenter != null) presenter.onKeyPressed(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (presenter != null) presenter.onKeyReleased(e.getKeyCode());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (presenter != null) presenter.onMousePressed(e.getX(), e.getY());
            }
        });
    }

    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
    }

    private void loadAssets() {
        try {
            backgroundImg = ImageIO.read(new File("res/images/background.png"));
            playerSpriteSheet = ImageIO.read(new File("res/images/CowBoyWalking.png"));
            outlawSpriteSheet = ImageIO.read(new File("res/images/CowboyAttackAnimation.png"));
            zombieSpriteSheet = ImageIO.read(new File("res/images/FunnelZombieWalk.png"));
            nameSignImage = ImageIO.read(new File("res/images/ForName.png"));
            scoreSignImage = ImageIO.read(new File("res/images/score_sign.png"));
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/VCR_OSD_MONO_1.001.ttf")).deriveFont(20f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Monospaced", Font.BOLD, 20);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (presenter == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);

        // Minta presenter untuk menggambar semua objek
        for (GameObjectPresenter obj : presenter.getGameObjects()) {
            drawGameObject(g2d, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), obj.getCurrentFrame(), obj.getFrameWidth(), obj.getFrameHeight(), obj.isFacingRight(), obj.isOutlaw(), this);
        }

        // Panggil drawPlayer dengan mengoper objek 'g' dari paintComponent
        drawPlayer(g, presenter.getPlayerX(), presenter.getPlayerY(), presenter.getCurrentPlayerFrame(), presenter.isPlayerFacingRight(), this);

        if (lassoLine != null) {
            g2d.setColor(new Color(139, 69, 19, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(lassoLine);
        }

        drawHUD(g2d);
    }

    // Metode ini sekarang private dan menerima Graphics sebagai argumen
    private void drawPlayer(Graphics g, int x, int y, int frame, boolean isFacingRight, ImageObserver observer) {
        if (playerSpriteSheet == null) return;
        int frameWidth = playerSpriteSheet.getWidth() / 8;
        int frameHeight = playerSpriteSheet.getHeight();
        int sx1 = frame * frameWidth;
        int sx2 = sx1 + frameWidth;
        int sy1 = 0;
        int sy2 = frameHeight;
        int player_width = 60;
        int player_height = (int) ((double) frameHeight / frameWidth * player_width);

        if (isFacingRight) {
            g.drawImage(playerSpriteSheet, x, y, x + player_width, y + player_height, sx1, sy1, sx2, sy2, observer);
        } else {
            g.drawImage(playerSpriteSheet, x + player_width, y, x, y + player_height, sx1, sy1, sx2, sy2, observer);
        }
    }

    private void drawHUD(Graphics2D g2d) {
        if (nameSignImage != null) {
            int nameSignWidth = 150, nameSignHeight = 75;
            g2d.drawImage(nameSignImage, 10, 10, nameSignWidth, nameSignHeight, this);
            g2d.setFont(customFont.deriveFont(16f));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(hudUsername);
            g2d.drawString(hudUsername, 10 + (nameSignWidth - stringWidth) / 2, 57);
        }

        if (scoreSignImage != null) {
            int scoreSignWidth = 180;
            int scoreSignHeight = (int) (scoreSignWidth * ((double) scoreSignImage.getHeight(null) / scoreSignImage.getWidth(null)));
            int scoreSignX = getWidth() - scoreSignWidth - 20;
            int scoreSignY = (getHeight() - scoreSignHeight) / 2;
            g2d.drawImage(scoreSignImage, scoreSignX, scoreSignY, scoreSignWidth, scoreSignHeight, this);
            g2d.setFont(customFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Score: " + hudScore, scoreSignX + 40, scoreSignY + (int)(scoreSignHeight * 0.45));
            g2d.drawString("Caught: " + hudCount, scoreSignX + 40, scoreSignY + (int)(scoreSignHeight * 0.6));
        }
    }

    // Implementasi IGameView
    @Override
    public void repaintView() {
        this.repaint();
    }

    @Override
    public void drawGameObject(Graphics2D g, int x, int y, int width, int height, int frame, int frameWidth, int frameHeight, boolean isFacingRight, boolean isOutlaw, ImageObserver observer) {
        BufferedImage spriteSheet = isOutlaw ? outlawSpriteSheet : zombieSpriteSheet;
        if (spriteSheet == null) return;

        int sx1 = frame * (spriteSheet.getWidth() / 5);
        int sx2 = sx1 + (spriteSheet.getWidth() / 5);
        int sy1 = 0;
        int sy2 = spriteSheet.getHeight();

        if (isFacingRight) {
            g.drawImage(spriteSheet, x, y, x + width, y + height, sx1, sy1, sx2, sy2, observer);
        } else {
            g.drawImage(spriteSheet, x + width, y, x, y + height, sx1, sy1, sx2, sy2, observer);
        }
    }

    @Override
    public void drawLasso(int startX, int startY, int endX, int endY) {
        this.lassoLine = new Line2D.Float(startX, startY, endX, endY);
    }

    @Override
    public void clearLasso() {
        this.lassoLine = null;
    }

    @Override
    public void setHUDInfo(String username, int score, int count) {
        this.hudUsername = username;
        this.hudScore = score;
        this.hudCount = count;
    }

    @Override
    public void requestViewFocus() {
        requestFocusInWindow();
    }

    @Override
    public int getGamePanelWidth() {
        return getWidth();
    }

    @Override
    public int getGamePanelHeight() {
        return getHeight();
    }
}