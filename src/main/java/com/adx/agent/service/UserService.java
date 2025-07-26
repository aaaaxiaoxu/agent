package com.adx.agent.service;

import com.adx.agent.model.dto.user.UserQueryRequest;
import com.adx.agent.model.entity.User;
import com.adx.agent.model.vo.LoginUserVO;
import com.adx.agent.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author XLW200420
* @description Database operation Service for table【user】
* @createDate 2025-03-27 20:29:34
*/
public interface UserService extends IService<User> {
    /**
     * User registration
     *
     * @param userAccount   User account
     * @param userPassword  User password
     * @param checkPassword Confirmation password
     * @return New user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String email, String code);
    String getEncryptPassword(String userPassword);

    /**
     * User login
     *
     * @param userAccount  User account
     * @param userPassword User password
     * @param request
     * @return Desensitized user information, many data fields are not returned to the frontend
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    // Get current user, returned to the backend
    User getLoginUser(HttpServletRequest request);


    /**
     * Get desensitized user login information
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * Get desensitized user information
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);



    /**
     * Get list of desensitized user information
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);



    /**
     * User logout
     * @param request
     * @return Desensitized user list
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * Get query conditions
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * Check if user is admin
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);



}
