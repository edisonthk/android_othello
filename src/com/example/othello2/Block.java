package com.example.othello2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class Block extends View{

	
	private int play_frame = 0;
	private int DELAY = 3;
	private long last_tick = 0;
	private Bitmap blackStoneBitmap,whiteStoneBitmap,reversableBitmap;
	private ArrayList<Bitmap> mBlack2whiteAnimate,mWhite2blackAnimate;
	private boolean mStartPlaying = false,mIsPlaying = false,playBlack2White = false,playWhite2Black = false,setupReversableBitmapFlag = false;

	
	private int whoBlock = GameBoard.EMPTY;
	
	
	public Block(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
	}
	
	public Block(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}

	public Block(Context context) {
		super(context);				
	}
	
	public void setupImage(Bitmap black,Bitmap white,Bitmap reversableBitmap,ArrayList<Bitmap> black2whiteAnimate,ArrayList<Bitmap> white2blackAnimate){
		this.blackStoneBitmap = black;
		this.whiteStoneBitmap = white;
		this.reversableBitmap = reversableBitmap;
		
		mBlack2whiteAnimate = new ArrayList<Bitmap>();
		for(int i = 0;i < black2whiteAnimate.size();i++){
			mBlack2whiteAnimate.add(black2whiteAnimate.get(i));
		}
		mWhite2blackAnimate = new ArrayList<Bitmap>();
		for(int i = 0;i < white2blackAnimate.size();i++){
			mWhite2blackAnimate.add(white2blackAnimate.get(i));
		}
		
	}	
	
	
	
	@Override
	protected void onDraw(Canvas c){
		
		int draw_x = 0,draw_y = 0;
				    
		
		if (mStartPlaying)
        {
            System.out.println("Start animation");
            play_frame = 0;
            mStartPlaying = false;
            mIsPlaying = true;
            setupReversableBitmapFlag = false;
            
            if(playBlack2White){
            	c.drawBitmap(blackStoneBitmap, draw_x, draw_y, null);
            }else if(playWhite2Black){
            	c.drawBitmap(whiteStoneBitmap, draw_x, draw_y, null);
            }
            
            postInvalidate();
        }
        else if (mIsPlaying)
        {
        	
            if (play_frame >= mBlack2whiteAnimate.size())
            {
                mIsPlaying = false;               
                if(playWhite2Black){
    	        	c.drawBitmap(blackStoneBitmap, draw_x, draw_y, null);
    	        }else if(playBlack2White){
    	        	c.drawBitmap(whiteStoneBitmap, draw_x, draw_y, null);
    	        }
            }
            else
            {
                long time = (System.currentTimeMillis() - last_tick);
                
                if (time >= DELAY) //the delay time has passed. set next frame
                {
                    last_tick = System.currentTimeMillis();
					//location of the frame can be declared by second and third parameter
                    if(playBlack2White){
                    	c.drawBitmap(mBlack2whiteAnimate.get(play_frame), draw_x, draw_y, null);
                    }else if(playWhite2Black){
                    	c.drawBitmap(mWhite2blackAnimate.get(play_frame), draw_x, draw_y, null);
                    }
                    
                    play_frame++;
                    postInvalidate();
                }
                else //still within delay.  redraw current frame
                {
					//location of the frame can be declared by second and third parameter
                	if(playBlack2White){
                    	c.drawBitmap(mBlack2whiteAnimate.get(play_frame), draw_x, draw_y, null);
                    }else if(playWhite2Black){
                    	c.drawBitmap(mWhite2blackAnimate.get(play_frame), draw_x, draw_y, null);
                    }
                    postInvalidate();
                }
            }
        }else if(!mIsPlaying && !mStartPlaying){
			if(whoBlock == GameBoard.BLACK){
	        	c.drawBitmap(blackStoneBitmap, draw_x, draw_y, null);
	        }else if(whoBlock == GameBoard.WHITE){
	        	c.drawBitmap(whiteStoneBitmap, draw_x, draw_y, null);
	        }else if(whoBlock == GameBoard.EMPTY){
	        	if(!setupReversableBitmapFlag){
	        		c.drawColor(Color.TRANSPARENT);
	        	}else{
	        		c.drawBitmap(reversableBitmap, draw_x, draw_y,null);
	        	}
	        	
	        }
		}
	}
	
	/**
	 * 	set the block view to any 3 option -> white,black,empty
	 */
	public void setBlock(int block){
		this.whoBlock = block;
		mStartPlaying = false;
		playBlack2White = false;
		playWhite2Black = false;
		postInvalidate();
	}
	
	public void setNotReversableBlock(){
		setupReversableBitmapFlag = false;
		postInvalidate();
	}
	
	public void setReversableBlock(){
		setupReversableBitmapFlag = true;
		postInvalidate();
	}
	
	public void playBlack2WhiteAnimation(){
		mStartPlaying = true;
		playBlack2White = true;
		playWhite2Black = false;		
		this.whoBlock = GameBoard.WHITE;
		postInvalidate();
	}
	
	public void playWhite2BlackAnimation(){
		mStartPlaying = true;
		playBlack2White = false;
		playWhite2Black = true;
		this.whoBlock = GameBoard.BLACK;
		postInvalidate();
	}
	
}
