package org.javaday.reactivestreams.flux;

import org.javaday.reactivestreams.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Random;

public class FluxRetryRepeatTest {

    @Test
    @DisplayName("Create flux with unlimited retry operator")
    void testFluxUnlimitedRetry() {
        final Random random = new Random();
        Flux.range(1, 10)
                .handle((i, sink) -> {
                    if (random.nextBoolean()) {
                        throw new RuntimeException("Any exception to test");
                    } else {
                        sink.next(i);
                    }
                })
                .retry() //retries stream consumption from the beginning
                .subscribe(Util::printlnThread);
    }

    @Test
    @DisplayName("Create flux with limited retry operator")
    void testFluxLimitedRetry() {
        final Random random = new Random();
        Flux.range(1, 10)
                .handle((i, sink) -> { // have control over emitting elements
                    if (random.nextBoolean()) {
                        throw new RuntimeException("Any exception to test");
                    } else {
                        sink.next(i);
                    }
                })
                .retry(3) // 3 times
                .subscribe(Util::printlnThread,
                        ex -> System.out.println("Error: " + ex.getMessage()),
                        () -> System.out.println("Completed"));
    }

    @Test
    @DisplayName("Create flux with retry operator based on matcher")
    void testFluxRetryWithMatcher() {
        final Random random = new Random();
        Flux.range(1, 10)
                .handle((i, sink) -> {
                    if (i == 10) {
                        throw new NullPointerException("NullPointerException");
                    } else if (random.nextBoolean()) {
                        sink.error(new IOException("IOException"));
                    } else {
                        sink.next(i);
                    }
                })
                // retry only for IOException
                // when catch the error, we resubscribe to the source, and process the data again
                .retry(ex -> ex instanceof IOException)
                .subscribe(Util::printlnThread,
                        ex -> System.out.println("Error: " + ex.getMessage()),
                        () -> System.out.println("Completed"));
    }

    @Test
    @DisplayName("Create flux with infinite repeat operator which resubscribe to given stream upon completion")
    void testFluxInfiniteRepeat() {
        Flux.just(1, 2, 3)
                .repeat()
                .subscribe(Util::printlnThread,
                        System.out::println,
                        () -> System.out.println("Completed"));
    }

    @Test
    @DisplayName("Create flux with repeat operator")
    void test06() {
        Flux.just(1, 2, 3)
                .log()
                .repeat(2) // 2 times
                .subscribe(Util::printlnThread,
                        System.out::println,
                        () -> System.out.println("Completed"));
    }

    @Test
    @DisplayName("Create flux with repeat operator after exception (won't be applied)")
    void testFluxRepeatAfterException() {
        Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("Any exception to test")))
                .repeat()  // is triggered ONLY after complete signal
                .subscribe(Util::printlnThread,
                        System.out::println,
                        () -> System.out.println("Completed"));
    }

}
