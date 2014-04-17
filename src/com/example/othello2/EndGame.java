package com.example.othello2;

public class EndGame {
	
	public static final int NO_END = -1;
	public static final int BLACK_WIN = 1;
	public static final int WHITE_WIN = 2;
	public static final int DRAW = 3;	
	public static final int UNEXPECTED = 4;
	
	public int reason;
	public boolean result;
	
	public EndGame(boolean result, int reason){
		this.reason = reason;
		this.result = result;
	}

}
