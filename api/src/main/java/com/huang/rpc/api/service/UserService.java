package com.huang.rpc.api.service;

import java.io.Serializable;

public interface UserService extends Serializable {
    
    String say(String param);
}
