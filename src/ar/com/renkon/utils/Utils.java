package ar.com.renkon.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

public class Utils 
{
	public static Point getCenterOf(final JFrame frame) {
		Dimension size = (Toolkit.getDefaultToolkit().getScreenSize());
		return new Point(size.width / 2 - frame.getWidth() / 2, size.height / 2 - frame.getHeight() / 2);
	}
	
	public static Dimension maxSizeOfScreen() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public static int getX(JFrame frame)
	{
		return new Double(getCenterOf(frame).getX()).intValue();
	}
	
	public static int getY(JFrame frame)
	{
		return new Double(getCenterOf(frame).getY()).intValue();
	}
	
	public static String getHomeDirectory()
	{
		return getHomeDirectoryAsFile().toString();
	}
	public static File getHomeDirectoryAsFile()
	{
		return FileSystemView.getFileSystemView().getHomeDirectory();
	}
	
	public static String combinePath(String path1, String path2)
	{
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    return file2.getPath();
	}
	
	public static String getOutputDefaultName()
	{
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String fileName = date + ".txt";
		return combinePath(getHomeDirectory(), fileName);
	}

	public static void copyToClipboard(String str)
	{
		StringSelection stringSelection = new StringSelection(str);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
}
