package com.huang.rpc.server.handler;

import java.io.Serializable;

public interface Invocation extends Serializable {
    
    Object invoke();
    
}
