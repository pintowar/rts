package br.uece.goes.rts.solver.impl

import br.uece.goes.rts.ListUtils
import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import rx.Observable

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Created by thiago on 03/12/16.
 */
class MockSolver implements Solver<TimeLine> {

    InstanceDao instanceDao

    @Override
    Observable<TimeLine> solve(String instanceName, double survivorRate) {
        LocalDateTime initialDate = LocalDateTime.now()
        def instance = instanceDao.observeInstanceByName(instanceName)
        def timer = Observable.interval(0, 1, TimeUnit.SECONDS)
        instance.switchMap { i ->
            timer.map { t -> i.toTimeLine(initialDate, representation(i)) }
                 .scan { TimeLine a, TimeLine b -> [a, b].min { it.maxHours } }
        }
    }

    List<Integer> representation(Instance instance) {
        ListUtils.shuffle(instance.indexes())
    }
}
