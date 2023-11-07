import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class BookBuyerGui extends JFrame {
	private final BookBuyerAgent myAgent;

	private final JTextField titleField;
	private final JLabel bookPrices;
	private final JButton searchButton;

	BookBuyerGui(BookBuyerAgent a) {
		super(a.getLocalName());
		
		myAgent = a;

		JPanel root = new JPanel();
		root.setLayout(new GridLayout(2, 1));
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));
		p.add(new JLabel("Book title:"));
		titleField = new JTextField(15);
		p.add(titleField);
		root.add(p);

		JPanel p2 = new JPanel();
		bookPrices = new JLabel();
		p2.add(bookPrices);
		root.add(p2);

		getContentPane().add(root, BorderLayout.CENTER);

		searchButton = new JButton("Search");
		searchButton.addActionListener( new searchButtonListener() );
		p = new JPanel();
		p.add(searchButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	public class searchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String title = titleField.getText().trim();
			if (!title.isEmpty()){
				myAgent.makeSearchRequest(title);
			}
		}
	}
	public void displayBookPrice(int price){
		bookPrices.setText("Book price: " + price);
		titleField.setEditable(false);
		searchButton.setText("Buy");
		searchButton.removeActionListener(searchButton.getActionListeners()[0]);
		searchButton.addActionListener(ev -> myAgent.confirmBuy());
	}
	public void displaySuccessOperation(String bookTitle, int price){
		bookPrices.setText("«" + bookTitle + "» successfully bought for " + price);
		titleField.setText("");
		titleField.setEditable(true);
		searchButton.setText("Search");
		searchButton.removeActionListener(searchButton.getActionListeners()[0]);
		searchButton.addActionListener( new searchButtonListener() );
	}
	public void displayNotFound(String bookTitle){
		bookPrices.setText("«" + bookTitle + "» not found");
	}
}
