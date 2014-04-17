package com.example.othello2;

import java.util.ArrayList;  

import android.util.Log;


public class GameBoard {
	
	private static final String TAG = "GameBoard";
	
	public final static int BLACK = 1;
	public final static int WHITE = 2;
	public final static int EMPTY = 3;
	
	public final static int BOARD_SIDE = 8;
	
	public boolean endGame = false; 
	
	private int round = 0;
	private int totalWhiteDisc = 2;
	private int totalBlackDisc = 2;
	private int totalReversableNumber = 0;
	private int totalBlackFrontierNumber = 0;
	private int totalWhiteFrontierNumber = 0;
	private int totalBlackSafeNumber = 0;
	private int totalWhiteSafeNumber = 0;

	
	private ArrayList<Disc> discToReverse;
	private ArrayList<Disc> movableDisc;
	private Disc[][] disc;
	
	
	public GameBoard(){
		this.disc = new Disc[BOARD_SIDE][BOARD_SIDE];		
		this.discToReverse = new ArrayList<Disc>();
		this.movableDisc = new ArrayList<Disc>();
		defaultBoard();
	}
	
	private GameBoard(boolean clone){
		this.disc = new Disc[BOARD_SIDE][BOARD_SIDE];		
		this.discToReverse = new ArrayList<Disc>();
		this.movableDisc = new ArrayList<Disc>();
	}
	
	public Disc getDisc(int x,int y){
		return disc[y][x];
	}
	
	public GameBoard clone(){
		GameBoard clonedBoard = new GameBoard(true);		
		clonedBoard.totalBlackDisc = this.totalBlackDisc;
		clonedBoard.totalWhiteDisc = this.totalWhiteDisc;
		clonedBoard.totalReversableNumber = this.totalReversableNumber;
		clonedBoard.totalWhiteFrontierNumber = this.totalWhiteFrontierNumber;
		clonedBoard.totalBlackFrontierNumber = this.totalBlackFrontierNumber;
		clonedBoard.totalBlackSafeNumber = this.totalBlackSafeNumber;
		clonedBoard.totalWhiteSafeNumber = this.totalWhiteSafeNumber;	
		clonedBoard.round = this.round;
		clonedBoard.endGame = this.endGame;	
		
		//clonedBoard.movableDisc.clear();
		
		for(Disc d : this.movableDisc){
			Disc addDisc = new Disc();
			addDisc.x = d.x;
			addDisc.y = d.y;			
			clonedBoard.movableDisc.add(addDisc);
		}
		
		
		
		for(int y =0;y < BOARD_SIDE;y++)
			for(int x = 0;x < BOARD_SIDE;x++){
				clonedBoard.disc[y][x] = new Disc();
				clonedBoard.getDisc(x, y).disc = disc[y][x].disc;
				clonedBoard.getDisc(x, y).reversable = disc[y][x].reversable;
				clonedBoard.getDisc(x, y).isFrontier = disc[y][x].isFrontier;
				clonedBoard.getDisc(x, y).isSafe = disc[y][x].isSafe;
			}
		
		
		return clonedBoard;
	}
	
