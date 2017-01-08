import br.uece.goes.rts.WebSocketConfig
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.dao.impl.JobExecuter
import br.uece.goes.rts.dao.impl.SolutionStore
import br.uece.goes.rts.solver.impl.FullDynGASolver

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    jobDao(JobExecuter)

    solutionDao(SolutionStore) {
        jobDao = jobDao
    }

    solver(FullDynGASolver) {
        instanceDao = instanceDao
    }

    webSocketConfig WebSocketConfig
}
