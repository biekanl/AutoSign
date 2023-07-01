package cn.unbug.autosign.entity.vo;

import cn.unbug.autosign.entity.SignInUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.entity.vo
 * @ClassName: SignInUserVo
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/6/10 23:43
 */
@Data
public class SignInUserVo extends SignInUser {

    /**
     * 打卡周期
     */
    @NotEmpty(message = "打卡周期不可为空！")
    private List<String> punchCycles;

    /**
     * 最近一次打卡时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime latestTime;
}
