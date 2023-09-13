package com.javateam.model;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    private int direction;

    VoteType(int direction) {
        this.direction = direction;
    }

    public Integer getDirection() {
        return direction;
    }
}
