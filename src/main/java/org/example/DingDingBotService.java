package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.qimuu.qiapisdk.exception.ApiException;
import icu.qimuu.qiapisdk.model.params.HoroscopeParams;
import icu.qimuu.qiapisdk.model.request.HoroscopeRequest;
import icu.qimuu.qiapisdk.model.response.ResultResponse;
import icu.qimuu.qiapisdk.service.ApiService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DingDingBotService {

    @Resource
    private ApiService apiService;


    @Value("${dingding.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    public DingDingBotService() {
        this.restTemplate = new RestTemplate();
    }


    public void sendImageMessage() {
        String imageUrl1 = fetchImageUrl("https://dayu.qqsuu.cn/weiyujianbao/apis.php?type=json");

        String markdownMsg = "## Have a good day!\n\n" +
            "![screenshot1](" + imageUrl1 + ")\n";
        String title = "Combined Images";

        String markdownText = "{\"msgtype\":\"markdown\",\"markdown\":{\"title\":\"" + title + "\",\"text\":\"" + markdownMsg + "\"}}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(markdownText, headers);

        restTemplate.exchange(webhookUrl, HttpMethod.POST, request, String.class);
    }
//
//    public void sendCombinedImageMessage() {
//        String imageUrl1 = fetchImageUrl("https://dayu.qqsuu.cn/weiyujianbao/apis.php?type=json");
//        String imageUrl2 = fetchImageUrl("https://dayu.qqsuu.cn/qingganhuayuan/apis.php?type=json");
//
//        String markdownMsg = "![screenshot1](" + imageUrl1 + ")\n\n![screenshot2](" + imageUrl2 + ")";
//        String title = "Combined Images";
//
//        String markdownText = "{\"msgtype\":\"markdown\",\"markdown\":{\"title\":\"" + title + "\",\"text\":\"" + markdownMsg + "\"}}";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(markdownText, headers);
//
//        restTemplate.exchange(webhookUrl, HttpMethod.POST, request, String.class);
//    }

    public String fetchImageUrl(String apiUrl) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            // 解析JSON响应，提取图片URL
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("data")) {
                    return jsonNode.get("data").asText();
                } else {
                    throw new RuntimeException("Response does not contain 'data' field");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse JSON response", e);
            }
        } else {
            throw new RuntimeException("Failed to fetch image URL, status code: " + responseEntity.getStatusCodeValue());
        }
    }


    public void sendTextMessage(String message) {
        Map<String, Object> text = new HashMap<>();
        text.put("content", message+".");

        Map<String, Object> request = new HashMap<>();
        request.put("msgtype", "text");
        request.put("text", text);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }

    public void sendMarkdownMessage(String title, String message, String atMobile, String atUserId) {
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("title", title);
        markdown.put("text", message + " @" + atMobile);

        Map<String, Object> request = new HashMap<>();
        request.put("msgtype", "markdown");
        request.put("markdown", markdown);

        Map<String, Object> at = new HashMap<>();
        at.put("atMobiles", new String[]{atMobile});
        at.put("atUserIds", new String[]{atUserId});
        at.put("isAtAll", false);

        request.put("at", at);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }

    public String sendDogDiaryMessage() {
        String dogDiary =  "舔狗日记." + getCurrentDate() + "\n\n" +fetchDogDiary("https://api.gumengya.com/Api/Dog?format=json");
        sendTextMessage(dogDiary);
        return dogDiary;
    }