	/**
	 * set the board to default (initial) state
	 */
	public void defaultBoard(){		
		this.round = 0;
		this.totalReversableNumber = 0;
		this.totalBlackDisc = 2;
		this.totalWhiteDisc = 2;
		this.totalWhiteFrontierNumber = 0;
		this.totalBlackFrontierNumber = 0;
		this.totalWhiteSafeNumber = 0;
		this.totalBlackSafeNumber = 0;
		this.endGame = false;
		
		this.movableDisc.clear();
		this.discToReverse.clear();
		
		for(int x = 0;x < BOARD_SIDE;x++)
			for(int y = 0;y < BOARD_SIDE ; y++){
					
				if(disc[y][x] == null) disc[y][x] = new Disc();
				
				disc[y][x].defaultDisc();
				if(y == 3 && x == 3){
					disc[y][x].disc = WHITE;					
				}else if(y == 3 && x == 4){
					disc[y][x].disc = BLACK;
				}else if(y == 4 && x == 4){
					disc[y][x].disc = WHITE;
				}else if(y == 4 && x == 3){
					disc[y][x].disc = BLACK;
				}
			}
		
		
		updateGameContent(BLACK);
	}
		
	
	/**
	 * Called this method after the disc is placed
	 * @param playerDisc
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<Disc> reverse(int playerDisc,int x,int y){		
				
		isReversable(playerDisc,x,y,true);		
		
		return this.discToReverse;
	}
	
	public int getRound(){
		return this.round;
	}
	
	public ArrayList<Disc> getMovableBlock(){
		return this.movableDisc;
	}
	
	
	public void nextRound(){
		this.round ++;
		if(round % 2 == 0){
			// black round
			updateGameContent(BLACK);
		}else{
			// white round
			updateGameContent(WHITE);
		}
	}
	
	public int getReversableNumber(){
		return this.totalReversableNumber;
	}
	
	public void updateGameContent(int playerDisc){
		
		this.totalBlackDisc = 0;
		this.totalWhiteDisc = 0;
		this.totalReversableNumber = 0;
		this.totalBlackFrontierNumber = 0;
		this.totalWhiteFrontierNumber = 0;
		this.totalWhiteSafeNumber = 0;
		this.totalBlackSafeNumber = 0;		
		
		this.movableDisc.clear();
		
		for(int x = 0;x < BOARD_SIDE;x++)
			for(int y = 0;y < BOARD_SIDE ; y++){
								
				if(disc[y][x].disc == EMPTY){
					// this block is empty block
					if(isReversable(playerDisc,x,y,false)){
						disc[y][x].reversable = true;
						Disc movableDisc = new Disc();
						movableDisc.x = x;
						movableDisc.y = y;
						this.movableDisc.add(movableDisc);
						this.totalReversableNumber++;
					}else{
						disc[y][x].reversable = false;
					}					
					
				}else{
					// this block might be black or white 
					disc[y][x].reversable = false;
					disc[y][x].isFrontier = false;
					
					for (int dr = -1; dr <= 1; dr++){
						for (int dc = -1; dc <= 1; dc++){
							if(isInsideBoard(x + dc,y+dr) && !(dr == 0 && dc ==0)){
								if(this.disc[y+dr][x+dc].disc == EMPTY){
									disc[y][x].isFrontier = true;
								}
							}							
						}
					}
										
					if(disc[y][x].disc == BLACK){
						totalBlackDisc++;
						
					
						if(disc[y][x].isFrontier) this.totalBlackFrontierNumber ++;
						if(this.isSafeDisc(x, y)){
							this.disc[y][x].isSafe = true;
							this.totalBlackSafeNumber ++;							
						}					
						
					}else if(disc[y][x].disc == WHITE){
						totalWhiteDisc++;
						
					
						if(disc[y][x].isFrontier) this.totalWhiteFrontierNumber ++;
						if(this.isSafeDisc(x, y)){
							this.disc[y][x].isSafe = true;
							this.totalWhiteSafeNumber ++;
						}	
					}
					
				}		
							
			}
				
	}	
	
	public int getSafeDiscNumber(int disc){
		if(disc == BLACK){
			return this.totalBlackSafeNumber;
		}else{
			return this.totalWhiteSafeNumber;
		}
	}
	
	public int getFrontierNumber(int disc){
		if(disc == BLACK){
			return this.totalBlackFrontierNumber;
		}else{
			return this.totalWhiteFrontierNumber;
		}
		
	}
	
	public int getBlackDiscsNumber(){
		return totalBlackDisc;
	}
	
	public int getWhiteDiscsNumber(){
		return totalWhiteDisc;
	}
	
	/**
	 * get whether if game is end
	 * @return
	 */
	public EndGame checkEndGame(){
		
		int totalWhiteDisc = getWhiteDiscsNumber();
		int totalBlackDisc = getBlackDiscsNumber();
		
		GameBoard nextRoundBoard = this.clone(); 
		nextRoundBoard.nextRound();
		
		if(totalWhiteDisc + totalBlackDisc == (GameBoard.BOARD_SIDE * GameBoard.BOARD_SIDE)){
			
			Log.w(TAG,"gameover all fill up black:"+totalBlackDisc+",white:"+totalWhiteDisc);
			
			if(totalBlackDisc > totalWhiteDisc){
				return new EndGame(true,EndGame.BLACK_WIN);
				
			}else if(totalBlackDisc == totalWhiteDisc){
				return new EndGame(true,EndGame.DRAW);
				
			}else if(totalBlackDisc < totalWhiteDisc){
				return new EndGame(true,EndGame.WHITE_WIN);
				
			}else{
				Log.e(TAG,"EndGame unexpected :1");
				return new EndGame(true,EndGame.UNEXPECTED);
			}
		}else if(totalBlackDisc == 0 || totalWhiteDisc == 0){
			Log.i(TAG,"gameover one side is zero black:"+totalBlackDisc+",white:"+totalWhiteDisc);
			if(totalBlackDisc == 0){
				return new EndGame(true,EndGame.WHITE_WIN);
				
			}else if(totalWhiteDisc == 0){
				return new EndGame(true,EndGame.BLACK_WIN);				
			}else{
				Log.e(TAG,"EndGame unexpected :2");
				return new EndGame(true,EndGame.UNEXPECTED);
				
			}
		}else if(this.getReversableNumber() == 0 &&
				nextRoundBoard.getReversableNumber() == 0){
			// if next round and current round also 0, it means game is end 
			Log.i(TAG,"gameover not more reversable black:"+totalBlackDisc+",white:"+totalWhiteDisc);
			if(totalBlackDisc > totalWhiteDisc){
				return new EndGame(true,EndGame.BLACK_WIN);
				
			}else if(totalBlackDisc == totalWhiteDisc){
				return new EndGame(true,EndGame.DRAW);
				
			}else if(totalBlackDisc < totalWhiteDisc){
				return new EndGame(true,EndGame.WHITE_WIN);
				
			}else{
				Log.e(TAG,"EndGame unexpected :3");
				return new EndGame(true,EndGame.UNEXPECTED);
			}
		}else{
			// There is no reason why the game is end so return false	
			return new EndGame(false,EndGame.NO_END);
		}
		
		
	}
	
