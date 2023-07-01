package cn.unbug.autosign.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.unbug.autosign.config.Constants;
import cn.unbug.autosign.config.SpringContextHolder;
import cn.unbug.autosign.config.exception.ServiceException;
import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.entity.SignReport;
import cn.unbug.autosign.service.SignReportService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: autosign
 * @Package: cn.unbug.autosign.utils
 * @ClassName: AutoSignUtils
 * @Author: Administrator
 * @Description: []
 * @Date: 2023/6/7 22:31
 */
@Slf4j
public class AutoSignUtils {

    private AutoSignUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * token url
     */
    private static final String TOKEN_URL = "http://sxbaapp.zcj.jyt.henan.gov.cn/interface/token.ashx";

    /**
     * 登录 url
     */
    private static final String LOGIN_URL = "http://sxbaapp.zcj.jyt.henan.gov.cn/interface/relog.ashx";

    /**
     * 签到 url
     */
    private static final String SIGN_URL = "http://sxbaapp.zcj.jyt.henan.gov.cn/interface/clockindaily20220827.ashx";

    /**
     * 汇报
     */
    private static final String REPORTING_INTERFACE = "https://sxbaapp.zcj.jyt.henan.gov.cn/interface/ReportHandler.ashx";

    /**
     * 请求头
     */
    private static final JSONObject headers;


    static {
        headers = new JSONObject();
        headers.put("os", "android");
        headers.put("appVersion", "40");
        headers.put("Sign", "Sign");
        headers.put("cl_ip", "192.168.1.3");
        headers.put("User-Agent", "okhttp/3.14.9");
        headers.put("Content-Type", "application/json;charset=utf-8");
    }

    /**
     * 获取token
     *
     * @return
     */
    private static String getToken(SignInUser signInUser) {
        log.info("=== AutoSignUtils getToken signInUser {}  ===", JSON.toJSONString(signInUser));
        JSONObject entries = new JSONObject(headers);
        //添加手机型号
        entries.put("phone", signInUser.getDeviceType());
        String body = HttpUtil.createPost(TOKEN_URL).headerMap(JSON.parseObject(entries.toString(), Map.class), true).execute().body();
        log.info("=== AutoSignUtils getToken body :{} ===", body);
        return body;
    }


    /**
     * 登录
     *
     * @return
     */
    public static JSONObject login(SignInUser signInUser) throws InterruptedException {
        // 获取token
        JSONObject tokenResult = JSON.parseObject(getToken(signInUser));
        if (Objects.nonNull(tokenResult) && tokenResult.getIntValue("code") == 1001) {
            JSONObject data = new JSONObject();
            data.put("phone", signInUser.getAccountNumber());
            data.put("password", MD5.create().digestHex(signInUser.getPassWord()));
            data.put("dtype", 6);
            data.put("dToken", signInUser.getDeviceId());
            log.info("=== AutoSignUtils login data :{} ===", data);
            JSONObject entries = new JSONObject(headers);
            entries.put("Sign", MD5.create().digestHex(JSON.toJSONString(data) + tokenResult.getJSONObject("data").getString("token")));
            TimeUnit.SECONDS.sleep(1);
            String result = HttpUtil.createPost(LOGIN_URL).headerMap(JSON.parseObject(entries.toString(), Map.class), true).body(data.toJSONString()).execute().body();
            log.info("=== AutoSignUtils login result :{} ===", result);
            JSONObject loginResult = new JSONObject();
            loginResult.put("loginResult", result);
            loginResult.put("token", tokenResult.getJSONObject("data").getString("token"));
            return loginResult;
        } else if (Objects.nonNull(tokenResult) && tokenResult.getIntValue("code") != 1001) {
            log.warn("=== AutoSignUtils login {}  ===", tokenResult.getString("msg"));
            throw new ServiceException(500, tokenResult.getString("msg"));
        } else {
            log.warn("=== AutoSignUtils login token获取异常！===");
            throw new ServiceException(500, "token获取异常！");
        }
    }

    /**
     * 打卡
     *
     * @param signInUser
     */
    public static boolean sign(SignInUser signInUser) throws InterruptedException {
        // 登录
        JSONObject login = login(signInUser);
        JSONObject loginResult = JSON.parseObject(login.getString("loginResult"));
        if (Objects.nonNull(loginResult) && loginResult.getIntValue("code") == 1001) {
            JSONObject data = new JSONObject();
            data.put("dtype", 1);
            data.put("probability", -1);
            data.put("address", signInUser.getAddress());
            data.put("longitude", signInUser.getLongitude());
            data.put("latitude", signInUser.getLatitude());
            data.put("phonetype", signInUser.getDeviceType());
            data.put("uid", loginResult.getJSONObject("data").getString("uid"));
            log.info("=== AutoSignUtils sign data :{} ===", data.toJSONString());
            JSONObject entries = new JSONObject(headers);
            entries.put("Sign", MD5.create().digestHex(JSON.toJSONString(data) + login.getString("token")));
            log.info("=== AutoSignUtils sign entries :{} ===", entries.toJSONString());
            TimeUnit.SECONDS.sleep(1);
            String result = HttpUtil.createPost(SIGN_URL).headerMap(JSON.parseObject(entries.toString(), Map.class), true).body(data.toJSONString()).execute().body();
            log.info("=== AutoSignUtils sign result : {} ===", result);
            JSONObject signResult = JSON.parseObject(result);
            if (Objects.nonNull(signResult) && signResult.getIntValue("code") == 1001) {
                return true;
            } else if (Objects.nonNull(signResult) && signResult.getIntValue("code") != 1001) {
                log.warn("=== AutoSignUtils sign {}  ===", signResult.getString("msg"));
                throw new ServiceException(500, loginResult.getString("msg"));
            } else {
                log.warn("=== AutoSignUtils sign 打卡异常！===");
                throw new ServiceException(500, "打卡异常！");
            }
        } else if (Objects.nonNull(loginResult) && loginResult.getIntValue("code") != 1001) {
            log.warn("=== AutoSignUtils sign {}  ===", loginResult.getString("msg"));
            throw new ServiceException(500, loginResult.getString("msg"));
        } else {
            log.warn("=== AutoSignUtils sign 登录异常！===");
            throw new ServiceException(500, "登录异常！");
        }
    }

