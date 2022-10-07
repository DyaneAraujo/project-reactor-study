package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    @Test
    public void monoSubscriber() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .log();

        //Subscribing
        mono.subscribe(); //All

        log.info("-------TEST-----");

        StepVerifier.create(mono)
            .expectNext(name) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoSubscriberConsumer() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .log();

        //Subscribing
        mono.subscribe(s -> log.info("Value {}", s)); //All Consumer

        log.info("-------TEST CONSUMER-----");

        StepVerifier.create(mono)
            .expectNext(name) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoSubscriberConsumerError() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .map(s -> {throw new RuntimeException("Testing mono with error");});

        //Subscribing
        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened")); //All Consumer
        mono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace); //All Consumer

        log.info("-------TEST CONSUMER ERROR-----");

        StepVerifier.create(mono)
            .expectError(RuntimeException.class) //onError
            .verify(); //Subscriber Cancelad
    }

    @Test
    public void monoSubscriberConsumerComplete() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        //Subscribing
        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!")); //All Consumer

        log.info("-------TEST CONSUMER COMPLETE-----");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase()) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoSubscriberConsumerSubscription() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        //Subscribing
        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!"),
            Subscription::cancel); //All Consumer, Subscriber Canceled
        //Clear Resources

        log.info("-------TEST CONSUMER SUBSCRIPTION-----");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase()) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoSubscriberConsumerSubscriptionBackpressure() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        //Subscribing
        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!"),
            subscription -> subscription.request(5)); //Consumer with Backpressure

        log.info("-------TEST CONSUMER SUBSCRIPTION BACKPRESSURE-----");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase()) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoDoOnMethods() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<Object> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase)
            .doOnSubscribe(subscription -> log.info("Subscribed"))
            .doOnRequest(longNumber -> log.info("Request Received, starting doing something..."))
            .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
            .flatMap(s -> Mono.empty())
            .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s)) //will not be executed
            .doOnSuccess(s -> log.info("doOnSuccess executed"));

        //Subscribing
        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!")); //All Consumer

        log.info("-------COMPLETE ON METHODS-----");
    }

    @Test
    public void monoDoOnError() {

        //Publisher Mono: 1 or void
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Illegal argument exception"))
            .doOnError(e -> log.error("Error message: {}", e.getMessage()))
            .doOnNext(s -> log.info("Executing this doOnNext")) //will not be executed
            .log();

        log.info("-------TEST CONSUMER ON METHODS ERROR-----");

        StepVerifier.create(mono)
            .expectError(IllegalArgumentException.class) //onError
            .verify(); //Subscriber Cancelad
    }

    @Test
    public void monoDoOnErrorResume() {

        String name = "Dyane Araújo";

        //Publisher Mono: 1 or void
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Illegal argument exception"))
            .doOnError(e -> log.error("Error message: {}", e.getMessage()))
            .onErrorResume(s -> {
                log.info("Inside On Error Resume"); //Function
                return Mono.just(name);
            })
            .log();

        log.info("-------TEST CONSUMER ON METHODS ERROR RESUME-----");

        StepVerifier.create(mono)
            .expectNext(name) //onNext
            .verifyComplete(); //onComplete
    }

    @Test
    public void monoDoOnErrorReturn() {

        //Publisher Mono: 1 or void
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Illegal argument exception"))
            .doOnError(e -> log.error("Error message: {}", e.getMessage()))
            .onErrorReturn("Empty")
            .log();

        log.info("-------TEST CONSUMER ON METHODS ERROR RETURN-----");

        StepVerifier.create(mono)
            .expectNext("Empty") //onNext
            .verifyComplete(); //onComplete
    }

}
