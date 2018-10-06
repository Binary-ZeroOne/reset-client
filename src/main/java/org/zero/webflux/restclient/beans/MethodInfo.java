package org.zero.webflux.restclient.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @program: rest-client
 * @description: 方法调用信息类
 * @author: 01
 * @create: 2018-10-06 11:25
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {
    /**
     * 接口url
     */
    private String url;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 请求参数（url）
     */
    private Map<String, Object> params;

    /**
     * 请求的body
     */
    private Mono body;

    /**
     * 请求的body类型
     */
    private Class<?> bodyElementType;

    /**
     * 返回的是flux还是mono
     */
    private boolean returnFlux;

    /**
     * 返回对象的类型
     */
    private Class<?> returnElementType;
}
