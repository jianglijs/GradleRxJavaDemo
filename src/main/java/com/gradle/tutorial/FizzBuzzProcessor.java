package com.gradle.tutorial;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

//https://maxwell-nc.github.io/android/rxjava2-2.html 学习rxjava
public class FizzBuzzProcessor {
    public static void main(String[] args) {
/*        Flowable.just("test","test2")
                .subscribe(str -> System.out.println("tag"+ str));*/
/*        Flowable.range(0, 5)
                .subscribe(x -> System.out.println("tag" + String.valueOf(x)));*/
    /*    Flowable.create((FlowableOnSubscribe<? super String>) e -> {
            System.out.println("hello world");

            e.onError(new RuntimeException("always fails"));
            e.onComplete();
        }, BackpressureStrategy.BUFFER)
                .retryWhen(attempts -> {
                    return attempts.zipWith(Flowable.range(1, 3), (n, i) -> i + n.getMessage()).concatMap(i -> {
                        System.out.println("delay retry by " + i + " second(s)");
                        return Flowable.timer(1, TimeUnit.SECONDS);
                    });
                })
                .blockingForEach(System.out::println);*/
        Flowable.create((FlowableOnSubscribe<String>) e -> {
            System.out.println("hello world time" + System.currentTimeMillis());
            //e.onNext("hello");
            e.onError(new IOException("always fails"));
            e.onComplete();
        }, BackpressureStrategy.BUFFER)
                .retryWhen(new RetryWhenHandler(2))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("accept " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("accept throwable" + throwable);
                    }
                });


/*        Flowable.just(1, 2, 3, 4, 5)

                .retryWhen(attempts -> {
                    return attempts.zipWith(Flowable.range(1, 3), (n, i) -> i).flatMap(i -> {
                        System.out.println("delay retry by " + i + " second(s)");
                        return Flowable.timer(i, TimeUnit.SECONDS);
                    });
                });*/

    }

    public static String convert(int fizzBuzz) {
        if (fizzBuzz % 15 == 0) {
            return "FizzBuzz";
        }
        if (fizzBuzz % 3 == 0) {
            return "Fizz";
        }
        if (fizzBuzz % 5 == 0) {
            return "Buzz";
        }
        return String.valueOf(fizzBuzz);
    }

    public final static class RetryWhenHandler implements Function<Flowable<? extends Throwable>, Flowable<Long>> {

        private static final int INITIAL = 1;
        private int maxConnectCount = 1;

        RetryWhenHandler(int retryCount) {
            this.maxConnectCount += retryCount;
        }

        @Override
        public Flowable<Long> apply(Flowable<? extends Throwable> errorObservable) {
            return errorObservable.zipWith(Flowable.range(INITIAL, maxConnectCount),
                    new BiFunction<Throwable, Integer, RetryWhenHandler.ThrowableWrapper>() {
                        @Override
                        public RetryWhenHandler.ThrowableWrapper apply(Throwable throwable, Integer i) {

                            if (throwable instanceof IOException)
                                return new RetryWhenHandler.ThrowableWrapper(throwable, i);

                            return new RetryWhenHandler.ThrowableWrapper(throwable, maxConnectCount);
                        }
                    }).concatMap(new Function<RetryWhenHandler.ThrowableWrapper, Flowable<Long>>() {
                @Override
                public Flowable<Long> apply(@NonNull RetryWhenHandler.ThrowableWrapper throwable) throws Exception {
                    final int retryCount = throwable.getRetryCount();
                    if (maxConnectCount == retryCount) {
                        return Flowable.error(throwable.getSourceThrowable());
                    }

                    return Flowable.timer(500, TimeUnit.MILLISECONDS, Schedulers.trampoline());
                }
            });
        }

        public static final class ThrowableWrapper {

            private Throwable sourceThrowable;
            private Integer retryCount;

            ThrowableWrapper(Throwable sourceThrowable, Integer retryCount) {
                this.sourceThrowable = sourceThrowable;
                this.retryCount = retryCount;
            }

            Throwable getSourceThrowable() {
                return sourceThrowable;
            }

            Integer getRetryCount() {
                return retryCount;
            }
        }
    }


}
