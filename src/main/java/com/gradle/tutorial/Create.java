package com.gradle.tutorial;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class Create {
    public static void main(String[] args) {
        Disposable disposable = Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            System.out.println("hello world");
            for (int i = 0; i < 10; i++) {
                if (!e.isCancelled()) {
                    e.onNext(i);
                    Thread.sleep(500);
                }
            }
            // e.onError(new RuntimeException("always fails"));
            e.onComplete();
        }, BackpressureStrategy.BUFFER)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
//                .toList()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("throwable " + throwable);
                    }
                });
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("a b c ");
        disposable.dispose();
    }


}
