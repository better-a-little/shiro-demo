package com.xf.dao;

import com.xf.vo.User;

import java.util.List;

public interface UserDao {

    User getUserByUsername(String username);

    List<String> queryRolesByUsername(String username);
}
