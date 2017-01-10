package br.uece.goes.rts.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by thiago on 03/12/16.
 */
public class Item implements Serializable {
    private final Integer id;
    private final Date start;
    private final Date end;
    private final String content;
    private final String className;
    private final Integer group;
    private final Integer position;
    private final Boolean locked;

    public Item(Integer id, String content, Date start, Date end, String className, Integer group, Integer position, Boolean locked) {
        this.id = id;
        this.content = content;
        this.start = start;
        this.end = end;
        this.className = className;
        this.group = group;
        this.position = position;
        this.locked = locked;
    }

    public Item(Integer id, String content, Date start, Date end, String className, Integer group, Integer position) {
        this(id, content, start, end, className, group, position, false);
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Integer getGroup() {
        return group;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getClassName() {
        return className;
    }

    public Integer getPosition() {
        return position;
    }

    public Boolean getLocked() {
        return locked;
    }
}
