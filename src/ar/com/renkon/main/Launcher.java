package ar.com.renkon.main;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import ar.com.renkon.ui.WFFFrame;
import ar.com.renkon.utils.LoggerFactory;

public class Launcher {

	private static final Logger logger = LoggerFactory.getClassLogger(Launcher.class.getSimpleName());
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					new WFFFrame();
				} 
				catch (Exception e) 
				{
					JOptionPane.showMessageDialog(null, e.toString(), "A critical error occurred", JOptionPane.ERROR_MESSAGE);
					logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		});
	}

}
