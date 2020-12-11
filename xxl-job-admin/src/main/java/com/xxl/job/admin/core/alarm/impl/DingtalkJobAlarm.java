package com.xxl.job.admin.core.alarm.impl;

import com.dianping.cat.Cat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import com.leqee.boot.autoconfiguration.common.SupportedEnv;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: derrick.tangp
 * 新添钉钉告警；
 */
@Component
public class DingtalkJobAlarm implements JobAlarm, SmartInitializingSingleton {

    private static final Gson GSON = new Gson();

    @Autowired
    private RestTemplate restTemplate;

    private static final String DINGTALK_BOT_URL = System.getProperty("dingtalk.bot.url", "");

    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();

    static {
        HTTP_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (StringUtils.isEmpty(DINGTALK_BOT_URL)) {
            Cat.logError("Unable to find property 'dingtalk.bot.url' in System.getProperty:\n"
                    + System.getProperties().toString(), new Exception());

            return false;
        }
        SupportedEnv supportedEnv = EnvUtil.getSupportedEnv();

        // alarmContent
        String alarmContent = String.format("定时任务失败：%d:%s", info.getId(), info.getJobDesc());
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += ("\nTriggerMsg=" + jobLog.getTriggerMsg());
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += ("\nHandleCode=" + jobLog.getHandleMsg());
        }
        alarmContent += ("\n\n环境：" + supportedEnv);

        alarmContent = new DingtalkContent(alarmContent).toString();

        HttpEntity<String> entity = new HttpEntity<>(alarmContent, HTTP_HEADERS);
        String response = restTemplate.postForObject(DINGTALK_BOT_URL, entity, String.class);

        JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
        if (null != responseJson && responseJson.has("errcode")) {
            return responseJson.get("errcode").getAsInt() == 0;
        }
        Cat.logError("Unable to send dingtalk message:" + alarmContent + ",\nresponse:" + response
                , new Exception());
        return false;
    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    private static final String MSG_FORMATTER = "【定时任务告警】%s";

    public static class DingtalkContent {
        private final String msgtype = "text";
        private final Map<String, String> text = new HashMap<>();

        public DingtalkContent(String msg) {
            msg = msg.replaceAll("<br>", "\n");
            msg = msg.replaceAll("<[^>]*>", "");
            text.put("content", String.format(MSG_FORMATTER, msg));
        }

        public String getMsgtype() {
            return msgtype;
        }

        public Map<String, String> getText() {
            return text;
        }

        @Override
        public String toString() {
            return GSON.toJson(this);
        }
    }
}
