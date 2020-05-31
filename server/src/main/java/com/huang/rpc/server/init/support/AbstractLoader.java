package com.huang.rpc.server.init.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.init.Loader;

public class AbstractLoader implements Loader
{

private static final Logger log = LoggerFactory.getLogger(RpcLoader.class);
    
    protected static volatile Map<String, String> propertyMap = new HashMap<>();
    
    @Override
    public void load(String properties)
    {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(properties))
        {
            Properties prop = new Properties();
            prop.load(is);
            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements())
            {
                String key = (String)e.nextElement();
                String value = prop.getProperty(key);
                propertyMap.put(key, value);
            }
        }
        catch (NullPointerException e)
        {
            log.warn("can not find {}, will init with default setting", properties);
            propertyMap.put(LoaderConstants.RPC_SERVICE_SINGLETON, "true");
        }
        catch (IOException e)
        {
            log.error("initialization exception with file {}", properties, e);
        }
    }

    public static Map<String, String> getPropertyMap()
    {
        return propertyMap;
    }
    
}
