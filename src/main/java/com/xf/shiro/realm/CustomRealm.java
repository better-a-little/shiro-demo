package com.xf.shiro.realm;

import com.xf.dao.UserDao;
import com.xf.vo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.*;

public class CustomRealm extends AuthorizingRealm {

//    Map<String,String> userMap = new HashMap<String,String>();
//
//    {
//        userMap.put("zc","d3df61764ee9a26091f714b88958caef");
//        super.setName("customRealm");
//    }

    @Resource
    private UserDao userDao;



    //授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String)principalCollection.getPrimaryPrincipal();
        Set<String> roles = getRolesByUsername(username);
        
        Set<String> permissions = getPermissionsByUsername(username);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);

        return simpleAuthorizationInfo;
    }

    private Set<String> getPermissionsByUsername(String username) {
        Set<String> sets = new HashSet<String>();
        sets.add("user:delete");
        sets.add("user:add");
        return sets;
    }

    private Set<String> getRolesByUsername(String username) {
        System.out.println("从数据库中获取授权数据");
        List<String> list = userDao.queryRolesByUsername(username);
        Set<String> sets = new HashSet<String>(list);
        return sets;
    }

    //认证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String username = (String)authenticationToken.getPrincipal();
        String password = getPasswordByUsername(username);
        if(password == null){
            return null;
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username,password,"customRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(username));

        return authenticationInfo;
    }

    /*模拟数据库*/
    private String getPasswordByUsername(String username) {
        User user = userDao.getUserByUsername(username);
        if (user != null){
            return user.getPassword();
        }
        return null;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123","zc");
//        202cb962ac59075b964b07152d234b70
//        System.out.println(md5Hash);
//        d3df61764ee9a26091f714b88958caef
        System.out.println(md5Hash.toString());

    }

}
