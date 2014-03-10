package im.ycz.dailyget.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createFont();
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createFont();
	}

	public RobotoTextView(Context context) {
		super(context);
		createFont();
	}

	public void createFont() {
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"font/Roboto-Light.ttf");
		setTypeface(font);
	}

}
