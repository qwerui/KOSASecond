//작업자 : 한수민

package com.app.sketchbook.friend.entity;

public enum FriendStatus {
    PENDING,    // 요청 중인 상태
    ACCEPTED,   // 친구인 상태
    REJECTED,   // 거절한 상태
    DELETED,    // 삭제한 상태
    BLOCKED,   // 차단한 상태
    NOT_FRIEND
}