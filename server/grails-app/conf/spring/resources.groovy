import br.uece.goes.rts.WebSocketConfig
import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.solver.impl.GASolver

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    solver(GASolver) {
        instanceDao = instanceDao
    }

    webSocketConfig WebSocketConfig
}
