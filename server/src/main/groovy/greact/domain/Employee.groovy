package greact.domain

import groovy.transform.Canonical

/**
 * Created by thiago on 03/12/16.
 */
@Canonical
class Employee {
    Integer id
    String content
    List<Integer> skills
}
