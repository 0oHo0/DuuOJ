package com.duu.ojuserservice.controller.inner;

import com.duu.ojmodel.model.entity.User;
import com.duu.ojserviceclient.service.UserFeignClient;
import com.duu.ojuserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/28
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
}
