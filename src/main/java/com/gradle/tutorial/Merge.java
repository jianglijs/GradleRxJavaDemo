package com.gradle.tutorial;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Merge {
    public static void main(String[] args) {
        Flowable<Integer> odds = Flowable.just(1, 3, 5).subscribeOn(Schedulers.newThread());
        Flowable<Integer> evens = Flowable.just(2, 4, 6);

/*        Flowable.merge(odds, evens)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Sequence complete.");
                    }

                });*/
        Flowable.merge(odds, evens)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        System.err.println("Error: " + error.getMessage());
                    }
                });
    }
}
