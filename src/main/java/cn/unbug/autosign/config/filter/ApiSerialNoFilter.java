package cn.unbug.autosign.config.filter;


import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @ProjectName: InformationSystem
 * @Package: cn.unbug.autosign.config.filter
 * @ClassName: ApiSeriaLog
 * @Author: zhangtao
 * @Description: []
 * @Date: 2021/12/11 17:44
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "ApiSerialNoFilter")
public class ApiSerialNoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String apiSerialNo = IdUtil.fastSimpleUUID();
        MDC.put("apiSerialNo", apiSerialNo);
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("=== ApiSerialNoFilter init===");
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        log.info("=== ApiSerialNoFilter destroy===");
        Filter.super.destroy();
    }
}
