import br.uece.goes.rts.dao.impl.InstanceParser
import br.uece.goes.rts.solver.impl.MockSolver

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    solver(MockSolver) {
        instanceDao = instanceDao
    }
}
