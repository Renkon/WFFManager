package ar.com.renkon.main;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import ar.com.renkon.ui.WFFFrame;

public class Launcher {

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
					e.printStackTrace();
				}
			}
		});
	}

}
