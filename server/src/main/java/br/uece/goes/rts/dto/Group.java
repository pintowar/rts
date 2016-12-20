package br.uece.goes.rts.dto;

import java.io.Serializable;

/**
 * Created by thiago on 03/12/16.
 */
public class Group implements Serializable {

    private final Integer id;
    private final String content;
    private final Integer value;

    public Group(Integer id, String content, Integer value) {
        this.id = id;
        this.content = content;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Integer getValue() {
        return value;
    }

    public String getContent() {
        return content;
    }
}
