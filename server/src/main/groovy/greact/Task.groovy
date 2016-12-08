package greact

import groovy.transform.Immutable

/**
 * Created by thiago on 03/12/16.
 */
@Immutable
class Task {
    Integer id
    String content
    String criticity
    Integer preced
    List<Integer> skills
}
