package greact.solver

import greact.ListUtils
import greact.Solver
import greact.dao.InstanceDao
import greact.domain.Instance
import greact.solution.TimeLine
import rx.Observable

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Created by thiago on 03/12/16.
 */
class MockSolver implements Solver<TimeLine> {

    private LocalDateTime initialDate = LocalDateTime.now()

    InstanceDao instanceDao

    @Override
    Observable<TimeLine> solve() {
        def instance = instanceDao.observeInstanceByName("initial")
        def timer = Observable.interval(0, 2, TimeUnit.SECONDS)
        Observable.combineLatest(instance, timer) { i, t -> i }
                .map { it.toTimeLine(initialDate, representation(it)) }
                .scan { TimeLine a, TimeLine b -> [a, b].min { it.maxHours }
        }
    }

    List<Integer> representation(Instance instance) {
        ListUtils.shuffle((1..(instance.tasks.size())) + (1..<(instance.employees.size())).collect { -it })
    }
}
