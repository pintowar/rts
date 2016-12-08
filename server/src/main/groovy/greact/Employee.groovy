package greact

import groovy.transform.Immutable

/**
 * Created by thiago on 03/12/16.
 */
@Immutable
class Employee {
    Integer id
    String content
    List<Integer> skills
}
