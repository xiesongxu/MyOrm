package com.xie.dao;

import com.xie.Annotation.Mapper;
import com.xie.Annotation.Param;
import com.xie.Annotation.Select;

import java.util.List;

@Mapper
public interface Dao1 {

    @Select("select * from user where password=123")
    public User select();

    @Select("select * from user")
    public List<User> selectAll();

    @Select("select * from user where id=${id}")
    public User selectOne(@Param("id") int id);
}
