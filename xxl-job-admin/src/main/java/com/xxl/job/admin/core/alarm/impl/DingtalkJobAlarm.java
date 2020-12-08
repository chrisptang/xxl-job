package com.xxl.job.admin.core.alarm.impl;

import com.dianping.cat.Cat;
import com.google.gson.JsonObject;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import com.leqee.boot.autoconfiguration.common.SupportedEnv;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author: derrick.tangp
 * 新添钉钉告警；
 */
@Component
public class DingtalkJobAlarm implements JobAlarm {

    @Autowired
    private RestTemplate restTemplate;

    private static final String DINGTALK_BOT_URL = System.getProperty("dingtalk.bot.url", "");

    private static final String DINGTALK_MSG_JSON_TPL = "{\"msgtype\": \"text\",\"text\": {\"content\": \"【定时任务告警】%s\"}}";

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (StringUtils.isEmpty(DINGTALK_BOT_URL)) {
            Cat.logError("Unable to find property 'dingtalk.bot.url' in System.getProperty:\n"
                    + System.getProperties().toString(), new Exception());

            return false;
        }
        SupportedEnv supportedEnv = EnvUtil.getSupportedEnv();

        // alarmContent
        String alarmContent = "Alarm Job LogId=" + jobLog.getId();
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += ("\nTriggerMsg=" + jobLog.getTriggerMsg());
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += ("\nHandleCode=" + jobLog.getHandleMsg());
        }
        alarmContent += ("\n\n环境：" + supportedEnv);

        alarmContent = String.format(DINGTALK_MSG_JSON_TPL, alarmContent);

        JsonObject responseJson = restTemplate.postForObject(DINGTALK_BOT_URL, alarmContent, JsonObject.class);
        if (null != responseJson && responseJson.has("errcode")) {
            return responseJson.get("errcode").getAsInt() == 0;
        }
        Cat.logError("Unable to send dingtalk message:" + alarmContent + ",\nresponse:" + responseJson
                , new Exception());
        return false;
    }
}
