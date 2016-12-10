package ar.com.renkon.model;

public class Player 
{
	private String name;
	private int firstPositions = 0, secondPositions = 0, thirdPositions = 0, forthPositions = 0;
	private boolean didShow = true;
	public int[] pointsPerRound = new int[20];
	
	public Player(String name)
	{
		this.name = name;
	}
	
	public void addFirstPosition(int round){
		firstPositions++;
		pointsPerRound[round] += 3;
	}
	
	public void addSecondPosition(int round){
		secondPositions++;
		pointsPerRound[round] += 2;
	}
	
	public void addThirdPosition(int round){
		thirdPositions++;
		pointsPerRound[round] += 1;
	}
	
	public void addForthPosition(int round){
		forthPositions++;
		pointsPerRound[round] += 1;
	}
	
	public void removeFirstPosition(int round){
		firstPositions--;
		pointsPerRound[round] -= 3;
	}
	
	public void removeSecondPosition(int round){
		secondPositions--;
		pointsPerRound[round] -= 2;
	}
	
	public void removeThirdPosition(int round){
		thirdPositions--;
		pointsPerRound[round] -= 1;
	}
	
	public void removeForthPosition(int round){
		forthPositions--;
		pointsPerRound[round] -= 1;
	}
	
	public int getFirstPositions() {
		return firstPositions;
	}

	public int getSecondPositions() {
		return secondPositions;
	}

	public int getThirdPositions() {
		return thirdPositions;
	}

	public int getForthPositions() {
		return forthPositions;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPoints() {
		int points = 0;
		if (!isDidShow())
			return -1;
		for (int i = 0; i < pointsPerRound.length; i++){
			points += pointsPerRound[i];
		}
		return points;
	}
	
	public int getPointsBeforeRound(int round)
	{
		int points = 0;
		for (int i = 0; i <= round; i++)
			points += pointsPerRound[i];
		return points;
	}
	
	public String getPointsAsString()
	{
		if (!isDidShow())
			return "DNS";
		return Integer.toString(getPoints());
	}
	
	public boolean isDidShow() {
		return didShow;
	}
	
	public void setDidShow(boolean didShow) {
		this.didShow = didShow;
	}
	
	public String toString()
	{
		if (isDidShow())
			return getName();
		return "<html><strike>" + getName() + "</strike></html>";
	}
}
