import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

public class CalculatorGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextArea textArea;
	private JTextPane textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
			       long digits = 1_000_000;
			        int threads = 1;
			        //String filename = "pi.txt";
			        boolean quiet = false;
			        
				try {
					CalculatorGUI frame = new CalculatorGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CalculatorGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 565, 716);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Calculate");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runCalculator(10000l,2,false);
			}
		});
		btnNewButton.setBounds(158, 204, 259, 36);
		contentPane.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(30, 107, 232, 57);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(303, 107, 232, 57);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Accuracy");
		lblNewLabel_1.setBounds(30, 85, 61, 16);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Threads");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setBounds(474, 85, 61, 16);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblPiCalculator = new JLabel("PI Calculator");
		lblPiCalculator.setHorizontalAlignment(SwingConstants.CENTER);
		lblPiCalculator.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblPiCalculator.setBounds(158, 19, 259, 57);
		contentPane.add(lblPiCalculator);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 443, 505, 230);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setRows(100);
		scrollPane.setViewportView(textArea);
		
		textPane = new JTextPane();
		textPane.setBounds(30, 301, 505, 117);
		contentPane.add(textPane);
	}
	
	public void runCalculator(long digits, int threads, boolean quiet){
		 ApfloatContext.getGlobalContext().setNumberOfProcessors(threads);
         
         long calcBeg = System.nanoTime();
         Apfloat pi = PiCalculator.calculate(digits, threads, quiet);
         long calcEnd = System.nanoTime();
         
         textArea.setText(pi.toString());
         textPane.setText("Total time of calculation: " + (calcEnd-calcBeg)/1_000_000 + " ms.");

//
//         long fileBeg = System.nanoTime();
//         try {
//             Files.write(Paths.get(filename), pi.toString(true).getBytes("utf8"));
//         } catch (IOException e) {
//             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
//         }

	}
}
