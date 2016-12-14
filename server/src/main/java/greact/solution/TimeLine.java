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

    private int maxHours;

    public TimeLine(List<Item> items, List<Group> groups, int maxHours) {
        this(LocalDateTime.now(), items, groups, maxHours);
    }

    public TimeLine(LocalDateTime initialPeriod, List<Item> items, List<Group> groups, int maxHours) {
        this.initialPeriod = initialPeriod;
        this.items = Collections.unmodifiableList(items);
        this.groups = Collections.unmodifiableList(groups);
        this.maxHours = maxHours;
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

}
