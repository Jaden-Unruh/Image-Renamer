package us.akana.tools.photo_renamer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Possible values for the information text area in the window
 * 
 * @author Jaden
 * 
 * @see Main#info
 * @see Main#infoText
 */
enum InfoText {
	SELECT_PROMPT, ERROR, DONE, COPYING
}

/**
 * Primary class for the Photo renaming tool, contains entry method
 * 
 * @author Jaden
 */
public class Main {

	/**
	 * Regular expression for a site number: 'IA', 'IE', or 'JS' followed by 3
	 * digits
	 */
	private static final String SITE_REGEX = "^(IA|IE|JS)\\d{3}$"; //$NON-NLS-1$
	/**
	 * Regular expression for a location number: a capital letter followed by 2
	 * digits, then a '-', then two more digits
	 */
	private static final String LOC_NUM_REGEX = "^[A-Z]\\d{2}-\\d{2}$"; //$NON-NLS-1$
	/**
	 * Regular expression for a year: '20' followed by two digits
	 */
	private static final String YEAR_REGEX = "^20\\d{2}$"; //$NON-NLS-1$

	/**
	 * Main program window
	 */
	static JFrame options;
	/**
	 * The input and output directories, null until the user selects them
	 */
	static File[] selectedFiles = new File[2];
	/**
	 * The information text area on {@link Main#options}. Empty when the program
	 * starts, but once changed will always contain text specified by
	 * {@link InfoText}
	 * 
	 * @see Main#infoText
	 * @see InfoText
	 */
	static JLabel info = new JLabel();

	/**
	 * User inputs for site, location number, and year
	 */
	static EntryField inputSite, inputLocNum, inputYear;
	/**
	 * Buttons to select input and output directories; open a file selection window
	 * upon click
	 */
	static JButton selectInput, selectOutput;

	/**
	 * Specifies the text currently showing in {@link Main#info}
	 */
	static InfoText infoText;

	/**
	 * Writes to the info file within the output directory - used to report errors
	 * and duplicate files
	 */
	static FileWriter writeToInfo;

	/**
	 * Entry method - opens the program window
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		openWindow();
	}

	/**
	 * Initializes the info file within the output directory and adds the header
	 * 
	 * @param output the output directory
	 * @throws IOException if there's an error creating the file or writing to it
	 */
	static void initInfo(File output) throws IOException {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS")); //$NON-NLS-1$
		File infoFile = new File(String.format("%s\\%s_%s.txt", output.getAbsolutePath(), //$NON-NLS-1$
				Messages.getString("Main.File.InfoName"), dateTime)); //$NON-NLS-1$
		infoFile.createNewFile();
		writeToInfo = new FileWriter(infoFile);
		writeToInfo.write(String.format(Messages.getString("Main.File.InfoHeader"), dateTime)); //$NON-NLS-1$
	}

