package br.uece.goes.rts.solver.impl

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.dto.Stats
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import br.uece.goes.rts.solver.impl.op.MixMutator
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.jenetics.*
import org.jenetics.engine.Codec
import org.jenetics.engine.Engine
import org.jenetics.engine.EvolutionResult
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
    final double SURVIVOR_RATE = 0.4
    final int NUM_SURVIVOR = SURVIVOR_RATE * POPULATION_SIZE


    @Override
    Observable<TimeLine> solve() {
        LocalDateTime initialDate = LocalDateTime.now()
        Observable<Instance> instance = instanceDao.observeInstanceByName("i_25_25/i_25_25")
        AtomicReference<Population<EnumGene<Integer>, Double>> initialPop = new AtomicReference<>(Population.empty())
        instance.switchMap {
            solving(initialDate, it, initialPop.get())
                    .doOnNext { el -> initialPop.set(el.second) }
        }.map { it.first }//.scan { a, b -> b.maxHours < a.maxHours ? b : a }
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> solving(LocalDateTime period, Instance instance,
                                                                                Population<EnumGene<Integer>, Double> pop) {
        Codec<TimeLine, EnumGene<Integer>> codec = createCodec(period, instance)
        int size = instance.indexes().size()
        def engine = createEngine(codec, size)
        gaSolver(codec, instance, engine, pop)
    }

    Codec<TimeLine, EnumGene<Integer>> createCodec(LocalDateTime period, Instance instance) {
        def indexes = instance.indexes()
        int size = indexes.size()
        Codec.of(Genotype.of([PermutationChromosome.ofInteger(size)])) { gt ->
            instance.toTimeLine(period, gt.chromosome.collect { indexes[it.allele] })
        }
    }


    Engine<EnumGene<Integer>, Double> createEngine(Codec<TimeLine, EnumGene<Integer>> codec, int size) {
        Function<TimeLine, Double> func = { TimeLine val -> val.fitness }

        Engine.builder(func, codec)
              .minimizing()
              .maximalPhenotypeAge(100)
              .offspringSelector(new RouletteWheelSelector<>())
              .offspringFraction(0.8)
              .populationSize(POPULATION_SIZE)
              .alterers(
                new PartiallyMatchedCrossover<>(CROSSOVER_RATE),
                new MixMutator<>(MUTATION_RATE))
              .build()
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> gaSolver(Codec<TimeLine, EnumGene<Integer>> codec,
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
                List<Genotype<EnumGene<Integer>>> list = newGenotypeList(pop, instance.indexes().size())

                engine.stream(list).limit(pred).parallel().forEach { result ->
                    TimeLine tl = codec.decode(result.bestPhenotype.genotype)
                    def aux = new DescriptiveStatistics((result.population*.fitness) as double[])
                    def stats = new Stats(aux.min, aux.max, aux.mean, aux.getPercentile(50),
                            aux.getPercentile(25), aux.getPercentile(75), aux.standardDeviation)
                    sub.onNext(new Tuple2(tl.addStats(stats), result.population))
                }
            }
            sub.onCompleted()
        }.subscribeOn(Schedulers.newThread())
    }

    List<Genotype<EnumGene<Integer>>> newGenotypeList(Population<EnumGene<Integer>, Double> pop, int instSize) {
        Comparator<Phenotype<EnumGene<Integer>, Double>> sort = { a, b -> a.fitness.compareTo(b.fitness) }
        pop.stream().sorted(sort)
           .map { adjustSolution(instSize, it.genotype) }
           .limit(NUM_SURVIVOR).collect(Collectors.toList())
    }

    Genotype<EnumGene<Integer>> adjustSolution(int instSize, Genotype<EnumGene<Integer>> genotype) {
        int chromosomeSize = genotype.chromosome.length()
        if (instSize == chromosomeSize) genotype
        else {
            Stream<Integer> rep = genotype.chromosome.stream().map { it.allele }
            Stream<Integer> nrep = instSize < chromosomeSize ?
                    rep.filter { it < instSize } :
                    Stream.concat(rep, (chromosomeSize..<instSize).stream())
            ISeq<Integer> seq = ISeq.of(0..<instSize)
            ISeq<EnumGene<Integer>> nseq = nrep.map { EnumGene.<Integer> of(it, seq) }
                                               .collect(ISeq.toISeq())
            Genotype.of([new PermutationChromosome<>(nseq)])
        }
    }
}
