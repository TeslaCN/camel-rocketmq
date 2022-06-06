package icu.wwj.camel.component.rocketmq;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * @author zhangchen
 */
public class AclUtils {

    public static RPCHook getAclRPCHook(String accessKey, String secretKey) {
        if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
            return new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
        }
        return null;
    }
}
