package com.project.parking_helper;

import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        Toast.makeText(this, "Call Service Started", Toast.LENGTH_SHORT).show();
        CallActivity.start(this, call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }
}