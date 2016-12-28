import br.uece.goes.rts.WebSocketConfig
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.dao.impl.JobExecuter
import br.uece.goes.rts.dao.impl.JobHazelcast
import br.uece.goes.rts.dao.impl.SolutionHazelcast
import br.uece.goes.rts.dao.impl.SolutionStore
import br.uece.goes.rts.solver.impl.GASolver
import hazelgrails.HazelService

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    hazelService(HazelService)

    jobDao(JobExecuter)

    solutionDao(SolutionStore)

    solver(GASolver) {
        instanceDao = instanceDao
    }

    webSocketConfig WebSocketConfig
}