//
    public String sendTuMessage() throws ApiException {

        HoroscopeRequest horoscopeRequest = new HoroscopeRequest();
        HoroscopeParams horoscopeParams =new HoroscopeParams();
        horoscopeParams.setTime("today");
        horoscopeParams.setType("pisces");

        horoscopeRequest.setRequestParams(horoscopeParams);

        ResultResponse poisonousChickenSoup = apiService.horoscope(horoscopeRequest);
            System.out.println("poisonousChickenSoup = " + poisonousChickenSoup);


        String result = getFormattedHoroscope(poisonousChickenSoup.getData());
        String title =  "xz";
        sendMarkdownMessage(title, result, "15683991079", "王志深");
        return   result;
    }


    public String getFormattedHoroscope(Map<String, Object> horoscopeData) {
        StringBuilder sb = new StringBuilder();

        String title = (String) horoscopeData.get("title");
        String type = (String) horoscopeData.get("type");
        String date = (String) horoscopeData.get("time");
        String shortComment = (String) horoscopeData.get("shortcomment");
        String luckyColor = (String) horoscopeData.get("luckycolor");
        String luckyNumber = (String) horoscopeData.get("luckynumber");
        String luckyConstellation = (String) horoscopeData.get("luckyconstellation");
        Map<String, String> todo = (Map<String, String>) horoscopeData.get("todo");
        Map<String, String> fortunetext = (Map<String, String>) horoscopeData.get("fortunetext");
        Map<String, Integer> fortune = (Map<String, Integer>) horoscopeData.get("fortune");
        Map<String, String> index = (Map<String, String>) horoscopeData.get("index");

        sb.append("## ").append(title).append(" ").append(type).append("\n\n");
        sb.append("**日期**: ").append(date).append("\n\n");
        sb.append("**简评**: ").append(shortComment).append("\n\n");

        sb.append("### 今日宜忌\n");
        sb.append("- 宜: ").append(todo.getOrDefault("yi", "暂无数据")).append("\n");
        sb.append("- 忌: ").append(todo.getOrDefault("ji", "暂无数据")).append("\n\n");

        sb.append("### 整体运势\n");
        sb.append(fortunetext.getOrDefault("all", "暂无数据")).append("\n\n");

        sb.append("### 爱情运势\n");
        sb.append(fortunetext.getOrDefault("love", "暂无数据")).append("\n\n");

        sb.append("### 工作运势\n");
        sb.append(fortunetext.getOrDefault("work", "暂无数据")).append("\n\n");

        sb.append("### 财运运势\n");
        sb.append(fortunetext.getOrDefault("money", "暂无数据")).append("\n\n");

        sb.append("### 健康运势\n");
        sb.append(fortunetext.getOrDefault("health", "暂无数据")).append("\n\n");

        sb.append("### 运势评分\n");
        sb.append("- 整体: ").append(fortune.getOrDefault("all", 0)).append(" 星").append("\n");
        sb.append("- 爱情: ").append(fortune.getOrDefault("love", 0)).append(" 星").append("\n");
        sb.append("- 工作: ").append(fortune.getOrDefault("work", 0)).append(" 星").append("\n");
        sb.append("- 财运: ").append(fortune.getOrDefault("money", 0)).append(" 星").append("\n");
        sb.append("- 健康: ").append(fortune.getOrDefault("health", 0)).append(" 星").append("\n\n");

        sb.append("### 幸运指数\n");
        sb.append("- 整体: ").append(index.getOrDefault("all", "暂无数据")).append("\n");
        sb.append("- 爱情: ").append(index.getOrDefault("love", "暂无数据")).append("\n");
        sb.append("- 工作: ").append(index.getOrDefault("work", "暂无数据")).append("\n");
        sb.append("- 财运: ").append(index.getOrDefault("money", "暂无数据")).append("\n");
        sb.append("- 健康: ").append(index.getOrDefault("health", "暂无数据")).append("\n\n");

        sb.append("### 幸运色和幸运数字\n");
        sb.append("- 幸运色: ").append(luckyColor).append("\n");
        sb.append("- 幸运数字: ").append(luckyNumber).append("\n\n");

        sb.append("### 幸运星座\n");
        sb.append("- 幸运星座: ").append(luckyConstellation).append("\n");

        return sb.toString();
    }

    private String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月d日");
        return currentDate.format(formatter);
    }

    public void sendNoDogDiaryMessage() {
        String noDogDiary = fetchDogDiary("https://api.gumengya.com/Api/YiYan?format=json");
        sendTextMessage(noDogDiary);
    }


    private String fetchDogDiary(String apiUrl) {
        String dogDiary = "";
        try {
            // Fetch dog diary text from the specified API
            String response = restTemplate.getForObject(apiUrl, String.class);
            // Parse JSON response to extract dog diary text
            dogDiary = parseDogDiaryFromJson(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(dogDiary);
        return dogDiary;
    }

    private String parseDogDiaryFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            // Extract the text of the dog diary from the JSON response
            JsonNode dataNode = jsonNode.get("data");
            if (dataNode != null) {
                JsonNode textNode = dataNode.get("text");
                if (textNode != null) {
                    return textNode.asText();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
