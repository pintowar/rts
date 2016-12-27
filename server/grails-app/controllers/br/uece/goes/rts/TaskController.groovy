package br.uece.goes.rts

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dao.SolutionDao
import br.uece.goes.rts.dto.TimeLine
import grails.converters.JSON
import grails.rx.web.RxController

class TaskController implements RxController {
    static responseFormats = ['json', 'xml']

    JobDao jobDao

    SolutionDao solutionDao

//    private Observable<RxResult<Object>> stream = Observable.create { Subscriber<RxResult<Object>> subscriber ->
//        on("solution") { Event<TimeLine> ev ->
//            subscriber.onNext(rx.render(JsonOutput.toJson(ev.data)))
//        }
//    }.onErrorReturn { rx.render(JsonOutput.toJson(TimeLine.EMPTY)) }

    def index() {
        def best = jobDao.isExecuting() ? solutionDao.bestSolution() : solutionDao.bestSolution().stopExecutionMode()
        render best as JSON
    }

    def solutions() {
        render(solutionDao.historicalSolutions().collect { [x: it.createdAt, y: it.maxHours] } as JSON)
    }

//    def channel() {
//        rx.stream("task", stream)
//    }

    def startSolver() {
        jobDao.startExecution()
        SolverJob.triggerNow()
        render TimeLine.EMPTY as JSON
    }

    def stopSolver() {
        jobDao.stopExecution()
        def result = solutionDao.stopAndGetBestSolution()
        render result as JSON
    }

}
