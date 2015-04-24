package edu.berkeley.mims.treasuretrail;

/**
 * Created by Dheean on 4/20/15.
 */


import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

public class AnimationView extends View {

    Paint paint;
    Paint paint2;

    Bitmap bm;
    int bm_offsetX, bm_offsetY;

    Path animPath;
    PathMeasure pathMeasure;
    float pathLength;

    float step;   //distance each step
    float distance;  //distance moved

    float[] pos;
    float[] tan;

    Matrix matrix;
    Canvas canvas;
    Bitmap alteredBitmap;
    Bitmap resizedBitmap;

    public AnimationView(Context context) {
        super(context);
        Log.i("Dheera - ","Inside AnimationView constructor");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("Dheera - ","Inside AnimationView constructor 2 attr");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("Dheera - ","Inside AnimationView constructor 3 attr");
        initMyView(context);
    }

    public void initMyView(Context context){

        //

        Drawable myDrawable = getResources().getDrawable(R.drawable.bg);
        alteredBitmap = ((BitmapDrawable) myDrawable).getBitmap();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width=size.x;
        int height=size.y;
//        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
//        float dp = 350f;
//        float fpixels = metrics.density * dp;
//        int pixels = (int) (fpixels + 0.5f);
        resizedBitmap = Bitmap.createScaledBitmap(alteredBitmap, 3*width/4, height, false);


        //

        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(50);
        paint.setStyle(Paint.Style.STROKE);

        //paint object for main line
        paint2 = new Paint();
        paint2.setColor(Color.DKGRAY);
        paint2.setStrokeWidth(50);
        paint2.setStyle(Paint.Style.STROKE);

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.pet2);
        bm_offsetX = bm.getWidth()/2;
        bm_offsetY = bm.getHeight()/2;

        animPath = new Path();
        animPath.moveTo(740, 1400);
        animPath.lineTo(740, 1270);
        animPath.lineTo(1330, 1270);
       // animPath.lineTo(500, 450);
        //animPath.lineTo(100, 300);
        //animPath.lineTo(600, 300);
        //animPath.lineTo(100, 100);
        //animPath.close();

       // Log.i("Dheera - ","Inside AnimationView initMyView() ");
        pathMeasure = new PathMeasure(animPath, false);
        pathLength = pathMeasure.getLength();

        Toast.makeText(getContext(), "pathLength: " + pathLength, Toast.LENGTH_LONG).show();

        step = 5;
        distance = 0;
        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {

       // canvas.drawBitmap(resizedBitmap,0,0,null);

        //Draw lines for main path
        canvas.drawLine(740,1400,740,1370,paint2);
        canvas.drawLine(740,1270,1360,1270,paint2);
        canvas.drawLine(1360-bm_offsetX/4,1270,1360-bm_offsetX/4,955,paint2);
        canvas.drawLine(1360-bm_offsetX/4,955,155-bm_offsetX/4,955,paint2);
        //canvas.drawLine(1100,500,750,500,paint2);

        //Draw path for the avatar's workout
        canvas.drawPath(animPath, paint);

        if(distance < pathLength){

            pathMeasure.getPosTan(distance, pos, tan);

            matrix.reset();
            float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
           // Log.i("Dheera - ", "Inside AnimationView distance < pathLength...degrees= " + degrees);
            matrix.postRotate(degrees, bm_offsetX, bm_offsetY);

            matrix.postTranslate(pos[0]-bm_offsetX, pos[1]-bm_offsetY);

            canvas.drawBitmap(bm, matrix, null);

            distance += step;


        }else{
            //distance = 0;
            matrix.reset();
            float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
          //  Log.i("Dheera - ","Inside else distance >= pathLength...degrees= "+degrees);
            matrix.postRotate(degrees, bm_offsetX, bm_offsetY);

            matrix.postTranslate(pos[0]-bm_offsetX, pos[1]-bm_offsetY);

            canvas.drawBitmap(bm, matrix, null);

        }
        //Log.i("Dheera - ","Inside AnimationView before invalidate() ");
        invalidate();
    }

}