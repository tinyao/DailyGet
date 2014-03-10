package im.ycz.dailyget.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;

import im.ycz.dailyget.R;

/**
 * Created by tinyao on 11/16/13.
 */
public class FlipView {


    private boolean isFront= true;


    private View parentView, frontView, backView;
    private Context context;

    private boolean enabled = true;


    /**
     * Contructor FlipView
     * @param context
     * @param parent parent holder
     * @param front_id frontView layout id
     * @param back_id backVIew layout id
     */
    public FlipView(Context context, View parent, int front_id, int back_id) {
        frontView = parent.findViewById(front_id);
        backView = parent.findViewById(back_id);
        parentView = parent;
        this.context = context;
        setListener();
    }

    private void setListener() {
//        parentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isFront) {
//                    flip2Back();
//                } else {
//                    flip2Front();
//                }
//            }
//        });
        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enabled) flip2Back();
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enabled) flip2Front();
            }
        });
    }

    private void flip2Back() {

        AnimatorSet animOut = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, R.animator.flip_front_out);

        AnimatorSet animIn = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, R.animator.flip_back_in);

        animOut.setTarget(frontView);
        animIn.setTarget(backView);

        animOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                backView.setVisibility(View.VISIBLE);
                frontView.setClickable(false);
                backView.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                frontView.setVisibility(View.INVISIBLE);
                isFront = false;
                frontView.setClickable(true);
                backView.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animOut.end();
        animIn.end();
        animOut.start();
        animIn.start();

    }

    public void flip2Front() {
        AnimatorSet animOut = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, R.animator.flip_back_out);

        AnimatorSet animIn = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, R.animator.flip_front_in);

        animOut.setTarget(backView);
        animIn.setTarget(frontView);

        animOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                frontView.setVisibility(View.VISIBLE);
                frontView.setClickable(false);
                backView.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                backView.setVisibility(View.INVISIBLE);
                isFront = true;
                frontView.setClickable(true);
                backView.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animOut.end();
        animIn.end();
        animOut.start();
        animIn.start();
    }


    public View getFrontView() {
        return frontView;
    }

    public View getBackView() {
        return backView;
    }

    public boolean isFront() {
        return isFront;
    }

    public void setEnable(boolean enable){
        this.enabled = enable;
    }

}
