package org.zero.webflux.restclient.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: rest-client
 * @description: 服务器信息
 * @author: 01
 * @create: 2018-10-06 11:21
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {

    /**
     * 服务器url
     */
    private String url;
}
