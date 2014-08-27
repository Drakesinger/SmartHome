package ch.hearc.smarthome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Util class used to read/write/check files
 * 
 * @author Roulin, 26.08.14
 * 
 */

public class FileUtil {

	/**
	 * Write in a text file
	 * 
	 * @param file
	 *            in which we write
	 * 
	 * @param text
	 *            that we write
	 * 
	 * @param append
	 *            indicates whether or not to append to an existing file
	 * 
	 * @throws IOException
	 *             Input/Output exceptions
	 * 
	 * @return content of 'file'
	 */
	public static void writeTextFile(File file, String text, boolean append)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
		writer.write(text);
		writer.close();
	}

	/**
	 * Read a text file
	 * 
	 * @param file
	 *            Which is read
	 * 
	 * @throws IOException
	 *             Input/Output exceptions
	 * 
	 * @return content of @param file
	 */
	public static String readTextFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			text.append(line);
			text.append("\n");
		}
		reader.close();
		return text.toString();
	}

	public static boolean isMediaMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static void createTree(File tree) {
		tree.mkdirs();
	}

	/**
	 * Return if a file is accessible
	 * 
	 * @param c
	 *            The context is used to toast the user about why a problem
	 *            could have happened
	 * 
	 * @param file
	 *            The file which is checked
	 * 
	 * @return if the file is accessible
	 */
	public static boolean isFileUsable(Context c, File folder, String filename) {
		String state = Environment.getExternalStorageState();
		boolean fileState = false;

		// Check if there's a SD card
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// Create folder if not existing
			if (!folder.exists()) {
				folder.mkdirs();
				Log.d("RDWR", "Create: Directory: " + folder.getAbsolutePath());
			}

			File file = new File(folder.getAbsolutePath() + File.separator
					+ filename);
			// Try creating file if not existing
			if (file.exists()) {
				fileState = true;
			}

			Toast.makeText(c, "No save yet", Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(c, "No SD mounted", Toast.LENGTH_SHORT).show();
		}

		return fileState;
	}

	/**
	 * Create file if !exist
	 * 
	 * @param filePath
	 *            The absolute file path we need to create
	 * 
	 * @throws IOException
	 *             Input/Output exceptions
	 */
	public static void createFile(File filePath) {
		if (!filePath.exists()) {
			try {
				filePath.createNewFile();
				Log.d("RDWR", "createFile: " + filePath);
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("RDWR", "CreateFile: " + e.getMessage());
			}
		}
	}
}
