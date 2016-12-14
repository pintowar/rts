import greact.dao.impl.InstanceParser
import greact.solver.MockSolver

// Place your Spring DSL code here
beans = {
    instanceDao(InstanceParser)

    solver(MockSolver) {
        instanceDao = instanceDao
    }
}
