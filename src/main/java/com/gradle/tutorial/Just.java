package com.gradle.tutorial;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Just {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(1);
        list.add(5);

        Flowable.just(list)
                .concatMap((Function<List<Integer>, Flowable<Integer>>) integers -> {
                    System.out.println("a " + integers);
                    return Flowable.fromIterable(integers);
                })
                .concatMap(new Function<Integer, Publisher<Integer>>() {
                    @Override
                    public Publisher<Integer> apply(Integer integer) throws Exception {
                        SomeArge bigInteger = new SomeArge(1);
                        return Flowable.just(integer).concatMap(new Function<Integer, Flowable<Integer>>() {
                            @Override
                            public Flowable<Integer> apply(Integer integer) throws Exception {
                                System.out.println("b " + integer);

                                if (bigInteger.i == 2) {
                                    return Flowable.just(integer);
                                }
                                bigInteger.i++;
                                return Flowable.error(new IOException("always fails"));

                            }
                        }).retryWhen(new FizzBuzzProcessor.RetryWhenHandler(2));
                    }
                })
                .toList()
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integer) throws Exception {
                        System.out.println("accept" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("throwable " + throwable);
                    }
                });
    }
}
