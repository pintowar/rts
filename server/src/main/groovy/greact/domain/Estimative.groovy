package greact.domain

import groovy.transform.Canonical
import groovy.transform.Memoized
import groovy.transform.TypeChecked
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation

/**
 * Created by thiago on 03/12/16.
 */
@Canonical
@TypeChecked
class Estimative {
    Employee employee
    Task task

    @Memoized
    Integer getHours() {
        def p = new PearsonsCorrelation()
        double val = p.correlation(employee.skills as double[], task.skills as double[])
        Math.round(39 * (1 - val) - 2)
    }
}
