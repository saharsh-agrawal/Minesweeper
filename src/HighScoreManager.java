import java.io.*;
import org.json.JSONObject;

public class HighScoreManager {
    private static final String HIGH_SCORE_DIR = System.getProperty("user.home") + File.separator + "Minesweeper";
    private static final File HIGH_SCORE_FILE = new File(HIGH_SCORE_DIR, "highScores.json");

    static {
        try {
            File dir = new File(HIGH_SCORE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!HIGH_SCORE_FILE.exists()) {
                try (FileWriter writer = new FileWriter(HIGH_SCORE_FILE)) {
                    writer.write("{}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readHighScores() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            return new JSONObject(jsonData.toString());
        } catch (IOException e) {
            return new JSONObject();
        }
    }

    public static void updateHighScore(String level, int currentScore) throws IOException {
        JSONObject highScores = readHighScores();
        int previousHighScore = highScores.optInt(level, Integer.MAX_VALUE);

        if (currentScore < previousHighScore) {
            highScores.put(level, currentScore);
            try (FileWriter writer = new FileWriter(HIGH_SCORE_FILE)) {
                writer.write(highScores.toString(4));
            }
            System.out.println("New high score for " + level + ": " + currentScore);
        } else {
            System.out.println("Score not a new high score for " + level + ": " + currentScore);
        }
    }

    public static int getHighScore(String level) throws IOException {
        JSONObject highScores = readHighScores();
        return highScores.optInt(level, 0);
    }
}
