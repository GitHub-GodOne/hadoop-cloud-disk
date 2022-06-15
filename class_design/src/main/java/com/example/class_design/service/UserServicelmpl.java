package com.example.class_design.service;

import com.example.class_design.dao.MapperImp.UserMapper;
import com.example.class_design.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServicelmpl implements UserService{
    @Autowired
    UserMapper userMapper;

    @Override
    public User queryUserByName(String name) {
        return userMapper.queryUserByName(name);
    }

    @Override
    public boolean exixtUsername(String username) {
        User user = userMapper.queryUserByName(username);
        if(user==null)
            return false;
        else
            return true;
    }

    @Override
    public void insert_user(User user) {
        userMapper.insert_user(user);
    }
}
