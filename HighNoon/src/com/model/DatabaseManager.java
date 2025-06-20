package com.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // --- GANTI DETAIL INI SESUAI KONFIGURASI MYSQL ANDA ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_highscore";
    private static final String DB_USER = "root"; // ganti jika user Anda bukan root
    private static final String DB_PASSWORD = ""; // ganti dengan password Anda

    // Metode untuk mendapatkan koneksi
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Mengambil semua skor untuk ditampilkan di tabel
    public List<ScoreData> getAllScores() {
        List<ScoreData> scores = new ArrayList<>();
        String sql = "SELECT username, score, count FROM highscores ORDER BY score DESC LIMIT 100";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ScoreData scoreData = new ScoreData();
                scoreData.setUsername(rs.getString("username"));
                scoreData.setScore(rs.getInt("score"));
                scoreData.setCount(rs.getInt("count"));
                scores.add(scoreData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    // Menyimpan atau memperbarui skor pemain menggunakan sintaks MySQL
    public void saveOrUpdateScore(String username, int finalScore, int finalCount) {
        String sql = "INSERT INTO highscores (username, score, count) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = score + VALUES(score), count = count + VALUES(count)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, finalScore);
            pstmt.setInt(3, finalCount);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}