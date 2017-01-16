package br.uece.goes.rts.solver.impl

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.dto.Stats
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import br.uece.goes.rts.solver.impl.op.OldTaskRepair
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
class FullDynGASolver implements Solver<TimeLine> {

    InstanceDao instanceDao
    final int POPULATION_SIZE = 500
    final double CROSSOVER_RATE = 0.8
    final double MUTATION_RATE = 0.05
    final double SURVIVOR_RATE = 0.9
    final int NUM_SURVIVOR = SURVIVOR_RATE * POPULATION_SIZE


    @Override
    Observable<TimeLine> solve() {
        LocalDateTime initialDate = LocalDateTime.now()
        Observable<Instance> instance = instanceDao.observeInstanceByName("initial", initialDate)
        AtomicReference<Population<EnumGene<Integer>, Double>> initialPop = new AtomicReference<>(Population.empty())
        instance.switchMap {
            solving(initialDate, it, initialPop.get())
                    .doOnNext { el -> initialPop.set(el.second) }
        }.map { it.first }//.scan { a, b -> b.maxHours < a.maxHours ? b : a }
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> solving(LocalDateTime period, Instance instance,
                                                                                Population<EnumGene<Integer>, Double> pop) {
        Codec<TimeLine, EnumGene<Integer>> codec = createCodec(period, instance)
        TimeLine tl = pop.isEmpty() ? TimeLine.EMPTY : codec.decode(pop.sort(false) { it.fitness }.first().genotype)

        def engine = createEngine(codec, tl, instance)
        gaSolver(codec, engine, instance, pop)
    }

    Codec<TimeLine, EnumGene<Integer>> createCodec(LocalDateTime period, Instance instance) {
        def indexes = instance.indexes()
        int size = indexes.size()
        Codec.of(Genotype.of([PermutationChromosome.ofInteger(size)])) { gt ->
            instance.toTimeLine(period, gt.chromosome.collect { indexes[it.allele] })
        }
    }

    Engine<EnumGene<Integer>, Double> createEngine(Codec<TimeLine, EnumGene<Integer>> codec, TimeLine tl, Instance instance) {
        Function<TimeLine, Double> func = { TimeLine val -> val.fitness }

        Engine.builder(func, codec)
              .minimizing()
              .maximalPhenotypeAge(300)
              .offspringSelector(new RouletteWheelSelector<>())
              .offspringFraction(0.8)
              .populationSize(POPULATION_SIZE)
              .alterers(
                new PartiallyMatchedCrossover<>(CROSSOVER_RATE),
                new SwapMutator<>(MUTATION_RATE),
                new OldTaskRepair(tl, codec, instance.indexes()))
              .build()
    }

    Observable<Tuple2<TimeLine, Population<EnumGene<Integer>, Double>>> gaSolver(Codec<TimeLine, EnumGene<Integer>> codec,
                                                                                 Engine<EnumGene<Integer>, Double> engine,
                                                                                 Instance instance,
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

                engine.stream(list).limit(pred).forEach { result ->
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

//    Genotype<EnumGene<Integer>> repair(Genotype<EnumGene<Integer>> gt) {
//        Map<Integer, Integer> fixed = [0: 15, 9: 25, 12: 6]
//        final Chromosome<EnumGene<Integer>> chromosome = gt.chromosome
//        final MSeq<EnumGene<Integer>> genes = chromosome.toSeq().copy()
//
//        final Map<Integer, Integer> alleles = (chromosome*.allele).indexed().collectEntries { k, v -> [v, k] }
//        fixed.forEach { k, v -> genes.swap(k, alleles[v]) }
//
//        Genotype.of([chromosome.newInstance(genes.toISeq())])
//    }

    List<Genotype<EnumGene<Integer>>> newGenotypeList(Population<EnumGene<Integer>, Double> pop, int instSize) {
        Comparator<Phenotype<EnumGene<Integer>, Double>> sort = { a, b -> a.fitness <=> b.fitness }
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
