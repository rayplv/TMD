package com.presenter;

import java.awt.Rectangle;
import java.util.Random;

public class GameObjectPresenter {
    // Variabel dengan akses package-private agar bisa diakses oleh GamePresenter
    int x, y, width, height, scoreValue;
    double speedX;
    int currentFrame = 0, totalFrames, animationDelay, animationCounter = 0;
    boolean isCaught = false;
    boolean isOutlaw;

    // 'random' bisa tetap private karena hanya digunakan di dalam kelas ini
    private Random random = new Random();

    public GameObjectPresenter(int y, boolean startsLeft, boolean isOutlaw, int panelWidth) {
        this.y = y;
        this.isOutlaw = isOutlaw;
        this.currentFrame = 0; // Inisialisasi frame
        this.animationCounter = 0; // Inisialisasi counter animasi

        if (isOutlaw) {
            this.scoreValue = 10;
            this.totalFrames = 5;
            this.animationDelay = 10;
            this.width = 80;
            this.height = 80;
        } else {
            this.scoreValue = -5;
            this.totalFrames = 5;
            this.animationDelay = 8;
            this.width = 70;
            this.height = 70;
        }

        if (startsLeft) {
            this.x = -this.width;
            this.speedX = random.nextDouble() * 2 + 1;
        } else {
            this.x = panelWidth;
            this.speedX = -(random.nextDouble() * 2 + 1);
        }
    }

    public void update(int playerX, int playerY, int playerWidth, int playerHeight) {
        if (isCaught) {
            double targetX = playerX + playerWidth / 2.0;
            double targetY = playerY + playerHeight / 2.0;
            double dx = targetX - (this.x + this.width / 2.0);
            double dy = targetY - (this.y + this.height / 2.0);
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 5) {
                this.x += (dx / dist) * 4;
                this.y += (dy / dist) * 4;
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

    // --- METODE GETTER PUBLIK UNTUK VIEW ---
    // Metode ini bisa diakses dari mana saja, termasuk GamePanel

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isFacingRight() {
        return speedX > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public boolean isOutlaw() {
        return isOutlaw;
    }

    public int getFrameWidth() {
        // Sesuaikan nilai ini dengan ukuran frame sebenarnya di sprite sheet Anda
        return isOutlaw ? 96 : 80;
    }

    public int getFrameHeight() {
        // Sesuaikan nilai ini dengan ukuran frame sebenarnya di sprite sheet Anda
        return isOutlaw ? 96 : 80;
    }
}