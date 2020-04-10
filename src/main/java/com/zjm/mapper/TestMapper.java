package com.zjm.mapper;

import com.zjm.model.entity.Test;


public interface TestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Test record);

    int insertSelective(Test record);

    Test selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Test record);

    int updateByPrimaryKeyWithBLOBs(Test record);

    int updateByPrimaryKey(Test record);
}