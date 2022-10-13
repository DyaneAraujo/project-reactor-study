package reactive.test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    @Test
    public void subscribeOnIO() throws Exception {

        //external API call or file

        Mono<List<String>> list = Mono.fromCallable(() -> Files.readAllLines(Path.of("text-file")))
            .log()
            .subscribeOn(Schedulers.boundedElastic());

        //background
        //list.subscribe(s -> log.info("{}",s));

        log.info("-------TEST SUBSCRIBE ON IO-----");

        StepVerifier.create(list)
            .expectSubscription()
            .thenConsumeWhile(l -> { //JUnit
                Assertions.assertFalse(l.isEmpty());
                log.info("Size {}", l.size());
                return true;
            })
            .verifyComplete();
    }

    @Test
    public void switchIfEmptyOperator() {

        Flux<Object> flux = emptyFlux()
            .switchIfEmpty(Flux.just("not empty anymore"))
            .log();

        //if the flux is empty, return this other flux

        log.info("-------TEST SWITCH IF EMPTY-----");

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("not empty anymore")
            .expectComplete()
            .verify();
    }

    private Flux<Object> emptyFlux() {
        return Flux.empty();
    }

    @Test
    public void deferOperator() throws Exception {

        Mono<Long> just = Mono.just(System.currentTimeMillis());

        //run currentTimeMillis only once

        Mono<Long> defer = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        //run currentTimeMillis every time there is a subscriber

        defer.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        defer.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        defer.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        defer.subscribe(l -> log.info("time {}", l));

        log.info("-------TEST DEFER-----");

        AtomicLong atomicLong = new AtomicLong();
        defer.subscribe(atomicLong::set);
        Assertions.assertTrue(atomicLong.get() > 0);
    }

    @Test
    public void concatOperator() {

        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> concatFlux = Flux.concat(flux1, flux2).log();

        //concatenate the two flux, only when there is subscriber in the first flux

        log.info("-------TEST CONCAT-----");

        StepVerifier
            .create(concatFlux)
            .expectSubscription()
            .expectNext("a", "b", "c", "d")
            .expectComplete()
            .verify();
    }

    @Test
    public void concatWithOperator() {

        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> concatFlux = flux1.concatWith(flux2).log();

        //combines a flux into another, only when there is subscriber in the first flux

        log.info("-------TEST CONCAT WITH-----");

        StepVerifier
            .create(concatFlux)
            .expectSubscription()
            .expectNext("a", "b", "c", "d")
            .expectComplete()
            .verify();
    }

    @Test
    public void combineLatestOperator() {

        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> combineLatest = Flux.combineLatest(flux1, flux2,
                (s1, s2) -> s1.toUpperCase() + s2.toUpperCase())
            .log();

        //combines the last values, but it depends on the execution time of each flux

        log.info("-------TEST COMBINE LATEST-----");

        StepVerifier
            .create(combineLatest)
            .expectSubscription()
            .expectNext("BC", "BD")
            .expectComplete()
            .verify();
    }

    @Test
    public void mergeOperator() throws Exception {

        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.merge(flux1, flux2)
            .delayElements(Duration.ofMillis(200))
            .log();

        //mergeFlux.subscribe(log::info);
        //Thread.sleep(1000);

        //both fluxes are merged, but 2 does not wait for 1 if executed

        log.info("-------TEST MERGE-----");

        StepVerifier
            .create(mergeFlux)
            .expectSubscription()
            .expectNext("c", "d", "a", "b")
            .expectComplete()
            .verify();
    }

    @Test
    public void mergeWithOperator() throws Exception {

        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = flux1.mergeWith(flux2)
            .delayElements(Duration.ofMillis(200))
            .log();

        //both fluxes are merged, but 2 does not wait for 1 if executed
        //the difference it merge with only one flux

        log.info("-------TEST MERGE WITH-----");

        StepVerifier
            .create(mergeFlux)
            .expectSubscription()
            .expectNext("c", "d", "a", "b")
            .expectComplete()
            .verify();
    }

    @Test
    public void mergeSequentialOperator() throws Exception {

        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.mergeSequential(flux1, flux2, flux1)
            .delayElements(Duration.ofMillis(200))
            .log();

        //Ensures sequential merge in parallel thread

        log.info("-------TEST MERGE SEQUENTIAL-----");

        StepVerifier
            .create(mergeFlux)
            .expectSubscription()
            .expectNext("a", "b", "c", "d", "a", "b")
            .expectComplete()
            .verify();
    }

    @Test
    public void mergeDelayErrorOperator() throws Exception {
        Flux<String> flux1 = Flux.just("a", "b")
            .map(s -> {
                if (s.equals("b")) {
                    throw new IllegalArgumentException();
                }
                return s;
            }).doOnError(t -> log.error("We could do something with this"));
        //handling error

        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.mergeDelayError(2, flux1, flux2, flux1)
            .log();

        //concatDelayError or mergeDelayError
        //handling error
        //concatenating flux with error

        log.info("-------TEST MERGE DELAY ERROR-----");

        mergeFlux.subscribe(log::info);

        StepVerifier
            .create(mergeFlux)
            .expectSubscription()
            .expectNext("a", "c", "d", "a")
            .expectError()
            .verify();
    }

    @Test
    public void flatMapOperator() throws Exception {

        Flux<String> flux = Flux.just("a", "b");

        Flux<String> flatFlux = flux.map(String::toUpperCase)
            .flatMap(this::findByName)
            .log();

        //asynchronous - for each flux will call the BD function
        //Map = Flux<Flux<String>>
        //flatMap = Flux<String>
        //does not preserve the original order of elements

        log.info("-------TEST FLATMAP-----");

        StepVerifier
            .create(flatFlux)
            .expectSubscription()
            .expectNext("nameB1", "nameB2", "nameA1", "nameA2")
            .verifyComplete();
    }

    @Test
    public void flatMapSequentialOperator() throws Exception {
        Flux<String> flux = Flux.just("a", "b");

        Flux<String> flatFlux = flux.map(String::toUpperCase)
            .flatMapSequential(this::findByName)
            .log();

        //asynchronous - for each flux will call the BD function
        //Map = Flux<Flux<String>>
        //flatMap = Flux<String>
        //preserves the original order of elements

        log.info("-------TEST FLATMAP SEQUENTIAL-----");

        StepVerifier
            .create(flatFlux)
            .expectSubscription()
            .expectNext("nameA1", "nameA2", "nameB1", "nameB2")
            .verifyComplete();
    }

    //simulating database
    private Flux<String> findByName(String name) {
        return name.equals("A") ? Flux.just("nameA1", "nameA2").delayElements(Duration.ofMillis(100)) : Flux.just("nameB1", "nameB2");
    }

    @Test
    public void zipOperator() {

        Flux<String> titleFlux = Flux.just("Grand Blue", "Baki");
        Flux<String> studioFlux = Flux.just("Zero-G", "TMS Entertainment");
        Flux<Integer> episodesFlux = Flux.just(12, 24);

        Flux<Anime> animeFlux = Flux.zip(titleFlux, studioFlux, episodesFlux)
            .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), tuple.getT2(), tuple.getT3())));

        //creating objects with zip
        //Tuple 3, Flux 3 many different

        log.info("-------TEST ZIP-----");

        StepVerifier
            .create(animeFlux)
            .expectSubscription()
            .expectNext(
                new Anime("Grand Blue", "Zero-G", 12),
                new Anime("Baki", "TMS Entertainment", 24))
            .verifyComplete();
    }

    @Test
    public void zipWithOperator() {

        Flux<String> titleFlux = Flux.just("Grand Blue", "Baki");
        Flux<Integer> episodesFlux = Flux.just(12, 24);

        Flux<Anime> animeFlux = titleFlux.zipWith(episodesFlux)
            .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), null, tuple.getT2())));

        //creating objects with zip
        //Tuple 2, Flux 2 many different
        //accepts only 2 flux

        log.info("-------TEST ZIP WITH-----");

        StepVerifier
            .create(animeFlux)
            .expectSubscription()
            .expectNext(
                new Anime("Grand Blue", null, 12),
                new Anime("Baki", null, 24))
            .verifyComplete();
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    class Anime {

        private String title;
        private String studio;
        private int episodes;

    }

}
