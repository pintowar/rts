package br.uece.goes.rts.dao.impl

import br.uece.goes.rts.dao.JobDao
import br.uece.goes.rts.dao.SolutionDao
import br.uece.goes.rts.dto.TimeLine

import java.util.concurrent.atomic.AtomicReference

/**
 * Created by thiago on 27/12/16.
 */
class SolutionStore implements SolutionDao {

    private AtomicReference<TimeLine> bestTimeLine = new AtomicReference<>(TimeLine.EMPTY)

    private List<TimeLine> history = new Vector<>()

    JobDao jobDao

    @Override
    void clearSolutions() {
        history.clear()
    }

    @Override
    void addSolution(TimeLine timeLine) {
        bestTimeLine.set(timeLine)
        history.add(timeLine)
    }

    @Override
    TimeLine bestSolution() {
        jobDao.isExecuting() ? bestTimeLine.get() : bestTimeLine.get().stopExecutionMode()
    }

    @Override
    TimeLine stopAndGetBestSolution() {
        def result = bestSolution()
        bestTimeLine.set(result.stopExecutionMode())
        bestSolution()
    }

    @Override
    List<TimeLine> historicalSolutions() {
        history
    }
}
