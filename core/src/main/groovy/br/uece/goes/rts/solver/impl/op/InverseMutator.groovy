package br.uece.goes.rts.solver.impl.op

import br.uece.goes.rts.ListUtils
import org.jenetics.Gene
import org.jenetics.Mutator
import org.jenetics.internal.util.Equality
import org.jenetics.internal.util.Hash
import org.jenetics.util.MSeq
import org.jenetics.util.RandomRegistry

import static java.lang.String.format

/**
 * Created by thiago on 07/03/17.
 */
class InverseMutator<
        G extends Gene<?, G>,
        C extends Comparable<? super C>> extends Mutator<G, C> {

    /**
     * Constructs an alterer with a given recombination probability.
     *
     * @param probability the crossover probability.
     * @throws IllegalArgumentException if the {@code probability} is not in the
     *          valid range of {@code [0 , 1]}.
     */
    InverseMutator(final double probability) {
        super(probability);
    }

    /**
     * Default constructor, with default mutation probability
     * ({@link org.jenetics.AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
     */
    InverseMutator() {
        this(0.2);
    }

    /**
     * Swaps the genes in the given array, with the mutation probability of this
     * mutation.
     */
    @Override
    protected int mutate(final MSeq<G> genes, final double p) {
        final Random random = RandomRegistry.getRandom();
        if (genes.length() > 2 && random.nextDouble() <= _probability) {
            List<Integer> idx = ListUtils.shuffle((0..<genes.length()).step(1), random).take(2).sort()
            List<Integer> range = idx.first()..idx.last() as List<Integer>
            List<Integer> inv = range.reverse()
            int hlf = (int) (range.size() / 2)
            (0..<hlf).each { int it -> genes.swap(range[it], inv[it]) }
            hlf
        } else 0
    }

    @Override
    public int hashCode() {
        return Hash.of(getClass()).and(super.hashCode()).value()
    }

    @Override
    public boolean equals(final Object obj) {
        return Equality.of(this, obj).test(super.&equals)
    }

    @Override
    public String toString() {
        return format("%s[p=%f]", getClass().getSimpleName(), _probability);
    }

}
