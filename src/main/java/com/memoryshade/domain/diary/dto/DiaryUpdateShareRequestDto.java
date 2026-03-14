package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DiaryUpdateShareRequestDto(

        @JsonProperty("is_shared")
        boolean isShared
) {
    public boolean toShared() {
        return isShared;
    }
}
