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
        Observable.interval(0, 30, TU.SECONDS)
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
        csvToMap(splitFile("${name}.txt")[1]).collect {
            new Task(id: it['id'].toInteger(), content: it['content'], criticity: it['criticity'],
                    preced: it['preced'].toInteger(), skills: (1..10).collect { el -> it["skill${el}"].toInteger() })
        }
    }

    @Override
    List<Employee> getInstanceEmployees(String name) {
        csvToMap(splitFile("${name}.txt")[0]).collect {
            new Employee(id: it['id'].toInteger(), content: it['content'],
                    skills: (1..10).collect { el -> it["skill${el}"].toInteger() })
        }
    }

    private List<Map<String, String>> csvToMap(String content) {
        def data = content.split('\n')*.split(',') as List
        List head = data.first()
        data.tail().collect { [head, it].transpose().collectEntries() }
    }

    private List<String> splitFile(String file) {
        this.class.classLoader.getResourceAsStream(file).text.split(/=+/)*.trim()
    }
}
