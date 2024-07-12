package org.example;

import com.qimuu.easyweb.common.BaseResponse;
import com.qimuu.easyweb.common.ResultUtils;
import com.qimuu.easyweb.exception.BusinessException;
import icu.qimuu.qiapisdk.exception.ApiException;
import icu.qimuu.qiapisdk.model.params.HoroscopeParams;
import icu.qimuu.qiapisdk.model.response.LoveResponse;
import icu.qimuu.qiapisdk.model.response.PoisonousChickenSoupResponse;
import icu.qimuu.qiapisdk.service.ApiService;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DingDingBotController {

    @Resource
    private ApiService apiService;

    private final DingDingBotService dingDingBotService;

    public DingDingBotController(DingDingBotService dingDingBotService) {
        this.dingDingBotService = dingDingBotService;
    }

    @GetMapping("/sendWaterReminder")
    public String sendWaterReminder(@RequestParam(value = "message", defaultValue = "记得喝水哦！💧") String message) {
        dingDingBotService.sendTextMessage(message);
        return "提醒已发送: " + message;
    }

    @GetMapping("/sendDailyImage")
    public String sendDailyImage() {
        try {
            dingDingBotService.sendImageMessage();
            return "图片消息已发送: " ;
        } catch (Exception e) {
            return "发送失败: " + e.getMessage();
        }
    }

    @GetMapping("/sendDogDiaryMessage")
    public ResponseEntity<String> sendDogDiaryMessage() {
        return ResponseEntity.ok(dingDingBotService.sendDogDiaryMessage());
    }

    @GetMapping("/sendNoDogDiaryMessage")
    public ResponseEntity<String> sendNoDogDiaryMessage() {
        dingDingBotService.sendNoDogDiaryMessage();
        return ResponseEntity.ok("No Dog diary message sent manually.");
    }


    @GetMapping("/getPoisonousChickenSoup")
    public BaseResponse<PoisonousChickenSoupResponse> getPoisonousChickenSoup() throws ApiException {
        PoisonousChickenSoupResponse poisonousChickenSoup = null;
        try {
            poisonousChickenSoup = apiService.getPoisonousChickenSoup();
        } catch (ApiException e) {
            throw new ApiException(e.getCode(), e.getMessage());
        }
        return ResultUtils.success(poisonousChickenSoup);
    }

    @GetMapping("/loveTalk")
    public LoveResponse getLoveTalk() {
        LoveResponse loveResponse;
        try {
            loveResponse = apiService.randomLoveTalk();
        } catch (ApiException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        }
        return loveResponse;
    }

    @GetMapping("/getHoroscope")
    public ResponseEntity getHoroscope(HoroscopeParams horoscopeParams) throws ApiException {

        return ResponseEntity.ok(  dingDingBotService.sendTuMessage());
    }

}
