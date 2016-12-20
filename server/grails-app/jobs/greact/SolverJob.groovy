package greact

import grails.events.Events
import greact.solution.TimeLine
import greact.solver.Solver
import hazelgrails.HazelService

class SolverJob implements Events {
//    static triggers = {
//      simple repeatInterval: 5000l // execute job once in 5 seconds
//    }

    def concurrent = false
    def description = "Solver Job"

    HazelService hazelService

    Solver<TimeLine> solver

    def execute() {
        // execute job
        def jobs = hazelService.map('jobs')
        def sol = hazelService.map('solutions')
        if (jobs['execute']) {
            sol['solutions'] = []
            solver.solve().takeWhile { jobs['execute'] }
            //.doAfterTerminate { println "end of Thread ${Thread.currentThread().name}!!" }
            .toBlocking()
            .subscribe { result ->
                sol['best-solution'] = result
                sol['solutions'] += [x: result.createdAt, y: result.maxHours]
                notify("solution", result)
            }
            sol['solutions'] = []
        }
    }
}
