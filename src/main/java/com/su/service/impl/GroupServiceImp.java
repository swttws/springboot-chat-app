package com.su.service.impl;

import com.su.pojo.Group;
import com.su.mapper.GroupMapper;
import com.su.service.GroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@Service
public class GroupServiceImp extends ServiceImpl<GroupMapper, Group> implements GroupService {

}
