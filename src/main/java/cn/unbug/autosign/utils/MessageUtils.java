package cn.unbug.autosign.utils;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.unbug.autosign.config.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.utils
 * @ClassName: MessageUtils
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/5/31 0:10
 */
@Slf4j
public class MessageUtils {

    /**
     * 手机消息推送
     *
     * @param token
     * @param title
     * @param content
     * @return
     */
    public static Boolean sendMsg(String token, String title, String content) {
        log.info("=== MessageUtils sendMsg token :{} title :{} content :{} ===", token, title, content);
        String url = String.format(Constants.MESSAGE_URL, token, title, content);
        String body = HttpUtil.get(url);
        if (StringUtils.isNotBlank(body)) {
            JSONObject result = JSON.parseObject(body);
            if (result.getIntValue("code") == HttpStatus.HTTP_OK) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
