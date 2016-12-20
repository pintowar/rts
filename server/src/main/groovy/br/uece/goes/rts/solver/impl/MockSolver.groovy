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
    Observable<TimeLine> solve() {
        LocalDateTime initialDate = LocalDateTime.now()
        def instance = instanceDao.observeInstanceByName("initial")
        def timer = Observable.interval(0, 1, TimeUnit.SECONDS)
        Observable.combineLatest(instance, timer) { i, t -> i }
                .map { it.toTimeLine(initialDate, representation(it)) }
                .scan { TimeLine a, TimeLine b -> [a, b].min { it.maxHours } }
    }

    List<Integer> representation(Instance instance) {
        ListUtils.shuffle((1..(instance.tasks.size())) + (1..<(instance.employees.size())).collect { -it })
    }
}
