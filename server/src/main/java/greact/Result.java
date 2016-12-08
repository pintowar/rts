package greact;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by thiago on 03/12/16.
 */
public class Result implements Serializable {
    private final List<Item> items;

    private final List<Group> groups;

    public Result(List<Item> items, List<Group> groups) {
        this.items = Collections.unmodifiableList(items);
        this.groups = Collections.unmodifiableList(groups);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Item> getItems() {
        return items;
    }
}
