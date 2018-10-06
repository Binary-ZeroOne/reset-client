package org.zero.webflux.restclient.interfaces;

/**
 * @program: rest-client
 * @description: 创建代理类接口
 * @author: 01
 * @create: 2018-10-06 11:15
 **/
public interface ProxyCreator {

    /**
     * 创建代理类
     *
     * @param type 类型
     * @return 代理类实例
     */
    Object createProxy(Class<?> type);
}
