package br.uece.goes.rts

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dto.TimeLine
import com.fasterxml.jackson.databind.ObjectMapper
import grails.rx.web.RxController
import groovy.json.JsonOutput

class TaskController implements RxController {
    static responseFormats = ['json', 'xml']

    JobDao jobDao

    def hazelService

//    private Observable<RxResult<Object>> stream = Observable.create { Subscriber<RxResult<Object>> subscriber ->
//        on("solution") { Event<TimeLine> ev ->
//            subscriber.onNext(rx.render(JsonOutput.toJson(ev.data)))
//        }
//    }.onErrorReturn { rx.render(JsonOutput.toJson(TimeLine.EMPTY)) }

    def index() {
        def sol = hazelService.map('solutions')
        render JsonOutput.toJson(sol['best-solution'] ?: [:])
    }

    def solutions() {
        def sol = hazelService.map('solutions')
        render JsonOutput.toJson(sol['solutions'] ?: [])
    }

//    def channel() {
//        rx.stream("task", stream)
//    }

    def startSolver() {

        jobDao.startExecution()
        SolverJob.triggerNow()
        render JsonOutput.toJson(TimeLine.EMPTY)
    }

    def stopSolver() {

        def sol = hazelService.map('solutions')
        jobDao.stopExecution()

        def result = sol['best-solution']
        sol['best-solution'] = result.stopExecutionMode()
        render JsonOutput.toJson(sol['best-solution'])
    }

}
