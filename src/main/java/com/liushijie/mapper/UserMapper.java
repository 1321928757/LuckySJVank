package com.liushijie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liushijie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户类Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
