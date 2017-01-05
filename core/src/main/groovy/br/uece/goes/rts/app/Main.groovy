package br.uece.goes.rts.app

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import br.uece.goes.rts.solver.impl.DynGASolver
import groovy.util.logging.Slf4j

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

    void batchSolver() {
        def solver = createSolver()
        def format = '%03d'
        (1..100).forEach { int exec ->
            solver.solve()
                  .throttleFirst(1, TimeUnit.SECONDS)
                  .toBlocking()
                  .subscribe({ result -> log.info "Exec ${String.format(format, exec)}, Result: ${result.maxHours}" },
                    { e -> log.error e.message; e.printStackTrace(System.err) },
                    { log.warn "Fim exec ${String.format(format, exec)}!!" })
        }

    }

    static void main(String[] args) {
        def main = new Main()
        main.batchSolver()
    }
}
