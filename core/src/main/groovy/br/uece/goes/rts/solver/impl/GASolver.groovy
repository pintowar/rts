package br.uece.goes.rts.solver.impl

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import org.jenetics.EnumGene
import org.jenetics.Optimize
import org.jenetics.PartiallyMatchedCrossover
import org.jenetics.SwapMutator
import org.jenetics.engine.Engine
import org.jenetics.engine.EvolutionResult
import org.jenetics.engine.codecs
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * Created by thiago on 24/12/16.
 */
class GASolver implements Solver<TimeLine> {

    InstanceDao instanceDao

    @Override
    Observable<TimeLine> solve(String instanceName, double survivorRate) {
        LocalDateTime initialDate = LocalDateTime.now()
        def instance = instanceDao.observeInstanceByName(instanceName)
        instance.switchMap { solving(initialDate, it) }
    }

    Observable<TimeLine> solving(LocalDateTime period, Instance instance) {
        def engine = createEngine(period, instance)
        gaSolver(period, instance, engine)
    }

    Engine<EnumGene<Integer>, Double> createEngine(LocalDateTime period, Instance instance) {
        List<Integer> indexes = instance.indexes()
        int size = indexes.size()
        Function<int[], Double> func = { val ->
            List<Integer> rep = Arrays.asList(Arrays.stream(val).map { it -> indexes[it] }.toArray())
            instance.toTimeLine(period, rep).maxHours
        }
        Engine.builder(func, codecs.ofPermutation(size))
                .optimize(Optimize.MINIMUM)
                .maximalPhenotypeAge(10)
                .populationSize(100)
                .alterers(
                new PartiallyMatchedCrossover<>(0.8),
                new SwapMutator<>(0.1))
                //.survivorsSelector()
                .build()
    }

    Observable<TimeLine> gaSolver(LocalDateTime period, Instance instance, Engine<EnumGene<Integer>, Double> engine) {
        Observable.create { sub ->
            AtomicBoolean running = new AtomicBoolean(true)
            Predicate<EvolutionResult<EnumGene<Integer>, Double>> pred = { running.get() }
            sub.add(new Subscription() {
                @Override
                void unsubscribe() {
                    running.set(false)
                }

                @Override
                boolean isUnsubscribed() {
                    return !running.get()
                }
            })

            if (!instance.isEmpty()) {
                engine.stream().limit(pred).forEach { pop ->
                    List<Integer> indexes = instance.indexes()
                    List<Integer> sol = pop.bestPhenotype.genotype.chromosome.stream().map { indexes[it.allele] }
                            .collect(Collectors.toList())
                    TimeLine tl = instance.toTimeLine(period, sol)
                    sub.onNext(tl)
                }
            }
            sub.onCompleted()
        }.subscribeOn(Schedulers.newThread())
    }
}
