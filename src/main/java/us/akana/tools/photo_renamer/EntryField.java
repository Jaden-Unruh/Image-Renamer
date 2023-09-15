package us.akana.tools.photo_renamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class EntryField extends JTextField implements DocumentListener {

	Pattern regex;
	boolean isValid = false;

	EntryField(String regex, String defaultText) {
		super();
		TextPrompt prompt = new TextPrompt(defaultText, this);
		prompt.changeAlpha(150);
		this.setPreferredSize(new Dimension(prompt.getPreferredSize().width + 10, this.getPreferredSize().height));
		this.regex = Pattern.compile(regex);
		
		getDocument().addDocumentListener(this);
	}
	
	private void checkText() {
		String text = this.getText();
		if (regex.matcher(text).matches()) {
			this.setForeground(Color.BLACK);
			isValid = true;
		} else {
			this.setForeground(Color.RED);
			isValid = false;
		}
	}

	public void changedUpdate(DocumentEvent arg0) {}

	public void insertUpdate(DocumentEvent arg0) {
		checkText();
	}

	public void removeUpdate(DocumentEvent arg0) {
		checkText();
	}
}

@SuppressWarnings("serial")
class TextPrompt extends JLabel implements FocusListener, DocumentListener {

	private JTextComponent component;
	private Document document;

	public TextPrompt(String text, JTextComponent component) {
		this.component = component;
		document = component.getDocument();

		setText(text);
		setFont(component.getFont());
		setForeground(component.getForeground());
		setBorder(new EmptyBorder(component.getInsets()));
		setHorizontalAlignment(JLabel.LEADING);

		component.addFocusListener(this);
		document.addDocumentListener(this);

		component.setLayout(new BorderLayout());
		component.add(this);
		checkForPrompt();
	}

	/**
	 * Convenience method to change the alpha value of the current foreground Color
	 * to the specifice value.
	 *
	 * @param alpha value in the range of 0 - 255.
	 */
	public void changeAlpha(int alpha) {
		alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

		Color foreground = getForeground();
		int red = foreground.getRed();
		int green = foreground.getGreen();
		int blue = foreground.getBlue();

		Color withAlpha = new Color(red, green, blue, alpha);
		super.setForeground(withAlpha);
	}

	/**
	 * Check whether the prompt should be visible or not. The visibility will change
	 * on updates to the Document and on focus changes.
	 */
	private void checkForPrompt() {
		if (document.getLength() > 0) {
			setVisible(false);
			return;
		}

		if (component.hasFocus()) {
			setVisible(false);
		} else {
			setVisible(true);
		}
	}

	public void focusGained(FocusEvent e) {
		checkForPrompt();
	}

	public void focusLost(FocusEvent e) {
		checkForPrompt();
	}

	public void insertUpdate(DocumentEvent e) {
		checkForPrompt();
	}

	public void removeUpdate(DocumentEvent e) {
		checkForPrompt();
	}

	public void changedUpdate(DocumentEvent e) {
	}
}
