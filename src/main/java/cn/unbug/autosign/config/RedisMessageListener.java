package cn.unbug.autosign.config;

import cn.unbug.autosign.entity.SignHistory;
import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.service.SignHistoryService;
import cn.unbug.autosign.service.SignInUserService;
import cn.unbug.autosign.utils.AutoSignUtils;
import cn.unbug.autosign.utils.MessageUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ProjectName: autosign
 * @Package: cn.unbug.autosign.config
 * @ClassName: RedisMessageListener
 * @Author: Administrator
 * @Description: []
 * @Date: 2023/6/7 9:15
 */
@Slf4j
@Component
public class RedisMessageListener extends KeyExpirationEventMessageListener {

    private SignInUserService signInUserService;

    private SignHistoryService signHistoryService;

    public RedisMessageListener(RedisMessageListenerContainer listenerContainer, SignInUserService signInUserService, SignHistoryService signHistoryService) {
        super(listenerContainer);
        this.signInUserService = signInUserService;
        this.signHistoryService = signHistoryService;
    }

    @Override
    protected void doHandleMessage(Message message) {
        String expiredKey = Objects.nonNull(message) ? String.valueOf(message) : StringUtils.EMPTY;
        log.info("=== RedisMessageListener doHandleMessage {} ===", expiredKey);
        if (StringUtils.isNotBlank(expiredKey) && expiredKey.startsWith(Constants.SIGN_USER_GROUP)) {
            String signId = expiredKey.replace(Constants.SIGN_USER_GROUP, StringUtils.EMPTY);
            SignInUser signInUser = signInUserService.getById(signId);
            log.info("=== RedisMessageListener doHandleMessage  :{} ===", JSON.toJSONString(signInUser));
            try {
                boolean sign = AutoSignUtils.sign(signInUser);
                log.info("=== RedisMessageListener doHandleMessage sign :{} ===", sign);
                if (sign) {
                    SignHistory signHistory = new SignHistory();
                    signHistory.setSignId(signInUser.getId());
                    signHistory.setCheckInStatus(Constants.STATUS_0);
                    signHistory.setCheckInContent("打卡成功！");
                    signHistoryService.save(signHistory);
                    if (StringUtils.isNotBlank(signInUser.getPushToken()) && StringUtils.equals(String.valueOf(signInUser.getWxPush()), Constants.STATUS_0)) {
                        MessageUtils.sendMsg(signInUser.getPushToken(), "打卡通知", signInUser.getAccountNumber()+": 打卡成功,待老师审核！");
                    }
                }
            } catch (Exception e) {
                SignHistory signHistory = new SignHistory();
                signHistory.setSignId(signInUser.getId());
                signHistory.setCheckInStatus(Constants.STATUS_1);
                signHistory.setCheckInContent(e.getMessage());
                signHistoryService.save(signHistory);
                if (StringUtils.isNotBlank(signInUser.getPushToken()) && StringUtils.equals(String.valueOf(signInUser.getWxPush()), Constants.STATUS_0)) {
                    MessageUtils.sendMsg(signInUser.getPushToken(), "打卡通知", signInUser.getAccountNumber()+": 打卡失败！");
                }
            }
        }
    }
}