	public void showBoardInLog(){		
		for(int y = 0;y < BOARD_SIDE;y++){
			String b = "";
			for(int x = 0;x < BOARD_SIDE ; x++){
				if(disc[y][x].disc == WHITE){
					b += "O";
				}else if(disc[y][x].disc == BLACK){
					b += "@";
				}else if(disc[y][x].disc == EMPTY){
					if(disc[y][x].reversable){
						b += "+";
					}else{
						b += ".";
					}
				}
			}
			Log.d("boardLog",y+" "+b);
		}		
	}
	
	public boolean isSafeDisc(int x0,int y0){
		
		int enemy = WHITE; 
		if(disc[y0][x0].disc == WHITE) enemy = BLACK;
				 
		// check horizontal
		
		boolean hasSpaceSide1 = false;
		boolean hasSpaceSide2 = false;
		boolean hasUnsafeSide1 = false;
		boolean hasUnsafeSide2 = false;
		
		// west
		for(int x = 0;x < x0;x++){
			if(disc[y0][x].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y0][x].disc == enemy || !disc[y0][x].isSafe) hasUnsafeSide1 = true;
		}
		
		// east
		for(int x = BOARD_SIDE - 1;x > x0;x--){
			if(disc[y0][x].disc == EMPTY) hasSpaceSide2 = true;
			else if(disc[y0][x].disc == enemy || !disc[y0][x].isSafe) hasUnsafeSide2 = true;
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
				return false;
		
		// check vertical
		
		hasSpaceSide1  = false;
		hasSpaceSide2  = false;
		hasUnsafeSide1 = false;
		hasUnsafeSide2 = false;
		
		// north
		for(int y = 0;y < y0;y++){
			if(disc[y][x0].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y][x0].disc == enemy || !disc[y][x0].isSafe) hasUnsafeSide1 = true;
		}
		
		// south
		for(int y = BOARD_SIDE - 1;y > y0;y--){
			if(disc[y][x0].disc == EMPTY) hasSpaceSide2 = true;
			else if(disc[y][x0].disc == enemy || !disc[y][x0].isSafe) hasUnsafeSide2 = true;
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
				return false;
		
		
		// check north-west south-east diagonal 
		hasSpaceSide1  = false;
		hasSpaceSide2  = false;
		hasUnsafeSide1 = false;
		hasUnsafeSide2 = false;
		
		// north-west
		for(int x = 0,y = 0; x < x0 && y < y0;){
			
			if(disc[y][x].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y][x].disc == enemy || !disc[y][x].isSafe) hasUnsafeSide1 = true;
			x ++;
			y ++;
		}
		
