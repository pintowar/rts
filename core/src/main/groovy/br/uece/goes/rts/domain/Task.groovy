package br.uece.goes.rts.domain

import groovy.transform.Canonical
import groovy.transform.Memoized
import groovy.transform.TypeChecked

/**
 * Created by thiago on 03/12/16.
 */
@Canonical
@TypeChecked
class Task {
    Integer id
    String content
    String criticity
    Integer preced
    List<Integer> skills

    @Memoized
    Integer getPriority() {
        switch (criticity) {
            case 'minor': return 2
            case 'major': return 3
            case 'critical': return 4
            default: return 1
        }
    }

    @Memoized
    String getColor() {
        switch (criticity) {
            case 'minor': return 'blue'
            case 'major': return 'yellow'
            case 'critical': return 'red'
            default: return 'green'
        }
    }
}
