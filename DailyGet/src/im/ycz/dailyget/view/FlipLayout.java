package im.ycz.dailyget.view;

import im.ycz.dailyget.R;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class FlipLayout extends RelativeLayout{

	private View frontView, backView;
	public static final int FRONT_2_BACK = 0;
	public static final int BACK_2_FRONT = 1;
	
	private boolean isFront = true;
	
	public FlipLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public FlipLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public FlipLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setFlipView(int frontId, int backId){
		frontView = LayoutInflater.from(this.getContext()).inflate(frontId, null);
		backView = LayoutInflater.from(getContext()).inflate(backId, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(backView, params);
		this.addView(frontView, params);
		backView.setVisibility(View.GONE);
	}
	
	
	public View getFrontView() {
		return frontView;
	}

	public void setFrontView(View frontView) {
		this.frontView = frontView;
	}

	public View getBackView() {
		return backView;
	}

	public void setBackView(View backView) {
		this.backView = backView;
	}

	public void flip() {
		if(isFront) {
			flip2Back();
		} else {
			// gone the back;
			flip2Front();
		}
//		fliplisten.onFlip();
	}
	
	
	private void flip2Front() {
		// TODO Auto-generated method stub
		
		AnimatorSet Back2FrontanimOut = (AnimatorSet) AnimatorInflater.loadAnimator(
	            this.getContext(), R.animator.flip_back_out);
	    AnimatorSet Back2FrontanimIn = (AnimatorSet) AnimatorInflater.loadAnimator(
	            this.getContext(), R.animator.flip_front_in);

		Back2FrontanimOut.setTarget(backView);
		Back2FrontanimIn.setTarget(frontView);

		Back2FrontanimOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                frontView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                backView.setVisibility(View.GONE);
                isFront = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        Back2FrontanimOut.end();
        Back2FrontanimIn.end();
        Back2FrontanimOut.start();
        Back2FrontanimIn.start();
	}

	
	
	private void flip2Back() {
		// TODO Auto-generated method stub
		
		AnimatorSet Front2BackanimOut = (AnimatorSet) AnimatorInflater.loadAnimator(
	            this.getContext(), R.animator.flip_front_out);

	    AnimatorSet Front2BackanimIn = (AnimatorSet) AnimatorInflater.loadAnimator(
	            this.getContext(), R.animator.flip_back_in);
		
		Front2BackanimOut.setTarget(frontView);
		Front2BackanimIn.setTarget(backView);

        Front2BackanimOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                backView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                frontView.setVisibility(View.GONE);
                isFront = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        Front2BackanimOut.end();
        Front2BackanimIn.end();
        Front2BackanimOut.start();
        Front2BackanimIn.start();
	}

	private FlipListener fliplisten;
	
	public void setOnFlipListner(FlipListener fliplistener) {
		this.fliplisten = fliplistener;
	}
	
}
