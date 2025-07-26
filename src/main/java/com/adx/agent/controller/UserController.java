package com.adx.agent.controller;

import com.adx.agent.common.BaseResponse;
import com.adx.agent.common.ResultUtils;
import com.adx.agent.constant.UserConstant;
import com.adx.agent.exception.BusinessException;
import com.adx.agent.exception.ErrorCode;
import com.adx.agent.exception.ThrowUtils;
import com.adx.agent.model.dto.user.UserLoginRequest;
import com.adx.agent.model.dto.user.UserQueryRequest;
import com.adx.agent.model.dto.user.UserRegisterRequest;
import com.adx.agent.model.dto.user.UserUpdateRequest;
import com.adx.agent.model.entity.User;
import com.adx.agent.model.vo.LoginUserVO;
import com.adx.agent.model.vo.UserVO;
import com.adx.agent.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;



    /**
     * User Register
     */
    @PostMapping("/register")
    public BaseResponse<UserVO> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // This is your own utility class
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String code = userRegisterRequest.getCode();
        
        // It does not include the upload of avatars, and the upload is null
        long userId = userService.userRegister(userAccount, userPassword, checkPassword, email, code);
        User user = userService.getById(userId);
        return ResultUtils.success(userService.getUserVO(user));
    }


    /**
     * User Login
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // This is your own utility class
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }


    /**
     * Get the current user to be desensitized
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }


    /**
     * The user is logged in
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR );
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }




    /**
     * Get users based on ID (admins only)
     */
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * Get the wrapper class based on ID for the average user
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        // The logic here is that I call the above to get the user and then desensitize
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }



    /**
     * Update User
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        // Salt value, obfuscated passwords
        final String SALT = "wyf_da_niu_niu";
        String userPassword = userUpdateRequest.getUserPassword();

        BeanUtils.copyProperties(userUpdateRequest, user);
        if (userPassword != null) {
            user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes()));
        }
//        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes()));
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Pagination to get a list of user encapsulations (admins only)
     *
     * @param userQueryRequest 查询请求参数
     */
    // Its practical get request here can also be used, but here it is to receive an object, and it is more standardized to use post, and the range of parameters that can be passed by post is larger
    @PostMapping("/list/page/vo")
    // 这里的Page是mybatis为我们封装好的 ??????
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }


}
