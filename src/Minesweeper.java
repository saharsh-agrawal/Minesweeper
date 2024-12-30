public class Minesweeper implements Runnable{

    GUI gui;

    public static void main(String[] args)
    {
    	new LevelChoser().setVisible(true);
    }

    public Minesweeper(char difficulty)
    {
    	gui=new GUI(difficulty);
    }

    @Override
    public void run() {
        // repaint time every second
        while (true){
            gui.repaint(gui.timeX,gui.timeY,100,100);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}