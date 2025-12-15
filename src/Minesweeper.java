public class Minesweeper {

        public static void main(String[] args)
        {
		    new LevelChoser().setVisible(true);
        }

        public Minesweeper(char difficulty)
        {
		    new GUI(difficulty);
        }

}