package com.adx.agent.model.dto.user;

import com.adx.agent.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;


/**
 * 用户查询请求，也是给管理员看的
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {


    private Long id;


    private String userName;


    private String userAccount;


    private String userProfile;


    private String userRole;

    private static final long serialVersionUID = 1L;
}
