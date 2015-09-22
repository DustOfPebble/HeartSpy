package com.dustcloud.heartspy;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Random;

import static com.dustcloud.heartspy.ToolBox.getScaledBitmap;

public class SmartWatchExchangeView extends FrameLayout implements SmartWatchNotifyCallBack{

    public int WidthToHeightFactor = 5; // Forcing an AspectRatio of subWidget

    private SmartWatchIndicator SmartWatchViewer=null;
    private SmartWatchManager SmartWatchProvider;


   // Default constructor (Seems to be Not mandatory)
    public SmartWatchExchangeView(Context context)
    {
        super(context);
        initObjects(context);
    }

    // Alternative constructor (Seems to effectively Called) --> Crash without !
    public SmartWatchExchangeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initObjects(context);
    }

    private void initObjects(Context context)
    {
        // Inflate the Layout from XML definition
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.smartwatch_exchange_view, this, true);
        setWillNotDraw(false);
        SmartWatchViewer = (SmartWatchIndicator) findViewById(R.id.smartwatch);
        SmartWatchViewer.WidthToHeightFactor = WidthToHeightFactor;
        SmartWatchProvider = new SmartWatchManager(this,getContext());
        if (SmartWatchProvider.isConnected()) SmartWatchViewer.setConnectedState(true);
     }

    // Callback from SmartWatch Mnager
    @Override
    public void BlocksReceived(int NbBlocks){
        SmartWatchViewer.StartReceiveBlocks(NbBlocks);
    };

    @Override
    public void ConnectedStateChanged(Boolean Connected){
        SmartWatchViewer.setConnectedState(Connected);
    };


}
