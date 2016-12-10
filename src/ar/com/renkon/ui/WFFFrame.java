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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class WFFFrame extends JFrame 
{
	private JTextField textFieldOutput;
	private JFrame self;
	private boolean loaded = false;
	private DataController dc = new DataController(this);
	private JLabel[] labelMaps = new JLabel[20];
	private JLabel[][] labelPoints = new JLabel[20][4];
	private JPositionedComboBox[][] comboPoints = new JPositionedComboBox[20][4];
	private JList<Player> list;
	
	public WFFFrame()
	{
		self = this;
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocation(Utils.getX(this), Utils.getY(this));
		this.setTitle("WFF Manager");
		ImageIcon icon = new ImageIcon(getClass().getResource("/ar/com/renkon/ui/fotl.png"));
		this.setIconImage(icon.getImage());
		getContentPane().setLayout(null);
		
		DefaultListModel<Player> listModel = new DefaultListModel<Player>();
		
		JLabel lblWffManager = new JLabel("WFF Manager - v1.0");
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
				int fcResult = fcSaveFile.showSaveDialog(self);
				if (fcResult == JFileChooser.APPROVE_OPTION)
				{
					textFieldOutput.setText(fcSaveFile.getSelectedFile().toString());
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
						for (int i = 0; i < 20; i++)
						{
							Object[] players = dc.getPlayers().toArray();
							for (int j = 0; j < 4; j++)
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
		
		JButton btnCurrentStandings = new JButton("Copy standings");
		btnCurrentStandings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (!loaded)
				{
					JOptionPane.showMessageDialog(self, "You haven't loaded referee's file yet", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dc.currentStandings();
				JOptionPane.showMessageDialog(self, "Results copied to clipboard (ctrl + v)\nBeware, if copied string is too large you may need to split it", "COPIED", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnCurrentStandings.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCurrentStandings.setBounds(659, 462, 121, 32);
		getContentPane().add(btnCurrentStandings);
		
		JLabel lblTable = new JLabel("           Mapname                         3 points                           2 points                           1 point                              1 point");
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTable.setBounds(20, 35, 600, 14);
		getContentPane().add(lblTable);
		
		JLabel lblRightClickA = new JLabel("For DNS right click player");
		lblRightClickA.setBounds(660, 431, 120, 20);
		getContentPane().add(lblRightClickA);
		
		for (int i = 0; i < 20; i++)
		{
			labelMaps[i] = new JLabel((i + 1) + ". undefined");
			labelMaps[i].setBounds(10, 55 + i * 22, 140, 20);
			getContentPane().add(labelMaps[i]);
			for (int j = 0; j < 4; j++)
			{
				labelPoints[i][j] = new JLabel("(0)");
				labelPoints[i][j].setBounds(255 + j * 125 + 5, 55 + i * 22, 30, 20);
				comboPoints[i][j] = new JPositionedComboBox(i, j);
				comboPoints[i][j].setBounds(155 + j * 125, 55 + i * 22, 100, 20);
				comboPoints[i][j].addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (loaded)
						{
							try
							{
								Player affected = (Player) e.getItem();
								int i = ((JPositionedComboBox) e.getSource()).getI();
								int j = ((JPositionedComboBox) e.getSource()).getJ();
								if (e.getStateChange() == ItemEvent.SELECTED)
								{
									switch(j)
									{
										case 0: affected.addFirstPosition(i); 
										break;
										case 1: affected.addSecondPosition(i); 
										break;
										case 2: affected.addThirdPosition(i);
										break;
										case 3: affected.addForthPosition(i);
									}
									labelPoints[i][j].setText("(" + affected.getPointsBeforeRound(i) + ")");
								}
								else if (e.getStateChange() == ItemEvent.DESELECTED)
								{
									labelPoints[i][j].setText("(0)");
									switch(j)
									{
										case 0: affected.removeFirstPosition(i); 
										break;
										case 1: affected.removeSecondPosition(i); 
										break;
										case 2: affected.removeThirdPosition(i); 
										break;
										case 3: affected.removeForthPosition(i);
									}
								}
							}
							catch (Exception ex) {
								ex.printStackTrace();
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