	/**
	 * Gets the String that should be showing in {@link Main#info} according to
	 * {@link Main#infoText}
	 * 
	 * @return the appropriate String
	 * @see InfoText
	 */
	static String getInfoText() {
		switch (infoText) {
		case SELECT_PROMPT:
			return Messages.getString("Main.Info.SelectPrompt"); //$NON-NLS-1$
		case ERROR:
			return Messages.getString("Main.Info.Error"); //$NON-NLS-1$
		case DONE:
			return Messages.getString("Main.Info.Done"); //$NON-NLS-1$
		case COPYING:
			return Messages.getString("Main.Info.Copying"); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Opens the program window and populates it with all of its elements
	 */
	static void openWindow() {
		options = new JFrame(Messages.getString("Main.Window.Title")); //$NON-NLS-1$
		options.setLayout(new GridBagLayout());
		options.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		options.add(new JLabel(Messages.getString("Main.Window.InputPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		selectInput = new SelectButton(0, true);
		options.add(selectInput,
				new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(new JLabel(Messages.getString("Main.Window.OutputPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		selectOutput = new SelectButton(1, true);
		options.add(selectOutput,
				new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(new JLabel(Messages.getString("Main.Window.SitePrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		inputSite = new EntryField(SITE_REGEX, Messages.getString("Main.Window.SiteDefault")); //$NON-NLS-1$
		options.add(inputSite,
				new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(new JLabel(Messages.getString("Main.Window.LocPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		inputLocNum = new EntryField(LOC_NUM_REGEX, Messages.getString("Main.Window.LocDefault")); //$NON-NLS-1$
		options.add(inputLocNum,
				new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(new JLabel(Messages.getString("Main.Window.YearPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		inputYear = new EntryField(YEAR_REGEX, Messages.getString("Main.Window.YearDefault")); //$NON-NLS-1$
		options.add(inputYear,
				new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		JButton cancel = new JButton(Messages.getString("Main.Window.Close")); //$NON-NLS-1$
		options.add(cancel,
				new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		final JButton run = new JButton(Messages.getString("Main.Window.Open")); //$NON-NLS-1$
		options.add(run,
				new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(info,
				new GridBagConstraints(0, 6, 2, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkCorrectSelections()) {
					SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
						@Override
						protected Boolean doInBackground() throws Exception {
							updateInfo(InfoText.COPYING);
							lockInputs();
							initInfo(selectedFiles[1]);
							copyPhotos(selectedFiles[0]);
							writeToInfo.close();
							updateInfo(InfoText.DONE);
							unlockInputs();
							run.setEnabled(true);
							return true;
						}

						@Override
						protected void done() {
							try {
								get();
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.getCause().printStackTrace();
								String[] choices = { Messages.getString("Main.Window.Error.Close"), //$NON-NLS-1$
										Messages.getString("Main.Window.Error.More") }; //$NON-NLS-1$
								updateInfo(InfoText.ERROR);
								run.setEnabled(true);
								if (JOptionPane.showOptionDialog(options,
										String.format(Messages.getString("Main.Window.Error.ProblemLabel"), //$NON-NLS-1$
												e.getCause().toString()),
										Messages.getString("Main.Window.Error.Error"), JOptionPane.DEFAULT_OPTION, //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE, null, choices, choices[0]) == 1) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw));
									JTextArea jta = new JTextArea(25, 50);
									jta.setText(String.format(Messages.getString("Main.Window.Error.FullTrace"), //$NON-NLS-1$
											sw.toString()));
									jta.setEditable(false);
									JOptionPane.showMessageDialog(options, new JScrollPane(jta),
											Messages.getString("Main.Window.Error.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
								}
							}
						}
					};
					run.setEnabled(false);
					sw.execute();
				} else
					updateInfo(InfoText.SELECT_PROMPT);
			}
		});

		options.pack();
		options.setVisible(true);
	}

	/**
	 * Disables user inputs while the program is running.
	 * 
	 * Sometimes we pull data right from the inputs; if they were changed while
	 * running that would be bad
	 * 
	 * @see Main#unlockInputs()
	 */
	private static void lockInputs() {
		inputLocNum.setEditable(false);
		inputSite.setEditable(false);
		inputYear.setEditable(false);
		selectInput.setEnabled(false);
		selectOutput.setEnabled(false);
	}
	
	/**
	 * Re-enables user inputs after the program is done running
	 * 
	 * @see Main#lockInputs()
	 */
	private static void unlockInputs() {
		inputLocNum.setEditable(true);
		inputSite.setEditable(true);
		inputYear.setEditable(true);
		selectInput.setEnabled(true);
		selectOutput.setEnabled(true);
	}
	
	/**
	 * Copies photos from the given directory to the output directory ({@link Main#selectedFiles}[1])
	 * 
	 * Recursive, will call itself on subdirectories
	 * 
	 * @param directory working directory
	 * @throws IOException if there's an error copying the photos
	 */
	private static void copyPhotos(File directory) throws IOException {
		if (directory.isDirectory()) {
			File[] subdirs = directory.listFiles(new DirectoryFilter());
			File[] images = directory.listFiles(new ImageFileFilter());

			for (File subdir : subdirs)
				copyPhotos(subdir);

			for (File image : images)
				copyImage(image);
		}
	}

	/**
	 * Regular expression for the information that should be within the path of an image file: 'AB' 6 digits - text - text (- ELEVATIONS)
	 */
	static final Pattern IMAGE_NAME_REGEX = Pattern
			.compile("(AB\\d{6})\\s+-\\s+(.+?)\\s+-\\s+(.+?)(?:\\s+-\\s+ELEVATIONS)?\\\\"); //$NON-NLS-1$
	/**
	 * Regular expression for the cardinal directions: one of 'N', 'E', 'S', or 'W'
	 */
	static final Pattern CARDINAL_REGEX = Pattern.compile("[NESW]"); //$NON-NLS-1$

	/**
	 * Copies the given image to the output directory ({@link Main#selectedFiles}[1]), gathering all the relevant information for the new file name
	 * 
	 * @param image the image File to copy
	 * @throws IOException if there's an error copying the image
	 */
	private static void copyImage(File image) throws IOException {
		Matcher match = IMAGE_NAME_REGEX.matcher(image.getAbsolutePath());
		Matcher cardinalMatch = CARDINAL_REGEX.matcher(image.getName());
		if (match.find() && cardinalMatch.find()) {
			String maximo = match.group(1);
			String name = match.group(2);
			// String buildingNum = match.group(3);
			String cardinalDir = cardinalMatch.group();
			boolean isElevations = image.getAbsolutePath().contains("ELEVATIONS"); //$NON-NLS-1$

			copyFile(image,
					String.format(Messages.getString("Main.File.OutputFormat"), selectedFiles[1].getAbsolutePath(), //$NON-NLS-1$
							inputYear.getText(), inputLocNum.getText(), inputSite.getText(), maximo,
							isElevations ? Messages.getString("Main.File.Elevations") //$NON-NLS-1$
									: Messages.getString("Main.File.Building"), //$NON-NLS-1$
							cardinalDir, name),
					"." + FilenameUtils.getExtension(image.getName())); //$NON-NLS-1$
		} else {
			writeToInfo.write(String.format(Messages.getString("Main.File.InfoFormat"), //$NON-NLS-1$
					image.getAbsolutePath()));
		}

	}
	
	/**
	 * Checks if the user has selected files and if the inputs are valid
	 * @return true if the selections are correct
	 */
	private static boolean checkCorrectSelections() {
		return inputSite.isValid && inputLocNum.isValid && inputYear.isValid && selectedFiles[0].isDirectory()
				&& selectedFiles[1].isDirectory();
	}
	
	/**
	 * Updates the text in {@link Main#info} to a new value
	 * @param text the value to update to
	 */
	static void updateInfo(InfoText text) {
		infoText = text;
		info.setText(getInfoText());
		options.pack();
	}
	
	/**
	 * Copies a file - if the specified output path already exists, adds a number to the end
	 * 
	 * @param file the file to copy
	 * @param outPath the path to copy to
	 * @param ext the file extension
	 * @throws IOException if there's an error copying the file
	 */
	static void copyFile(File file, String outPath, String ext) throws IOException {

		String newOutPath = outPath;

		File outFile = new File(newOutPath + ext);

		int count = 1;
		boolean didCount = false;

		while (outFile.isFile()) {
			newOutPath = outPath;
			didCount = true;
			newOutPath += String.format(" (%s)", count); //$NON-NLS-1$
			count++;
			outFile = new File(newOutPath + ext);
		}

		FileUtils.copyFile(file, outFile);

		if (didCount)
			writeToInfo.write(String.format(Messages.getString("Main.File.InfoDuplicate"), outPath + ext)); //$NON-NLS-1$
	}
}

/**
 * A file filter that only accepts directories
 * @author Jaden
 */
class DirectoryFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {
		return arg0.isDirectory();
	}
}

/**
 * A file filter that only accepts image files - files with an extension in ImageExtensions.dat
 * @author Jaden
 */
class ImageFileFilter implements FilenameFilter {

	static final String[] IMAGE_EXTENSIONS = Messages.getExtensions();
	static final List<String> IMAGE_EXTENSIONS_LIST = Arrays.asList(IMAGE_EXTENSIONS);

	public boolean accept(File arg0, String arg1) {
		return IMAGE_EXTENSIONS_LIST.contains(FilenameUtils.getExtension(arg1).toLowerCase());
	}
}
