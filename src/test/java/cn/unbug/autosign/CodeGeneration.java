package cn.unbug.autosign;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动生成类
 *
 * @author zhangtao
 * @date 2020/06/13
 */
public class CodeGeneration {

    //main函数
    public static void main(String[] args) {
        AutoGenerator autoGenerator = new AutoGenerator();

        //全局配置
        GlobalConfig gc = new GlobalConfig();
        //得到当前项目的路径
        String path = System.getProperty("user.dir");
        //gc.setOutputDir(path + "/src/main/java");   //生成文件输出 根目录
        //生成完成后不弹出文件框
        gc.setOpen(false);
        //文件覆盖
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);
        // 作者
        gc.setAuthor("zhangtao");

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setControllerName("%sController");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        //设置主键生成策略
        gc.setIdType(IdType.ASSIGN_ID);
        //baseResultMap
        gc.setBaseResultMap(true);
        //二级缓存
        gc.setEnableCache(true);
        autoGenerator.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);   //设置数据库类型
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("zhangtao");
        dsc.setPassword("Tao_200967");
        dsc.setUrl("jdbc:mysql://rm-bp11d61s1d01e7c03io.mysql.rds.aliyuncs.com:3306/autosign?useUnicode=true&characterEncoding=utf-8&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true");  //指定数据库
        autoGenerator.setDataSource(dsc);

        //自动填充字段
        List<TableFill> tableFills = new ArrayList<>();
        //创建人
        tableFills.add(new TableFill("CREATED_BY", FieldFill.INSERT));
        //创建时间
        tableFills.add(new TableFill("CREATED_TIME", FieldFill.INSERT));
        //更新人
        tableFills.add(new TableFill("UPDATED_BY", FieldFill.INSERT_UPDATE));
        //更新时间
        tableFills.add(new TableFill("UPDATED_TIME", FieldFill.INSERT_UPDATE));
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(new String[]{"sign_report"});
        // 自动lombok
        strategy.setEntityLombokModel(true);
        //自动填充
        strategy.setTableFillList(tableFills);
        //逻辑删除标识字段
        //strategy.setLogicDeleteFieldName("DEL_FLAG");
        // 乐观锁
        //strategy.setVersionFieldName("REVISION");
        //rest风格
        strategy.setRestControllerStyle(true);
        //链式编程
        strategy.setEntityLombokModel(true);
        //设置父类
        //strategy.setSuperEntityClass(BaseEntity.class);
        autoGenerator.setStrategy(strategy);

        //模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setController("/templates/controller.java");
        templateConfig.setEntity("/templates/entity.java");
        templateConfig.setService("/templates/service.java");
        templateConfig.setServiceImpl("/templates/serviceImpl.java");
        templateConfig.setMapper("/templates/mapper.java");
        templateConfig.setXml("/templates/mapper.xml");
        autoGenerator.setTemplate(templateConfig);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("cn");
        pc.setModuleName("unbug.autosign");
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("mapper");
        pc.setXml("mapper");
        pc.setEntity("entity");

        //文件路径
        //指定生成文件的绝对路径
        Map<String, String> pathInfo = new HashMap<>();
        String parentPath = "\\src\\main\\java\\cn\\unbug\\autosign";
        String entityPath = path.concat(parentPath).concat("\\entity");
        String mapper_path = path.concat(parentPath).concat("\\mapper");
        String xml_path = path.concat("\\src\\main").concat("\\resources\\mapper");
        String service_path = path.concat(parentPath).concat("\\service");
        String service_impl_path = path.concat(parentPath).concat("\\service").concat("\\impl");
        String controller_path = path.concat(parentPath).concat("\\controller");

        pathInfo.put("entity_path", entityPath);
        pathInfo.put("mapper_path", mapper_path);
        pathInfo.put("xml_path", xml_path);
        pathInfo.put("service_path", service_path);
        pathInfo.put("service_impl_path", service_impl_path);
        pathInfo.put("controller_path", controller_path);

        pc.setPathInfo(pathInfo);

        autoGenerator.setPackageInfo(pc);

        // 执行生成
        autoGenerator.execute();
        System.out.println("-----end-----");
    }
}
