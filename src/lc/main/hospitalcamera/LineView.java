package lc.main.hospitalcamera;

import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.util.AttributeSet;  
import android.view.View;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.LinearLayout; 

public class LineView extends View{
    int left,right,top,bottom;
    public LineView(Context context) {  
        super(context);  
        setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT  
            ,LinearLayout.LayoutParams.FILL_PARENT));   
    }  
    public LineView(Context context,int left,int top,int right,int bottom) {  
        super(context);  
        setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT  
            ,LinearLayout.LayoutParams.FILL_PARENT));  
        this.bottom = bottom;
        this.top = top;
        this.left = left;
        this.right = right;
    }  
    public LineView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override   
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        Paint p = new Paint();  
        p.setColor(Color.RED);  
        p.setStrokeWidth(5);  
        canvas.drawLine(left, top, right, bottom, p);  
    }  
}
