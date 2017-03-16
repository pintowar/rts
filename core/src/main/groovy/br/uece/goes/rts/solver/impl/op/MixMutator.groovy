package br.uece.goes.rts.solver.impl.op

import org.jenetics.Gene
import org.jenetics.Mutator
import org.jenetics.SwapMutator
import org.jenetics.internal.util.Equality
import org.jenetics.internal.util.Hash
import org.jenetics.util.MSeq
import org.jenetics.util.RandomRegistry

import static java.lang.String.format

/**
 * Created by thiago on 07/03/17.
 */
class MixMutator<
        G extends Gene<?, G>,
        C extends Comparable<? super C>> extends Mutator<G, C> {

    private InverseMutator inverseMutator;
    private SwapMutator swapMutatorMutator;
    /**
     * Constructs an alterer with a given recombination probability.
     *
     * @param probability the crossover probability.
     * @throws IllegalArgumentException if the {@code probability} is not in the
     *          valid range of {@code [0 , 1]}.
     */
    MixMutator(final double probability) {
        super(probability);
        inverseMutator = new InverseMutator(probability)
        swapMutatorMutator = new SwapMutator(probability)
    }

    /**
     * Default constructor, with default mutation probability
     * ({@link org.jenetics.AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
     */
    MixMutator() {
        this(0.2);
    }

    /**
     * Swaps the genes in the given array, with the mutation probability of this
     * mutation.
     */
    @Override
    protected int mutate(final MSeq<G> genes, final double p) {
        final Random random = RandomRegistry.getRandom()
        if (random.nextDouble() <= 0.3) {
            inverseMutator.mutate(genes, p)
        } else {
            swapMutatorMutator.mutate(genes, p)
        }
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
