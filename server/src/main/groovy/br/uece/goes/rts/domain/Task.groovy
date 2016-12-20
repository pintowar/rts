package br.uece.goes.rts.domain

import groovy.transform.Canonical

/**
 * Created by thiago on 03/12/16.
 */
@Canonical
class Task {
    Integer id
    String content
    String criticity
    Integer preced
    List<Integer> skills
}
