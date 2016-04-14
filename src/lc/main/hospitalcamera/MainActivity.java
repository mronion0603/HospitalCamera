package lc.main.hospitalcamera;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import lc.main.hospitalcamera.R;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnTouchListener{
    private Button choosePic,setScale,enterBt,confirm,cancel,trim;
    private LinearLayout preLl;
    private LinearLayout preUp,preDown;
    private RelativeLayout afterLl;
    private RelativeLayout titleRl;
    ImageView imageView ;  
    private ImageView myImageView;
	Camera myCamera;
	SurfaceView mySurfaceView;
	SurfaceHolder mySurfaceHolder;
	//private MySurfaceView dotSurfaceView = null;
	int mwidth;
	int mheight;
	private boolean mPreviewRunning= false; 
	private int screenWidth;
	private int screenHeight;
	private int lastX, lastY;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void init(){
		imageView = (ImageView)findViewById(R.id.iv01);
		choosePic = (Button)findViewById(R.id.choosePic);
		setScale = (Button)findViewById(R.id.setScale);
		enterBt = (Button)findViewById(R.id.enter);
		confirm = (Button)findViewById(R.id.confirm);
		cancel = (Button)findViewById(R.id.cancel);
		trim = (Button)findViewById(R.id.trim);
		preLl = (LinearLayout)findViewById(R.id.prell);
		preUp = (LinearLayout)findViewById(R.id.preUp);
		preDown = (LinearLayout)findViewById(R.id.preDown);
		afterLl = (RelativeLayout)findViewById(R.id.afterll);
		titleRl = (RelativeLayout)findViewById(R.id.rlTitle);
		mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
	    mySurfaceHolder = mySurfaceView.getHolder();//获得SurfaceHolder
	    mySurfaceHolder.addCallback(this);
	    mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	  
		ButtonEffect.setButtonStateChangeListener(choosePic);
		ButtonEffect.setButtonStateChangeListener(setScale);
		ButtonEffect.setButtonStateChangeListener(enterBt);
		ButtonEffect.setButtonStateChangeListener(confirm);
		ButtonEffect.setButtonStateChangeListener(cancel);
		ButtonEffect.setButtonStateChangeListener(trim);
		this.myImageView = (ImageView) this.findViewById(R.id.ImageView);
		this.myImageView.setOnTouchListener(this);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - 150;
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
				imageView.setAlpha(255);
				myImageView.setVisibility(View.VISIBLE);
				Toast.makeText(MainActivity.this, "请设置起始点", Toast.LENGTH_SHORT).show();
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
				
			}
		});
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
		trim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
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

			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();

			break;
		case MotionEvent.ACTION_UP:
			break;
		}
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
                int degree = getDegree(bitmap );
                Bitmap smallBitmap;
                if(bitmap.getWidth()<=bitmap.getHeight()){
                 smallBitmap = zoomHImage(bitmap,width);
                }else{
                 smallBitmap = zoomVImage(bitmap,width);
                }
                smallBitmap = rotateBitmap(smallBitmap,degree) ;
                imageView.setImageBitmap(smallBitmap);  
                imageView.setAlpha(50);
                
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
