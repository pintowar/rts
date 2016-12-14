package greact.dao.impl

import greact.dao.InstanceDao
import greact.domain.Employee
import greact.domain.Instance
import greact.domain.Task
import org.springframework.core.io.ClassPathResource

/**
 * Created by thiago on 09/12/16.
 */
class InstanceParser implements InstanceDao {

    @Override
    Instance getInstanceByName(String name) {
        new Instance(getInstanceTasks(name), getInstanceEmployees(name))
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
        List content = new ClassPathResource(file).inputStream.text.split('\n')*.split(',')
        List head = content.first()
        content.tail().collect { [head, it].transpose().collectEntries() }
    }
}
