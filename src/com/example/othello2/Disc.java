package com.example.othello2;

public class Disc {
	public int disc;
	public boolean reversable,isFrontier,isSafe;
	public int x,y;
	public Disc(){
		defaultDisc();
	}
	
	public void defaultDisc(){
		this.disc = GameBoard.EMPTY;
		this.reversable = false;
		this.isFrontier = false;
		this.isSafe = false;
	}
}
