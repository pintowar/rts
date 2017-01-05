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
    private final Integer group;

    public Item(Integer id, String content, Date start, Date end, Integer group) {
        this.id = id;
        this.content = content;
        this.start = start;
        this.end = end;
        this.group = group;
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

}
