package ar.com.renkon.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ar.com.renkon.controller.DataController;
import ar.com.renkon.model.Player;
import ar.com.renkon.utils.LoggerFactory;
import ar.com.renkon.utils.Utils;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class WFFFrame extends JFrame 
{
	private static final Logger logger = LoggerFactory.getClassLogger(WFFFrame.class.getSimpleName());
	private JTextField textFieldOutput;
	private JFrame self;
	private boolean loaded = false;
	private DataController dc = new DataController(this);
	private JLabel[] labelMaps = new JLabel[13];
	private JLabel[][] labelPoints = new JLabel[13][4];
	private JPositionedComboBox[][] comboPoints = new JPositionedComboBox[13][4];
	private JList<Player> list;
	
	public WFFFrame()
	{
		self = this;
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 610);
		this.setLocation(Utils.getX(this), Utils.getY(this));
		this.setTitle("WFF Manager");
		ImageIcon icon = new ImageIcon(getClass().getResource("/ar/com/renkon/ui/fotl.png"));
		this.setIconImage(icon.getImage());
		getContentPane().setLayout(null);
		
		DefaultListModel<Player> listModel = new DefaultListModel<Player>();
		
		JLabel lblWffManager = new JLabel("Lucky Seven DM Manager - v1.0");
		lblWffManager.setFont(new Font("Calibri", Font.PLAIN, 24));
		lblWffManager.setHorizontalAlignment(SwingConstants.CENTER);
		lblWffManager.setForeground(new Color(128, 0, 0));
		lblWffManager.setBounds(10, 11, 774, 20);
		getContentPane().add(lblWffManager);
		
		JCheckBox chckbxAutosave = new JCheckBox("Autosave file");
		chckbxAutosave.setBounds(687, 542, 97, 23);
		chckbxAutosave.setSelected(true);
		getContentPane().add(chckbxAutosave);
		
		textFieldOutput = new JTextField();
		textFieldOutput.setBounds(111, 543, 350, 20);
		getContentPane().add(textFieldOutput);
		textFieldOutput.setColumns(10);
		textFieldOutput.setText(Utils.getOutputDefaultName());
		
		JFileChooser fcLoadFile = new JFileChooser(Utils.getHomeDirectoryAsFile());
		JFileChooser fcSaveFile = new JFileChooser(Utils.getHomeDirectoryAsFile());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt", "text");
		fcLoadFile.setFileFilter(filter);
		fcSaveFile.setFileFilter(filter);
		
		JLabel lblOutputLocation = new JLabel("Output location:");
		lblOutputLocation.setBounds(10, 546, 91, 14);
		getContentPane().add(lblOutputLocation);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				logger.log(Level.INFO, "Button browse pressed");
				int fcResult = fcSaveFile.showSaveDialog(self);
				if (fcResult == JFileChooser.APPROVE_OPTION)
				{
					try 
					{
						new FileOutputStream(fcSaveFile.getSelectedFile(), true).close();
						textFieldOutput.setText(fcSaveFile.getSelectedFile().toString());
					} 
					catch (IOException ex) 
					{
						JOptionPane.showMessageDialog(self, "Beware, you have selected an invalid destination path!", "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnBrowse.setBounds(471, 542, 89, 23);
		getContentPane().add(btnBrowse);
		
		JButton btnLoadRefereee = new JButton("Load referee file");
		btnLoadRefereee.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnLoadRefereee.addActionListener(new ActionListener() {
			@SuppressWarnings("rawtypes")
			public void actionPerformed(ActionEvent e) 
			{
				logger.log(Level.INFO, "Button load pressed");
				int fcResult = fcLoadFile.showOpenDialog(self);
				if (fcResult == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fcLoadFile.getSelectedFile();
					if (!selectedFile.exists())
					{
						JOptionPane.showMessageDialog(self, "Selected file does not exist", "FILE NOT FOUND", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try 
					{
						dc.loadFile(selectedFile);
						for (Player p : dc.getPlayers())
							listModel.addElement(p);
						for (int i = 0; i < 13; i++)
						{
							Object[] players = dc.getPlayers().toArray();
							for (int j = 0; j < 1; j++)
							{
								AutoCompleteSupport a = AutoCompleteSupport.install(comboPoints[i][j], GlazedLists.eventListOf(players));
								comboPoints[i][j].setAutoCompleteSupport(a);
							}
							labelMaps[i].setText(dc.getMaps().get(i));
						}
						loaded = true;
						btnLoadRefereee.setEnabled(false);
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(self, ex.toString(), "Exception found", JOptionPane.ERROR_MESSAGE);
						logger.log(Level.SEVERE, ex.toString(), ex);
					}
				}
			}
		});
		btnLoadRefereee.setBounds(20, 505, 760, 32);
		getContentPane().add(btnLoadRefereee);
		
		JButton btnSaveOutput = new JButton("Save output");
		btnSaveOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				logger.log(Level.INFO, "Button save pressed");
				if (!loaded)
				{
					JOptionPane.showMessageDialog(self, "You haven't loaded referee's file yet", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try 
				{
					dc.outputToFile(textFieldOutput.getText());
					JOptionPane.showMessageDialog(self, "Saved successfully in " + textFieldOutput.getText(), "SAVED", JOptionPane.INFORMATION_MESSAGE);
				} 
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(self, ex.toString(), "Error while trying to save", JOptionPane.ERROR_MESSAGE);
					logger.log(Level.SEVERE, ex.toString(), ex);
				}
			}
		});
		btnSaveOutput.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSaveOutput.setBounds(659, 48, 120, 40);
		getContentPane().add(btnSaveOutput);
		
		list = new JList<Player>(listModel);
		
		list.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,Color.BLACK));
                return listCellRendererComponent;
            }
            
            @Override
            public int getHorizontalAlignment()
            {
				return SwingConstants.CENTER;
            }
            
        });
		list.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					list.setSelectedIndex(getRow(e.getPoint()));
					JPopupMenu menu = new JPopupMenu();
					Player p = list.getSelectedValue();
					JMenuItem item = new JMenuItem(p.isDidShow() ? "Player didn't show" : "Player did show");
					item.addActionListener(new ActionListener() {	
						@Override
						public void actionPerformed(ActionEvent e) {
							p.setDidShow(!p.isDidShow());							
						}
					});
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setBounds(659, 120, 120, 300);
		getContentPane().add(listScroll);
		
		JLabel lblPlayerList = new JLabel("Player list");
		lblPlayerList.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerList.setBounds(658, 99, 122, 14);
		getContentPane().add(lblPlayerList);
		
		JButton btnCurrentStandings = new JButton("Copy BBCode");
		btnCurrentStandings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				logger.log(Level.INFO, "Button copy to clipboard pressed");
				if (!loaded)
				{
					JOptionPane.showMessageDialog(self, "You haven't loaded referee's file yet", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dc.copyCode();
				JOptionPane.showMessageDialog(self, "BBCode copied to clipboard (ctrl + v)", "COPIED", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnCurrentStandings.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCurrentStandings.setBounds(659, 462, 121, 32);
		getContentPane().add(btnCurrentStandings);
		
		JLabel lblTable = new JLabel("           Mapname                         1 point                             State                                                                             ");
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTable.setBounds(20, 35, 600, 14);
		getContentPane().add(lblTable);
		
		JLabel lblRightClickA = new JLabel("For DNS right click player");
		lblRightClickA.setBounds(660, 431, 120, 20);
		getContentPane().add(lblRightClickA);
		
		for (int i = 0; i < 13; i++)
		{
			labelMaps[i] = new JLabel((i + 1) + ". undefined");
			labelMaps[i].setBounds(10, 55 + i * 22, 140, 20);
			getContentPane().add(labelMaps[i]);
			for (int j = 0; j < 1; j++)
			{
				labelPoints[i][j] = new JLabel("no result yet");
				labelPoints[i][j].setBounds(290 + j * 125 + 5, 55 + i * 22, 100, 20);
				comboPoints[i][j] = new JPositionedComboBox(i, j);
				comboPoints[i][j].setBounds(155 + j * 125, 55 + i * 22, 100, 20);
				comboPoints[i][j].addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (loaded)
						{
							try
							{
								Player p1 = (Player) ((JPositionedComboBox) e.getSource()).getItemAt(0);
								Player p2 = (Player) ((JPositionedComboBox) e.getSource()).getItemAt(1);
								Player affected = (Player) e.getItem();
								int i = ((JPositionedComboBox) e.getSource()).getI();
								int j = ((JPositionedComboBox) e.getSource()).getJ();
								if (e.getStateChange() == ItemEvent.SELECTED)
								{
									switch(j)
									{
										case 0: affected.addFirstPosition(i); 
										break;
									}
									labelPoints[i][j].setText(p1.getName() + " " + p1.getPointsBeforeRound(i)
											+ " : " + p2.getPointsBeforeRound(i) + " " + p2.getName());
								}	
								else if (e.getStateChange() == ItemEvent.DESELECTED)
								{
									switch(j)
									{
										case 0: affected.removeFirstPosition(i); 
										break;
									}
									labelPoints[i][j].setText("no result yet");
								}
							}
							catch (Exception ex) {
								if (!((JPositionedComboBox) e.getSource()).getAutoCompleteSupport().isStrict())
									((JPositionedComboBox) e.getSource()).getAutoCompleteSupport().setStrict(true);
							}
						}
					}
				});
				getContentPane().add(comboPoints[i][j]);
				getContentPane().add(labelPoints[i][j]);
			}
		}
		
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	    service.scheduleWithFixedDelay(new Runnable(){
        @Override
        public void run()
        {
        	if (chckbxAutosave.isSelected())
        	{
	        	try 
	        	{
	        		dc.outputToFile(textFieldOutput.getText());
	        	} 
	        	catch (Exception e) 
	        	{
	        		JOptionPane.showMessageDialog(self, e.toString(), "Error while trying to save automatically", JOptionPane.ERROR_MESSAGE);
	        	}
        	}
        }
      }, 2, 2, TimeUnit.MINUTES);
		this.setVisible(true);
	}
	
	public JPositionedComboBox[][] getComboPoints()
	{
		return comboPoints;
	}

	private int getRow(Point point)
	{
		return list.locationToIndex(point);
	}
}
