import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class BookSellerGui extends JFrame {	
	private final BookSellerAgent myAgent;
	
	private final JTextField titleField, priceField;
	private final DefaultTableModel booksTable;
	
	BookSellerGui(BookSellerAgent a) {
		super(a.getLocalName());
		
		myAgent = a;

		booksTable = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		booksTable.addColumn("Title");
		booksTable.addColumn("Price");

		JTable table = new JTable(booksTable);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(0, 120));

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(booksTable);
		table.setRowSorter(sorter);

		getContentPane().add(scrollPane, BorderLayout.NORTH);
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		p.add(new JLabel("Book title:"));
		titleField = new JTextField(15);
		p.add(titleField);
		p.add(new JLabel("Price:"));
		priceField = new JTextField(15);
		p.add(priceField);
		getContentPane().add(p, BorderLayout.CENTER);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(ev -> {
            try {
                String title = titleField.getText().trim();
                String price = priceField.getText().trim();
                myAgent.updateCatalogue(title, Integer.parseInt(price));
                titleField.setText("");
                priceField.setText("");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(BookSellerGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );

		setResizable(false);
	}

	public void updateTable(Hashtable<String, Integer> data){
		booksTable.setRowCount(0);
		for (Map.Entry<String, Integer> entry : data.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			booksTable.addRow(new Object[]{key, value});
		}
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth(), centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}
