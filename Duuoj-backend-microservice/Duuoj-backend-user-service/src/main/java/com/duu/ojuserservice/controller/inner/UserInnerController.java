package com.duu.ojuserservice.controller.inner;

import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojserviceclient.service.UserFeignClient;
import com.duu.ojuserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author : duu
 * @date : 2023/12/28
 * @from ：https://github.com/0oHo0
 **/
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {
    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getUserById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }

    @Override
    @GetMapping("/get/login")
    public User getLoginUser(@RequestParam("request") HttpServletRequest request) {
        return userService.getLoginUser(request);
    }

    @GetMapping("/get/secretKey")
    public String getUserSecretKey(@RequestParam("accessKey") String accessKey) {
        Optional.ofNullable(accessKey).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "accessKey" +
                "不能为空"));
        return userService.getUserSecretKey(accessKey);
    }
}
