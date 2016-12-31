package br.uece.goes.rts.solver.impl

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import org.jenetics.*
import org.jenetics.engine.Engine
import org.jenetics.engine.EvolutionResult
import org.jenetics.engine.codecs
import org.jenetics.util.ISeq
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.schedulers.Schedulers

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Created by thiago on 24/12/16.
 */
class DynGASolver implements Solver<TimeLine> {

    InstanceDao instanceDao
    final int POPULATION_SIZE = 100
    final double CROSSOVER_RATE = 0.8
    final double MUTATION_RATE = 0.05
    final double SURVIVOR_RATE = 0.1
    final int NUM_SURVIVOR = SURVIVOR_RATE * POPULATION_SIZE


    @Override
    Observable<TimeLine> solve() {
        LocalDateTime initialDate = LocalDateTime.now()
        Observable<Instance> instance = instanceDao.observeInstanceByName("initial")
        AtomicReference<Population<EnumGene<Integer>, Double>> initialPop = new AtomicReference<>(Population.empty())
        instance.switchMap {
            solving(initialDate, it, initialPop.get())
                    .doOnNext { el -> initialPop.set(el.second) }
        }.map { it.first }
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> solving(LocalDateTime period, Instance instance,
                                                                                Population<EnumGene<Integer>, Double> pop) {
        def engine = createEngine(period, instance)
        gaSolver(period, instance, engine, pop)
    }

    Function<int[], Double> generateFunc(LocalDateTime period, Instance instance) {
        List<Integer> indexes = instance.indexes()
        Function<int[], Double> func = { int[] val ->
            List<Integer> rep = Arrays.asList(Arrays.stream(val).map { it -> indexes[it] }.toArray())
            instance.toTimeLine(period, rep).maxHours
        }
        func
    }

    Engine<EnumGene<Integer>, Double> createEngine(LocalDateTime period, Instance instance) {
        int size = instance.indexes().size()
        Function<int[], Double> func = generateFunc(period, instance)
        Engine.builder(func, codecs.ofPermutation(size))
              .optimize(Optimize.MINIMUM)
              .maximalPhenotypeAge(10)
              .populationSize(POPULATION_SIZE)
              .alterers(
                new PartiallyMatchedCrossover<>(CROSSOVER_RATE),
                new SwapMutator<>(MUTATION_RATE))
              .build()
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> gaSolver(LocalDateTime period,
                                                                                 Instance instance,
                                                                                 Engine<EnumGene<Integer>, Double> engine,
                                                                                 Population<EnumGene<Integer>, Double> pop) {
        Observable.create { Subscriber<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> sub ->
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
                Comparator<Phenotype<EnumGene<Integer>, Double>> sort = { a, b -> a.fitness.compareTo(b.fitness) }
                Population<EnumGene<Integer>, Double> list = pop.stream().sorted(sort)
                                                                .map { adjustSolution(instance, period, it) }
                                                                .limit(NUM_SURVIVOR).collect(Population.toPopulation())

                engine.stream(list, 1).limit(pred).forEach { result ->
                    List<Integer> indexes = instance.indexes()
                    List<Integer> sol = result.bestPhenotype.genotype.chromosome.stream().map { indexes[it.allele] }
                                              .collect(Collectors.toList())
                    TimeLine tl = instance.toTimeLine(period, sol)
                    sub.onNext(new Tuple2(tl, result.population))
                }
            }
            sub.onCompleted()
        }.subscribeOn(Schedulers.newThread())
    }

    Phenotype<EnumGene<Integer>, Double> adjustSolution(Instance instance, LocalDateTime period,
                                                        Phenotype<EnumGene<Integer>, Double> phenotype) {
        List<Integer> indexes = instance.indexes()
        int instSize = instance.indexes().size()
        int chromosomeSize = phenotype.genotype.chromosome.length()
        if (instSize == chromosomeSize) phenotype
        else {
            Stream<Integer> rep = phenotype.genotype.chromosome.stream().map { it.allele }
            if (instSize < chromosomeSize) {
                Genotype<EnumGene<Integer>> gen = Genotype.of([PermutationChromosome
                                                                       .of(rep.filter { it < instSize }
                                                                           .collect(ISeq.toISeq()))])
                def p = Phenotype.of(gen, 1, { g ->
                    instance.toTimeLine(period, g.chromosome.collect { indexes[it.allele] }).maxHours
                })
                p
            } else {
                phenotype
            }
        }
//        rep.chromosome.
//                def list = []
//        IntegerChromosome.of(*list.collect { IntegerGene.of(it, 1, 10) })
    }
}
