package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {

    @Test
    public void flux() {

        //Publisher Mono: 1 or N
        Flux<String> flux = Flux.just("Dyane", "Araújo", "Dev", "Java")
            .log();

        log.info("-------TEST-----");

        StepVerifier.create(flux)
            .expectNext("Dyane", "Araújo", "Dev", "Java") //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumer() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 5)
            .log();

        //Subscribing
        flux.subscribe(s -> log.info("Value {}", s)); //All Consumer

        log.info("-------TEST CONSUMER-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumerFromList() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.fromIterable(List.of(1, 2, 3, 4, 5))
            .log();

        //Subscribing
        flux.subscribe(s -> log.info("Value {}", s)); //All Consumer

        log.info("-------TEST CONSUMER FROM LIST-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumerError() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 5)
            .log()
            .map(i -> {
                if (i == 4) { //stop
                    throw new IndexOutOfBoundsException("Index Error");
                }
                return i;
            });

        //Subscribing
        flux.subscribe(s -> log.info("Value {}", s), Throwable::printStackTrace,
            () -> log.info("DONE!"), subscription -> subscription.request(3)); //Consumer with Backpressure

        log.info("-------TEST CONSUMER ERROR-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3) //onNext
            .expectError(IndexOutOfBoundsException.class)//onError
            .verify(); //Subscriber Cancelad
    }

    @Test
    public void fluxSubscriberConsumerUglyBackpressure() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 10)
            .log();

        //Subscribing
        flux.subscribe(new Subscriber<>() {

            //Backpressure via Interface

            private int count = 0;
            private Subscription subscription;
            private final int requestCount = 2;

            @Override
            public void onSubscribe(final Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(final Integer integer) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(final Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("-------TEST CONSUMER BACKPRESSURE INTERFACE-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumerNotSoUglyBackpressure() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 10)
            .log();

        //Subscribing
        flux.subscribe(new BaseSubscriber<>() {

            //Backpressure via Abstract, with Internal Subscription

            private int count = 0;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(final Subscription subscription) {
                request(requestCount);
            }

            @Override
            protected void hookOnNext(final Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount);
                }
            }
        });

        log.info("-------TEST CONSUMER BACKPRESSURE ABSTRACT-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumerPrettyBackpressure() {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 10)
            .log()
            .limitRate(3);

        //Subscribing
        flux.subscribe(s -> log.info("Value {}", s)); //with Backpressure

        log.info("-------TEST CONSUMER BACKPRESSURE LIMIT RATE-----");

        StepVerifier.create(flux)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void fluxSubscriberConsumerInterval() throws Exception {

        //Publisher Mono: 1 or N
        Flux<Long> flux = Flux.interval(Duration.ofMillis(2000))
            .take(10) //to 10
            .log();

        //Subscribing
        flux.subscribe(i -> log.info("Value {}", i));

        Thread.sleep(10000); //Run in Parallel Thread
    }

    @Test
    public void fluxSubscriberConsumerIntervalVirtualTime() {

        log.info("-------TEST CONSUMER INTERVAL-----");

        //Test with Days Margin

        StepVerifier.withVirtualTime(this::createFluxInterval)
            .expectSubscription()
            .expectNoEvent(Duration.ofDays(1)) //delay
            .thenAwait(Duration.ofDays(1))
            .expectNext(0L)
            .thenAwait(Duration.ofDays(1))
            .expectNext(1L)
            .thenCancel()
            .verify(); //Subscriber Cancelad
    }

    private Flux<Long> createFluxInterval() {
        //Publisher Mono: 1 or N
        return Flux.interval(Duration.ofDays(1))
            .log();
    }

    @Test
    public void connectableFlux() throws Exception {

        //Hot Publisher
        ConnectableFlux<Integer> connectableFlux = Flux.range(1, 10)
            .log()
            .delayElements(Duration.ofMillis(100))
            .publish();

        //The moment you connect, there will be a Publisher, without a Subscriber

        log.info("-------TEST CONNECTABLE FLUX-----");

        StepVerifier
            .create(connectableFlux)
            .then(connectableFlux::connect)
            .thenConsumeWhile(i -> i <= 5) //Consumer
            .expectNext(6, 7, 8, 9, 10) //onNext
            .expectComplete() //onComplete
            .verify();
    }

    @Test
    public void fluxAutoConnect() throws Exception {

        //Publisher Mono: 1 or N
        Flux<Integer> flux = Flux.range(1, 5)
            .log()
            .delayElements(Duration.ofMillis(100))
            .publish()
            .autoConnect(2);

        //Emits event after 2 Subscriber

        log.info("-------TEST AUTO CONNECT-----");

        StepVerifier
            .create(flux)
            .then(flux::subscribe) //2 Subscriber
            .expectNext(1, 2, 3, 4, 5) //onNext
            .expectComplete() //onComplete
            .verify();
    }
}
