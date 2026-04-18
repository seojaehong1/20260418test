package com.safecall.safecall.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.safecall.safecall.entity.FcmToken;
import com.safecall.safecall.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final FcmTokenRepository fcmTokenRepository;

    public void sendToMember(Long memberId, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findByMemberId(memberId);

        if (tokens.isEmpty()) {
            log.warn("FCM 토큰 없음: memberId={}", memberId);
            return;
        }

        for (FcmToken fcmToken : tokens) {
            Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("FCM 발송 성공: {} → {}", memberId, response);
            } catch (Exception e) {
                log.error("FCM 발송 실패: memberId={}", memberId, e);
            }
        }
    }
}