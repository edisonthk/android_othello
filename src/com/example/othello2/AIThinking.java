package com.example.othello2; 


/**
 * Latest one
 * @author LikWee
 *
 */
public class AIThinking {
	
	private static final String TAG = "EvaluationFunction";
		
	private final int forfeitWeight = 7;
	private final int mobilityWeight = 1;
	private final int frontierWeight = 2;
	private final int stabilityWeight = 10;
		
	private static final int MAX = 500;
	private int computerDisc;
	private GameBoard mBoard;
	
	public AIThinking(GameBoard board,int computer,int player){
		this.mBoard = board;
		this.computerDisc = computer;
	}
		
	
	
	public SearchCoordinate Minimax(){
		
		int a = MAX + 64;
		int b  = -a;
		
		if(mBoard.getRound() > 52){
			int emptyCount = (GameBoard.BOARD_SIDE * GameBoard.BOARD_SIDE) - this.mBoard.getBlackDiscsNumber() - this.mBoard.getWhiteDiscsNumber();
			return Minimax(this.mBoard, this.computerDisc, emptyCount,a, b);
		}else{
			return Minimax(this.mBoard, this.computerDisc,3,a,b);
		}
		
	}
	
	/**
	 * Parameter depth must be greater then 0 ( > 0), else null exception will be thrown.
	 * At initial state, parameter sc must be null
	 * @param board
	 * @param currentDisc
	 * @param depth
	 * @param computerDisc
	 * @param sc
	 * @param a
	 * @param b
	 * @return
	 */
	public SearchCoordinate Minimax(GameBoard board,int disc, int depth,int a,int b){
		
		int color = 1; // if disc is white
		int enemy = GameBoard.BLACK;
		
		if(disc == GameBoard.BLACK) {
			color = -1; // if disc is black
			enemy = GameBoard.WHITE;
		}
			
		
		
		// Initialize the best move.
		SearchCoordinate bestMove = new SearchCoordinate(-1, -1, -color * MAX );
		

		// Find out how many valid moves we have so we can initialize the
		// mobility score.
		int validMoves = board.getReversableNumber();

		
		for(Disc movableDisc : board.getMovableBlock()){
						
			// Make the move.
			GameBoard testBoard = board.clone();
			testBoard.reverse(disc, movableDisc.x, movableDisc.y);
			testBoard.nextRound();
			int score = testBoard.getWhiteDiscsNumber() - testBoard.getBlackDiscsNumber();

			
			// instance searchCoordinate
			SearchCoordinate sc = new SearchCoordinate(movableDisc.x,movableDisc.y,0);
			
			// Check the board.
			int nextColor = -color;
			int forfeit = 0;
			boolean isEndGame = false;
			int opponentValidMoves = testBoard.getReversableNumber();
			if (opponentValidMoves == 0)
			{
				// The opponent cannot move, count the forfeit.
				forfeit = color;

				// Switch back to the original color.
				nextColor = -nextColor;

				// If that player cannot make a move either, the
				// game is over.
				if (testBoard.checkEndGame().result)
					isEndGame = true;
			}

			// If we reached the end of the look ahead (end game or
			// max depth), evaluate the board and set the move
			// rank.
			if (isEndGame || depth == 0)
			{
				// For an end game, max the ranking and add on the
				// final score.
				if (isEndGame)
				{
					// Negative value for black win.
					if (score < 0)
						sc.score = -MAX + score;

					// Positive value for white win.
					else if (score > 0)
						sc.score = MAX + score;

					// Zero for a draw.
					else
						sc.score = 0;
				}

				// It's not an end game so calculate the move rank.
				else
					sc.score =
						this.forfeitWeight   * forfeit +
						this.frontierWeight  * (testBoard.getFrontierNumber(GameBoard.BLACK) - testBoard.getFrontierNumber(GameBoard.WHITE)) +
						this.mobilityWeight  * color * (validMoves - opponentValidMoves) +
						this.stabilityWeight * (testBoard.getSafeDiscNumber(GameBoard.WHITE) - testBoard.getSafeDiscNumber(GameBoard.BLACK)) +									
						                       score;
			}

			// Otherwise, perform a look ahead.
			else
			{
				int nextMoveScore = this.Minimax(testBoard, enemy, depth-1, a, b).score;

				// Pull up the rank.
				sc.score = nextMoveScore;

				// Forfeits are cumulative, so if the move did not
				// result in an end game, add any current forfeit
				// value to the rank.
				if (forfeit != 0 && Math.abs(sc.score) < MAX)
					sc.score += forfeitWeight * forfeit;

				// Adjust the alpha and beta values, if necessary.
				if (color == 1 && sc.score > b)
					b = sc.score;
				if (color == -1 && sc.score < a)
					a = sc.score;
			}

			// Perform a cutoff if the rank is outside tha alpha-beta range.
			if (color == 1 && sc.score > a)
			{
				sc.score = a;
				return sc;
			}
			if (color == -1 && sc.score < b)
			{
				sc.score = b;
				return sc;
			}

			// If this is the first move tested, assume it is the
			// best for now.
			if (bestMove.x < 0 && bestMove.y < 0)
				bestMove = sc;

			// Otherwise, compare the test move to the current
			// best move and take the one that is better for this
			// color.
			else if (color * sc.score > color * bestMove.score)
				bestMove = sc;
			
		
		}

		// Return the best move found.
		return bestMove;
		
	}

}
