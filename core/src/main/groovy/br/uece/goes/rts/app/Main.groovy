package br.uece.goes.rts.app

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import br.uece.goes.rts.solver.impl.DynGASolver
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import java.util.concurrent.TimeUnit

/**
 * Created by thiago on 04/01/17.
 */
@Slf4j
class Main {

    InstanceDao createInstanceDao() {
        new InstanceParser()
    }

    Solver<TimeLine> createSolver() {
        new DynGASolver(instanceDao: createInstanceDao())
    }

    void multiexec(List<Integer> percents) {
        def solver = createSolver()
        def format = '%02d'
        def execs = 30
        def params = [['i_25_10'], ['employee', 'task'], percents].combinations()
        params.each { String i, String var, double sur ->
            String instanceid = [i, var, (int) sur].join('_')
            MDC.put('instanceid', instanceid)
            log.info(['id', 'version', 'seconds', 'fitness', 'maxHours', 'min', 'q1', 'median', 'q3', 'max',
                      'mean', 'stdDev'].join(';'))
            execs.times { int exec ->
                String id = instanceid + "_${String.format(format, exec)}"
                solver.solve([i, var, i].join('/'), sur / 100)
                      .throttleFirst(1, TimeUnit.SECONDS)
                      .map { res -> [id, res.version, res.secondsElapsed, res.fitness, res.maxHours,
                                     res.stats.min, res.stats.q1, res.stats.median, res.stats.q3, res.stats.max,
                                     String.format('%.2f', res.stats.mean), String.format('%.2f', res.stats.stdDev)] }
                      .toBlocking()
                      .subscribe({ log.info it.join(';') },
                        { e -> log.error e.message; e.printStackTrace(System.err) },
                        { log.warn "Fim exec ${id}!!" })
            }
        }
    }

    void phase01() {
        multiexec([30, 60, 90])
    }

    void phase01b() {
        multiexec([0, 100])
    }

    void phase02() {
        def solver = createSolver()
        def format = '%02d'
        def execs = 30
        def params = [[25, 50, 100], [10, 25, 50], ['employee', 'task'], [30, 60, 90]].combinations()
        params.each { int t, int dep, String var, double sur ->
            String i = ['i', t, dep].join('_')
            if ('i_25_10' != i) {
                String instanceid = [i, var, (int) sur].join('_')
                MDC.put('instanceid', instanceid)
                log.info(['id', 'version', 'seconds', 'fitness', 'maxHours', 'min', 'q1', 'median', 'q3', 'max',
                          'mean', 'stdDev'].join(';'))
                execs.times { int exec ->
                    String id = instanceid + "_${String.format(format, exec)}"
                    solver.solve([i, var, i].join('/'), sur / 100)
                      .throttleFirst(1, TimeUnit.SECONDS)
                      .map { res -> [id, res.version, res.secondsElapsed, res.fitness, res.maxHours,
                                     res.stats.min, res.stats.q1, res.stats.median, res.stats.q3, res.stats.max,
                                     String.format('%.2f', res.stats.mean), String.format('%.2f', res.stats.stdDev)] }
                      .toBlocking()
                      .subscribe({ log.info it.join(';') },
                        { e -> log.error e.message; e.printStackTrace(System.err) },
                        { log.warn "Fim exec ${id}!!" })
                }
            }
        }
    }

    void batchSolver() {
        def solver = createSolver()
        def format = '%03d'
        (1..100).forEach { int exec ->
            solver.solve("i_25_10/employee/i_25_10", 0.6)
                  .throttleFirst(1, TimeUnit.SECONDS)
                  .toBlocking()
                  .subscribe({ result -> log.info "Exec ${String.format(format, exec)}, Result: ${result.maxHours}" },
                    { e -> log.error e.message; e.printStackTrace(System.err) },
                    { log.warn "Fim exec ${String.format(format, exec)}!!" })
        }

    }

    void assertInstances() {
        [[25, 50, 100], [10, 25, 50], ['employee', 'task'], [0, 1, 2]].combinations { a, b, c, d ->
            def inst = "i_${a}_${b}"
            def insta = createInstanceDao().getInstanceByName("${inst}/${c}/${inst}-${d}")
            assert insta.tasks.size() == a + (c == 'task' ? 1 : 0) * 5 * d
            assert insta.employees.size() == 3 + (c == 'employee' ? 1 : 0) * d
        }
    }

    static void main(String[] args) {
        def main = new Main()
        main.phase02()
    }
}
