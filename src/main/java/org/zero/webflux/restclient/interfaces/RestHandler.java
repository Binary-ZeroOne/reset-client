package org.zero.webflux.restclient.interfaces;

import org.zero.webflux.restclient.beans.MethodInfo;
import org.zero.webflux.restclient.beans.ServerInfo;

/**
 * @program: rest-client
 * @description: rest请求调用handler
 * @author: 01
 * @create: 2018-10-06 11:30
 **/
public interface RestHandler {

    /**
     * 初始化服务器信息（初始化webclient）
     *
     * @param serverInfo serverInfo
     */
    void init(ServerInfo serverInfo);

    /**
     * 调用rest请求，返回结果数据
     *
     * @param methodInfo methodInfo
     * @return Object
     */
    Object invokeRest(MethodInfo methodInfo);
}