		// south-east 
		for(int x = BOARD_SIDE-1, y = BOARD_SIDE-1;y > y0 && x > x0;){
			
			if(disc[y][x].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y][x].disc == enemy || !disc[y][x].isSafe) hasUnsafeSide1 = true;			
			x --;
			y --;
		}
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
				return false;
		
		
		// check north-east south-west diagonal 
		hasSpaceSide1  = false;
		hasSpaceSide2  = false;
		hasUnsafeSide1 = false;
		hasUnsafeSide2 = false;
		
		// north east
		for(int x = BOARD_SIDE-1, y=0;x>x0 && y<y0;){
			
			if(disc[y][x].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y][x].disc == enemy || !disc[y][x].isSafe) hasUnsafeSide1 = true;			
			x--;
			y++;
		}
		
		// south-west
		for(int x = 0,y=BOARD_SIDE-1;x<x0 && y>y0;){
			
			if(disc[y][x].disc == EMPTY) hasSpaceSide1 = true;
			else if(disc[y][x].disc == enemy || !disc[y][x].isSafe) hasUnsafeSide1 = true;			
			x++;
			y--;
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
				return false;

		/*
		// All lines are safe so the disc cannot be outflanked.
		if(disc[y0][x0].disc == BLACK){
			Log.i(TAG,"Safe disc:black  x:"+x0+",y:"+y0);
		}else if(disc[y0][x0].disc == WHITE){
			Log.i(TAG,"Safe disc:white  x:"+x0+",y:"+y0);
		}*/		
		return true;		
		
	}
	
	public boolean isReversable(int playerDisc,int x,int y,boolean reverse){
		
		int opponentDisc = WHITE;
		if(playerDisc == WHITE) opponentDisc = BLACK;
		
		
		// {y,x}
		int[][] dir = {
				{-1, -1}, {-1, 0}, {-1 , 1},
				{ 0, -1},          { 0 , 1},
				{ 1, -1}, { 1, 0}, { 1 , 1}
		};
		
		if(reverse){
			discToReverse.clear();
			disc[y][x].disc = playerDisc;
			disc[y][x].reversable = false;
			Disc d0 = new Disc();
			d0.x = x;
			d0.y = y;
			d0.disc = EMPTY;
			discToReverse.add(d0);
		}
		
		boolean reversable = false;	
				
		for(int d = 0;d < dir.length;d++){
			int x0 = x + dir[d][0];
			int y0 = y + dir[d][1];
						
			if(isInsideBoard(x0,y0)){
				
				if(getDisc(x0,y0).disc == opponentDisc){				
				
					for(int i = 1;i < BOARD_SIDE;i++){
						int x1 = x0 + dir[d][0] * i;
						int y1 = y0 + dir[d][1] * i;
						
						if(isInsideBoard(x1,y1)){						
							if(getDisc(x1,y1).disc == playerDisc){
								
								reversable = true;
								if(reverse){						
									int x2 = x0;
									int y2 = y0;
									while(true){
										
										if(x2 == x1 && y2 == y1) break;
										
										//Log.i(TAG,"x2:"+x2+",y2:"+y2);
										
										disc[y2][x2].disc = playerDisc;
										disc[y2][x2].reversable = false;
										
										Disc disc = new Disc();
										disc.x = x2;
										disc.y = y2;
										disc.disc = opponentDisc;
										discToReverse.add(disc);										
										

										x2 += dir[d][0];
										y2 += dir[d][1];
										
									}

								}	
								break;
							}else if(getDisc(x1,y1).disc == EMPTY) break;
						}else{
							break;
						}
					}
				}	
			}
					
		}	
				
		return reversable;
	}
	
	/**
	 * Return true if inside the board, else if outside the board, return false
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInsideBoard(int x,int y){
		if(x >= 0 && y >= 0 && x < BOARD_SIDE && y < BOARD_SIDE){
			return true;
		}else{
			return false;
		}
	}
	
	
}
