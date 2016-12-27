package br.uece.goes.rts

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

    HazelService hazelService

    SimpMessagingTemplate brokerMessagingTemplate

    Solver<TimeLine> solver

    def execute() {
        // execute job
        def jobs = hazelService.map('jobs')
        def sol = hazelService.map('solutions')
        if (jobs['execute']) {
            sol['solutions'] = []
            solver.solve()
            .throttleFirst(1, TimeUnit.SECONDS).takeWhile { jobs['execute'] }
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
