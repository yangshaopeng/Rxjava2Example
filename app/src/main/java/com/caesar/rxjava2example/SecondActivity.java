package com.caesar.rxjava2example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Flowable 和 Subscribe
 * ERROR
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        /**
                         * 指定下游能处理多少事件。
                         */
                        s.request(2);
                        //s.request(Integer.MAX_VALUE);//默认处理
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i("yang", "onNext");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i("yang", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("yang", "onComplete");
                    }
                });
    }
}
