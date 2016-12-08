package greact

import grails.events.Events
import rx.Observable

import java.util.concurrent.TimeUnit

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
            Observable.interval(1, TimeUnit.SECONDS)
                    .takeWhile { jobs['execute'] }
                    //.doAfterTerminate { println "fim da treta ${Thread.currentThread().name}!!" }
                    .toBlocking()
                    .subscribe {
                def result = solver.solve()
                //println "É treta ${Thread.currentThread().name}..."
                sol['best-solution'] = result
                notify("solution", result)
            }
        }
    }
}
