import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GUI extends JFrame{
    
	char gameState;
	
	public char difficulty;
    public int a;//columns
	public int b;//rows
	public int l;//size of cell
	public int spacing;//spacing
	public int mineCount;//no of mines
	
	public int fontSize;
	public int cellPadding;
	
	public int titleBar=31;
	public int gap=57;
	
	int mx=-100;
    int my=-100;
    
    public int msgX;
    public int msgY;
    
    public int autoPlayX;
    public int autoPlayY;
    public boolean autoPlay;
    
    public int smileyX;
    public int smileyY;
    
    public int minesLeftX;
    public int minesLeftY;
    
    Date startDate;
    public int sec;
    public int timeX;
    public int timeY;
    
    public int revealedCount,flaggedCount;
    
    int[][] mines;
    int[][] neighbours;
    boolean[][] revealed;
    boolean[][] flagged;
    
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
        
        this.setTitle("Minesweeper");
        this.setSize(a*l+16,b*l+95);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        
        Board board=new Board();
        this.setContentPane(board);
        
        Move move=new Move();
        this.addMouseMotionListener(move);
        
        Click click=new Click();
        this.addMouseListener(click);
    }
    
    public class Board extends JPanel{
        @Override
        public void paintComponent(Graphics g){
            //game board
        	g.setColor(Color.DARK_GRAY);
            g.fillRect(0,0,a*l,b*l+gap);
            
            //msgbox
            String msg="";
            switch(gameState)
            {
            	case 'w':
            		msg="YOU WIN!";
            		g.setColor(Color.green);
            		msgY=40;
            		break;
            	case 'l':
            		msg="YOU LOST!";
                	g.setColor(Color.red);
                	msgY=40;
                	break;
            }
            g.setFont(new Font("Tahoma",Font.BOLD,30));
            g.drawString(msg,msgX,msgY);
            
            
            //autoplay button
            g.setColor(Color.black);
            g.setFont(new Font("Tahoma",Font.BOLD,25));
            g.fillRect(autoPlayX,autoPlayY,130,50);
            g.setColor(Color.white);
            g.drawString("Auto Play",autoPlayX+6,autoPlayY+35);
            
            
          //smiley painting
            g.setColor(Color.yellow);
            g.fillOval(smileyX,smileyY,50,50);
            g.setColor(Color.black);
            g.fillOval(smileyX+10,smileyY+12,10,10);
            g.fillOval(smileyX+30,smileyY+12,10,10);
            if(gameState=='l')
            {
                g.fillRect(smileyX+12,smileyY+32,26,5);
                
                g.fillRect(smileyX+11,smileyY+34,5,5);
                g.fillRect(smileyX+34,smileyY+34,5,5);
                
                g.fillRect(smileyX+10,smileyY+36,5,5);
                g.fillRect(smileyX+35,smileyY+36,5,5);
            }
            else if(gameState=='w' || gameState=='o' || gameState=='c')
            {
                g.fillRect(smileyX+12,smileyY+36,26,5);

                g.fillRect(smileyX+11,smileyY+34,5,5);
                g.fillRect(smileyX+34,smileyY+34,5,5);
                
                g.fillRect(smileyX+10,smileyY+32,5,5);
                g.fillRect(smileyX+35,smileyY+32,5,5);
            }
            
          //mines left display
            g.setColor(Color.white);
            g.setFont(new Font("Tahoma",Font.BOLD,28));
            g.drawString("Mines left: "+(mineCount-flaggedCount),minesLeftX,minesLeftY);
            
            //timer display
            g.setColor(Color.black);
            g.fillRect(timeX,timeY,100,50);
            if(gameState=='o') sec=(int)((new Date().getTime()-startDate.getTime())/1000);
            
            String time=sec+"";
            if(sec<10) time="00"+time;
            if(sec<100 && sec>=10) time="0"+time;
            if(sec>999) time="999";
            
            g.setColor(Color.white);
            if(gameState=='w') g.setColor(Color.green);
            if(gameState=='l') g.setColor(Color.red);
            g.setFont(new Font("Tahoma",Font.BOLD,40));
            g.drawString(time,timeX+16,timeY+40);
            
            //divider line
            g.setColor(Color.gray);
            g.fillRect(0,gap-1,a*l,1);
            
            //a by b will be the grid...each box l*l pixels with 'spacing' padding within
            //cells
            for(int i=0;i<a;i++){
                for(int j=0;j<b;j++){
                    
                    //background
                    g.setColor(new Color(10,110,210));
                    
                    if(revealed[i][j])
                    {
                        g.setColor(Color.white);
                        if(mines[i][j]==1)
                            g.setColor(Color.red);
                    }
                    //background when hover
                    if(mx>=l*i+spacing && mx<l*i+l-spacing && my>=j*l+spacing+gap+titleBar && my<j*l+l-spacing+gap+titleBar)
                        g.setColor(Color.LIGHT_GRAY);
                    
                    g.fillRect(l*i+spacing,j*l+spacing+gap, l-2*spacing, l-2*spacing);
                    
                    //flags
                    if(flagged[i][j])
                    {
                    	g.setColor(Color.red);
                        g.fillRect(l*i+spacing+30*l/70,j*l+spacing+gap+7*l/70,30*l/70,20*l/70);
                        g.setColor(Color.black);
                        g.fillRect(l*i+spacing+25*l/70,j*l+spacing+gap+7*l/70,5*l/70,50*l/70);
                        g.fillRect(l*i+spacing+15*l/70,j*l+spacing+gap+57*l/70,30*l/70,5*l/70);
                    }
                    
                    //number or mine
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
        int lastMinX = -1;
        int lastMinY = -1;
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
            if (my > gap+titleBar) {
                int currentMinX = mx - (mx % l)+spacing;
                int currentMaxX = mx - (mx % l)+l-spacing;
                int currentMinY = my - ((my-gap-titleBar) %l)+spacing;
                int currentMaxY = my - ((my-gap-titleBar) % l)+l-spacing;
                if (mx >= currentMinX && my >= currentMinY  && mx < currentMaxX && my < currentMaxY){
                    if (lastMinX != currentMinX || lastMinY != currentMinY) {
                        lastMinX = currentMinX;
                        lastMinY = currentMinY;
                        repaint();
                    }
                }else {
                    lastMinX = -1;
                    lastMinY = -1;
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
            
            if(inSmiley() && gameState=='o')
            	newGame();
            
            if(mx>autoPlayX && mx<autoPlayX+130 && my-titleBar>autoPlayY && my-titleBar<autoPlayY+50)
            {
            	if(!autoPlay)
            		autoPlay=true;
            	else
            		autoPlay=false;
            	System.out.println(autoPlay);
            }
            	
            int x=inBoxX();
            int y=inBoxY();
            if(x!=-1 && y!=-1 && gameState=='o')
            {
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
            	gameState='l';
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
    	if(a*b-revealedCount==mineCount && gameState=='o')
        {
        	gameState='w';
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
    	gameState='o';
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
    
    public boolean inSmiley()
    {
    	double diff=Math.sqrt((mx-(smileyX+25))*(mx-(smileyX+25))+(my-titleBar-(smileyY+25))*(my-titleBar-(smileyY+25)));
    	if(diff<=25)
    		return true;
    	else
    		return false;
    }
    
    public int inBoxX(){
        for(int i=0;i<a;i++){
                for(int j=0;j<b;j++){
                    if(mx>=l*i+spacing && mx<l*i+l-spacing && my>=j*l+spacing+gap+titleBar && my<j*l+l-spacing+gap+titleBar)
                        return i;
                }
            }
        return -1;
    }
    public int inBoxY(){
        for(int i=0;i<a;i++){
                for(int j=0;j<b;j++){
                    if(mx>=l*i+spacing && mx<l*i+l-spacing && my>=j*l+spacing+gap+titleBar && my<j*l+l-spacing+gap+titleBar)
                        return j;
                }
            }
        return -1;
    }
    
}
