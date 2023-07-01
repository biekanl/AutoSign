package cn.unbug.autosign.config;

import cn.unbug.autosign.entity.SysUser;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @ProjectName: informationsystem
 * @Package: cn.unbug.autosign.config.mybatis
 * @ClassName: BaseHandler
 * @Author: zhangtao
 * @Description: []
 * @Date: 2022/5/4 5:00
 */
@Slf4j
@Component
@AllArgsConstructor
public class TableMateHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        //当前登录用户
        SysUser sysUser = ThreadLocalUtils.get(Constants.SYS_USER);
        if (Objects.nonNull(sysUser) && StringUtils.isNotBlank(sysUser.getPhone())) {
            this.setFieldValByName("createdBy", sysUser.getPhone(), metaObject);
            this.setFieldValByName("updatedBy", sysUser.getPhone(), metaObject);
        }
        this.setFieldValByName("createdTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updatedTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //当前登录用户
        SysUser sysUser = ThreadLocalUtils.get(Constants.SYS_USER);
        if (Objects.nonNull(sysUser) && StringUtils.isNotBlank(sysUser.getPhone())) {
            this.setFieldValByName("updatedBy", sysUser.getPhone(), metaObject);
        }
        this.setFieldValByName("updatedTime", LocalDateTime.now(), metaObject);
    }
}
