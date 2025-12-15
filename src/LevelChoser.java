import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelChoser extends JFrame {

	private JPanel contentPane;

	public LevelChoser() {
		
		setType(Type.POPUP);
		setResizable(false);
		// Level chooser is the entry window; closing it should exit the app
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("CHOOSE LEVEL");
		setBounds(100, 100, 440, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Choose Difficulty Level");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 26));
		lblNewLabel.setBounds(0, 21, 424, 54);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel);
		
		JButton btnEasy = new JButton("Easy");
		btnEasy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start('e');
			}
		});
		btnEasy.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnEasy.setFocusPainted(false);
		btnEasy.setFocusable(false);
		btnEasy.setBounds(10, 116, 126, 54);
		contentPane.add(btnEasy);
		
		JButton btnMedium = new JButton("Medium");
		btnMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start('m');
			}
		});
		btnMedium.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnMedium.setFocusPainted(false);
		btnMedium.setFocusable(false);
		btnMedium.setBounds(146, 116, 126, 54);
		contentPane.add(btnMedium);
		
		JButton btnHard = new JButton("Hard");
		btnHard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start('h');
			}
		});
		btnHard.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnHard.setFocusPainted(false);
		btnHard.setFocusable(false);
		btnHard.setBounds(282, 116, 126, 54);
		contentPane.add(btnHard);
		
		JLabel lblNewLabel_1 = new JLabel("10 X 8 Grid - 10 mines");
		lblNewLabel_1.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 181, 126, 67);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("20 X 10 Grid - 40 mines");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(146, 181, 126, 67);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("30 X 16 Grid - 99 mines");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(282, 181, 132, 67);
		contentPane.add(lblNewLabel_3);
	}
	
	private void start(char difficulty)
	{
		new Minesweeper(difficulty);
        this.dispose();
	}
}
