package br.uece.goes.rts

import spock.lang.Specification
import spock.util.mop.Use

/**
 * Created by thiago on 08/01/17.
 */
@Use(ListUtils)
class ListUtilsTest extends Specification {

    def "GroupWhere"() {
        expect:
        list.groupWhere(0) { it < 0 } == result

        where:
        list                       || result
        []                         || [:]
        [1, 2, 3]                  || [0: [1, 2, 3]]
        [1, 2, -1, 3, 4, -2, 5, 6] || [0: [1, 2], (-1): [3, 4], (-2): [5, 6]]
        [1, 2, -2, 3, 4, -1, 5, 6] || [0: [1, 2], (-2): [3, 4], (-1): [5, 6]]
    }

    def "SplitWhere"() {
        expect:
        list.splitWhere { it < 0 } == result

        where:
        list                       || result
        []                         || [[]]
        [1, 2, 3]                  || [[1, 2, 3]]
        [1, 2, -1, 3, 4, -2, 5, 6] || [[1, 2], [3, 4], [5, 6]]
    }

    def "Intercale"() {
        expect:
        list.intercale(data) == result

        where:
        list         | data     || result
        []           | []       || []
        [1, 2, 3]    | [-1]     || [1, -1, 2, 3]
        [1, 2, 3, 4] | [-1]     || [1, 2, -1, 3, 4]
        1..10        | [-1, -2] || [1, 2, 3, -1, 4, 5, 6, -2, 7, 8, 9, 10]
        []           | [-1, -2] || []
        [1, 2]       | [-1, -2] || [1, -1, 2]
    }
}
