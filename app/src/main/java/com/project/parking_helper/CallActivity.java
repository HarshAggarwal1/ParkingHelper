package com.project.parking_helper;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.collections.CollectionsKt;

public class CallActivity extends AppCompatActivity {

    Button answer;
    Button hangup;

    private CompositeDisposable disposables;
    private OngoingCall ongoingCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        answer = findViewById(R.id.answer);
        hangup = findViewById(R.id.hangup);

        answer.setOnClickListener(v -> {
            ongoingCall.answer();
        });

        hangup.setOnClickListener(v -> {
            ongoingCall.hangup();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        assert updateUi(-1) != null;
        disposables.add(
                OngoingCall.state
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                updateUi(integer);
                            }
                        }));

        disposables.add(
                OngoingCall.state
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer == Call.STATE_DISCONNECTED;
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                finish();
                            }
                        }));

    }

    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {

        if (state != Call.STATE_RINGING) {
            answer.setVisibility(View.GONE);
        } else answer.setVisibility(View.VISIBLE);

        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE}).contains(state)) {
            hangup.setVisibility(View.VISIBLE);
        } else
            hangup.setVisibility(View.GONE);

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }
}
