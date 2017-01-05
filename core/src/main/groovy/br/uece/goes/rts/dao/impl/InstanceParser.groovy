package br.uece.goes.rts.dao.impl

import br.uece.goes.rts.dao.InstanceDao
import br.uece.goes.rts.domain.Employee
import br.uece.goes.rts.domain.Instance
import br.uece.goes.rts.domain.Task
import rx.Observable

import java.util.concurrent.TimeUnit as TU

/**
 * Created by thiago on 09/12/16.
 */
//@TypeChecked
class InstanceParser implements InstanceDao {

    @Override
    Observable<Instance> observeInstanceByName(String name) {
        Observable.interval(0, 20, TU.SECONDS)
                  .map { "${name}-$it" }
                  .map { getInstanceByName(it) }
                  .onErrorReturn { Instance.EMPTY }
    }

    @Override
    Instance getInstanceByName(String name) {
        def ver = name.split('-').last().toInteger()
        new Instance(getInstanceTasks(name), getInstanceEmployees(name), ver)
    }

    @Override
    List<Task> getInstanceTasks(String name) {
        csvToMap("${name}_tasks.csv").collect {
            new Task(id: it['id'].toInteger(), content: it['content'], criticity: it['criticity'],
                    preced: it['preced'].toInteger(), skills: (1..10).collect { el -> it["skill${el}"].toInteger() })
        }
    }

    @Override
    List<Employee> getInstanceEmployees(String name) {
        csvToMap("${name}_employees.csv").collect {
            new Employee(id: it['id'].toInteger(), content: it['content'],
                    skills: (1..10).collect { el -> it["skill${el}"].toInteger() })
        }
    }

    private List<Map<String, String>> csvToMap(String file) {
        List content = this.class.classLoader.getResourceAsStream(file).text.split('\n')*.split(',')
//        List content = new ClassPathResource(file).inputStream.text.split('\n')*.split(',')
        List head = content.first()
        content.tail().collect { [head, it].transpose().collectEntries() }
    }
}
