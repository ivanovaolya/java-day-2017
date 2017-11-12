package org.javaday.reactivestreams.flux;

import org.javaday.reactivestreams.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FluxThreadsTest {

    @Test
    @Tag("async")
    @DisplayName("Create flux which is executed in different thread")
    void testFluxInDifferentThread() {
        Flux.just(1, 2, 3)
                .subscribeOn(Schedulers.single())
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with subscribeOn and publishOn usage")
    void testFluxWithSubscribeOnAndPublishOnUsage() {
        Flux.just(1, 2, 3)
                .doOnNext(Util::printlnThread)
                //could be anywhere in chain because applied to the beginning anyway
                .subscribeOn(Schedulers.newSingle("thread1"))
                .publishOn(Schedulers.newSingle("thread2"))
                .subscribe(Util::printlnThread);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with concurrent publishers being executed in it's own thread")
    void testFluxWithConcurrentPublishers() {
        Flux.just(1, 2, 3)
                .subscribeOn(Schedulers.newSingle("thread1"))
                .mergeWith(Flux.just(4, 5, 6)
                        .subscribeOn(Schedulers.newSingle("thread2")))
                .subscribe(Util::printlnThread);
    }


    @Test
    @Tag("async")
    @DisplayName("Create flux with parallel scheduler (which doesn't make the stream parallel)")
    void testFluxWithParallelScheduler() {
        Flux.range(1, 1000)
                .subscribeOn(Schedulers.parallel()) //usage of parallel scheduler doesn't make stream parallel
                .subscribe(Util::printlnThread);
        Util.wait(1, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with parallel processing of data using flatMap with parallel scheduler")
    void testFluxParallelProcessingUsingParallelScheduler() {
        Flux.range(1, 1000)
                .flatMap(i -> Flux.just(i) // can use Mono, create 100 of sequences
                        // will be executed in separate threads
                        .doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .subscribeOn(Schedulers.parallel()))
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with parallel processing of data using flatMap with elastic scheduler")
    void testFluxParallelProcessingUsingElasticScheduler() {
        Flux.range(1, 1000)
                .flatMap(i -> Flux.just(i)
                        .doOnNext(j -> Util.wait(10, MILLISECONDS))
                        .subscribeOn(Schedulers.elastic())) // growing thread-pool
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with native Reactor parallel stream processing")
    void test10() {
        Flux.range(1, 1000)
                // tells Reactor how many parallel rails to create with regard of splitting original stream
                .parallel(8)
                // specifies how many threads will actually do the job
                .runOn(Schedulers.newParallel("myThread", 4))
                // we will have only 4 threads (myThread), not matter of number of parallelism
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create flux with native Reactor parallel stream processing with defaults")
    void test11() {
        Flux.range(1, 100)
                .parallel()
                .runOn(Schedulers.newParallel("myThread"))
                .doOnNext(i -> Util.wait(10, MILLISECONDS)) // emulating long running operation
                .sequential() // go back to normal Flux but not introducing new threads!
                .subscribe(Util::printlnThread);
        Util.wait(5, SECONDS);
    }

}
