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
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

public class Main extends JFrame {
    public static String log;
    
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField numberOfDigitsField;
	private JTextField numberOfThreadsField;
	private JTextArea resultField;
	private JTextPane logField;
    private long digits;
    private int threads;
    private boolean quiet;
    private String filename;
    private boolean useGUI;

	public static void main(String[] args) {
		Main frame = new Main();
		frame.parseInitialInput(args, frame);
	}
	
	public void parseInitialInput(String[] args, Main frame){
		useGUI = true;
		threads = 1;
		digits = 3;
		filename = "pi.txt";
		
		for (int i = 0; i < args.length; i++) {
            switch(args[i]){
                case "-p":
                    digits = Long.parseLong(args[i+1]);
                    i++;
                    break;
                case "-t":
                case "-tasks":
                    threads = Integer.parseInt(args[i+1]);
                    i++;
                    break;
                case "-o":
                    filename = args[i+1];
                    i++;
                    break;
                case "-q":
                    quiet = true;
                    useGUI = false;
                    break;
            }
        }
		
		if (useGUI) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					frame.setVisible(true);
				}
			});
		}
		else {
			this.runCalculator(digits, threads, quiet);
		}
		
	}

	public Main() {
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
				
				if (!numberOfDigitsField.getText().isEmpty() && !numberOfThreadsField.getText().isEmpty()) {
					try {
						digits = Long.parseLong(numberOfDigitsField.getText());
						threads = Integer.parseInt(numberOfThreadsField.getText());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
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
		logField.setBounds(30, 270, 505, 160);
		contentPane.add(logField);
	}
	
	public void runCalculator(long digits, int threads, boolean quiet) {
		 
		 ApfloatContext.getGlobalContext().setNumberOfProcessors(threads);
         
         long calcBeg = System.nanoTime();
         Apfloat pi = PiCalculator.calculate(digits, threads, quiet);
         long calcEnd = System.nanoTime();
         
         if (useGUI){
        	 resultField.setText(pi.toString());
             logField.setText(Main.log + System.lineSeparator() + "Total time of calculation: " + (calcEnd-calcBeg)/1_000_000 + " ms.");
         }
         else{
        	 long fileBeg = System.nanoTime();
             try {
                 Files.write(Paths.get(filename), pi.toString(true).getBytes("utf8"));
                 System.out.println(pi.toString());
             } catch (IOException e) {
                 e.printStackTrace();
             }
             long fileEnd = System.nanoTime();
                 System.out.println("Writing result to file '" + filename +
                         "' took " + (fileEnd-fileBeg)/1000 + " us." + System.lineSeparator());
         }
	}
}
