package lc.main.hospitalcamera;

import java.io.FileNotFoundException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import lc.main.hospitalcamera.R;

public class MainActivity extends Activity{
    private Button choosePic;
    private LinearLayout preLl;
    private RelativeLayout afterLl;
    private RelativeLayout titleRl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void init(){
		choosePic = (Button)findViewById(R.id.choosePic);
		preLl = (LinearLayout)findViewById(R.id.prell);
		afterLl = (RelativeLayout)findViewById(R.id.afterll);
		titleRl = (RelativeLayout)findViewById(R.id.rlTitle);
		ButtonEffect.setButtonStateChangeListener(choosePic);
		choosePic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				 /* 开启Pictures画面Type设定为image */  
                intent.setType("image/*");  
                /* 使用Intent.ACTION_GET_CONTENT这个Action */  
                intent.setAction(Intent.ACTION_GET_CONTENT);
                
                /* 取得相片后返回本画面 */  
                startActivityForResult(intent, 1);  
			}
		});
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            
            preLl.setVisibility(View.GONE);
            afterLl.setVisibility(View.VISIBLE);
            titleRl.setVisibility(View.GONE);
            ImageView imageView = (ImageView) findViewById(R.id.iv01);  
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

    /*
	public static String getRealFilePath( final Context context, final Uri uri ) {  
	    if ( null == uri ) return null;  
	    final String scheme = uri.getScheme();  
	    String data = null;  
	    if ( scheme == null )  
	        data = uri.getPath();  
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {  
	        data = uri.getPath();  
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {  
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );  
	        if ( null != cursor ) {  
	            if ( cursor.moveToFirst() ) {  
	                int index = cursor.getColumnIndex( ImageColumns.DATA );  
	                if ( index > -1 ) {  
	                    data = cursor.getString( index );  
	                }  
	            }  
	            cursor.close();  
	        }  
	    }  
	    return data;  
	} 
	*/ 
}
