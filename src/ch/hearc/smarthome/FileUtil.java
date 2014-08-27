package ch.hearc.smarthome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

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

	/**
	 * Return if the state of the media is Mounted
	 * 
	 * @return boolean if a SD is mounted on the device
	 */
	public static boolean isMediaMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Creates the directory named by this file, creating missing parent
	 * directories if necessary.
	 * 
	 * @param tree
	 *            The directory name by this file
	 */
	public static void createTree(File tree) {
		tree.mkdirs();
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
