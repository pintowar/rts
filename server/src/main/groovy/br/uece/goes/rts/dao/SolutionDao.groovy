package br.uece.goes.rts.dao

import br.uece.goes.rts.dto.TimeLine

/**
 * Created by thiago on 27/12/16.
 */
interface SolutionDao {

    void clearSolutions()

    void addSolution(TimeLine timeLine)

    TimeLine bestSolution()

    TimeLine stopAndGetBestSolution()

    List<TimeLine> historicalSolutions()

}