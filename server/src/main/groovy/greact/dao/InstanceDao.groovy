package greact.dao

import greact.domain.Employee
import greact.domain.Instance
import greact.domain.Task
import rx.Observable

/**
 * Created by thiago on 09/12/16.
 */
interface InstanceDao {

    Observable<Instance> observeInstanceByName(String name);

    Instance getInstanceByName(String name)

    List<Task> getInstanceTasks(String name)

    List<Employee> getInstanceEmployees(String name)
}
