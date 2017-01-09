package br.uece.goes.rts

import groovy.transform.TypeChecked

/**
 * Created by thiago on 09/12/16.
 */
@TypeChecked
class ListUtils {

    static <T> List<T> shuffle(List<T> list) {
        List<T> aux = list.clone() as List<T>
        Collections.shuffle(aux)
        aux
    }

    static <T> List<List<T>> splitWhere(List<T> list, Closure<Boolean> cond) {
        ([-1] + list.indexed().findResults { k, v -> if (cond(v)) k } + [list.size()])
                .collate(2, 1, false)
                .collect { list[(it[0] + 1)..<it[1]] }
    }

    static <T> List<T> intercale(List<T> a, List<T> b) {
        if (!a.isEmpty()) {
            def sb = a.size() > b.size() ? b : b.take(a.size() - 1)
            int miniSize = (int) Math.floor(a.size() / (sb.size() + 1))
            List<List<T>> aux = a.collate(miniSize, miniSize)
            (0..<aux.size()).collectMany { int it -> aux[it] + (sb[it] ? [sb[it]] : []) }
        } else []
    }
}
