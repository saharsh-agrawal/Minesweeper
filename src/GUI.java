import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame{
    
	private GameState gameState;
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
    
    Date startDate;
    int sec;
    int timeX;
    int timeY;
    
    int revealedCount,flaggedCount;
    
    int[][] mines;
    int[][] neighbours;
    boolean[][] revealed;
    boolean[][] flagged;

	private javax.swing.Timer timer;
    
    public GUI(char diff){
        
        difficulty=diff;
    	switch(difficulty)
        {
        	case 'e':
        		a=10;
        		b=8;
        		l=70;
        		spacing=3;
        		mineCount=10;
        		fontSize=40;
        		cellPadding=23;
        		break;
        	case 'm':
        		a=20;
        		b=12;
        		l=50;
        		spacing=2;
        		mineCount=40;
        		fontSize=30;
        		cellPadding=17;
        		break;
        	case 'h':
        		a=30;
        		b=16;
        		l=35;
        		spacing=2;
        		mineCount=99;
        		fontSize=20;
        		cellPadding=11;
        		break;
        	default:
        		this.dispose();
        }
        
        mines=new int[a][b];
        neighbours=new int[a][b];
        revealed=new boolean[a][b];
        flagged=new boolean[a][b];
        
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
		setMines();
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
            switch(gameState)
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
            g.setFont(new Font("Tahoma",Font.BOLD,25));
            g.fillRect(autoPlayX,autoPlayY,130,50);
            g.setColor(Color.white);
            g.drawString("Auto Play",autoPlayX+6,autoPlayY+35);
            
            
          // smiley painting
            g.setColor(Color.yellow);
            g.fillOval(smileyX,smileyY,50,50);
            g.setColor(Color.black);
            g.fillOval(smileyX+10,smileyY+12,10,10);
            g.fillOval(smileyX+30,smileyY+12,10,10);
            if(gameState==GameState.LOST)
            {
                g.fillRect(smileyX+12,smileyY+32,26,5);
                
                g.fillRect(smileyX+11,smileyY+34,5,5);
                g.fillRect(smileyX+34,smileyY+34,5,5);
                
                g.fillRect(smileyX+10,smileyY+36,5,5);
                g.fillRect(smileyX+35,smileyY+36,5,5);
            }
            else if(gameState==GameState.WON || gameState==GameState.OPEN)
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
            g.drawString("Mines left: "+(mineCount-flaggedCount),minesLeftX,minesLeftY);
            
            // timer display
            g.setColor(Color.black);
            g.fillRect(timeX,timeY,100,50);
            if(gameState==GameState.OPEN) sec=(int)((new Date().getTime()-startDate.getTime())/1000);
            
            String time=sec+"";
            if(sec<10) time="00"+time;
            if(sec<100 && sec>=10) time="0"+time;
            if(sec>999) time="999";
            
            g.setColor(Color.white);
            if(gameState==GameState.WON) g.setColor(Color.green);
            if(gameState==GameState.LOST) g.setColor(Color.red);
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
                    
                    if(revealed[i][j])
                    {
                        g.setColor(Color.white);
                        if(mines[i][j]==1)
                            g.setColor(Color.red);
                    }
                    // background when hover
                    if(mx>=l*i+spacing && mx<l*i+l-spacing && my>=j*l+spacing+gap+titleBar && my<j*l+l-spacing+gap+titleBar)
                        g.setColor(Color.LIGHT_GRAY);
                    
                    g.fillRect(l*i+spacing,j*l+spacing+gap, l-2*spacing, l-2*spacing);
                    
                    // flags
                    if(flagged[i][j])
                    {
                    	g.setColor(Color.red);
                        g.fillRect(l*i+spacing+30*l/70,j*l+spacing+gap+7*l/70,30*l/70,20*l/70);
                        g.setColor(Color.black);
                        g.fillRect(l*i+spacing+25*l/70,j*l+spacing+gap+7*l/70,5*l/70,50*l/70);
                        g.fillRect(l*i+spacing+15*l/70,j*l+spacing+gap+57*l/70,30*l/70,5*l/70);
                    }
                    
                    // number or mine
                    if(revealed[i][j])
                    {
                        if(mines[i][j]==0 && neighbours[i][j]!=0)
                        {
                            switch(neighbours[i][j])
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
                            g.drawString(neighbours[i][j]+"",l*i+spacing+cellPadding,j*l+spacing+gap+2*cellPadding);
                        }
                        else if(mines[i][j]==1){
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
            
            if(gameState==GameState.OPEN && (mx>autoPlayX && mx<autoPlayX+130 && my-titleBar>autoPlayY && my-titleBar<autoPlayY+50))
            {
            	if(!autoPlay)
            		autoPlay=true;
            	else
            		autoPlay=false;
            	System.out.println(autoPlay);
            }
            	
            	Point cell = getCellFromMouse(mx, my);
            	if(cell != null && gameState==GameState.OPEN)
            {
            		int x = cell.x;
            		int y = cell.y;
            	if(SwingUtilities.isLeftMouseButton(e))
            	{
            		if(e.getClickCount()==1)
            			reveal(x,y);
            		else if(e.getClickCount()==2)
            			explode(x,y);
            	}
            	
                if(SwingUtilities.isRightMouseButton(e))
                	flag(x,y);
            }
            checkVictory();
            repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    void flag(int x,int y)
    {
    	if(!revealed[x][y])
    	{
    		if(!flagged[x][y])
        	{
        		flagged[x][y]=true;
        		flaggedCount++;
        	}
        	else
        	{
        		flagged[x][y]=false;
        		flaggedCount--;
        	}
    	}
    }
    
    void reveal(int x,int y)
    {
    	if(!flagged[x][y] && !revealed[x][y])
    	{
    		revealed[x][y]=true;
            revealedCount++;
            if(mines[x][y]==1)
            {
            		gameState=GameState.LOST;
            	new GameOver(this).setVisible(true);
            	return;
            }
            if(neighbours[x][y]==0)
            	revealNeighbours(x,y);
    	}
    }
    
    void explode(int x,int y)
    {
    	if(revealed[x][y])
    	{
    		int count=0;
    		if(x!=0 && y!=0 && flagged[x-1][y-1]) count++;
            if(x!=0  && flagged[x-1][y]) count++;
            if(x!=0 && y!=(b-1) && flagged[x-1][y+1]) count++;
            
            if(y!=0 && flagged[x][y-1]) count++;
            if(y!=(b-1) && flagged[x][y+1]) count++;
            
            if(x!=(a-1) && y!=0 && flagged[x+1][y-1]) count++;
            if(x!=(a-1) && flagged[x+1][y]) count++;
            if(x!=(a-1) && y!=(b-1) && flagged[x+1][y+1]) count++;
            
            if(count==neighbours[x][y])
            	revealNeighbours(x,y);
    	}
    }
    
    void revealNeighbours(int x,int y)
    {
    	if(x!=0 && y!=0) reveal(x-1,y-1);
        if(x!=0) reveal(x-1,y);
        if(x!=0 && y!=(b-1)) reveal(x-1,y+1);
        
        if(y!=0) reveal(x,y-1);
        if(y!=(b-1)) reveal(x,y+1);
        
        if(x!=(a-1) && y!=0) reveal(x+1,y-1);
        if(x!=(a-1)) reveal(x+1,y);
        if(x!=(a-1) && y!=(b-1)) reveal(x+1,y+1);
    }
    
    void checkVictory()
    {
        if(a*b-revealedCount==mineCount && gameState==GameState.OPEN)
        {
            	gameState=GameState.WON;
        	new GameOver(this).setVisible(true);
        }
    }
    
    public void newGame()
    {
    	this.dispose();
    	new LevelChoser().setVisible(true);
    }
    
    public void restart()
    {
    	autoPlay=false;
        gameState=GameState.OPEN;
    	revealedCount=flaggedCount=sec=0;
    	msgY=-100;
    	startDate=new Date();
    	
    	for(int i=0;i<a;i++){
            for(int j=0;j<b;j++){
            	revealed[i][j]=false;
                flagged[i][j]=false;
            }
        }
        repaint();
    }
    
    void setMines()
    {
    	for(int i=0;i<a;i++)
            for(int j=0;j<b;j++)
            	mines[i][j]=0;
    	
    	Random rand=new Random();
    	int count=0;
    	while (count < mineCount)
        {
            int i = (int) (rand.nextDouble() * a);
            int j = (int) (rand.nextDouble() * b);
            if (mines[i][j]==1)
                continue;
            else
            {
            	mines[i][j]=1;
                count++;
            }
        }
        for(int i=0;i<a;i++){
            for(int j=0;j<b;j++){
            	
            	int neighs=0;
                
                if(i!=0 && j!=0) neighs+=mines[i-1][j-1];
                if(i!=0) neighs+=mines[i-1][j];
                if(i!=0 && j!=(b-1)) neighs+=mines[i-1][j+1];
                
                if(j!=0) neighs+=mines[i][j-1];
                if(j!=(b-1)) neighs+=mines[i][j+1];
                
                if(i!=(a-1) && j!=0) neighs+=mines[i+1][j-1];
                if(i!=(a-1)) neighs+=mines[i+1][j];
                if(i!=(a-1) && j!=(b-1)) neighs+=mines[i+1][j+1];
                
                neighbours[i][j]=neighs;
            }
        }
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

    public boolean inSmiley()
    {
    	double diff=Math.sqrt((mx-(smileyX+25))*(mx-(smileyX+25))+(my-titleBar-(smileyY+25))*(my-titleBar-(smileyY+25)));
    	if(diff<=25)
    		return true;
    	else
    		return false;
    }
    

    private Point getCellFromMouse(int mouseX, int mouseY) {
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
        return gameState;
    }

    public char getDifficulty() {
        return difficulty;
    }

    public int getMineCount() {
        return mineCount;
    }

    public int getFlaggedCount() {
        return flaggedCount;
    }

    public int getElapsedSeconds() {
        return sec;
    }
	
}
