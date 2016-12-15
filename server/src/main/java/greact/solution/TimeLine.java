package greact.solution;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
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

    public TimeLine(List<Item> items, List<Group> groups, int maxHours, int version) {
        this(LocalDateTime.now(), items, groups, maxHours, version);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours, int version) {
        this.initialPeriod = initialPeriod;
        this.items = Collections.unmodifiableList(items);
        this.groups = Collections.unmodifiableList(groups);
        this.maxHours = maxHours;
        this.version = version;
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
}
