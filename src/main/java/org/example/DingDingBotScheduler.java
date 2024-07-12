package org.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DingDingBotScheduler {

    private final DingDingBotService dingDingBotService;

    public DingDingBotScheduler(DingDingBotService dingDingBotService) {
        this.dingDingBotService = dingDingBotService;
    }

    @Scheduled(cron = "0 0 10,14,16 * * ?") // 定时每天10点、14点、16点提醒
    public void sendWaterReminder() {
        String message = "记得喝水哦！💧.";
        dingDingBotService.sendTextMessage(message);
    }

    @Scheduled(cron = "0 0 11 * * ?") // 每天中午11点提醒
    @Scheduled(cron = "0 0 16 * * ?") // 每天下午3点提醒
    public void sendMoodCheckIn() {
        dingDingBotService.sendTextMessage("心情打卡时间到，请分享你的心情：😀 😐 😞.");
    }

    @Scheduled(cron = "0 0 9 * * ?") // 每天早上9点15分发送图片
    public void sendDailyImage() {
        try {
            dingDingBotService.sendImageMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 30 17 * * ?") // 每天下午5:30触发
    public void sendScheduledDogDiaryMessage() {
        dingDingBotService.sendDogDiaryMessage();
    }

    @Scheduled(cron = "0 0 9 * * ?") // 每天下午5:30触发
    public void sendScheduledNoDogDiaryMessage() {
        dingDingBotService.sendDogDiaryMessage();
    }

}
