package com.commonrail.mtf.util.common;

import android.os.CountDownTimer;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/27 上午12:46
 * 修改人：wengyiming
 * 修改时间：16/3/27 上午12:46
 * 修改备注：
 */
public class RtTimer extends CountDownTimer {
    
    
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public RtTimer(final long millisInFuture, final long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(final long millisUntilFinished) {
       mTimeOut.timing(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        mTimeOut.timeOut();
    }

    public interface TimeOut {
        void timeOut();
        void timing(final long mMillisUntilFinished);
    }

    public void setTimeOut(final TimeOut mTimeOut) {
        this.mTimeOut = mTimeOut;
    }

    private TimeOut mTimeOut;
}
