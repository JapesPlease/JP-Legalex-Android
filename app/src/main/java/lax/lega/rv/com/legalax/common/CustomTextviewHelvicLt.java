package lax.lega.rv.com.legalax.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")

public class CustomTextviewHelvicLt extends TextView {
    public CustomTextviewHelvicLt(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLt.ttf");
        this.setTypeface(face);
    }

    public CustomTextviewHelvicLt(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLt.ttf");
        this.setTypeface(face);
    }

    public CustomTextviewHelvicLt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLt.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
