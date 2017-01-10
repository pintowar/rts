package br.uece.goes.rts.solver.impl.op

import br.uece.goes.rts.dto.TimeLine
import org.jenetics.*
import org.jenetics.engine.Codec
import org.jenetics.util.MSeq

import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

/**
 * Created by thiago on 08/01/17.
 */
class OldTaskRepair<G extends Gene<?, G>,
        C extends Comparable<? super C>> implements Alterer<G, C> {

    Map<Integer, Integer> fixed
    TimeLine oldTimeLine
    Codec<TimeLine, G> codec

    OldTaskRepair(TimeLine timeLine, Codec<TimeLine, G> codec) {
        this.oldTimeLine = timeLine
        this.codec = codec
        this.fixed = [0: 15, 9: 25, 12: 6]//oldTimeLine.items.findResults { if (it.locked) [it.position, it.id] }.collectEntries()
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
                best = best ? [best, pt].min { it.fitness } : pt
            }
            this.oldTimeLine = codec.decode(best.genotype)
//            this.fixed = oldTimeLine.items.findResults { if (it.locked) [it.position, it.id] }.collectEntries()
            return alterations.get()
        }
    }

    Genotype<G> repair(Genotype<G> gt, AtomicInteger alterations) {
        final Chromosome<G> chromosome = gt.chromosome
        final MSeq<G> genes = chromosome.toSeq().copy()

        final Map<Integer, Integer> alleles = (chromosome*.allele).indexed().collectEntries { k, v -> [v, k] }
        fixed.forEach { k, v ->
            genes.swap(k, alleles[v])
            alterations.incrementAndGet()
        }

        Genotype.of([chromosome.newInstance(genes.toISeq())])
    }
}