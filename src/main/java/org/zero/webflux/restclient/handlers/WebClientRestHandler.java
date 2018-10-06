package org.zero.webflux.restclient.handlers;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import org.zero.webflux.restclient.beans.MethodInfo;
import org.zero.webflux.restclient.beans.ServerInfo;
import org.zero.webflux.restclient.interfaces.RestHandler;
import reactor.core.publisher.Mono;

/**
 * @program: rest-client
 * @description: 使用webclient调用rest接口
 * @author: 01
 * @create: 2018-10-06 13:07
 **/
public class WebClientRestHandler implements RestHandler {
    private WebClient webClient;

    @Override
    public void init(ServerInfo serverInfo) {
        this.webClient = WebClient.create(serverInfo.getUrl());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invokeRest(MethodInfo methodInfo) {
        // 存储返回结果
        Object result;

        RequestBodySpec request =
                // 请求的方法
                webClient.method(methodInfo.getMethod())
                        // 请求的url
                        .uri(methodInfo.getUrl(), methodInfo.getParams())
                        // 允许的数据
                        .accept(MediaType.APPLICATION_JSON_UTF8);

        // rest接口返回的数据
        ResponseSpec retrieve;

        // 是否是有body的请求
        if (methodInfo.getBody() != null) {
            // 添加body并发出请求
            retrieve = request.body(methodInfo.getBody(), methodInfo.getBodyElementType()).retrieve();
        } else {
            retrieve = request.retrieve();
        }

        // 处理返回的数据
        if (methodInfo.isReturnFlux()) {
            result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
        } else {
            result = retrieve.bodyToMono(methodInfo.getReturnElementType());
        }

        return result;
    }
}
