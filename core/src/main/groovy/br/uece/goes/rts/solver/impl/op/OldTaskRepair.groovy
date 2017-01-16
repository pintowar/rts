package br.uece.goes.rts.solver.impl.op

import br.uece.goes.rts.ListUtils
import br.uece.goes.rts.dto.TimeLine
import org.jenetics.*
import org.jenetics.engine.Codec
import org.jenetics.util.ISeq

import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

/**
 * Created by thiago on 08/01/17.
 */
class OldTaskRepair<G extends Gene<?, G>,
        C extends Comparable<? super C>> implements Alterer<G, C> {

    List<List<Integer>> fixed
    TimeLine oldTimeLine
    Codec<TimeLine, G> codec
    Set<Integer> breakPoints
    Set<Integer> fixSet
    ISeq<Integer> instanceSeq

    OldTaskRepair(TimeLine timeLine, Codec<TimeLine, G> codec, List<Integer> indexes) {
        this.oldTimeLine = timeLine
        this.codec = codec
        Map<Integer, Integer> idxs = indexes.indexed().collectEntries { k, v -> [v, k] }
        int groups = indexes.count { it < 0 }
        def fix = oldTimeLine.items.findResults { if (it.locked) [it.group, it.id] }.groupBy { it[0] }.values()
        this.fixed = fix.collect { xs -> (xs.collect { idxs[it[1]] }) }
        this.fixed = groups <= this.fixed.size() ? this.fixed : this.fixed + (this.fixed.size()..<groups).collect { [] }
        //this.fixed = [[15], [25], [6]].collect { xs -> xs.collect { idxs[it] } }

        this.fixSet = fixed.flatten() as Set
        this.breakPoints = indexes.indexed().findResults { k, v -> if (v < 0) k } as SortedSet
        this.instanceSeq = ISeq.of(0..<indexes.size())
    }

    @Override
    int alter(Population<G, C> population, long generation) {
        if (oldTimeLine.isEmpty()) return 0
        else {
            AtomicInteger alterations = new AtomicInteger(0)
            Phenotype<G, C> best = null
            IntStream.range(0, population.size()).forEach { idx ->
                final Phenotype<G, C> pt = population.get(idx)

                final Genotype<G> gt = pt.getGenotype()
                final Genotype<G> mgt = repair(gt, alterations)

                final Phenotype<G, C> mpt = pt.newInstance(mgt, generation)
                population.set(idx, mpt)
                best = best ? [best, mpt].min { it.fitness } : mpt
            }
            this.oldTimeLine = codec.decode(best.genotype)
            //assert
            return alterations.get()
        }
    }

    Genotype<G> repair(Genotype<G> gt, AtomicInteger alterations) {
        final Chromosome<G> chromosome = gt.chromosome
        List<Integer> rep = chromosome*.allele

        List<List<Integer>> aux = ListUtils.splitWhere(rep) { it in breakPoints }.indexed()
                                           .collect { k, v -> (fixed[k] + v.findAll { !(it in fixSet) }) }
        List<Integer> sol = breakPoints.indexed().inject(aux.first()) { acc, entry ->
            acc + [entry.value] + aux[entry.key + 1]
        }.unique()

//        ListUtils.splitWhere(sol) {
//            it in breakPoints
//        }.indexed().each { k, v -> assert fixed[k] == v.take(fixed[k].size()) }

        alterations.addAndGet(fixSet.size())
        ISeq<EnumGene<Integer>> seq = ISeq.of(sol.collect { EnumGene.<Integer> of(it, instanceSeq) })
        Genotype.of([new PermutationChromosome<>(seq)])
    }
}