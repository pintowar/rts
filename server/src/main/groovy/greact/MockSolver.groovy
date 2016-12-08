package greact

import groovy.time.TimeCategory as TC
import org.springframework.core.io.ClassPathResource

/**
 * Created by thiago on 03/12/16.
 */
class MockSolver implements Solver {

    private Date initialDate = new Date()

    private Map<Integer, Task> tasks = csvToMap("initial_tasks.csv").collectEntries {
        [it['id'].toInteger(), new Task(id: it['id'].toInteger(), content: it['content'], criticity: it['criticity'],
                preced: it['preced'].toInteger(), skills: (1..10).collect { el -> it["skill${el}"].toInteger() })]
    }

    private Map<Integer, Employee> employees = csvToMap("initial_resources.csv").collectEntries {
        [it['id'].toInteger(), new Employee(id: it['id'].toInteger(), content: it['content'],
                skills: (1..10).collect { el -> it["skill${el}"].toInteger() })]
    }

    private Map<List<Integer, Integer>, Integer> estimatives = csvToMap("initial_estimates.csv").collectEntries {
        //new Estimative(employee: employees[it['employee'].toInteger()], task: tasks[it['task'].toInteger()],
        //        hours: it['hours'].toInteger())
        [[it['employee'].toInteger(), it['task'].toInteger()], it['hours'].toInteger()]
    }

    private List<Map<String, String>> csvToMap(String file) {
        List content = new ClassPathResource(file).inputStream.text.split('\n')*.split(',')
        List head = content.first()
        content.tail().collect { [head, it].transpose().collectEntries() }
    }

    @Override
    Result solve() {
        def sol = shuffle((1..tasks.size()) + (1..<employees.size()).collect { -it })
        List<Item> items = []
        splitWhere(sol) { it < 0 }.indexed().each { k, v ->
            int employeeId = k + 1
            Date beginning = initialDate.clone()
            v.each { int taskId ->
                Date end = use(TC) { beginning + estimatives[[employeeId, taskId]].hours }
                items << new Item(tasks[taskId].id, tasks[taskId].content, beginning, end, employeeId)
                beginning = use(TC) { end + 5.hours }
            }
        }
        List<Group> groups = employees.values().collect { new Group(it.id, it.content, it.id) }

        return new Result(items, groups)
    }

    private List<? extends Object> shuffle(List<? extends Object> list) {
        List<? extends Object> aux = list.clone() as List<? extends Object>
        Collections.shuffle(aux)
        aux
    }

    private List<List<? extends Object>> splitWhere(List<? extends Object> list, Closure<Boolean> cond) {
        ([-1] + list.indexed().findResults { k, v -> if (cond(v)) k } + [list.size()]).collate(2, 1, false).collect {
            list[(it[0] + 1)..<it[1]]
        }
    }

}
