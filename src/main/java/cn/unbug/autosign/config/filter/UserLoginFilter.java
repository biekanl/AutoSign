package cn.unbug.autosign.config.filter;

import cn.unbug.autosign.config.*;
import cn.unbug.autosign.config.redis.RedisUtils;
import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.mapper.SysUserMapper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.config.filter
 * @ClassName: UserLoginFilter
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/5/30 20:59
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "UserLoginFilter")
public class UserLoginFilter implements Filter {

    private RedisUtils redisUtils;

    private SysUserMapper sysUserMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        //放行登录
        if (StringUtils.equals(Constants.SYS_LOGIN_URL, httpServletRequest.getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String token = httpServletRequest.getHeader(Constants.SYS_TOKEN);
        if (StringUtils.isBlank(token) || Objects.isNull(redisUtils.get(Constants.SYS_USER_GROUP + token))) {
            AjaxResult error = AjaxResult.error(403, "会话已过期，请重新登录！");
            // 请求域名
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            // 请求方法
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            // 允许的请求头
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
            // 浏览器默认会发起异常 OPTIONS 的请求方式 这个时候我们通过过滤器直接拦截返回200后就可以解决跨越问题
            if ("OPTIONS" == (httpServletRequest.getMethod())) {
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            }
            ServletUtils.renderString(httpServletResponse, 200, JSON.toJSONString(error));
        } else {
            SysUser loginUser = JSON.parseObject(JSON.toJSONString(redisUtils.get(Constants.SYS_USER_GROUP + token)), SysUser.class);
            SysUser sysUser = sysUserMapper.selectById(loginUser.getId());
            if (Objects.isNull(loginUser)) {
                AjaxResult error = AjaxResult.error(403, "用户信息已删除！");
                ServletUtils.renderString(httpServletResponse, 200, JSON.toJSONString(error));
                return;
            }
            if (StringUtils.equals(sysUser.getStatus(), Constants.STATUS_1)) {
                AjaxResult error = AjaxResult.error(403, "用户已被锁定！");
                ServletUtils.renderString(httpServletResponse, 200, JSON.toJSONString(error));
                return;
            }
            // token 续时
            redisUtils.set(Constants.SYS_USER_GROUP + token, sysUser, Constants.MILLIS_MINUTE_TEN);
            ThreadLocalUtils.set(Constants.SYS_USER, sysUser);
            filterChain.doFilter(servletRequest, servletResponse);
            ThreadLocalUtils.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        redisUtils = SpringContextHolder.getBean(RedisUtils.class);
        sysUserMapper = SpringContextHolder.getBean(SysUserMapper.class);
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
