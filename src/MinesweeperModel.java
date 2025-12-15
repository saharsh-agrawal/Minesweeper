import java.util.Date;
import java.util.Random;

public class MinesweeperModel {

    private final char difficulty;
    private final int cols; // columns (a)
    private final int rows; // rows (b)
    private final int mineCount;

    private int revealedCount;
    private int flaggedCount;

    private final int[][] mines;
    private final int[][] neighbours;
    private final boolean[][] revealed;
    private final boolean[][] flagged;

    private GameState gameState;
    private Date startDate;
    private int elapsedSeconds;

    public MinesweeperModel(char difficulty) {
        this.difficulty = difficulty;
        switch (difficulty) {
            case 'e':
                cols = 10;
                rows = 8;
                mineCount = 10;
                break;
            case 'm':
                cols = 20;
                rows = 12;
                mineCount = 40;
                break;
            case 'h':
                cols = 30;
                rows = 16;
                mineCount = 99;
                break;
            default:
                throw new IllegalArgumentException("Unsupported difficulty: " + difficulty);
        }

        mines = new int[cols][rows];
        neighbours = new int[cols][rows];
        revealed = new boolean[cols][rows];
        flagged = new boolean[cols][rows];

        restart();
    }

    public void restart() {
        gameState = GameState.OPEN;
        revealedCount = 0;
        flaggedCount = 0;
        elapsedSeconds = 0;
        startDate = new Date();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                mines[i][j] = 0;
                neighbours[i][j] = 0;
                revealed[i][j] = false;
                flagged[i][j] = false;
            }
        }

        setMines();
    }

    private void setMines() {
        Random rand = new Random();
        int count = 0;
        while (count < mineCount) {
            int i = (int) (rand.nextDouble() * cols);
            int j = (int) (rand.nextDouble() * rows);
            if (mines[i][j] == 1) {
                continue;
            }
            mines[i][j] = 1;
            count++;
        }

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int neighs = 0;

                if (i != 0 && j != 0) neighs += mines[i - 1][j - 1];
                if (i != 0) neighs += mines[i - 1][j];
                if (i != 0 && j != (rows - 1)) neighs += mines[i - 1][j + 1];

                if (j != 0) neighs += mines[i][j - 1];
                if (j != (rows - 1)) neighs += mines[i][j + 1];

                if (i != (cols - 1) && j != 0) neighs += mines[i + 1][j - 1];
                if (i != (cols - 1)) neighs += mines[i + 1][j];
                if (i != (cols - 1) && j != (rows - 1)) neighs += mines[i + 1][j + 1];

                neighbours[i][j] = neighs;
            }
        }
    }

    public void flag(int x, int y) {
        if (gameState != GameState.OPEN) {
            return;
        }
        if (!revealed[x][y]) {
            if (!flagged[x][y]) {
                flagged[x][y] = true;
                flaggedCount++;
            } else {
                flagged[x][y] = false;
                flaggedCount--;
            }
        }
    }

    public void reveal(int x, int y) {
        if (gameState != GameState.OPEN) {
            return;
        }
        if (!flagged[x][y] && !revealed[x][y]) {
            revealed[x][y] = true;
            revealedCount++;
            if (mines[x][y] == 1) {
                gameState = GameState.LOST;
                return;
            }
            if (neighbours[x][y] == 0) {
                revealNeighbours(x, y);
            }
            checkVictory();
        }
    }

    public void chord(int x, int y) {
        if (gameState != GameState.OPEN) {
            return;
        }
        if (revealed[x][y]) {
            int count = 0;
            if (x != 0 && y != 0 && flagged[x - 1][y - 1]) count++;
            if (x != 0 && flagged[x - 1][y]) count++;
            if (x != 0 && y != (rows - 1) && flagged[x - 1][y + 1]) count++;

            if (y != 0 && flagged[x][y - 1]) count++;
            if (y != (rows - 1) && flagged[x][y + 1]) count++;

            if (x != (cols - 1) && y != 0 && flagged[x + 1][y - 1]) count++;
            if (x != (cols - 1) && flagged[x + 1][y]) count++;
            if (x != (cols - 1) && y != (rows - 1) && flagged[x + 1][y + 1]) count++;

            if (count == neighbours[x][y]) {
                revealNeighbours(x, y);
                checkVictory();
            }
        }
    }

    private void revealNeighbours(int x, int y) {
        if (x != 0 && y != 0) reveal(x - 1, y - 1);
        if (x != 0) reveal(x - 1, y);
        if (x != 0 && y != (rows - 1)) reveal(x - 1, y + 1);

        if (y != 0) reveal(x, y - 1);
        if (y != (rows - 1)) reveal(x, y + 1);

        if (x != (cols - 1) && y != 0) reveal(x + 1, y - 1);
        if (x != (cols - 1)) reveal(x + 1, y);
        if (x != (cols - 1) && y != (rows - 1)) reveal(x + 1, y + 1);
    }

    private void checkVictory() {
        if (cols * rows - revealedCount == mineCount && gameState == GameState.OPEN) {
            gameState = GameState.WON;
        }
    }

    public void updateElapsedTime() {
        if (gameState == GameState.OPEN) {
            elapsedSeconds = (int) ((new Date().getTime() - startDate.getTime()) / 1000);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public char getDifficulty() {
        return difficulty;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getMineCount() {
        return mineCount;
    }

    public int getRevealedCount() {
        return revealedCount;
    }

    public int getFlaggedCount() {
        return flaggedCount;
    }

    public int getMinesLeft() {
        return mineCount - flaggedCount;
    }

    public boolean isRevealed(int x, int y) {
        return revealed[x][y];
    }

    public boolean isFlagged(int x, int y) {
        return flagged[x][y];
    }

    public boolean isMine(int x, int y) {
        return mines[x][y] == 1;
    }

    public int getNeighbourCount(int x, int y) {
        return neighbours[x][y];
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }
}
