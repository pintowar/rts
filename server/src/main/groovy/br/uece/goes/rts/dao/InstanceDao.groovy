package br.uece.goes.rts.dao

import br.uece.goes.rts.domain.Employee
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.domain.Task
import rx.Observable

/**
 * Created by thiago on 18/12/16.
 */
interface InstanceDao {
    Observable<Instance> observeInstanceByName(String name)

    Instance getInstanceByName(String name)

    List<Task> getInstanceTasks(String name)

    List<Employee> getInstanceEmployees(String name)
}