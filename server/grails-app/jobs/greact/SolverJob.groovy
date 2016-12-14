package greact

import grails.events.Events

class SolverJob implements Events {
//    static triggers = {
//      simple repeatInterval: 5000l // execute job once in 5 seconds
//    }

    def concurrent = false
    def description = "Solver Job"

    def hazelService

    def solver

    def execute() {
        // execute job
        def jobs = hazelService.map('jobs')
        def sol = hazelService.map('solutions')
        if (jobs['execute']) {
            solver.solve().takeWhile { jobs['execute'] }
            //.doAfterTerminate { println "fim da treta ${Thread.currentThread().name}!!" }
                    .toBlocking()
                    .subscribe { result ->
                //println "É treta ${Thread.currentThread().name}..."
                sol['best-solution'] = result
                notify("solution", result)
            }
        }
    }
}
