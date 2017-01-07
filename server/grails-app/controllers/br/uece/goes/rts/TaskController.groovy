package br.uece.goes.rts

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dao.SolutionDao
import br.uece.goes.rts.dto.TimeLine
import grails.rx.web.RxController
import groovy.json.JsonOutput

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
        def sol = solutionDao.bestSolution()
        header 'Content-Type', "application/json"
        render JsonOutput.toJson(sol ?: [:])
    }

    def solutions() {
        def hist = solutionDao.historicalSolutions().collect { [x: it.createdAt, y: it.maxHours] }
        header 'Content-Type', "application/json"
        render JsonOutput.toJson(hist ?: [])
    }

//    def channel() {
//        rx.stream("task", stream)
//    }

    def startSolver() {
        jobDao.startExecution()
        SolverJob.triggerNow()
        header 'Content-Type', "application/json"
        render JsonOutput.toJson(TimeLine.EMPTY)
    }

    def stopSolver() {
        jobDao.stopExecution()
        def sol = solutionDao.stopAndGetBestSolution()
        header 'Content-Type', "application/json"
        render JsonOutput.toJson(sol)
    }

}
