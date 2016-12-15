package greact

import com.fasterxml.jackson.databind.ObjectMapper
import grails.events.Events
import grails.rx.web.RxController
import greact.solution.TimeLine
import groovy.json.JsonOutput
import org.grails.plugins.rx.web.result.RxResult
import reactor.bus.Event
import rx.Observable
import rx.Subscriber

class TaskController implements RxController, Events {
    static responseFormats = ['json', 'xml']

    private ObjectMapper mapper = new ObjectMapper()

    def hazelService

    private Observable<RxResult<Object>> stream = Observable.create { Subscriber<RxResult<Object>> subscriber ->
        on("solution") { Event<TimeLine> ev ->
            subscriber.onNext(rx.render(JsonOutput.toJson(ev.data)))
        }
    }

    def index() {
        def sol = hazelService.map('solutions')
        render JsonOutput.toJson(sol['best-solution'] ?: [:])
    }

    def channel() {
        rx.stream("task", stream)
    }

    def startSolver() {
        def jobs = hazelService.map('jobs')
        jobs['execute'] = true
        SolverJob.triggerNow()
        render JsonOutput.toJson([solver: 'started'])
    }

    def stopSolver() {
        def jobs = hazelService.map('jobs')
        jobs['execute'] = false
        render JsonOutput.toJson([solver: 'stopped'])
    }

}
