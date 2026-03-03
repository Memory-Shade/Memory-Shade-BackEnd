package com.memoryshade.domain.notification.model;

public enum NotiType {
    SCHEDULE, // 사용자 일정 알람
    DIARY_WRITTEN, // 사용자가 일기 작성 완료 (보호자 수신)
    DIARY_SHARED, // 보호자에게 일기 공유됨 (보호자 수신)
    GPS_ALERT, // 사용자 안전 반경 이탈 (보호자 수신)
    EMOTION_REPORT // 감정 분석 리포트 생성
}
