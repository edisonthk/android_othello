package com.example.othello2;

/**
 * This class are used for ai.
 * When result is get from searching algorithm, this (SearchCoordinate) class
 * will be returned
 */
public class SearchCoordinate {	
	public int x,y,score;
	public SearchCoordinate(int x,int y,int score){
		this.x = x;
		this.y = y;
		this.score = score; 
	}

}
