import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

public class CalculatorGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField numberOfDigitsField;
	private JTextField numberOfThreadsField;
	private JTextArea resultField;
	private JTextPane logField;
    private long digits;
    private  int threads;
    private boolean quiet;

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalculatorGUI frame = new CalculatorGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public CalculatorGUI() {
		quiet = false;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 565, 716);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton calculateButton = new JButton("Calculate");
		calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					digits = Long.parseLong(numberOfDigitsField.getText());
					threads = Integer.parseInt(numberOfThreadsField.getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				runCalculator(digits,threads,false);
			}
		});
		calculateButton.setBounds(158, 204, 259, 36);
		contentPane.add(calculateButton);
		
		numberOfDigitsField = new JTextField();
		numberOfDigitsField.setBounds(30, 107, 232, 57);
		contentPane.add(numberOfDigitsField);
		numberOfDigitsField.setColumns(10);
		
		numberOfThreadsField = new JTextField();
		numberOfThreadsField.setBounds(303, 107, 232, 57);
		contentPane.add(numberOfThreadsField);
		numberOfThreadsField.setColumns(10);
		
		JLabel accuracyLabel = new JLabel("Accuracy");
		accuracyLabel.setBounds(30, 85, 61, 16);
		contentPane.add(accuracyLabel);
		
		JLabel threadsLabel = new JLabel("Threads");
		threadsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		threadsLabel.setBounds(474, 85, 61, 16);
		contentPane.add(threadsLabel);
		
		JLabel titleLabel = new JLabel("PI Calculator");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		titleLabel.setBounds(158, 19, 259, 57);
		contentPane.add(titleLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 443, 505, 230);
		contentPane.add(scrollPane);
		
		resultField = new JTextArea();
		resultField.setEditable(false);
		resultField.setLineWrap(true);
		resultField.setRows(100);
		scrollPane.setViewportView(resultField);
		
		logField = new JTextPane();
		logField.setBounds(30, 301, 505, 117);
		contentPane.add(logField);
	}
	
	public void runCalculator(long digits, int threads, boolean quiet){
		 ApfloatContext.getGlobalContext().setNumberOfProcessors(threads);
         
         long calcBeg = System.nanoTime();
         Apfloat pi = PiCalculator.calculate(digits, threads, quiet);
         long calcEnd = System.nanoTime();
         
         resultField.setText(pi.toString());
         logField.setText("Total time of calculation: " + (calcEnd-calcBeg)/1_000_000 + " ms.");
	}
}
