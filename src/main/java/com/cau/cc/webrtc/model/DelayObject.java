package com.cau.cc.webrtc.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Objects;

@Data
@Builder
public class DelayObject {
    private Long id; //자신과 매칭되었던 사람의 id
    private int delayCount; //자신과 매칭되었던 사람과 또 매칭된 경우

    public DelayObject(Long id, int delayCount) {
        this.id = id;
        this.delayCount = delayCount;
    }

    public DelayObject() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayObject that = (DelayObject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
