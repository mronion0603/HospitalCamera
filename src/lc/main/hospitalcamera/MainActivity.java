package lc.main.hospitalcamera;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import com.gc.materialdesign.views.ButtonFloat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import lc.main.hospitalcamera.R;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener{
	private final static int TRIM_DISTANCE = 2;
	private final static int DIALOG_MESSAGE = 1;
	private final static int IMAGECHANGE_MESSAGE = 2;
	private final static int IMAGE_ALPHA1 = 100;
	private final static int IMAGE_ALPHA2 = 255;
    private Button choosePic,setScale,enterBt,confirm,cancel,trim;
    private RelativeLayout preLl;
    private LinearLayout preUp,preDown;
    private RelativeLayout afterLl,innerAfterLl;
    private RelativeLayout titleRl;
    private RelativeLayout trimrl;
    ImageView imageView ;  
    private ImageView myImageView;
    private ImageView myImageView2;
	Camera myCamera;
	SurfaceView mySurfaceView;
	SurfaceHolder mySurfaceHolder;
	int mwidth;
	int mheight;
	private boolean mPreviewRunning= false; 
	private int screenWidth;
	private int screenHeight;
	private int lastX, lastY;
	int flagFoucs ;
	private ImageView direction_up,direction_down,direction_left,direction_right;
	double distance,getlength;
	double standardLength,realLength;
	int state = 0;  //0表示设置标准长度页面   1表示对比页面
	boolean isTrimOn= false;
	private int _xDelta;  
    private int _yDelta;  
    final static int IMAGE_SIZE = 72; 
    final static int MARGIN_SIZE = 50; 
    final static int MARGIN_SIZE2 = 250; 
   
    ButtonFloat buttonfloat;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DIALOG_MESSAGE:{
                mySurfaceView.setVisibility(View.VISIBLE);
                imageView.setAlpha(IMAGE_ALPHA1);
                myImageView.setVisibility(View.VISIBLE);
                myImageView2.setVisibility(View.VISIBLE);
                preUp.setVisibility(View.VISIBLE);
                preDown.setVisibility(View.VISIBLE);
                trimrl.setVisibility(View.INVISIBLE);
                state = 1;
                isTrimOn = false;
                myImageView.setBackgroundResource(R.drawable.pin);
                myImageView2.setBackgroundResource(R.drawable.pin2);
                cancel.setVisibility(View.VISIBLE);
                }break;
            case IMAGECHANGE_MESSAGE:{
                if(msg.arg1==1){
                   myImageView.setBackgroundResource(R.drawable.pin2);
                   myImageView2.setBackgroundResource(R.drawable.pin);
                }else{
                   myImageView.setBackgroundResource(R.drawable.pin);
                   myImageView2.setBackgroundResource(R.drawable.pin2);  
                }
                }break;
            }
            
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void init(){
		flagFoucs=2;
		imageView = (ImageView)findViewById(R.id.iv01);
		myImageView2 = (ImageView)findViewById(R.id.ImageView2);
		buttonfloat = (ButtonFloat)findViewById(R.id.buttonFloat);
		choosePic = (Button)findViewById(R.id.choosePic);
		setScale = (Button)findViewById(R.id.setScale);
		enterBt = (Button)findViewById(R.id.enter);
		confirm = (Button)findViewById(R.id.confirm);
		cancel = (Button)findViewById(R.id.cancel);
		trim = (Button)findViewById(R.id.trim);
		preLl = (RelativeLayout)findViewById(R.id.prell);
		preUp = (LinearLayout)findViewById(R.id.preUp);
		preDown = (LinearLayout)findViewById(R.id.preDown);
		afterLl = (RelativeLayout)findViewById(R.id.afterll);
		innerAfterLl= (RelativeLayout)findViewById(R.id.InnerAfterll);
		titleRl = (RelativeLayout)findViewById(R.id.rlTitle);
		trimrl = (RelativeLayout)findViewById(R.id.trimrl);
		mySurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
		direction_up = (ImageView)findViewById(R.id.up);
		direction_down = (ImageView)findViewById(R.id.down);
		direction_left = (ImageView)findViewById(R.id.left );
		direction_right = (ImageView)findViewById(R.id.right);
	    mySurfaceHolder = mySurfaceView.getHolder();//获得SurfaceHolder
	    mySurfaceHolder.addCallback(this);
	    mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	  
		ButtonEffect.setButtonStateChangeListener(choosePic);
		ButtonEffect.setButtonStateChangeListener(setScale);
		ButtonEffect.setButtonStateChangeListener(enterBt);
		ButtonEffect.setButtonStateChangeListener(confirm);
		ButtonEffect.setButtonStateChangeListener(cancel);
		ButtonEffect.setButtonStateChangeListener(trim);
		ButtonEffect.setButtonStateChangeListener(direction_up);
		ButtonEffect.setButtonStateChangeListener(direction_down);
		ButtonEffect.setButtonStateChangeListener(direction_left);
		ButtonEffect.setButtonStateChangeListener(direction_right);
		this.myImageView = (ImageView) this.findViewById(R.id.ImageView);
		this.myImageView.setOnTouchListener(this);
		this.myImageView2.setOnTouchListener(this);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - 150;
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(  
		        IMAGE_SIZE, IMAGE_SIZE);  
		
	    layoutParams.leftMargin = screenWidth/2;  
	    layoutParams.topMargin = screenHeight/2;  
	   
	    myImageView.setLayoutParams(layoutParams);  
	    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(  
	            IMAGE_SIZE, IMAGE_SIZE);  
	    
        layoutParams2.leftMargin = screenWidth/2;  
        layoutParams2.topMargin = screenHeight/2;  
     
        myImageView2.setLayoutParams(layoutParams2); 
		
		
        buttonfloat.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setType("image/*");  
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);  
			}
		});
		choosePic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setType("image/*");  
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);  
			}
		});
		setScale.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setScale.setVisibility(View.GONE);
				enterBt.setVisibility(View.GONE);
				titleRl.setVisibility(View.GONE);
				mySurfaceView.setVisibility(View.GONE);
				imageView.setAlpha(IMAGE_ALPHA2);
				myImageView.setVisibility(View.VISIBLE);
				myImageView2.setVisibility(View.VISIBLE);
				preUp.setVisibility(View.VISIBLE);
				preDown.setVisibility(View.VISIBLE);
			}
		});
		enterBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setScale.setVisibility(View.GONE);
				enterBt.setVisibility(View.GONE);
				titleRl.setVisibility(View.GONE);
			}
		});
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			  if(state == 0){
			    int x = myImageView.getLeft()- myImageView2.getLeft();
			    int y = myImageView.getBottom()- myImageView2.getBottom();
			    distance =Math.sqrt(x*x+y*y);
			    if(distance>0){
    			    AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
    	            AlertDialog customDialog;    
    	            LayoutInflater inflater = getLayoutInflater();
    	            final View layout = inflater.inflate(R.layout.dialog_set, (ViewGroup) findViewById(R.id.addcourse_dialog));
    	            builder.setTitle("设置长度单位");
    	            builder.setView(layout);
    	            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface arg0, int arg1) {
    	                    EditText setdistance =(EditText)layout.findViewById(R.id.addcourse1);
    	                    getlength = Double.parseDouble(setdistance.getText().toString());
    	                    System.out.println(getlength);
    	                    if(getlength>0){
    	                    standardLength = getlength/distance;
    	                    Toast.makeText(getApplication(), "设置长度成功", Toast.LENGTH_SHORT).show();
    	                    handler.sendEmptyMessage(DIALOG_MESSAGE);
    	                    }else{
    	                    Toast.makeText(getApplication(), "标准长度不能为负数", Toast.LENGTH_SHORT).show();     
    	                    }
    	                }
    	            });
    	            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {      
    	                @Override
    	                public void onClick(DialogInterface arg0, int arg1) {
    	                    
    	                }
    	            });
    	            customDialog = builder.create();
    	            customDialog.show();
			    }else{
			        Toast.makeText(getApplication(), "请先设置标尺", Toast.LENGTH_SHORT).show();
			    }
			  }else if(state == 1){
			    int x = myImageView.getLeft()- myImageView2.getLeft();
	            int y = myImageView.getBottom()- myImageView2.getBottom();
	            distance =Math.sqrt(x*x+y*y);
	            realLength += distance*standardLength;
	            BigDecimal   b   =   new   BigDecimal(realLength);   //保留两位有效数字
	            realLength   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
	            
	            Toast.makeText(getApplication(), "实际长度为"+realLength+"毫米", Toast.LENGTH_LONG).show();
	            innerAfterLl.addView(new LineView(MainActivity.this,myImageView.getLeft(),myImageView.getBottom()
	                    ,myImageView2.getLeft(),myImageView2.getBottom()));
			  }
			}
		});
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			    innerAfterLl.removeAllViews();
			    realLength = 0;
			}
		});
		trim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				if(isTrimOn){
				    isTrimOn = false;
				    trimrl.setVisibility(View.INVISIBLE);
				}else{
				    isTrimOn = true;
				    trimrl.setVisibility(View.VISIBLE);
				}
			}
		});
		direction_up.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(flagFoucs==1){
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView 
				            .getLayoutParams();  
				    layoutParams.topMargin = layoutParams.topMargin-TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView.setLayoutParams(layoutParams);
				}else{
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView2 
				            .getLayoutParams();  
				    layoutParams.topMargin = layoutParams.topMargin-TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView2.setLayoutParams(layoutParams);
				}
			}
		});
		direction_down.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(flagFoucs==1){
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView 
				            .getLayoutParams();  
				    layoutParams.topMargin = layoutParams.topMargin+TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView.setLayoutParams(layoutParams);
				}else{
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView2 
				            .getLayoutParams();  
				    layoutParams.topMargin = layoutParams.topMargin+TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView2.setLayoutParams(layoutParams);
				}
			}
		});
		direction_right.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(flagFoucs==1){
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView 
				            .getLayoutParams();  
				    layoutParams.leftMargin = layoutParams.leftMargin +TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView.setLayoutParams(layoutParams);
				}else{
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView2 
				            .getLayoutParams();  
				    layoutParams.leftMargin = layoutParams.leftMargin +TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView2.setLayoutParams(layoutParams);
				}
			}
		});
		direction_left.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(flagFoucs==1){
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView 
				            .getLayoutParams();  
				    layoutParams.leftMargin = layoutParams.leftMargin -TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView.setLayoutParams(layoutParams);			
				}else{
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myImageView2 
				            .getLayoutParams();  
				    layoutParams.leftMargin = layoutParams.leftMargin -TRIM_DISTANCE;  
				    layoutParams.height=IMAGE_SIZE;
				    layoutParams.width = IMAGE_SIZE;
				    myImageView2.setLayoutParams(layoutParams);
				}
			}
		});
		  
		
	}
	

	public boolean onTouch(View v, MotionEvent event) {
	    final int X = (int) event.getRawX();  
        final int Y = (int) event.getRawY(); 
      
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			
            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v  
                    .getLayoutParams();  
            _xDelta = X - lParams.leftMargin;  
            _yDelta = Y - lParams.topMargin;  
            //System.out.println(X+":"+Y+":"+_xDelta+":"+_yDelta);
			if(v.equals(myImageView)){
			    flagFoucs=1;
			    Message msg = new Message();
			    msg.what = IMAGECHANGE_MESSAGE;
			    msg.arg1 = flagFoucs;
			    handler.sendMessage(msg);
			}else if(v.equals(myImageView2)){
			    flagFoucs=2;
			    Message msg = new Message();
                msg.what = IMAGECHANGE_MESSAGE;
                msg.arg1 = flagFoucs;
                handler.sendMessage(msg);
			}
			break;
		case MotionEvent.ACTION_MOVE:
		    /*
			int dx = (int) event.getRawX() - lastX;
			int dy = (int) event.getRawY() - lastY;

			int left = v.getLeft() + dx;
			int top = v.getTop() + dy;
			int right = v.getRight() + dx;
			int bottom = v.getBottom() + dy;
          
			// 设置不能出界
			if (left < 0) {
				left = 0;
				right = left + v.getWidth();
			}

			if (right > screenWidth) {
				right = screenWidth;
				left = right - v.getWidth();
			}

			if (top < 0) {
				top = 0;
				bottom = top + v.getHeight();
			}

			if (bottom > screenHeight) {
				bottom = screenHeight;
				top = bottom - v.getHeight();
			}
			v.layout(left, top, right, bottom);
			*/
		    int dx = (int) event.getRawX() - lastX;
            int dy = (int) event.getRawY() - lastY;

            int left = v.getLeft() + dx;
            int top = v.getTop() + dy;
		    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v  
            .getLayoutParams();  
		    
            layoutParams.leftMargin = X - _xDelta;  
            layoutParams.topMargin = Y - _yDelta;  
            //layoutParams.rightMargin = X;  
            //layoutParams.bottomMargin = Y; 
            layoutParams.height=IMAGE_SIZE;
            layoutParams.width = IMAGE_SIZE;
            layoutParams.leftMargin =left;  
            layoutParams.topMargin =top;  
           // System.out.println(layoutParams.leftMargin+":"+layoutParams.topMargin+":"
           //         +layoutParams.rightMargin+":"+layoutParams.bottomMargin);
            v.setLayoutParams(layoutParams);
            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		afterLl.invalidate();  
		return true;
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            
            preLl.setVisibility(View.GONE);
            afterLl.setVisibility(View.VISIBLE);
            
            ContentResolver cr = this.getContentResolver();  
        
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
                WindowManager wm = this.getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();
                int degree = getDegree(bitmap);
                Bitmap smallBitmap;
                if(bitmap.getWidth()<=bitmap.getHeight()){
                 smallBitmap = zoomHImage(bitmap,width);
                }else{
                 smallBitmap = zoomVImage(bitmap,width);
                }
                smallBitmap = rotateBitmap(smallBitmap,degree) ;
                imageView.setImageBitmap(smallBitmap);  
                imageView.setAlpha(IMAGE_ALPHA1);
                
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            } catch (Exception e) {  
                Log.e("Exception", e.getMessage(),e);  
            }
            
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  
	
	public static Bitmap zoomVImage(Bitmap bgimage, double newHeight) {
		// 获取这个图片的宽和高
	    float width = bgimage.getWidth();
	    float height = bgimage.getHeight();
	    // 创建操作图片用的matrix对象
	    Matrix matrix = new Matrix();
	    // 计算宽高缩放率
	    //float scaleWidth = ((float) newHeight) / height;
	    float scaleHeight = ((float) newHeight) / height;
	    // 缩放图片动作
	    matrix.postScale(scaleHeight, scaleHeight);
	    Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
	                    (int) height, matrix, true);
	    return bitmap;
    	
	}
	public static Bitmap zoomHImage(Bitmap bgimage, double newWidth) {
	    // 获取这个图片的宽和高
	    float width = bgimage.getWidth();
	    float height = bgimage.getHeight();
	    // 创建操作图片用的matrix对象
	    Matrix matrix = new Matrix();
	    // 计算宽高缩放率
	    float scaleWidth = ((float) newWidth) / width;
	    //float scaleHeight = ((float) newHeight) / height;
	    // 缩放图片动作
	    matrix.postScale(scaleWidth, scaleWidth);
	    Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
	                    (int) height, matrix, true);
	    return bitmap;
    }
	
	public static int getDegree(Bitmap bitmap) { 
		
	  if(bitmap.getWidth()>bitmap.getHeight()){
		  return 90;
	  }else{
		  return 0;
	  }
	}  
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){  
        if(bitmap == null)  
            return null ;  
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();    
        // Setting post rotate to 90  
        Matrix mtx = new Matrix();  
        mtx.postRotate(rotate);  
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);  
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		 this.mwidth = width;  
		 this.mheight = height;  
		 if (mPreviewRunning) {  
			 myCamera.stopPreview();  
	        }  
	        Parameters params = myCamera.getParameters();  
	        params.setPictureFormat(PixelFormat.JPEG);// 设置图片格式  
	        //params.setPreviewSize(width, height);  
	        myCamera.setDisplayOrientation(90);
	        myCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。  
	        myCamera.setParameters(params);  
	        try {  
	        	myCamera.setPreviewDisplay(mySurfaceHolder);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        myCamera.startPreview();  
	        mPreviewRunning = true;  
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		myCamera = Camera.open();  
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		myCamera.stopPreview();  
        mPreviewRunning = false;  
        myCamera.release();  
        myCamera = null;  
	}  

	
}
