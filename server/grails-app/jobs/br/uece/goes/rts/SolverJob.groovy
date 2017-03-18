package br.uece.goes.rts

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dao.SolutionDao
import br.uece.goes.rts.dto.TimeLine
import br.uece.goes.rts.solver.Solver
import org.springframework.messaging.simp.SimpMessagingTemplate

import java.util.concurrent.TimeUnit

class SolverJob {
//    static triggers = {
//      simple repeatInterval: 5000l // execute job once in 5 seconds
//    }

    def concurrent = false
    def description = "Solver Job"

    JobDao jobDao

    SolutionDao solutionDao

    SimpMessagingTemplate brokerMessagingTemplate

    Solver<TimeLine> solver

    def execute() {
        // execute job
        def topic = "/topic/solution"
        if (jobDao.isExecuting()) {
            solutionDao.clearSolutions()
            solver.solve("i_25_10/employee/i_25_10", 0.4)
            .throttleFirst(1, TimeUnit.SECONDS).takeWhile { jobDao.isExecuting() }
            .toBlocking()
            .subscribe({ result ->
                solutionDao.addSolution(result)
                brokerMessagingTemplate.convertAndSend(topic, result)
            }, { e -> println "Erro: ${e.message}"; e.printStackTrace(System.err); jobDao.stopExecution() }, {
                jobDao.stopExecution()
                brokerMessagingTemplate.convertAndSend(topic, solutionDao.bestSolution())
            })
        }
    }
}
