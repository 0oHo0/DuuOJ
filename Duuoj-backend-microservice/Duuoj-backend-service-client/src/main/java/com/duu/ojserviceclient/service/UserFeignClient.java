package com.duu.ojserviceclient.service;


import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.enums.UserRoleEnum;
import com.duu.ojmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

import static com.duu.ojcommon.constant.UserConstant.USER_LOGIN_STATE;

@FeignClient(name = "Duuoj-backend-user-service",path = "/api/user/inner",contextId = "DuuApi3")
public interface UserFeignClient {

    @GetMapping("/get/id")
    User getUserById(@RequestParam("userId") long userId);

    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

//    @GetMapping("/get/login")
//    User getLoginUser(@RequestParam("request") HttpServletRequest request);

    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
}
