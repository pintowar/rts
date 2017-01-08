package br.uece.goes.rts.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by thiago on 13/12/16.
 */
public class TimeLine implements Serializable {

    private final LocalDateTime initialPeriod;

    private final List<Item> items;

    private final List<Group> groups;

    private final int maxHours;

    private final int priorityPunishment;

    private final int precedsPunishment;

    private final int version;

    private final long secondsElapsed;

    private final boolean running;

    private final Date createdAt = new Date();

    private final Stats stats;

    public static final TimeLine EMPTY = new TimeLine(LocalDateTime.now(), Collections.emptyList(),
            Collections.emptyList(), Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, false);

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours,
                    int priorityPunishment, int precedsPunishment, int version, Stats stats, boolean running) {
        this.initialPeriod = initialPeriod;
        this.items = Collections.unmodifiableList(items);
        this.groups = Collections.unmodifiableList(groups);
        this.maxHours = maxHours;
        this.priorityPunishment = priorityPunishment;
        this.precedsPunishment = precedsPunishment;
        this.version = version;
        this.stats = stats;
        this.running = running;
        this.secondsElapsed = initialPeriod.until(LocalDateTime.now(), ChronoUnit.SECONDS);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours,
                    int priorityPunishment, int precedsPunishment, int version, boolean running) {
        this(initialPeriod, items, groups, maxHours, priorityPunishment, precedsPunishment, version, new Stats(),
                running);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours,
                    int priorityPunishment, int precedsPunishment, int version) {
        this(initialPeriod, items, groups, maxHours, priorityPunishment, precedsPunishment, version, true);
    }

    public TimeLine(List<Item> items, List<Group> groups, int maxHours, int priorityPunishment, int precedsPunishment,
                    int version) {
        this(LocalDateTime.now(), items, groups, maxHours, priorityPunishment, precedsPunishment, version, true);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getMaxHours() {
        return maxHours;
    }

    public int getPriorityPunishment() {
        return priorityPunishment;
    }

    public int getPrecedsPunishment() {
        return precedsPunishment;
    }

    public double getFitness() {
        return maxHours + 30 * priorityPunishment + 100 * precedsPunishment;
    }

    public int getVersion() {
        return version;
    }

    public long getSecondsElapsed() {
        return secondsElapsed;
    }

    public boolean isRunning() {
        return running;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getCurrentTime() {
        return Date.from(initialPeriod.plusHours(secondsElapsed).atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean isEmpty() {
        return items.isEmpty() && groups.isEmpty();
    }

    public Stats getStats() {
        return stats;
    }

    public TimeLine changeExecutionMode() {
        return new TimeLine(initialPeriod, items, groups, maxHours, priorityPunishment, precedsPunishment, version, !running);
    }

    public TimeLine stopExecutionMode() {
        return new TimeLine(initialPeriod, items, groups, maxHours, priorityPunishment, precedsPunishment, version, false);
    }

    public TimeLine addStats(Stats stats) {
        return new TimeLine(initialPeriod, items, groups, maxHours, priorityPunishment, precedsPunishment, version, stats, running);
    }

    public List<Item> listStartingItemsBefore(Date date) {
        return items.stream().filter(it -> it.getStart().before(date)).collect(Collectors.toList());
    }

    public List<Item> listStartingItemsBefore(LocalDateTime date) {
        return listStartingItemsBefore(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
    }
}