    /**
     * 发送汇报
     *
     * @param signInUser 发送人
     * @param code       （0 日报 1 周报 2 月报）
     * @return
     */
    public static boolean sendReport(SignInUser signInUser, String code) throws NoSuchAlgorithmException, InterruptedException {
        // 登录
        JSONObject login = login(signInUser);
        JSONObject loginResult = JSON.parseObject(login.getString("loginResult"));
        if (Objects.nonNull(loginResult) && loginResult.getIntValue("code") == 1001) {
            SignReportService signReportService = SpringContextHolder.getBean(SignReportService.class);
            JSONObject data = new JSONObject();
            // 数据处理
            AutoSignUtils.dataProcessing(data, code);
            List<String> ids = signReportService.obtainTheIdList(signInUser.getSuperior(), code);
            if (CollectionUtils.isEmpty(ids)) {
                throw new ServiceException(500, "日报数据为空！");
            }
            // 随机值
            int index = SecureRandom.getInstanceStrong().nextInt(ids.size());
            SignReport signReport = signReportService.getById(ids.get(index));
            // 实习总结
            data.put("summary", signReport.getSummary());
            // 实习记录
            data.put("record", signReport.getRecord());
            // 实习地点
            data.put("address", signInUser.getAddress());
            // 实习单位
            data.put("project", "毕业实习");
            data.put("uid", loginResult.getJSONObject("data").getString("uid"));
            log.info("=== AutoSignUtils sendReport data :{} ===", data.toJSONString());
            JSONObject entries = new JSONObject(headers);
            entries.put("Sign", MD5.create().digestHex(JSON.toJSONString(data) + login.getString("token")));
            log.info("=== AutoSignUtils sendReport entries :{} ===", entries.toJSONString());
            TimeUnit.SECONDS.sleep(1);
            String result = HttpUtil.createPost(REPORTING_INTERFACE).headerMap(JSON.parseObject(entries.toString(), Map.class), true).body(data.toJSONString()).execute().body();
            log.info("=== AutoSignUtils sendReport result : {} ===", result);
            JSONObject signResult = JSON.parseObject(result);
            if (Objects.nonNull(signResult) && signResult.getIntValue("code") == 1001) {
                if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                    MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, signInUser.getAccountNumber() + ": 汇报成功！");
                }
                return true;
            } else if (Objects.nonNull(signResult) && signResult.getIntValue("code") != 1001) {
                log.warn("=== AutoSignUtils sendReport {}  ===", signResult.getString("msg"));
                if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                    MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, loginResult.getString("msg"));
                }
                throw new ServiceException(500, loginResult.getString("msg"));
            } else {
                log.warn("=== AutoSignUtils sendReport 汇报异常！===");
                if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                    MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, signInUser.getAccountNumber() + ": 汇报异常！");
                }
                throw new ServiceException(500, "汇报异常！");
            }
        } else if (Objects.nonNull(loginResult) && loginResult.getIntValue("code") != 1001) {
            log.warn("=== AutoSignUtils sendReport {}  ===", loginResult.getString("msg"));
            if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, loginResult.getString("msg"));
            }
            throw new ServiceException(500, loginResult.getString("msg"));
        } else {
            log.warn("=== AutoSignUtils sendReport 登录异常！===");
            if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, signInUser.getAccountNumber() + ": 登录异常！");
            }
            throw new ServiceException(500, "登录异常！");
        }
    }

    /**
     * 数据处理
     *
     * @param data
     * @param code
     */
    private static void dataProcessing(JSONObject data, String code) {
        if (StringUtils.equals(code, "0")) {
            data.put("starttime", DateUtil.formatDate(new Date()));
            data.put("dtype", "1");
        } else if (StringUtils.equals(code, "1")) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            // 周日
            data.put("starttime", DateUtil.formatDate(cal.getTime()));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            // 周六
            data.put("endtime", DateUtil.formatDate(cal.getTime()));
            data.put("stype", "2");
            data.put("dtype", "2");
        } else if (StringUtils.equals(code, "2")) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            // 周日
            data.put("starttime", DateUtil.formatDate(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DATE, -1);
            // 周六
            data.put("endtime", DateUtil.formatDate(cal.getTime()));
            data.put("stype", "3");
            data.put("dtype", "2");
        } else {
            log.error("=== AutoAutoSignUtils dataProcessing exception code 值非法！===");
            throw new ServiceException("code 值非法！");
        }
    }
}
