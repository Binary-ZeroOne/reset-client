package org.zero.webflux.restclient.proxys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.zero.webflux.restclient.annotations.ApiServer;
import org.zero.webflux.restclient.beans.MethodInfo;
import org.zero.webflux.restclient.beans.ServerInfo;
import org.zero.webflux.restclient.handlers.WebClientRestHandler;
import org.zero.webflux.restclient.interfaces.ProxyCreator;
import org.zero.webflux.restclient.interfaces.RestHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: rest-client
 * @description: 使用JDK动态代理创建代理类
 * @author: 01
 * @create: 2018-10-06 11:19
 **/
@Slf4j
public class JdkProxyCreator implements ProxyCreator {

    @Override
    public Object createProxy(Class<?> type) {
        log.info("createProxy: {}", type);
        // 根据接口得到API服务器信息
        ServerInfo serverInfo = extractServerInfo(type);
        log.info("serverInfo: {}", serverInfo);

        // 给每一个代理类一个实现
        RestHandler handler = new WebClientRestHandler();
        // 初始化服务器信息
        handler.init(serverInfo);

        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            // 根据方法和参数得到调用信息
            MethodInfo methodInfo = extractMethodInfo(method, args);
            log.info("methodInfo: {}", methodInfo);
            // 调用rest
            return handler.invokeRest(methodInfo);
        });
    }

    /**
     * 根据方法定义和调用参数得到调用的相关信息
     *
     * @param method 方法
     * @param args   参数
     * @return MethodInfo
     */
    private MethodInfo extractMethodInfo(Method method, Object[] args) {
        MethodInfo methodInfo = MethodInfo.builder().build();

        extractUrlAndMethod(method, methodInfo);
        extractRequestParamAndBody(method, args, methodInfo);
        extractReturnInfo(method, methodInfo);

        return methodInfo;
    }

    /**
     * 提取返回对象信息
     *
     * @param method     method
     * @param methodInfo methodInfo
     */
    private void extractReturnInfo(Method method, MethodInfo methodInfo) {
        // 返回flux还是mono
        // 这里没有使用instanceof的原因是isAssignableFrom判断类型是否为某个类的子类
        // 而instanceof则是用于判断实例是否为某个类的子类
        boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
        methodInfo.setReturnFlux(isFlux);

        // 得到返回对象的实际类型，getGenericReturnType可以获取泛型信息
        Class<?> elementType = extractElementType(method.getGenericReturnType());
        methodInfo.setReturnElementType(elementType);
    }

    /**
     * 得到返回对象的实际类型
     *
     * @param genericReturnType genericReturnType
     * @return 对象的实际类型
     */
    private Class<?> extractElementType(Type genericReturnType) {
        Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();

        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * 提取服务器信息
     *
     * @param type type
     * @return ServerInfo
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ApiServer apiServer = type.getAnnotation(ApiServer.class);

        return ServerInfo.builder().url(apiServer.value()).build();
    }

    /**
     * 得到请求的url和请求方法
     *
     * @param method     method
     * @param methodInfo methodInfo
     */
    private void extractUrlAndMethod(Method method, MethodInfo methodInfo) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof GetMapping) {
                GetMapping getMapping = (GetMapping) annotation;
                methodInfo.setUrl(getMapping.value()[0]);
                methodInfo.setMethod(HttpMethod.GET);
            } else if (annotation instanceof PostMapping) {
                PostMapping postMapping = (PostMapping) annotation;
                methodInfo.setUrl(postMapping.value()[0]);
                methodInfo.setMethod(HttpMethod.POST);
            } else if (annotation instanceof PutMapping) {
                PutMapping putMapping = (PutMapping) annotation;
                methodInfo.setUrl(putMapping.value()[0]);
                methodInfo.setMethod(HttpMethod.PUT);
            } else if (annotation instanceof DeleteMapping) {
                DeleteMapping deleteMapping = (DeleteMapping) annotation;
                methodInfo.setUrl(deleteMapping.value()[0]);
                methodInfo.setMethod(HttpMethod.DELETE);
            }
        }
    }

    /**
     * 得到请求的param和body
     *
     * @param method     method
     * @param args       args
     * @param methodInfo methodInfo
     */
    private void extractRequestParamAndBody(Method method, Object[] args, MethodInfo methodInfo) {
        // 存储方法的参数和值
        Map<String, Object> params = new LinkedHashMap<>();
        methodInfo.setParams(params);

        Parameter[] parameters = method.getParameters();
        // 得到调用的参数和body
        for (int i = 0; i < method.getParameters().length; i++) {
            // 参数上是否有 @PathVariable
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                params.put(pathVariable.value(), args[i]);
            }

            // 参数上是否有 @RequestBody
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                methodInfo.setBody((Mono<?>) args[i]);
                // 获取并设置请求对象的实际类型
                methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
            }
        }
    }
}
