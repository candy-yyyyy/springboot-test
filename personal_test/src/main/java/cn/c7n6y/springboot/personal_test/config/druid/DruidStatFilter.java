package cn.c7n6y.springboot.personal_test.config.druid;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * @Author c7n6y
 * @Description druid监控页面
 * @Date 0:45 2021/5/10
 * @Param
 * @return
 **/
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",  
initParams={  
    @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源  
}  
)  
public class DruidStatFilter extends WebStatFilter{

}
