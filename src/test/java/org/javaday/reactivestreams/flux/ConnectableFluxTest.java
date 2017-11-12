package org.javaday.reactivestreams.flux;

import org.javaday.reactivestreams.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ConnectableFluxTest {

    @Test
    @Tag("async")
    @DisplayName("Create connectable flux with ability to cache infinite number of messages and replay them")
    void testConnectableFlux() {
        ConnectableFlux<Long> connectableFlux = Flux.interval(Duration.ofSeconds(1)).replay();
        // when you subscribe to connectibleFlux, nothing yet happened
        connectableFlux.subscribe(i -> Util.printlnThread("First subscriber received: " + i));
        System.out.println("Nothing yet happened at this point!!!");
        connectableFlux.connect();  // here you receive msg AFTER .connect()
        Util.wait(5, SECONDS);
        connectableFlux.subscribe(i -> Util.printlnThread("Second subscriber received: " + i));
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create connectable flux with ability to cache 3 last messages and replay them")
    void testConnectableFluxWithNumberOfBufferedElements() {
        ConnectableFlux<Long> connectableFlux = Flux.interval(Duration.ofSeconds(1))
                .replay(3); // buffered 3 elements
        connectableFlux.subscribe(i -> Util.printlnThread("First subscriber received: " + i));
        System.out.println("Nothing yet happened at this point!!!");
        connectableFlux.connect();
        Util.wait(5, SECONDS);
        connectableFlux.subscribe(i -> Util.printlnThread("Second subscriber received: " + i));
        Util.wait(10, SECONDS);
    }

    @Test
    @Tag("async")
    @DisplayName("Create connectable flux with ability to cache messages for the last 2 sec")
    void testConnectableFluxWithLastElements() {
        ConnectableFlux<Long> connectableFlux = Flux.interval(Duration.ofSeconds(1))
                .replay(Duration.ofSeconds(2)); // cache all the elements for the last 2 seconds
        connectableFlux.subscribe(i -> Util.printlnThread("First subscriber received: " + i));
        System.out.println("Nothing yet happened at this point!!!");
        connectableFlux.connect();
        Util.wait(5, SECONDS);
        connectableFlux.subscribe(i -> Util.printlnThread("Second subscriber received: " + i));
        Util.wait(10, SECONDS);
    }

}


