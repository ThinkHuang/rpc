package com.huang.rpc.server.constants;

import lombok.Data;

/**
 * @author huangyejun
 *
 */
@Data
public class Result {

    private Object data;
    
    public Result(Object data) {
        this.data = data;
    }
}
