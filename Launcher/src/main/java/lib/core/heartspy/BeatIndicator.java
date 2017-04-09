package lib.core.heartspy;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class BeatIndicator extends ImageView implements Runnable {

    private String LogTag = this.getClass().getSimpleName();
    private Handler Synchronized = null;
    public int WidthToHeightFactor = 1;

    private Bitmap Sensor_NotConnected_Background;
    private Bitmap Sensor_Connected_Background;
    private Bitmap Sensor_Heart_Pulsing;

    private BitmapShader ShaderImage = null;
    private Paint ShaderPainter = null;
    private Paint TextPainter = null;
    private Matrix ImageScaler = null;

    private ObjectAnimator ScaleAnimated;
    private AnimatorSet ScaleAnimation;

    private int StoredWidth=0;
    private int StoredHeight=0;

    private float ScaleFactor = 0.0f;

    private int Frequency = 0;
    private boolean Connected = false;

    public BeatIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setAdjustViewBounds(true);

        ShaderPainter = new Paint();
        ImageScaler = new Matrix();
        ScaleAnimation = new AnimatorSet();

        TextPainter = new Paint();
        TextPainter.setTextAlign(Paint.Align.CENTER);
        TextPainter.setColor(Color.parseColor("#aaccffff"));
        TextPainter.setTypeface(Typeface.DEFAULT_BOLD);

        Synchronized = new Handler(Looper.getMainLooper());
    }

    public void setHeartRate(int Value){
        Frequency = Value;
        if (!Connected) return;
        Synchronized.post(this);
    }

    public void setConnectionState(Boolean State){
        Log.d(LogTag, "Sensor is "+(Connected? "connected":"disconnected"));
        Connected = State;
        Synchronized.post(this);
    }

    void LoadResources(int Width, int Height) {        // Loading or Reloading Bitmaps ...
        if ((Height == 0) || (Width == 0)) return;
        if (( Height == StoredHeight) && (Width == StoredWidth)) return;

        StoredHeight = Height;
        StoredWidth = Width;
        Resources EmbeddedDatas = getContext().getResources();
        Sensor_NotConnected_Background = ToolBox.getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.sensor_not_connected);
        Sensor_Connected_Background = ToolBox.getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.sensor_connected);
        Sensor_Heart_Pulsing = ToolBox.getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.heart_pulsing);
    }

    void LoadShader() { // Creating shader for Heart Scaling Animation ...
        if (Sensor_Heart_Pulsing == null) return;
        ShaderImage = new BitmapShader(Sensor_Heart_Pulsing, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        ShaderPainter.setShader(ShaderImage);

        TextPainter.setTextSize(Sensor_Heart_Pulsing.getHeight()/4);
    }

    void LoadAnimation() { //Redefine Animation
        float[] KeyValues = {0.80f,1.00f,0.80f};
        ScaleAnimated = ObjectAnimator.ofFloat(this, "ScaleFactor", KeyValues);
        ScaleAnimated.setDuration(300);
        ScaleAnimated.setStartDelay(0);

        ScaleAnimation.play(ScaleAnimated);
        ScaleAnimation.setInterpolator(new LinearInterpolator());
    }
    // Called by ScaleAnimated ..
    public void setScaleFactor(float ScaleFactor) {
        this.ScaleFactor = ScaleFactor;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int Width = MeasureSpec.getSize(widthMeasureSpec);
        int Height = Math.min(Width/ WidthToHeightFactor, MeasureSpec.getSize(heightMeasureSpec));
        ScaleAnimation.cancel();
        LoadResources(Width, Height);
        if (Sensor_NotConnected_Background ==null) return;
        LoadAnimation();
        LoadShader();
        this.setMeasuredDimension(Sensor_NotConnected_Background.getWidth(), Sensor_NotConnected_Background.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Connected) {
            // Draw image Background
            canvas.drawBitmap(Sensor_Connected_Background, 0f, 0f, null);

            // Draw Heart with scaling effect
            ShaderImage.getLocalMatrix(ImageScaler);
            ImageScaler.setScale(ScaleFactor, ScaleFactor, canvas.getWidth() / 2, canvas.getHeight() / 2);
            ShaderImage.setLocalMatrix(ImageScaler);
            canvas.drawPaint(ShaderPainter);

            // Write Heart Rate ...
            canvas.drawText(Integer.toString(Frequency),canvas.getWidth() / 2, canvas.getHeight() / 2 ,TextPainter);

        } else {
            canvas.drawBitmap(Sensor_NotConnected_Background, 0f, 0f, null);
        }
        super.onDraw(canvas);
    }

    /*********************************************************************************************
     *  Refreshing and animating UI thread
     *********************************************************************************************/
    @Override
    public void run() {
        invalidate();
        ScaleAnimation.start();
    }
}

