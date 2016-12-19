import greact.dao.impl.InstanceParser
import greact.solver.impl.MockSolver

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    solver(MockSolver) {
        instanceDao = instanceDao
    }
}
