package br.uece.goes.rts.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by thiago on 13/12/16.
 */
public class TimeLine implements Serializable {

    private final LocalDateTime initialPeriod;

    private final List<Item> items;

    private final List<Group> groups;

    private final int maxHours;

    private final int version;

    private final long secondsElapsed;

    private final boolean running;

    private final Date createdAt = new Date();

    public static final TimeLine EMPTY = new TimeLine(LocalDateTime.now(), Collections.emptyList(),
            Collections.emptyList(), -1, 0, false);

    public TimeLine(List<Item> items, List<Group> groups, int maxHours, int version) {
        this(LocalDateTime.now(), items, groups, maxHours, version, true);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours, int version) {
        this(initialPeriod, items, groups, maxHours, version, true);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours, int version, boolean running) {
        this.initialPeriod = initialPeriod;
        this.items = Collections.unmodifiableList(items);
        this.groups = Collections.unmodifiableList(groups);
        this.maxHours = maxHours;
        this.version = version;
        this.running = running;
        this.secondsElapsed = initialPeriod.until(LocalDateTime.now(), ChronoUnit.SECONDS);
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

    public TimeLine changeExecutionMode() {
        return new TimeLine(initialPeriod, items, groups, maxHours, version, !running);
    }

    public TimeLine stopExecutionMode() {
        return new TimeLine(initialPeriod, items, groups, maxHours, version, false);
    }
}
