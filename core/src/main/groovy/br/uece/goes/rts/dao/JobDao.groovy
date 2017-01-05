package br.uece.goes.rts.dao

/**
 * Created by thiago on 27/12/16.
 */
interface JobDao {

    boolean isExecuting()

    void startExecution()

    void stopExecution()
}
