import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame{
    
	private final MinesweeperModel model;
    private Board board;
	
	private final char difficulty;
    private int a; // columns
	private int b; // rows
	private int l; // size of cell
	private int spacing; // spacing
	private int mineCount; // no of mines
	
	private int fontSize;
	private int cellPadding;
	
	private int titleBar=31;
    private int gap=57;
	
    int mx=-100;
    int my=-100;
    
    int msgX;
    int msgY;
    
    int autoPlayX;
    int autoPlayY;
    boolean autoPlay;
    
    int smileyX;
    int smileyY;
    
    int minesLeftX;
    int minesLeftY;
    
    int timeX;
    int timeY;

	private javax.swing.Timer timer;
    private javax.swing.Timer autoPlayTimer;
    private Point lastAutoMoveCell;
    private AutoMoveType lastAutoMoveType;
    
    public GUI(char diff){
        
        model = new MinesweeperModel(diff);
        difficulty=diff;
        a = model.getCols();
        b = model.getRows();
        mineCount = model.getMineCount();

        switch(difficulty)
        {
            case 'e':
                l=70;
                spacing=3;
                fontSize=40;
                cellPadding=23;
                break;
            case 'm':
                l=50;
                spacing=2;
                fontSize=30;
                cellPadding=17;
                break;
            case 'h':
                l=35;
                spacing=2;
                fontSize=20;
                cellPadding=11;
                break;
            default:
                this.dispose();
        }
        
        msgX=15;
        msgY=-100;
        autoPlayX=180;
        autoPlayY=4;
        
        smileyX=(a*l)/2-25;
        smileyY=2;
        
        minesLeftX=a*l-313;
        minesLeftY=40;
        timeX=a*l-105;
        timeY=4;
       
        restart();
		startTimer();
        
        this.setTitle("Minesweeper");
        this.setSize(a*l+16,b*l+95);
        // Main game window: closing it should exit the application
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        
        board=new Board();
        this.setContentPane(board);
        
        Move move=new Move();
        this.addMouseMotionListener(move);
        
        Click click=new Click();
        this.addMouseListener(click);
    }
    
    public class Board extends JPanel{
        @Override
        public void paintComponent(Graphics g){
            // game board
        	g.setColor(Color.DARK_GRAY);
            g.fillRect(0,0,a*l,b*l+gap);
            
            // msgbox
            String msg="";
            switch(model.getGameState())
            {
            	case WON:
            		msg="YOU WIN!";
            		g.setColor(Color.green);
            		msgY=40;
            		break;
            	case LOST:
            		msg="YOU LOST!";
                	g.setColor(Color.red);
                	msgY=40;
                	break;
            }
            g.setFont(new Font("Tahoma",Font.BOLD,30));
            g.drawString(msg,msgX,msgY);
            
            
            // autoplay button
            g.setColor(Color.black);
            g.setFont(new Font("Tahoma",Font.BOLD,16));
            g.fillRect(autoPlayX,autoPlayY,130,50);
            g.setColor(Color.white);
            g.drawString("Auto Play: " + (autoPlay ? "ON" : "OFF"),autoPlayX+6,autoPlayY+30);
            
            
            // smiley painting
            g.setColor(Color.yellow);
            g.fillOval(smileyX,smileyY,50,50);
            g.setColor(Color.black);
            g.fillOval(smileyX+10,smileyY+12,10,10);
            g.fillOval(smileyX+30,smileyY+12,10,10);
            if(model.getGameState()==GameState.LOST)
            {
                g.fillRect(smileyX+12,smileyY+32,26,5);
                
                g.fillRect(smileyX+11,smileyY+34,5,5);
                g.fillRect(smileyX+34,smileyY+34,5,5);
                
                g.fillRect(smileyX+10,smileyY+36,5,5);
                g.fillRect(smileyX+35,smileyY+36,5,5);
            }
            else if(model.getGameState()==GameState.WON || model.getGameState()==GameState.OPEN)
            {
                g.fillRect(smileyX+12,smileyY+36,26,5);

                g.fillRect(smileyX+11,smileyY+34,5,5);
                g.fillRect(smileyX+34,smileyY+34,5,5);
                
                g.fillRect(smileyX+10,smileyY+32,5,5);
                g.fillRect(smileyX+35,smileyY+32,5,5);
            }
            
            // mines left display
            g.setColor(Color.white);
            g.setFont(new Font("Tahoma",Font.BOLD,28));
            g.drawString("Mines left: "+model.getMinesLeft(),minesLeftX,minesLeftY);
            
            // timer display
            g.setColor(Color.black);
            g.fillRect(timeX,timeY,100,50);
            model.updateElapsedTime();
			int sec = model.getElapsedSeconds();
			String time=sec+"";
            if(sec<10) time="00"+time;
            if(sec<100 && sec>=10) time="0"+time;
            if(sec>999) time="999";
            
            g.setColor(Color.white);
            if(model.getGameState()==GameState.WON) g.setColor(Color.green);
            if(model.getGameState()==GameState.LOST) g.setColor(Color.red);
            g.setFont(new Font("Tahoma",Font.BOLD,40));
            g.drawString(time,timeX+16,timeY+40);
            
            // divider line
            g.setColor(Color.gray);
            g.fillRect(0,gap-1,a*l,1);
            
            // a by b will be the grid...each box l*l pixels with 'spacing' padding within
            // cells
            for(int i=0;i<a;i++){
                for(int j=0;j<b;j++){
                    
                    // background
                    g.setColor(new Color(10,110,210));
                    
                    if(model.isRevealed(i,j))
                    {
                        g.setColor(Color.white);
                        if(model.isMine(i,j))
                            g.setColor(Color.red);
                    }
                    
                    // highlight last auto-play move
                    if(lastAutoMoveCell != null && lastAutoMoveCell.x==i && lastAutoMoveCell.y==j)
                    {
                        g.setColor(Color.YELLOW);
                        // if(lastAutoMoveType==AutoMoveType.FLAG)
                        //     g.setColor(Color.ORANGE);
                        // else
                        //     g.setColor(Color.YELLOW);
                        // g.drawRect(l*i+spacing,j*l+spacing+gap, l-2*spacing-1, l-2*spacing-1);
                    }

                    // background when hover
                    if(mx>=l*i+spacing && mx<l*i+l-spacing && my>=j*l+spacing+gap+titleBar && my<j*l+l-spacing+gap+titleBar)
                        g.setColor(Color.LIGHT_GRAY);
                    
                    g.fillRect(l*i+spacing,j*l+spacing+gap, l-2*spacing, l-2*spacing);
                    
                    // flags
                    if(model.isFlagged(i,j))
                    {
                    	g.setColor(Color.red);
                        g.fillRect(l*i+spacing+30*l/70,j*l+spacing+gap+7*l/70,30*l/70,20*l/70);
                        g.setColor(Color.black);
                        g.fillRect(l*i+spacing+25*l/70,j*l+spacing+gap+7*l/70,5*l/70,50*l/70);
                        g.fillRect(l*i+spacing+15*l/70,j*l+spacing+gap+57*l/70,30*l/70,5*l/70);
                    }
                    
                    // number or mine
                    if(model.isRevealed(i,j))
                    {
                        if(!model.isMine(i,j) && model.getNeighbourCount(i,j)!=0)
                        {
                            switch(model.getNeighbourCount(i,j))
                            {
                            	case 1:
                            		g.setColor(Color.blue);
                            		break;
                            	case 2:
                            		g.setColor(Color.green);
                            		break;
                            	case 3:
                                    g.setColor(Color.red);
                            		break;
                            	case 4:
                            		g.setColor(new Color(0,0,128));
                            		break;
                            	case 5:
                            		g.setColor(new Color(178,34,34));
                            		break;
                            	case 6:
                            		g.setColor(new Color(72,209,204));
                            		break;
                            	case 7:
                            		g.setColor(Color.black);
                            		break;
                            	case 8:
                            		g.setColor(Color.darkGray);
                            		break;
                            }
                            g.setFont(new Font("Tahoma",Font.BOLD,fontSize));
                            g.drawString(model.getNeighbourCount(i,j)+"",l*i+spacing+cellPadding,j*l+spacing+gap+2*cellPadding);
                        }
                        else if(model.isMine(i,j)){
                            g.setColor(Color.black);
                            g.fillRect(l*i+spacing+25*l/70,j*l+spacing+gap+25*l/70,20*l/70,20*l/70);
                            
                            g.fillRect(l*i+spacing+22*l/70,j*l+spacing+gap+28*l/70,26*l/70,14*l/70);
                            g.fillRect(l*i+spacing+28*l/70,j*l+spacing+gap+22*l/70,14*l/70,26*l/70);
                            
                            g.fillRect(l*i+spacing+18*l/70,j*l+spacing+gap+33*l/70,34*l/70,4*l/70);
                            g.fillRect(l*i+spacing+33*l/70,j*l+spacing+gap+18*l/70,4*l/70,34*l/70);
                            
                        }
                    }
                }
            }
        }
    }
    
    public class Move implements MouseMotionListener
    {
        Point lastCell = null;
        @Override
        public void mouseMoved(MouseEvent e) {
            mx=e.getX();
            my=e.getY();
            hover();
        }
    	@Override
        public void mouseDragged(MouseEvent e) {
    		mx=e.getX();
            my=e.getY();
            hover();
    	}
        // repaint on hover new cell/none
        private void hover(){
            Point currentCell = getCellFromMouse(mx, my);
            if (currentCell != null) {
                if (lastCell == null || !lastCell.equals(currentCell)) {
                    lastCell = currentCell;
                    repaint();
                }
            } else {
                if (lastCell != null) {
                    lastCell = null;
                    repaint();
                }
            }
        }
    }

    private enum AutoMoveType {
        REVEAL,
        FLAG
    }

    private static class AutoMove {
        final AutoMoveType type;
        final int x;
        final int y;

        AutoMove(AutoMoveType type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }
    
    public class Click implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e)
        {
        	mx=e.getX();
            my=e.getY();
            
            if(inSmiley())
	            newGame();
            
            if(model.getGameState()==GameState.OPEN && (mx>autoPlayX && mx<autoPlayX+130 && my-titleBar>autoPlayY && my-titleBar<autoPlayY+50))
            {
                autoPlay = !autoPlay;
                System.out.println("AutoPlay="+autoPlay);
                if(autoPlay)
                {
                	startAutoPlayRunIfNeeded();
                }
                else
                {
                	stopAutoPlayTimer();
                }
            }
            	
            Point cell = getCellFromMouse(mx, my);
            GameState before = model.getGameState();
            if(cell != null && before==GameState.OPEN)
            {
            	int x = cell.x;
            	int y = cell.y;
                if(SwingUtilities.isLeftMouseButton(e))
            	{
            		if(e.getClickCount()==1)
                		model.reveal(x,y);
            		else if(e.getClickCount()==2)
                		model.chord(x,y);
            	}
            	
                if(SwingUtilities.isRightMouseButton(e))
                    model.flag(x,y);
            }
            GameState after = model.getGameState();
            if(after!=before && (after==GameState.WON || after==GameState.LOST)){
            	new GameOver(GUI.this).setVisible(true);
            }
            else if(autoPlay && after==GameState.OPEN)
            {
            	// user move may enable new obvious auto moves
            	startAutoPlayRunIfNeeded();
            }
            repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    public void newGame()
    {
    	this.dispose();
    	new LevelChoser().setVisible(true);
    }
    
    public void restart()
    {
    	autoPlay=false;
    	msgY=-100;
      	model.restart();
        stopAutoPlayTimer();
        repaint();
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (board != null) {
					board.repaint(timeX, timeY, 100, 50);
				}
            }
        });
        timer.start();
    }

    private void startAutoPlayRunIfNeeded()
    {
            if(!autoPlay || model.getGameState() != GameState.OPEN)
            {
            	stopAutoPlayTimer();
            	return;
            }
            if(autoPlayTimer == null)
            {
            	autoPlayTimer = new javax.swing.Timer(2000, new ActionListener() {
            		@Override
            		public void actionPerformed(ActionEvent e) {
            			runAutoPlayStep();
            		}
            	});
            	autoPlayTimer.setRepeats(true);
            }
            // restart ensures next step is 2 seconds from now
            autoPlayTimer.restart();
    }

    private void stopAutoPlayTimer()
    {
        if(autoPlayTimer != null)
        {
            autoPlayTimer.stop();
            autoPlayTimer = null;
        }
        lastAutoMoveCell = null;
        lastAutoMoveType = null;
    }

    private void runAutoPlayStep()
    {
        if(!autoPlay || model.getGameState() != GameState.OPEN)
        {
            stopAutoPlayTimer();
            return;
        }
        List<AutoMove> moves = computeObviousMoves();
        if(moves.isEmpty())
        {
        	// no more obvious moves for current board
        	stopAutoPlayTimer();
        	return;
        }
        AutoMove move = moves.get(0);
        int x = move.x;
        int y = move.y;
        GameState before = model.getGameState();
        if(move.type == AutoMoveType.REVEAL)
        {
            model.reveal(x, y);
        }
        else if(move.type == AutoMoveType.FLAG)
        {
            model.flag(x, y);
        }
        lastAutoMoveCell = new Point(x, y);
        lastAutoMoveType = move.type;
        board.repaint();
        GameState after = model.getGameState();
        if(after != before && (after == GameState.WON || after == GameState.LOST))
        {
            new GameOver(GUI.this).setVisible(true);
            stopAutoPlayTimer();
        }
    }

    private List<AutoMove> computeObviousMoves()
    {
        List<AutoMove> moves = new ArrayList<AutoMove>();
        boolean[][] addedReveal = new boolean[a][b];
        boolean[][] addedFlag = new boolean[a][b];
        for(int x=0; x<a; x++)
        {
            for(int y=0; y<b; y++)
            {
                if(!model.isRevealed(x, y))
                    continue;
                int number = model.getNeighbourCount(x, y);
                if(number <= 0)
                    continue;
                int flagged = 0;
                List<Point> covered = new ArrayList<Point>();
                for(int dx=-1; dx<=1; dx++)
                {
                    for(int dy=-1; dy<=1; dy++)
                    {
                        if(dx==0 && dy==0)
                            continue;
                        int nx = x + dx;
                        int ny = y + dy;
                        if(nx<0 || nx>=a || ny<0 || ny>=b)
                            continue;
                        if(model.isFlagged(nx, ny))
                        {
                            flagged++;
                        }
                        else if(!model.isRevealed(nx, ny))
                        {
                            covered.add(new Point(nx, ny));
                        }
                    }
                }
                if(covered.isEmpty())
                    continue;
                // safe to reveal all covered neighbours
                if(flagged == number)
                {
                    for(Point p : covered)
                    {
                        if(!addedReveal[p.x][p.y] && !model.isRevealed(p.x, p.y) && !model.isFlagged(p.x, p.y))
                        {
                            moves.add(new AutoMove(AutoMoveType.REVEAL, p.x, p.y));
                            addedReveal[p.x][p.y] = true;
                        }
                    }
                }
                // all covered neighbours must be mines -> flag them
                if(flagged + covered.size() == number)
                {
                    for(Point p : covered)
                    {
                        if(!addedFlag[p.x][p.y] && !model.isFlagged(p.x, p.y) && !model.isRevealed(p.x, p.y))
                        {
                            moves.add(new AutoMove(AutoMoveType.FLAG, p.x, p.y));
                            addedFlag[p.x][p.y] = true;
                        }
                    }
                }
            }
        }
        return moves;
    }

    public boolean inSmiley()
    {
    	double diff=Math.sqrt((mx-(smileyX+25))*(mx-(smileyX+25))+(my-titleBar-(smileyY+25))*(my-titleBar-(smileyY+25)));
    	if(diff<=25)
    		return true;
    	else
    		return false;
    }
    

    private Point getCellFromMouse(int mouseX, int mouseY)
    {
        if (mouseY < gap + titleBar || mouseY >= gap + titleBar + b * l) {
            return null;
        }
        if (mouseX < 0 || mouseX >= a * l) {
            return null;
        }
        int cellX = mouseX / l;
        int cellY = (mouseY - gap - titleBar) / l;
        if (cellX < 0 || cellX >= a || cellY < 0 || cellY >= b) {
            return null;
        }
        int cellMinX = cellX * l + spacing;
        int cellMaxX = cellX * l + l - spacing;
        int cellMinY = cellY * l + spacing + gap + titleBar;
        int cellMaxY = cellY * l + l - spacing + gap + titleBar;
        if (mouseX >= cellMinX && mouseX < cellMaxX && mouseY >= cellMinY && mouseY < cellMaxY) {
            return new Point(cellX, cellY);
        }
        return null;
    }
	
	public GameState getGameState() {
		return model.getGameState();
    }

    public char getDifficulty() {
		return model.getDifficulty();
    }

    public int getMineCount() {
		return model.getMineCount();
    }

    public int getFlaggedCount() {
		return model.getFlaggedCount();
    }

    public int getElapsedSeconds() {
		return model.getElapsedSeconds();
    }
	
}
