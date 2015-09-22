package com.dustcloud.heartspy;

import android.view.View;

public class DataStreamAnimation {

    private float Move;
    private int Transparency;
    private View Parent;

    public DataStreamAnimation(View Parent, float StartX, int StartTransparency) {
        Move = StartX;
        Transparency = StartTransparency;
        this.Parent = Parent;
    }

    public void setMove(float Move) {
        this.Move = Move;
        Parent.postInvalidate();
    }

    public float getMove() {
        return  this.Move;
    }
    public void setTransparency(int Transparency) {
        this.Transparency = Transparency;
        Parent.postInvalidate();
    }
    public int getTransparency () {
        return this.Transparency;
    }
}

