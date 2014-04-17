package com.example.othello2;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewBoard {
	
	private static final String TAG = "ViewBoard";
	
	private Activity mActivity;
	
	private TextView bottomLeftText,bottomRightText,topText;
	private ImageView whoturnImage;
	private TableLayout boardView;
	
	private Bitmap whiteStoneBitmap,blackStoneBitmap;
	
	private Block block[][];
	
	private int BLOCK_HEIGHT,BLOCK_WIDTH;
	
	public ViewBoard(Activity activity){
		this.mActivity = activity;
		configureView();
	}

	public Block getBlock(int x,int y){
		return this.block[y][x];				
	}
	
	public void setBlockView(int x,int y,int disc){
		this.block[y][x].setBlock(disc);
	}
	
	public void setReversableBlockView(int x,int y){
		this.block[y][x].setReversableBlock();
	}
	
	public void unsetReversableBlockView(int x,int y){
		this.block[y][x].setNotReversableBlock();
	}
	
	public void setTopText(String text){
		topText.setText(text);
	}
	
	public void setBottomLeftText(String text){
		bottomLeftText.setText(text);
	}
	
	public void setBottomRightText(String txt){
		bottomRightText.setText(txt);
	}
	
	/**
	 * Second parameter set to true, it will update reversable view.
	 * If second parameter set to false, all the reversable view will be hidden
	 * @param board
	 * @param set
	 */
	public void updateReversableView(GameBoard board,boolean set){
		for(int y = 0;y < GameBoard.BOARD_SIDE;y++)
			for(int x =0;x < GameBoard.BOARD_SIDE;x++){
			if(set){
				if(board.getDisc(x, y).reversable){
					setReversableBlockView(x, y);
				}else{
					unsetReversableBlockView(x, y);
				}								
			}else{
				unsetReversableBlockView(x, y);
			}
			
		}
		
	}
	
	public void updateWhoTurnImage(int disc){
		if(disc == GameBoard.BLACK){
			whoturnImage.setImageBitmap(blackStoneBitmap);
		}else{
			whoturnImage.setImageBitmap(whiteStoneBitmap);			
		}
	}
	
	public void updateViewWithoutAnimation(GameBoard board){
		for(int y = 0;y < GameBoard.BOARD_SIDE;y++){
			for(int x = 0;x < GameBoard.BOARD_SIDE;x++){
				
				this.block[y][x].setBlock(board.getDisc(x, y).disc);
				
			}
		}
	}
	
	/**
	 * This method is used for change black to white disc ONLY, so never tried to use this
	 * this method on black to black or empty to black
	 * @param list
	 */
	public void changeToBlack(ArrayList<Disc> list){
		for(Disc d : list){
			Log.i(TAG,"changeToBlack y:"+d.y+",x:"+d.x);
			if(d.disc == GameBoard.EMPTY){
				this.block[d.y][d.x].setBlock(GameBoard.BLACK);				
			}else{				
				this.block[d.y][d.x].playWhite2BlackAnimation();
			}			
		}
	}
	
	/**
	 * This method is used for change black to white disc ONLY, so never tried to use this
	 * this method on white to white or empty to white
	 * @param list
	 */
	public void changeToWhite(ArrayList<Disc> list){
		for(Disc d : list){
			Log.i(TAG,"changeToWhite y:"+d.y+",x:"+d.x);
			if(d.disc == GameBoard.EMPTY){
				this.block[d.y][d.x].setBlock(GameBoard.WHITE);
			}else{				
				this.block[d.y][d.x].playBlack2WhiteAnimation();
			}			
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@SuppressWarnings("deprecation")
	private void configureView(){
		topText = (TextView)mActivity.findViewById(R.id.top_layout_text);
		whoturnImage = (ImageView)mActivity.findViewById(R.id.who_turn);		
		bottomLeftText = (TextView)mActivity.findViewById(R.id.bottom_left_text);		
		bottomRightText = (TextView)mActivity.findViewById(R.id.bottom_right_text);

		// get screen width and offset value
		int width = 0,height = 0;		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
			Display display = mActivity.getWindowManager().getDefaultDisplay();
			width = display.getWidth();			
		}else{
			Display display = mActivity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			width = size.x;			
		}
		height = width;
		
		double offset = width * 22.0 / 640.0;
		System.out.println("Testing123 "+offset);
		BLOCK_HEIGHT = (int) (height-offset-offset)/GameBoard.BOARD_SIDE;
		BLOCK_WIDTH = (int) (width-offset-offset)/GameBoard.BOARD_SIDE;
		
		LinearLayout innerBoardLayout = (LinearLayout)mActivity.findViewById(R.id.inner_board);

		boardView = new TableLayout(mActivity);		
		innerBoardLayout.addView(boardView);				
		
		boardView.setLayoutParams(new TableLayout.LayoutParams(width,width));		
		boardView.setBackgroundResource(R.drawable.board);
		boardView.setPadding((int)offset, (int)offset, (int)offset, (int)offset);	
		
		// instantiate block array
		block = new Block[GameBoard.BOARD_SIDE][GameBoard.BOARD_SIDE];
		
		
		// install image to every block
		Bitmap b = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.game_ishi_01);
		Bitmap scaledB1 = Bitmap.createScaledBitmap(b,BLOCK_WIDTH*3,BLOCK_HEIGHT*2, false);
		ArrayList<Bitmap> blist = cutImages(scaledB1,3,2);
		Bitmap reversableBitmap = blist.get(4);
		Bitmap b2 = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.game_anim_stone_01);
		Bitmap scaledB2 = Bitmap.createScaledBitmap(b2,BLOCK_WIDTH*4,BLOCK_HEIGHT*19, false);
		ArrayList<Bitmap> black2whiteAnimate = cutAnimation(scaledB2,4,19,1);
		ArrayList<Bitmap> white2blackAnimate = cutAnimation(scaledB2,4,19,0);

		// instantiate white and black disc bitmap
		this.whiteStoneBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.w1);
		this.blackStoneBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.b1);

		
		// instantiate every block with installed image	
		for(int y = 0;y  < GameBoard.BOARD_SIDE ; y ++){
			TableRow tableRow = new TableRow(mActivity);
			tableRow.setLayoutParams(new TableRow.LayoutParams(BLOCK_WIDTH * GameBoard.BOARD_SIDE, BLOCK_HEIGHT));

			for(int x = 0;x < GameBoard.BOARD_SIDE ; x ++){
				
				block[y][x] = new Block(mActivity);			
				
				block[y][x].setupImage(blist.get(2), blist.get(0),reversableBitmap,black2whiteAnimate,white2blackAnimate);

				block[y][x].setLayoutParams(new TableRow.LayoutParams(BLOCK_WIDTH, BLOCK_HEIGHT));
				block[y][x].setPadding(0, 0, 0, 0);
				block[y][x].setBlock(GameBoard.EMPTY);
				tableRow.addView(block[y][x]);

			}

			boardView.addView(tableRow);			
		}
		
	}
	
	
	/**
	 * cut a image and store it to arrayList<Bitmap> according to widthRatio and heightRatio
	 * @param b
	 * @param witdhRatio
	 * @param heightRatio
	 * @return
	 */
	private ArrayList<Bitmap> cutImages(Bitmap b,int witdhRatio,int heightRatio){
		ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

		int width = b.getWidth();
		int height = b.getHeight();

		int pixelByCol = width/witdhRatio;
		int pixelByRow = height/heightRatio;

		for(int col = 0;col < witdhRatio;col++){
			for(int row = 0;row < heightRatio;row++){
				int startx = pixelByCol * col;
				int starty = pixelByRow * row;
				Bitmap b1 = Bitmap.createBitmap(b,startx,starty,pixelByCol,pixelByRow);
				bitmapList.add(b1);
				b1 = null;
			}
		}

		return bitmapList;
	}

	/**
	 * cut images and store it to arrayList<Bitmap> according to widthRatio and heightRatio for animation
	 * @param b
	 * @param witdhRatio
	 * @param heightRatio
	 * @return
	 */
	private ArrayList<Bitmap> cutAnimation(Bitmap b,int witdhRatio,int heightRatio,int colUsed){
		ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

		int width = b.getWidth();
		int height = b.getHeight();

		int pixelByCol = width/witdhRatio;
		int pixelByRow = height/heightRatio;

		for(int col = 0;col < witdhRatio;col++){
			for(int row = 0;row < heightRatio;row++){
				int startx = pixelByCol * col;
				int starty = pixelByRow * row;
				Bitmap b1 = Bitmap.createBitmap(b,startx,starty,pixelByCol,pixelByRow);
				if(col == colUsed){
					bitmapList.add(b1);
				}				
				b1 = null;
			}
		}

		return bitmapList;
	}
	
}
