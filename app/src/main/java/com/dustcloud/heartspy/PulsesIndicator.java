package com.dustcloud.heartspy;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Canvas;


import static com.dustcloud.heartspy.ToolBox.getScaledBitmap;

public class PulsesIndicator extends ImageView implements ObjectAnimator.AnimatorListener {

    public int WidthToHeightFactor = 1;

    private boolean FrequencyChanged = true;
    private int Frequency = 0;
    final private int ScanDuration = 3000; // Total time Scan duration in ms
    final private int PulseDuration = 200; // Fixed time Pulse duration in ms

    private int StoredWidth=0;
    private int StoredHeight=0;

    AnimatorSet AnimationProperties;
    private BitmapShader ShaderImage = null;
    private Matrix ImageMover = null;

    private Paint ShaderPainter = null;
    private Path SpotTrace=null;
    private Path PulseElement=null;

    // Scan offset ==> Shift ***
    private float Shift;

    private Bitmap SpotLight;
    private Bitmap SpotGrid;

    public PulsesIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setAdjustViewBounds(true);

        ShaderPainter = new Paint();
        ImageMover = new Matrix();

        SpotTrace = new Path();
        AnimationProperties = new AnimatorSet();

        PulseElement = new Path();
        PulseElement.moveTo(0.00f, 0.00f);
        PulseElement.quadTo(0.25f, 0.00f, 0.25f, -0.20f);
        PulseElement.quadTo(0.25f,-0.45f, 0.35f, -0.45f);
        PulseElement.quadTo(0.50f,-0.45f, 0.50f,  0.00f);
        PulseElement.quadTo(0.50f, 0.40f, 0.65f,  0.40f);
        PulseElement.quadTo(0.75f, 0.40f, 0.75f,  0.20f);
        PulseElement.quadTo(0.75f, 0.00f, 1.00f, 0.00f);
     }

    public void setHeartRate(int Frequency){
        if (this.Frequency == Frequency) return;
        this.Frequency = Frequency;
        FrequencyChanged = true;
    }

    public void setShift(float Shift) {
        this.Shift = Shift;
        postInvalidate();
    }

    void LoadResources(int Width, int Height) {        // Loading or Reloading Bitmaps ...
        if ((Height == 0) || (Width == 0)) return;
        if (( Height == StoredHeight) && (Width == StoredWidth)) return;

        StoredHeight = Height;
        StoredWidth = Width;
        Resources EmbeddedDatas = getContext().getResources();
        SpotLight = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.spot_light);
        SpotGrid = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.spot_grid);
    }

    private void LoadAnimation() { //Redefine Animation
        if (SpotLight == null) return;
        if (SpotGrid == null) return;

        ObjectAnimator ShiftAnimated = ObjectAnimator.ofFloat(this, "Shift", - SpotLight.getWidth(), SpotGrid.getWidth() );
        ShiftAnimated.setRepeatCount(ValueAnimator.INFINITE);
        ShiftAnimated.setDuration(ScanDuration);
        ShiftAnimated.setStartDelay(0);

        AnimationProperties.play(ShiftAnimated);
        AnimationProperties.setInterpolator(new LinearInterpolator());
        ShiftAnimated.addListener(this);
        AnimationProperties.start();
    }

    private void LoadShader() { // Creating shader for Heart Scaling Animation ...

        if (SpotLight == null) return;
        ShaderImage = new BitmapShader(SpotLight, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        ShaderPainter.setShader(ShaderImage);
        ShaderPainter.setStrokeWidth(SpotLight.getHeight() / 30f);
        ShaderPainter.setStyle(Paint.Style.STROKE);
    }

    private void BuildTrace(){
        SpotTrace.reset();
        if (SpotGrid == null) return;
        int Pulses = (Frequency * ScanDuration) / 60000;
        int DeadTime = ScanDuration;
        float DeadTimePathIncrement = (float)DeadTime / (float)ScanDuration;

        if (Pulses != 0) {
            DeadTime = ScanDuration - (Pulses * PulseDuration);
            DeadTimePathIncrement = (float) DeadTime / (float) Pulses / (float) PulseDuration;
        }

        float Offset = 0;
        SpotTrace.moveTo(0, 0.5f);
        SpotTrace.lineTo(DeadTimePathIncrement / 2f, 0.5f);
        Offset = Offset + DeadTimePathIncrement /2f;
        for (int Count = 2; Count <= Pulses; Count++)
        {
            SpotTrace.addPath(PulseElement,Offset,0.5f);
            Offset = Offset + 1f;
            SpotTrace.lineTo(Offset + DeadTimePathIncrement,  0.5f);
            Offset = Offset + DeadTimePathIncrement;
        }
        if (Pulses != 0) {
            SpotTrace.addPath(PulseElement, Offset, 0.5f);
            Offset = Offset + 1f;
        }
        SpotTrace.lineTo(Offset + DeadTimePathIncrement /2f, 0.5f);
        Offset = Offset + DeadTimePathIncrement /2f;

        // Scaling Trace to Widget
        Matrix TraceScaler = new Matrix();
        TraceScaler.setScale(SpotGrid.getWidth() / Offset ,SpotGrid.getHeight() );
        SpotTrace.transform(TraceScaler);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int Width = MeasureSpec.getSize(widthMeasureSpec);
        int Height = Math.min(Width/ WidthToHeightFactor, MeasureSpec.getSize(heightMeasureSpec));
        AnimationProperties.cancel();
        if ((Height == 0) || (Width == 0)) return;

        LoadResources(Width, Height);
        if (SpotGrid ==null) return;
        LoadShader();
        BuildTrace();
        LoadAnimation();
        this.setMeasuredDimension(SpotGrid.getWidth(), SpotGrid.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ShaderImage.getLocalMatrix(ImageMover);
        ImageMover.setTranslate(Shift, 0f);
        ShaderImage.setLocalMatrix(ImageMover);
        canvas.drawPath(SpotTrace, ShaderPainter);
        canvas.drawBitmap(SpotGrid, 0f,0f, null);
        super.onDraw(canvas);
    }

    @Override
    public void onAnimationEnd(Animator animation) {}
    @Override
    public void onAnimationStart(Animator animation) {}
    @Override
    public void onAnimationCancel(Animator animation) {}
    @Override
    public void onAnimationRepeat(Animator animation) {
        if (!FrequencyChanged) return;
        FrequencyChanged = false;
        BuildTrace();
    }

}




