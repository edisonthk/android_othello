package com.example.othello2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class OthelloMainActivity extends Activity {
	
	private static final String TAG = "OthelloMainActivity";
	
	private GameBoard gameBoard;
	private ViewBoard viewBoard;
	
	private boolean AI = true;
	
	public int playerDisc = GameBoard.BLACK;
	public int opponentDisc = GameBoard.WHITE;	
	private int computerDisc = opponentDisc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// configure the view of the game by instantiate ViewBoard		
		viewBoard = new ViewBoard(this);
		
		// configure the board of the game by instantiate GameBoard		
		gameBoard = new GameBoard();
		
		// call this method to adapt the default (initial) state of gameBoard
		// to viewBoard
		configureGame();
	}

	
	/**
	 * This method adapt the content of gameBoard to viewBoard
	 */
	private void configureGame(){
				
		for(int x = 0;x < GameBoard.BOARD_SIDE;x++){
			for(int y = 0;y < GameBoard.BOARD_SIDE ; y++){
				Disc d = gameBoard.getDisc(x,y);
				if(d.reversable){					
					viewBoard.setReversableBlockView(x, y);
				}else{
					viewBoard.setBlockView(x, y,d.disc);
				}
				final int clickedX = x;
				final int clickedY = y;
				viewBoard.getBlock(x,y).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
												
						if(gameBoard.getDisc(clickedX, clickedY).reversable){
							showToast("x:"+clickedX+",y:"+clickedY);
							viewBoard.updateReversableView(gameBoard, false);
							
							if(AI){
								if(gameBoard.getRound() % 2 == 0 ){									
									
									if(playerDisc == GameBoard.BLACK){
										// player
										viewBoard.changeToBlack(gameBoard.reverse(GameBoard.BLACK, clickedX, clickedY));
										nextRound();
									}
								}else{
									if(playerDisc == GameBoard.WHITE){
										viewBoard.changeToWhite(gameBoard.reverse(GameBoard.WHITE, clickedX, clickedY));
										nextRound();
									}
								}
								
							}else{
								if(gameBoard.getRound() % 2 == 0){								
									viewBoard.changeToBlack(gameBoard.reverse(GameBoard.BLACK, clickedX, clickedY));
								}else{								
									viewBoard.changeToWhite(gameBoard.reverse(GameBoard.WHITE, clickedX, clickedY));							
								}
								
								nextRound();
							}							
						}						
					}
				});
			}
		}			
	}	
	
	private void nextRound(){
		
		gameBoard.nextRound();	
		
		if(gameBoard.getReversableNumber() == 0){
			Log.e(TAG,"Exception reversable number is 0");
			EndGame endGame = gameBoard.checkEndGame();
			if(endGame.result){
				gameBoard.endGame = true;
				viewBoard.setTopText("ゲーム終了");
				
				if(endGame.reason == EndGame.BLACK_WIN){
					viewBoard.setBottomRightText("黒の勝利");										
				}else if(endGame.reason == EndGame.WHITE_WIN){
					viewBoard.setBottomRightText("白の勝利");
				}else if(endGame.reason == EndGame.DRAW){
					viewBoard.setBottomRightText("DRAW");
				}else{
					viewBoard.setBottomRightText("Unexpected:"+endGame.reason);
				}
				
				
			}else{
				// pass
				if(gameBoard.getRound() % 2 == 0){
					// pass black
					showToast("黒をパス！！");
				}else{
					// pass white
					showToast("白のパス！！");
				}
				nextRound();
			}
		}else{											
				
			if(gameBoard.getRound() % 2 == 0){								
				viewBoard.updateWhoTurnImage(GameBoard.BLACK);
			}else{
				viewBoard.updateWhoTurnImage(GameBoard.WHITE);
			}				
			
			viewBoard.updateReversableView(gameBoard,true);
			viewBoard.setTopText("Round "+gameBoard.getRound());
			viewBoard.setBottomRightText("黒："+gameBoard.getBlackDiscsNumber()+" 白："+gameBoard.getWhiteDiscsNumber());
			
			if(AI){
				if(gameBoard.getRound() % 2 == 0 && computerDisc == GameBoard.BLACK){					
					Handler handler = new Handler();
					new AIThinkingThread(handler).start();
					viewBoard.updateReversableView(gameBoard,false);
				}else if(gameBoard.getRound() % 2 == 1 && computerDisc == GameBoard.WHITE){
					Handler handler = new Handler();
					new AIThinkingThread(handler).start();
					viewBoard.updateReversableView(gameBoard,false);
				}													
			}
		
		}
		
		
	}
	
	private class AIThinkingThread extends Thread{	
		
		private Handler mHandler;
		public AIThinkingThread(Handler h){
			this.mHandler = h;
		}

		@Override
		public void run(){
			GameBoard thinkingBoard = gameBoard.clone();
			final SearchCoordinate c = new AIThinking(thinkingBoard,computerDisc,playerDisc).Minimax();	
			Log.i(TAG,"evalutionScore decision score:"+c.score+",y:"+c.y+",x:"+c.x);
			this.mHandler.post(new Runnable(){
				@Override
				public void run(){
					viewBoard.changeToWhite(gameBoard.reverse(computerDisc, c.x, c.y));
					nextRound();					
				}
			});
		}
	}
	
	private void showToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	
	
	private void resetGame(){
		gameBoard.defaultBoard();
		viewBoard.updateViewWithoutAnimation(gameBoard);		
		viewBoard.updateReversableView(gameBoard,true);
		viewBoard.setTopText("Round "+gameBoard.getRound());
		viewBoard.setBottomRightText("黒："+gameBoard.getBlackDiscsNumber()+" 白："+gameBoard.getWhiteDiscsNumber());
		showToast("リセットしました！");
	}
	
	
	private class ResetDialog extends AlertDialog{

		protected ResetDialog(final Context context) {
			super(context);
			setMessage("リセットしますか？");
			setCancelable(true);
			setButton(DialogInterface.BUTTON_NEGATIVE,"いいえ",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			setButton(DialogInterface.BUTTON_POSITIVE, "はい",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					resetGame();
				}
			});
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId(); 
		if(itemId == R.id.reset_game){
			new ResetDialog(this).show();
			return true;   		
		}else{
			return super.onOptionsItemSelected(item);
		}		
	}

}
