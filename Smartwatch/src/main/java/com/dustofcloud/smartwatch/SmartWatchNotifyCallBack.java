package com.dustofcloud.smartwatch;

public interface SmartWatchNotifyCallBack {
        void BlocksReceived(int NbBlocks);
        void ConnectedStateChanged(Boolean Connected);
}

