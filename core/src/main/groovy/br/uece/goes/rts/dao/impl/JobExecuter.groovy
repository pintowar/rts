package br.uece.goes.rts.dao.impl

import br.uece.goes.rts.dao.JobDao

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by thiago on 27/12/16.
 */
class JobExecuter implements JobDao {

    private AtomicBoolean executing = new AtomicBoolean(false)

    @Override
    boolean isExecuting() {
        executing.get()
    }

    @Override
    void startExecution() {
        executing.set(true)
    }

    @Override
    void stopExecution() {
        executing.set(false)
    }
}
