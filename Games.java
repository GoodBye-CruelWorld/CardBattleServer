package com.cardServer.server;

public class Games {
	private int playersNum=0;
	private int gameState=0;
	private String playersID[]=new String[2]; 
	
	
	public Games() {
		super();
	}

	boolean addPlayer(String playerID){
		if(playersNum<2){
		playersID[playersNum]=playerID;
		playersNum++;
		return true;
		}
		return false;
	} 
	
	public int getPlayersNum() {
		return playersNum;
	}

	public void setPlayersNum(int playersNum) {
		this.playersNum = playersNum;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}

	public String getPlayersID(int num) {
		return playersID[num];
	}

	public void setPlayersID(String[] playersID) {
		this.playersID = playersID;
	}


	
}
