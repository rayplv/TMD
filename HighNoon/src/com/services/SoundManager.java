package com.services; // Ganti dengan package Anda

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private static Clip musicClip;

    // Metode untuk musik latar (looping)
    public static void playMusic(String filePath) {
        if (musicClip != null && musicClip.isRunning()) {
            // Jika musik yang sama sudah berjalan, jangan lakukan apa-apa
            // Ini untuk mencegah musik dimulai ulang saat kembali ke menu
            // Anda bisa hapus kondisi ini jika ingin musik selalu restart
            return;
        }
        stopMusic(); // Hentikan musik lama sebelum memulai yang baru

        try {
            File musicFile = new File(filePath);
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();
            } else {
                System.err.println("File musik tidak ditemukan: " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    // --- METODE BARU UNTUK EFEK SUARA ---
    public static void playSoundEffect(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioStream);
                soundClip.start(); // Putar sekali saja
            } else {
                System.err.println("File efek suara tidak ditemukan: " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}