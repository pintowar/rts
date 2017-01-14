package br.uece.goes.rts.domain

import br.uece.goes.rts.ListUtils
import br.uece.goes.rts.dto.Group
import br.uece.goes.rts.dto.Item
import br.uece.goes.rts.dto.Stats
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
    public static final Instance EMPTY = new Instance([], [], -1, LocalDateTime.now())

    List<Task> tasks
    List<Employee> employees
    int version
    LocalDateTime currentTime
    private final Map<Integer, Integer> precedes

    Instance(List<Task> tasks, List<Employee> employees, int version, LocalDateTime currentTime) {
        this.tasks = tasks
        this.employees = employees
        this.version = version
        this.currentTime = currentTime

        this.precedes = Collections.unmodifiableMap(tasks.findAll { it.preced > 0 }.collectEntries {
            [it.id, it.preced]
        })

    }

    @Memoized
    List<Estimative> getEstimates() {
        tasks.collectMany { t ->
            employees.collect { e ->
                new Estimative(e, t)
            }
        }
    }

    @Memoized
    Map<List<Integer, Integer>, Integer> transformEstimatives() {
        estimates.collectEntries { e -> [[e.employee.id, e.task.id], e.hours] }
    }

    @Memoized
    Map<Integer, Employee> transformEmployees() {
        employees.collectEntries { e -> [e.id, e] }
    }

    @Memoized
    Map<Integer, Task> transformTasks() {
        tasks.collectEntries { t -> [t.id, t] }
    }

    TimeLine toTimeLine(LocalDateTime initialDate, List<Integer> representation, Stats stats) {
        List<Item> items = []
        LocalDateTime maxTime = initialDate
        boolean hasEstimatives = !transformEstimatives().isEmpty()
        int priorityPunishment = 0
        int position = 0
        int breaker = 0
        Map<Integer, Item> itemsMap = [:]
        ListUtils.groupWhere(representation, 0) { int it -> it < 0 }.each { k, v ->
            int employeeId = Math.abs(k) + 1
            LocalDateTime beginning = initialDate
            int priorityCounter = 4
            if (hasEstimatives) {
                v.each { taskId ->
                    LocalDateTime end = beginning.plusHours(transformEstimatives().get([employeeId, taskId]))

                    maxTime = end > maxTime ? end : maxTime
                    Task t = transformTasks().get(taskId)
                    items << new Item(t.id, t.content, Date.from(beginning.atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(end.atZone(ZoneId.systemDefault()).toInstant()), t.color, employeeId,
                            breaker + position++, beginning < currentTime)
                    priorityPunishment += (priorityCounter < t.priority ? t.priority - priorityCounter : 0)
                    priorityCounter = t.priority
                    itemsMap[t.id] = items.last()
                    beginning = end.plusMinutes(20)
                }
                breaker++
            }
        }
        int precedesPunishment = precedes.count { k, v -> itemsMap[k].end >= itemsMap[v].start }.intValue()
        List<Group> groups = transformEmployees().values().collect { new Group(it.id, it.content, it.id) }
        int maxHours = (int) initialDate.until(maxTime, ChronoUnit.HOURS)

        new TimeLine(initialDate, items, groups, maxHours, priorityPunishment, precedesPunishment, version, stats, true)
    }

    TimeLine toTimeLine(LocalDateTime initialDate, List<Integer> representation) {
        toTimeLine(initialDate, representation, new Stats())
    }

    @Memoized
    List<Integer> indexes() {
        //(1..(this.tasks.size())) + (1..<(this.employees.size())).collect { -it }
        ListUtils.intercale(1..(this.tasks.size()), (1..<(this.employees.size())).collect { -it })
    }

    Integer size() {
        indexes().size()
    }

    @Memoized
    boolean isEmpty() {
        tasks.isEmpty() && employees.isEmpty()
    }
}
