package us.akana.tools.photo_renamer;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

/**
 * A file select button using swing - opens a file selection when clicked,
 * changes its text to reflect the selected file
 * 
 * @author Jaden Unruh 
 */
@SuppressWarnings("serial")
class SelectButton extends JButton {
	SelectButton(final int whichSelect, final boolean isDir) {
		super(Messages.getString("SelectButton.Text")); //$NON-NLS-1$
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
					@Override
					protected Boolean doInBackground() throws Exception {
						JFileChooser fc = new JFileChooser();
						if (isDir)
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int returnVal = fc.showOpenDialog(Main.options);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							Main.selectedFiles[whichSelect] = fc.getSelectedFile();
							rename(fc.getSelectedFile().getName());
						}
						deSelected();
						return null;
					}
				};
				selected();
				sw.execute();
			}
		});
	}

	/**
	 * Disables the button when selected
	 */
	void selected() {
		this.setEnabled(false);
	}

	/**
	 * Re-enables the button when deselected, and repacks Main.options to reflect
	 * the button's new width
	 */
	void deSelected() {
		this.setEnabled(true);
		Main.options.pack();
	}

	/**
	 * Renames the button to the given text
	 * 
	 * @param text String to rename to
	 */
	void rename(String text) {
		this.setText(text);
	}
}