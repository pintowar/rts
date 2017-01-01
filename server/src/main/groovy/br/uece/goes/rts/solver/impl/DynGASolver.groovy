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
    final int POPULATION_SIZE = 500
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

    Engine<EnumGene<Integer>, Double> createEngine(LocalDateTime period, Instance instance) {
        List<Integer> indexes = instance.indexes()
        int size = indexes.size()
        Function<int[], Double> func = { int[] val ->
            List<Integer> rep = Arrays.asList(Arrays.stream(val).map { it -> indexes[it] }.toArray())
            instance.toTimeLine(period, rep).maxHours
        }
        Engine.builder(func, codecs.ofPermutation(size))
              .optimize(Optimize.MINIMUM)
              .maximalPhenotypeAge(10)
              .genotypeValidator { (it.chromosome*.allele).unique(false).size() == size }
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
            final Predicate<EvolutionResult<EnumGene<Integer>, Double>> pred = { running.get() }
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
                List<Genotype<EnumGene<Integer>>> list = newGenotypeList(pop, instance)

                engine.stream(list).limit(pred).forEach { result ->
                    List<Integer> indexes = instance.indexes()
                    List<Integer> sol = result.bestPhenotype.genotype.chromosome.stream().map { indexes[it.allele] }
                                              .collect(Collectors.toList())

//                    def sols = result.population.collect { (it.genotype.chromosome*.allele) as int[] } as Set
//                    print "num sols: ${sols.size()}, stats: "
//                    def stats = new DescriptiveStatistics((result.population*.fitness) as double[])
//                    println([stats.max, stats.min, stats.mean, stats.standardDeviation].join(', '))
                    TimeLine tl = instance.toTimeLine(period, sol)
                    sub.onNext(new Tuple2(tl, result.population))
                }
            }
            sub.onCompleted()
        }.subscribeOn(Schedulers.newThread())
    }

    List<Genotype<EnumGene<Integer>>> newGenotypeList(Population<EnumGene<Integer>, Double> pop, Instance instance) {
        Comparator<Phenotype<EnumGene<Integer>, Double>> sort = { a, b -> a.fitness.compareTo(b.fitness) }
        pop.stream().sorted(sort)
           .map { adjustSolution(instance, it.genotype) }
           .limit(NUM_SURVIVOR).collect(Collectors.toList())
    }

    Genotype<EnumGene<Integer>> adjustSolution(Instance instance, Genotype<EnumGene<Integer>> genotype) {
        int instSize = instance.indexes().size()
        int chromosomeSize = genotype.chromosome.length()
        if (instSize == chromosomeSize) genotype
        else {
            Stream<Integer> rep = genotype.chromosome.stream().map { it.allele }
            Stream<Integer> nrep = instSize < chromosomeSize ?
                    rep.filter { it < instSize } :
                    Stream.concat(rep, (chromosomeSize..<instSize).stream())
            ISeq<Integer> seq = nrep.collect(ISeq.toISeq())
            ISeq<EnumGene<Integer>> nseq = (0..<seq.size()).stream().map { int it -> EnumGene.<Integer> of(it, seq) }
                                                           .collect(ISeq.toISeq())

            Genotype.of([new PermutationChromosome<>(nseq)])
        }
    }
}
