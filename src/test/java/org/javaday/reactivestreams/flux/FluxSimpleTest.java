package org.javaday.reactivestreams.flux;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.Arrays;

public class FluxSimpleTest {

    @Test
    @DisplayName("Creating simple flux and subscriber")
    void testFluxCreationAndSubscription() {
        Flux.just(1, 2, 3)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1); // number of elements
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("Received:" + value);
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("Completed");
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getMessage());
                    }
                });
    }

    @Test
    @DisplayName("Creating simple flux from iterable and subscriber")
    void testFluxCreationFromIterableAndSubscription() {
        Flux.fromIterable(Arrays.asList(1, 2, 3))
                .log()
                .subscribe(System.out::println);
    }

    @Test
    @DisplayName("Creating empty flux and subscriber")
    void testEmptyFluxCreationAndSubscription() {
        Flux.empty()
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Flux completed!"));
    }

    @Test
    @DisplayName("Creating simple flux and subscriber")
    void testFluxCreationAndFiltrationAndSubscription() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .log()
                .subscribe(System.out::println);
    }

    @Test
    @Tag("blocking")
    @DisplayName("Creating Flux and blocking transformation to Stream")
    void testFluxCreationWithBlockingTransformation() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .toStream() // blocking operation, be careful with it
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Creating Flux and collecting into Mono<List<Integer>>")
    void testFluxCreationAndCollecting() {
        Flux.just(1, 2, 3)
                .filter(num -> num % 2 != 0)
                .map(String::valueOf)
                .collectList() // returns Mono<List<Integer>> instead of simple List<Integer>
                .subscribe(System.out::println);
    }


}
