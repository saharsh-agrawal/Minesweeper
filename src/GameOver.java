import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

public class GameOver extends JFrame {

	private JPanel contentPane;
	
	public GameOver(GUI gui)
	{
		setType(Type.UTILITY);
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("GAME OVER");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnRestart = new JButton("RESTART");
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restart(gui);
			}
		});
		// Avoid showing default focus highlight on this button
		btnRestart.setFocusPainted(false);
		btnRestart.setFocusable(false);
		btnRestart.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnRestart.setBounds(33, 170, 140, 80);
		contentPane.add(btnRestart);
		
		JButton btnNewGame = new JButton("NEW GAME");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame(gui);
			}
		});
		btnNewGame.setFocusPainted(false);
		btnNewGame.setFocusable(false);
		btnNewGame.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewGame.setBounds(253, 170, 140, 80);
		contentPane.add(btnNewGame);
		
		JLabel lblMinesLeft = new JLabel("Mines Left");
		lblMinesLeft.setHorizontalAlignment(SwingConstants.CENTER);
		lblMinesLeft.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblMinesLeft.setBounds(33, 39, 140, 37);
		contentPane.add(lblMinesLeft);
		
		JLabel lblMinesLeftAnswer = new JLabel(""+(gui.getMineCount()-gui.getFlaggedCount()));
		lblMinesLeftAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		lblMinesLeftAnswer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblMinesLeftAnswer.setBounds(253, 39, 140, 37);
		contentPane.add(lblMinesLeftAnswer);
		
		JLabel lblTimetaken = new JLabel("Time Taken");
		lblTimetaken.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimetaken.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTimetaken.setBounds(33, 74, 140, 37);
		contentPane.add(lblTimetaken);
		
		JLabel lblTimeTakenAnswer = new JLabel(gui.getElapsedSeconds()+" seconds");
		lblTimeTakenAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeTakenAnswer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTimeTakenAnswer.setBounds(253, 74, 140, 37);
		contentPane.add(lblTimeTakenAnswer);
		
		JLabel lblBestTime = new JLabel("Best Time");
		lblBestTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblBestTime.setBounds(33, 112, 140, 37);
		contentPane.add(lblBestTime);
		
		JLabel lblBestTimeAnswer = new JLabel(highScore(gui));
		lblBestTimeAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestTimeAnswer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblBestTimeAnswer.setBounds(253, 112, 140, 37);
		contentPane.add(lblBestTimeAnswer);
		
		String msg="";
		if(gui.getGameState()==GameState.WON) msg="Congratulations!!! You have Won!!!";
		if(gui.getGameState()==GameState.LOST) msg="Oops!!! You have Lost!!!";
		
		JLabel lblNewLabel = new JLabel(msg);
		lblNewLabel.setFont(new Font("Perpetua Titling MT", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 414, 31);
		contentPane.add(lblNewLabel);
		
	}
	
	private String highScore(GUI gui) {
	        try {
	        	String level = gui.getDifficulty() == 'e' ? "Easy" : gui.getDifficulty() == 'm' ? "Medium" : "Hard";
	            if (gui.getGameState() == GameState.WON) {
	                HighScoreManager.updateHighScore(level, gui.getElapsedSeconds());
	            }
	            int bestTime = HighScoreManager.getHighScore(level);
	            return bestTime == 0 ? "(N.A.)" : bestTime + " seconds";

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return "(N.A.)";
    }
	
	
	private void restart(GUI gui)
	{
		gui.restart();
        this.dispose();
	}
	
	private void newGame(GUI gui)
	{
		gui.newGame();
        this.dispose();
	}
}
