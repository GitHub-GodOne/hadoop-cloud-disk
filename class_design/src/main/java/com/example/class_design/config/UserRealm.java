package com.example.class_design.config;

import com.example.class_design.domain.User;
import com.example.class_design.service.HdfsUtiles;
import com.example.class_design.service.UserService;
import org.apache.hadoop.fs.Path;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userServicelmpl;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        User user = userServicelmpl.queryUserByName(userToken.getUsername());
        if(user==null){
            return null; //返回null抛出异常 UnknownAccountException
        }
        String path = "/" + userToken.getUsername();
        try {
            if(!HdfsUtiles.getfs().exists(new Path(path))){
                HdfsUtiles.getfs().mkdirs(new Path(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}
