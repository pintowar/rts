package br.uece.goes.rts.domain

import br.uece.goes.rts.ListUtils
import br.uece.goes.rts.dto.Group
import br.uece.goes.rts.dto.Item
import br.uece.goes.rts.dto.TimeLine
import groovy.transform.Canonical
import groovy.transform.Memoized
import groovy.transform.TypeChecked

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Created by thiago on 09/12/16.
 */
@Canonical
@TypeChecked
class Instance {
    public static final Instance EMPTY = new Instance([], [], -1)

    List<Task> tasks
    List<Employee> employees
    int version

    @Memoized
    List<Estimative> getEstimatives() {
        tasks.collectMany { t ->
            employees.collect { e ->
                new Estimative(e, t)
            }
        }
    }

    @Memoized
    Map<List<Integer, Integer>, Integer> transformEstimatives() {
        estimatives.collectEntries { e -> [[e.employee.id, e.task.id], e.hours] }
    }

    @Memoized
    Map<Integer, Employee> transformEmployees() {
        employees.collectEntries { e -> [e.id, e] }
    }

    @Memoized
    Map<Integer, Task> transformTasks() {
        tasks.collectEntries { t -> [t.id, t] }
    }

    TimeLine toTimeLine(LocalDateTime initialDate, List<Integer> representation) {
        List<Item> items = []
        LocalDateTime maxTime = initialDate
        boolean hasEstimatives = !transformEstimatives().isEmpty()
        ListUtils.splitWhere(representation) { int it -> it < 0 }.indexed().each { k, v ->
            int employeeId = k + 1
            LocalDateTime beginning = initialDate
            if (hasEstimatives) {
                v.each { taskId ->
                    LocalDateTime end = beginning.plusHours(transformEstimatives().get([employeeId, taskId]))

                    maxTime = end > maxTime ? end : maxTime
                    Task t = transformTasks().get(taskId)
                    items << new Item(t.id, t.content, Date.from(beginning.atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(end.atZone(ZoneId.systemDefault()).toInstant()), employeeId)
                    beginning = end.plusHours(5)
                }
            }
        }
        List<Group> groups = transformEmployees().values().collect { new Group(it.id, it.content, it.id) }


        new TimeLine(initialDate, items, groups, (int) initialDate.until(maxTime, ChronoUnit.HOURS), version)
    }
}
