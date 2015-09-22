package com.dustcloud.heartspy;

public interface SmartWatchNotifyCallBack {
        void BlocksReceived(int NbBlocks);
        void ConnectedStateChanged(Boolean Connected);
}

