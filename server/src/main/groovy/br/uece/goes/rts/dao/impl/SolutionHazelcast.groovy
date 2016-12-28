package br.uece.goes.rts.dao.impl

import br.uece.goes.rts.dao.SolutionDao
import br.uece.goes.rts.dto.TimeLine
import hazelgrails.HazelService

import javax.annotation.PostConstruct

/**
 * Created by thiago on 27/12/16.
 */
class SolutionHazelcast implements SolutionDao {

    HazelService hazelService

    private Map<String, Object> solutions

    @PostConstruct
    void init() {
        solutions = hazelService.map("solutions")
    }

    @Override
    void clearSolutions() {
        solutions.put('solutions', [])
    }

    @Override
    void addSolution(TimeLine timeLine) {
        solutions.put('best-solution', timeLine)
        solutions['solutions'] += timeLine
    }

    @Override
    TimeLine bestSolution() {
        solutions.get('best-solution') as TimeLine
    }

    @Override
    TimeLine stopAndGetBestSolution() {
        def result = bestSolution()
        solutions.put('best-solution', result.stopExecutionMode())
        bestSolution()
    }

    @Override
    List<TimeLine> historicalSolutions() {
        solutions.get('solutions') as List<TimeLine>
    }
}
