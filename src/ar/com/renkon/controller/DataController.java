package ar.com.renkon.controller;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ar.com.renkon.model.Player;
import ar.com.renkon.ui.JPositionedComboBox;
import ar.com.renkon.ui.WFFFrame;
import ar.com.renkon.utils.LoggerFactory;
import ar.com.renkon.utils.Utils;

public class DataController 
{
	private static final Logger logger = LoggerFactory.getClassLogger(DataController.class.getSimpleName());
	private List<Player> players = new ArrayList<Player>();
	private List<String> maps = new ArrayList<String>();
	private String tournament, stage, refereeName = null, trialRefereeName = null, assistantName = null;
	private WFFFrame frame;
	
	public DataController(WFFFrame frame)
	{
		this.frame = frame;
	}
	
	public ArrayList<String> getOutput()
	{
		ArrayList<String> output = new ArrayList<String>();
		output.add("[b]" + tournament + "[/b], [b]" + stage + "[/b] with " + refereeName);
		output.add("");
		output.add("[table]");
		output.add("[td]");
		output.add("[tr]");
		output.add("[td]Mapname[/td]");
		output.add("[td]1 point[/td]");
		output.add("[td]Players alive[/td]");
		output.add("[/tr]");
		output.add("");
		output.add("");
		for (int i = 0; i < maps.size(); i++)
		{
			JPositionedComboBox[] combos = frame.getComboPoints()[i];
			Player p1 = (Player) combos[0].getItemAt(0);
			Player p2 = (Player) combos[0].getItemAt(1);
			output.add("[tr]");
			output.add("[td]" + maps.get(i) + "[/td]");
			output.add("[td]" + p1.getName() + " " + p1.getPointsBeforeRound(i) + " : " + 
					p2.getPointsBeforeRound(i) + " " + p2.getName() + "[/td]");
			output.add("[td]" + frame.getAlivePlayers()[i].getText().replace("Ex.: Council, Tails, Renkon, ..." ,"") + "[/td]");
			output.add("[/tr]");
		}
		output.add("");
		output.add("[/table]");
		output.add("");
		output.add("");
		List<Player> sortedPlayers = getPlayersSorted();
		for (int i = 0; i < sortedPlayers.size(); i++)
		{
			if (i <= 2)
				output.add("[b]" + sortedPlayers.get(i).getPointsAsString() + " " + sortedPlayers.get(i).getName() + "[/b]");
			else
				output.add(sortedPlayers.get(i).getPointsAsString() + " " + sortedPlayers.get(i).getName());
		}
		output.add("");
		output.add("Referee: " + refereeName);
		if (assistantName != null)
			output.add("Assistant: " + assistantName);
		else
			output.add("Trial referee: " + trialRefereeName);
		return output;
	}
	
	public void outputToFile(String destination) throws Exception
	{
		try
		{
			logger.log(Level.INFO, "Outputting to " + destination);
			PrintWriter writer = new PrintWriter(destination, "UTF-8");
			for (String s : getOutput())
				writer.println(s);
			writer.close();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	public List<Player> getPlayersSorted()
	{
		List<Player> currentPlayers = new ArrayList<Player>(players);
		Collections.sort(currentPlayers, new Comparator<Player>() {
		    @Override
		    public int compare(Player p1, Player p2) {
		        if (p1.getPoints() != p2.getPoints())
		        {
		        	if (p1.getPoints() > p2.getPoints())
		        		return -1;
		        	else
		        		return 1;
		        }
		        if (p1.getFirstPositions() != p2.getFirstPositions())
		        {
		        	if (p1.getFirstPositions() > p2.getFirstPositions())
		        		return -1;
		        	else
		        		return 1;
		        }
		        return 0;
		    }
		});
		return currentPlayers;
	}
	
	public void copyCode()
	{
		StringBuilder sb = new StringBuilder();
		for (String s : getOutput())
		{
			sb.append(s + "\n");
		}
		Utils.copyToClipboard(sb.toString());
	}
	
	public void loadFile(File refereeFile)
	{
		logger.log(Level.INFO, "Initializing file load from " + refereeFile.getPath());
		try
		{
			List<String> fileLines = Files.readAllLines(refereeFile.toPath());
			// STAGE 0: we clean EMPTY lines
			fileLines.removeAll(Collections.singleton(""));
			// STAGE 1: we load refereeName, assistant and trial referee name
			Iterator<String> fileIterator = fileLines.iterator();
			while (fileIterator.hasNext())
			{
				String line = fileIterator.next();
				if (line.startsWith("Referee: ")) // Referee name!
					refereeName = line.replace("Referee: ", "");
				if (line.startsWith("Assistant: ")) // Assistant name!
					assistantName = line.replace("Assistant: ", "");
				if (line.startsWith("Trial referee: ") || line.startsWith("Trial Referee: "))
					trialRefereeName = line.replace("Trial referee: ", "").replace("Trial Referee: ", "");
				if (refereeName != null)
				{
					if (assistantName != null || trialRefereeName != null)
						break;
				}
			}
			logger.log(Level.INFO, "Loaded initial data (ref, assist and trial)");
			// STAGE 2: we need to find <mapsCode> tag to know where data initializes
			while (fileIterator.hasNext()) 
			{
				String nextLine = fileIterator.next();
				if (nextLine.contains("<mapsCode>"))
					break;
			}
			// STAGE 3: Read information regarding the tournament
			String dataLine = fileIterator.next();
			dataLine = dataLine.replace("[b]", "").replace("[/b]", "").replace(" with ", ", ").trim(); // removed bbcode tags and added , 
			String info[] = dataLine.split(", ");
			tournament = info[0];
			stage = info[1];
			// STAGE 4: Find map names and save them
			while (fileIterator.hasNext())
			{
				// We make sure its like this: [td]X. Map name[/td]
				String lineText = fileIterator.next();
				if (lineText.contains("[td]") && lineText.contains("[/td]") && lineText.contains("."))
				{
					maps.add(lineText.replace("[td]", "").replace("[/td]", "").trim());
				}
				if (lineText.contains("</mapsCode>"))
				{
					// Success, parsed maplist
					logger.log(Level.INFO, "Parsed maplist");
					break;
				}
			}
			
			// STAGE 5: Find players tag
			while (fileIterator.hasNext())
			{
				String line = fileIterator.next();
				if (line.contains("<playerList>"))
				{
					// We found players now, time to loop
					while(!line.contains("</playerList>"))
					{
						line = fileIterator.next();
						if (!line.contains("</playerList>"))
							players.add(new Player(line));
					}
					logger.log(Level.INFO, "Parsed playerlist");
					break;
				}
			}
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<String> getMaps() {
		return maps;
	}

	public String getTournament() {
		return tournament;
	}

	public String getStage() {
		return stage;
	}

	public String getRefereeName() {
		return refereeName;
	}

	public String getTrialRefereeName() {
		return trialRefereeName;
	}
	
	public String getAssistantName() {
		return assistantName;
	}
}
