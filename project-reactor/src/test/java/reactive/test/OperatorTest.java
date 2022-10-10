package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class OperatorTest {

    @Test
    public void subscribeOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 2- Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //subscribeOn with parallel thread will apply all flux

        log.info("-------TEST SUBSCRIBE ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    public void publishOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //publishOn with parallel thread will apply from a point in the flux

        log.info("-------TEST PUBLISH ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    public void multipleSubscribeOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .subscribeOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .subscribeOn(Schedulers.single())
            .map(i -> {
                log.info("Map 2- Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //subscribeOn with parallel thread
        //only the first will be applied to the flux

        log.info("-------TEST MULTIPLE SUBSCRIBE ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    public void multiplePublishOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .publishOn(Schedulers.single())
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //publishOn with parallel thread
        //both will be applied to the flux

        log.info("-------TEST MULTIPLE PUBLISH ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    public void publishAndSubscribeOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .publishOn(Schedulers.single())
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //publishOn and subscribeOn with parallel thread
        //only the first will be applied to the flux

        log.info("-------TEST PUBLISH ON AND SUBSCRIBE ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    public void subscribeAndPublishOnSimple() {

        Flux<Integer> flux = Flux.range(1, 4)
            .subscribeOn(Schedulers.single())
            .map(i -> {
                log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                return i;
            });

        //subscribeOn and publishOn with parallel thread
        //both will be applied to the flux

        log.info("-------TEST SUBSCRIBE ON AND PUBLISH ON-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }
}
