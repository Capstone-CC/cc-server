package com.cau.cc.webrtc.model;

import lombok.Builder;
import lombok.Data;

import java.util.TimerTask;

@Data
@Builder
public class MyTimerTask extends TimerTask {

    MatchingAccount myAccount;

    public MyTimerTask(MatchingAccount myAccount) {
        this.myAccount = myAccount;
    }

    @Override
    public void run() {

    }
}
