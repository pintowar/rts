package br.uece.goes.rts.solver.impl.op

import org.jenetics.*
import org.jenetics.util.MSeq

import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

/**
 * Created by thiago on 08/01/17.
 */
class OldTaskRepair<G extends Gene<?, G>,
        C extends Comparable<? super C>> implements Alterer<G, C> {

    Map<Integer, Integer> fixed

    OldTaskRepair(Map<Integer, Integer> fixed) {
        this.fixed = fixed
    }

    @Override
    int alter(Population<G, C> population, long generation) {
        if (fixed.isEmpty()) return 0
        else {
            AtomicInteger alterations = new AtomicInteger(0)
            IntStream.range(0, population.size()).forEach { idx ->
                final Phenotype<G, C> pt = population.get(idx)

                final Genotype<G> gt = pt.getGenotype()
                final Genotype<G> mgt = repair(gt, alterations)

                final Phenotype<G, C> mpt = pt.newInstance(mgt, generation)
                population.set(idx, mpt)
            }
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