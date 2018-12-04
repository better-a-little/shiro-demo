package com.xf.session;

import com.xf.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX = "zc-session";

    private byte[] getKey(String key){
        return (SHIRO_SESSION_PREFIX + key).getBytes();
    }

    private void savaSession(Session session){
        if (session != null && session.getId() != null){
            byte[] key = getKey(session.getId().toString());
            System.out.println("key-----------" + key );
            byte[] value = SerializationUtils.serialize(session);
            System.out.println("value-----------" + value );
            jedisUtil.set(key,value);
            jedisUtil.expire(key,600);
        }
    }

    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        System.out.println("sessionId-----------" + session );
        assignSessionId(session,sessionId);
        savaSession(session);
        return sessionId;
    }

    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null){
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        return (Session) SerializationUtils.deserialize(value);
    }

    public void update(Session session) throws UnknownSessionException {
        savaSession(session);
    }

    public void delete(Session session) {
        if (session == null || session.getId() == null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);

    }

    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX);
        Set<Session> sessions = new HashSet<Session>();
        if (CollectionUtils.isEmpty(keys)){
            return sessions;
        }
        for (byte[] key : keys){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
