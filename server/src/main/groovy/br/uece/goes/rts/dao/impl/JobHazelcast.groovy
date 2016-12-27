package br.uece.goes.rts.dao.impl

import br.uece.goes.rts.dao.JobDao
import hazelgrails.HazelService

import javax.annotation.PostConstruct

/**
 * Created by thiago on 27/12/16.
 */
class JobHazelcast implements JobDao {

    HazelService hazelService

    private Map<String, Boolean> jobs

    @PostConstruct
    void init() {
        jobs = hazelService.map("jobs")
    }

    @Override
    boolean isExecuting() {
        jobs.getOrDefault('execute', false)
    }

    @Override
    void startExecution() {
        jobs.put('execute', true)
    }

    @Override
    void stopExecution() {
        jobs.put('execute', false)
    }
}
