package com.caesar.rxjava2example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FirstActivity extends AppCompatActivity {

    /**
     * 用来保存多个Disposable,用于页面退出的时候取消所有订阅，防止页面退出之后获得数据UI导致的crash。
     */
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //getObservabe().subscribe(getObserver());
        //链式表示

        /**
         * 上游可以发送多个onNext，下游也可以接收多个onNext。
         * 上游发送onComplete/onError之后还会继续发送事件，而下游将不会继续接收。
         */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribeOn(Schedulers.io())
                .sample(2, TimeUnit.SECONDS)        //从上游每2秒取一次数据
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
            /**
             * 用于控制取消订阅。
             */
            Disposable disposable;
            /**
             * 最先调用这个方法。
             * @param d
             */
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
                disposable = d;
            }

            @Override
            public void onNext(Integer value) {
                if (value == 2) {
                    disposable.dispose(); //取消订阅。
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 创建一个上游，产生数据。
     * @return
     */
    public Observable getObservabe() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });
    }

    /**
     * 创建一个下游，消费数据。
     * @return
     */
    public Observer getObserver() {
        return new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("yang", " onSubscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.i("yang", " onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("yang", " onError");
            }

            @Override
            public void onComplete() {
                Log.i("yang", " onComplete");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
