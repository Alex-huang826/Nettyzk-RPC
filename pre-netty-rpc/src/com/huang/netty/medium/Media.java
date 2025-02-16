package com.huang.netty.medium;

import com.alibaba.fastjson.JSONObject;
import com.huang.netty.handler.param.ServerRequest;
import com.huang.netty.util.Response;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {
    public static Map<String, BeanMethod>beanMap;//存储业务方法与 Bean 实例的映射关系。
    static {
        beanMap = new HashMap<String,BeanMethod>();//静态初始化块在类加载时执行，保证 beanMap 在第一次使用前被初始化。
    }
    private static Media m = null;// 静态变量，存储 Media 的唯一实例。
    private Media(){

    }
    public static Media newInstance(){//提供全局唯一访问 Media 实例的方法。
        if(m==null){
            m = new Media();//只有第一次调用时才会创建实例。
        }
        return m;
    }
    //反射处理业务代码
    public Response process(ServerRequest request){
        Response result = null;
        try {
            String command = request.getCommand();// 获取请求中的命令
            BeanMethod beanMethod = beanMap.get(command);// 从 beanMap 查找对应的 BeanMethod
            if(beanMethod==null){
                return null;// 如果未找到对应的方法，返回 null
            }
            Object bean = beanMethod.getBean();// 获取与方法关联的 Bean 实例
            Method m = beanMethod.getMethod();// 获取方法对象
            Class<?> paramType = m.getParameterTypes()[0];// 获取方法的第一个参数类型
            Object content = request.getContent();// 从请求中提取内容
            Object args = JSONObject.parseObject(JSONObject.toJSONString(content),paramType);// 将请求内容解析为目标参数类型
            result = (Response) m.invoke(bean,args);// 使用反射调用方法，传入解析后的参数
            result.setId(request.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
