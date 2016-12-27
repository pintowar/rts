import br.uece.goes.rts.WebSocketConfig
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.dao.impl.JobHazelcast
import br.uece.goes.rts.dao.impl.SolutionHazelcast
import br.uece.goes.rts.solver.impl.GASolver
import hazelgrails.HazelService

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    hazelService(HazelService)

    jobDao(JobHazelcast) {
        hazelService = hazelService
    }

    solutionDao(SolutionHazelcast) {
        hazelService = hazelService
    }

    solver(GASolver) {
        instanceDao = instanceDao
    }

    webSocketConfig WebSocketConfig
}
