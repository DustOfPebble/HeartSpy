package com.dustcloud.heartspy;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.dustcloud.heartspy.ToolBox.getScaledBitmap;

//ToDo : remove any related smart watch element after imported to dayly race

public class SmartWatchIndicator extends ImageView{

    public int WidthToHeightFactor = 1;

    private Bitmap WatchInactive;
    private Bitmap WatchActive;
    private Bitmap FaceNeutral;
    private Bitmap FaceHappy;

    private int StoredWidth=0;
    private int StoredHeight=0;

    private boolean Connected = false;

    private ArrayList<DataStreamAnimation> Parameters = new ArrayList<>();
    private Bitmap DataBlockLight;
    private Bitmap BlockEmitter;
    private int Delay = 300; // 100 ms between block ...
    private int MoveDuration = 500; // 300 ms for movement ...
    private int ShadeDuration = 300; // 100 ms for shade In effect ...
    private float BlockStreamEmitter = 0;
    private float BlockStreamSmartWatch = 0;
    private float OffsetWatch = 0;
    private float OffsetEmitter = 0;
    private Paint AlphaPainter;

    public SmartWatchIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setAdjustViewBounds(true);
        AlphaPainter = new Paint();
    }

     private void LoadResources(int Width, int Height) {        // Loading or Reloading Bitmaps ...
        if ((Height == 0) || (Width == 0)) return;
        if (( Height == StoredHeight) && (Width == StoredWidth)) return;

        StoredHeight = Height;
        StoredWidth = Width;
        Resources EmbeddedDatas = getContext().getResources();
        WatchActive = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.smartwatch_red_connected);
        WatchInactive = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.smartwatch_not_connected);
        FaceHappy = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.face_active_happy);
        FaceNeutral = getScaledBitmap(StoredWidth, StoredHeight, EmbeddedDatas, R.drawable.face_active_neutral);
        DataBlockLight = getScaledBitmap(Width, Height, EmbeddedDatas, R.drawable.block_light);
        BlockEmitter = getScaledBitmap(Width, Height, EmbeddedDatas, R.drawable.bluetooth_emitter);
    }


    private void StartDataStreamAnimation(int NbBlock, float StartX, float StopX) {
        if (DataBlockLight == null) return;
        Parameters.clear();

        for (int Sprite = 1; Sprite <= NbBlock; Sprite++) {
            DataStreamAnimation Parameter = new DataStreamAnimation(this,StartX, 0 );

            ObjectAnimator ShadeInSprite = ObjectAnimator.ofInt(Parameter, "Transparency", 0, 255);
            ShadeInSprite.setDuration(ShadeDuration);
            AnimatorSet ShadeInEngine = new AnimatorSet();
            ShadeInEngine.setInterpolator(new LinearInterpolator());
            ShadeInEngine.play(ShadeInSprite);
            ShadeInEngine.setStartDelay(Sprite * Delay);
            ShadeInEngine.start();

            ObjectAnimator MoveSprite = ObjectAnimator.ofFloat(Parameter, "Move", StartX, StopX);
            MoveSprite.setDuration(MoveDuration);
            AnimatorSet MoveEngine = new AnimatorSet();
            MoveEngine.setInterpolator(new AccelerateDecelerateInterpolator());
            MoveEngine.play(MoveSprite);
            MoveEngine.setStartDelay((Sprite * Delay)+ ShadeDuration);
            MoveEngine.start();

            ObjectAnimator ShadeOutSprite = ObjectAnimator.ofInt(Parameter, "Transparency", 255, 0);
            ShadeOutSprite.setDuration(ShadeDuration);
            AnimatorSet ShadeOutEngine = new AnimatorSet();
            ShadeOutEngine.setInterpolator(new LinearInterpolator());
            ShadeOutEngine.play(ShadeOutSprite);
            ShadeOutEngine.setStartDelay((Sprite * Delay) + ShadeDuration + MoveDuration);
            ShadeOutEngine.start();

            Parameters.add(Parameter);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int Width = MeasureSpec.getSize(widthMeasureSpec);
        int Height = Math.min(Width/ WidthToHeightFactor, MeasureSpec.getSize(heightMeasureSpec));
        if ((Height == 0) || (Width == 0)) return;

        LoadResources(Width, Height);

        BlockStreamEmitter = (BlockEmitter.getWidth() > DataBlockLight.getWidth())
                ? (Width-BlockEmitter.getWidth())+ (BlockEmitter.getWidth() - DataBlockLight.getWidth())/2f
                : (Width-DataBlockLight.getWidth())+ (DataBlockLight.getWidth() - BlockEmitter.getWidth())/2f;

        BlockStreamSmartWatch = (WatchInactive.getWidth() > DataBlockLight.getWidth())
                ? (WatchInactive.getWidth()- DataBlockLight.getWidth()) /2f
                : 0f;

        OffsetEmitter = (BlockEmitter.getWidth() > DataBlockLight.getWidth())
                ? (Width-DataBlockLight.getWidth())+ (DataBlockLight.getWidth() - BlockEmitter.getWidth())/2f
                : (Width-BlockEmitter.getWidth())+ (BlockEmitter.getWidth() - DataBlockLight.getWidth())/2f;

        OffsetWatch = (WatchInactive.getWidth() > DataBlockLight.getWidth())
                ? 0f
                : (DataBlockLight.getWidth() - WatchInactive.getWidth()) /2f;

        this.setMeasuredDimension(Width, Height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // Drawing DataStream
        for (DataStreamAnimation Parameter : Parameters) {
            AlphaPainter.setAlpha(Parameter.getTransparency());
            canvas.drawBitmap(DataBlockLight, Parameter.getMove(), 0f, AlphaPainter);
        }
        canvas.drawBitmap(BlockEmitter,OffsetEmitter,0f, null);

        // Drawing WatchFrame
        if (Connected) {
            // Draw image Background
            canvas.drawBitmap(FaceHappy, OffsetWatch, 0f, null);
            canvas.drawBitmap(WatchActive, OffsetWatch, 0f, null);
        } else {
            canvas.drawBitmap(WatchInactive, OffsetWatch, 0f, null);
        }
        super.onDraw(canvas);
    }

    // Callable from Parent
    public void setConnectedState(Boolean Connected){
        this.Connected = Connected;
        postInvalidate();
    }

    public void StartReceiveBlocks(int NbBlocks){
        StartDataStreamAnimation(NbBlocks, BlockStreamSmartWatch, BlockStreamEmitter);
    }

    public void StartSendBlocks(int NbBlocks){
        StartDataStreamAnimation(NbBlocks, BlockStreamEmitter, BlockStreamSmartWatch);
    }

}

