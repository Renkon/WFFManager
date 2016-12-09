package ar.com.renkon.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ar.com.renkon.model.Player;
import ar.com.renkon.ui.JPositionedComboBox;
import ar.com.renkon.ui.WFFFrame;
import ar.com.renkon.utils.Utils;

public class DataController 
{
	private List<Player> players = new ArrayList<Player>();
	private List<String> maps = new ArrayList<String>();
	private String tournament, stage, refereeName;
	private WFFFrame frame;
	
	public DataController(WFFFrame frame)
	{
		this.frame = frame;
	}
	
	public void outputToFile(String destination) throws Exception
	{
		PrintWriter writer = new PrintWriter(destination, "UTF-8");
		writer.println("[b]" + tournament + "[/b], [b]" + stage + "[/b] with " + refereeName);
		writer.println("");
		writer.println("[table]");
		writer.println("[td]");
		writer.println("[tr]");
		writer.println("[td]Mapname[/td]");
		writer.println("[td]3 points[/td]");
		writer.println("[td]2 points[/td]");
		writer.println("[td]1 point[/td]");
		writer.println("[td]1 point[/td]");
		writer.println("[/tr]");
		writer.println("");
		writer.println("");
		for (int i = 0; i < maps.size(); i++)
		{
			JPositionedComboBox[] combos = frame.getComboPoints()[i];
			writer.println("[tr]");
			writer.println("[td]" + maps.get(i) + "[/td]");
			for (int j = 0; j < combos.length; j++)
				writer.println("[td]" + (combos[j].getSelectedItem() == null ? " " : ((Player) combos[j].getSelectedItem()).getName() + " (" + ((Player) combos[j].getSelectedItem()).getPointsBeforeRound(i) +  ")") + "[/td]");
			writer.println("[/tr]");
		}
		writer.println("");
		writer.println("[/table]");
		writer.println("");
		writer.println("");
		List<Player> sortedPlayers = getPlayersSorted();
		for (int i = 0; i < sortedPlayers.size(); i++)
		{
			if (i <= 2)
				writer.println("[b]" + sortedPlayers.get(i).getPointsAsString() + " " + sortedPlayers.get(i).getName() + "[/b]");
			else
				writer.println(sortedPlayers.get(i).getPointsAsString() + " " + sortedPlayers.get(i).getName());
		}
		writer.println("");
		writer.print("Referee: " + refereeName);
		writer.close();
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
		        if (p1.getSecondPositions() != p2.getSecondPositions())
		        {
		        	if (p1.getSecondPositions() > p2.getSecondPositions())
		        		return -1;
		        	else
		        		return 1;
		        }
		        if (p1.getThirdPositions() != p2.getThirdPositions())
		        {
		        	if (p1.getThirdPositions() > p2.getThirdPositions())
		        		return -1;
		        	else
		        		return 1;
		        }
		        if (p1.getForthPositions() != p2.getForthPositions())
		        {
		        	if (p1.getForthPositions() > p2.getForthPositions())
		        		return -1;
		        	else
		        		return 1;
		        }
		        return 0;
		    }
		});
		return currentPlayers;
	}
	
	public void currentStandings()
	{
		List<Player> playersSorted = getPlayersSorted();
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (Player p : playersSorted)
		{
			sb.append(prefix);
			prefix = ", ";
			sb.append(p.getName());
			sb.append(" ");
			sb.append(p.getPointsAsString());
		}
		Utils.copyToClipboard(sb.toString());
	}
	
	public void loadFile(File refereeFile) throws IOException
	{
		List<String> fileLines = Files.readAllLines(refereeFile.toPath());
		// STAGE 0: we clean EMPTY lines
		fileLines.removeAll(Collections.singleton(""));
		// STAGE 1: we need to find <mapsCode> tag to know where data initializes
		Iterator<String> fileIterator = fileLines.iterator();
		while (fileIterator.hasNext()) 
		{
			String nextLine = fileIterator.next();
			if (nextLine.contains("<mapsCode>"))
				break;
		}
		// STAGE 2: Read information regarding the tournament
		String dataLine = fileIterator.next();
		dataLine = dataLine.replace("[b]", "").replace("[/b]", "").replace(" with ", ", ").trim(); // removed bbcode tags and added , 
		String info[] = dataLine.split(", ");
		tournament = info[0];
		stage = info[1];
		refereeName = info[2];
		// STAGE 3: Find map names and save them
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
				break;
			}
		}
		
		// STAGE 4: Find players tag
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
				break;
			}
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
	
}
