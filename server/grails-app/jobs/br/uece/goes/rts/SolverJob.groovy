package br.uece.goes.rts

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import hazelgrails.HazelService
import org.springframework.messaging.simp.SimpMessagingTemplate

import java.util.concurrent.TimeUnit

class SolverJob {
//    static triggers = {
//      simple repeatInterval: 5000l // execute job once in 5 seconds
//    }

    def concurrent = false
    def description = "Solver Job"

    JobDao jobDao

    HazelService hazelService

    SimpMessagingTemplate brokerMessagingTemplate

    Solver<TimeLine> solver

    def execute() {
        // execute job
        def sol = hazelService.map('solutions')
        if (jobDao.isExecuting()) {
            sol['solutions'] = []
            solver.solve()
            .throttleFirst(1, TimeUnit.SECONDS).takeWhile { jobDao.isExecuting() }
            .toBlocking()
            .subscribe { result ->
                sol['best-solution'] = result
                sol['solutions'] += [x: result.createdAt, y: result.maxHours]
                brokerMessagingTemplate.convertAndSend("/topic/solution", result)
            }
            //sol['solutions'] = []
        }
    }
}
