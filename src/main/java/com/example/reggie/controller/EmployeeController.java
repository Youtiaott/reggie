package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.entity.Employee;
import com.example.reggie.common.R;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2022-06-09
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /*** 
     * @Description //TODO 登录
     * @param employee 
 * @param request 
     * @return: com.example.reggie.result.R<com.example.reggie.entity.Employee>
     **/
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //将明文密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //Employee::getUsername 可能是获取数据库表的username字段名
        //相当于select * from employee where username = #{username}
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee result = employeeService.getOne(queryWrapper);

        //用户不存在
        if(null==result){
            return R.error("登陆失败！");
        }

        //密码错误
        if(!result.getPassword().equals(password)){
            return R.error("密码错误！");
        }

        //判断用户状态是否为锁定状态
        if(result.getStatus() != 1 ){
            return R.error("用户已被禁用，请联系管理员");
        }

        //登入成功，将用户id存入session中
        request.getSession().setAttribute("employee",result);
        return R.success(result);
    }


    /*** 
     * @Description //TODO 退出
     * @param request 
     * @return: com.example.reggie.result.R<java.lang.String>
     **/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /***
     * @Description //TODO 添加员工----保存信息
     * @param request
     * @param employee
     * @return: com.example.reggie.result.R<java.lang.String>
     **/
    @PostMapping
    public R<String> Add(HttpServletRequest request,@RequestBody Employee employee){


        //设置初始密码进行加密,如果是新员工
        Employee result = employeeService.getById(employee.getId());
        if(null==result)
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间
       /* employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/

        //设置创建人
        /*Employee createUser = (Employee) request.getSession().getAttribute("employee");
        employee.setCreateUser(createUser.getId());
        employee.setUpdateUser(createUser.getId());*/

        employeeService.save(employee);
        return R.success("添加员工成功");
    }

    /***
     * @Description //TODO 分页查询，用于页面展示数据
     * @param page 当前页面数
     * @param pageSize 当前页面最大数据个数
     * @param name 员工名字
     * @return: com.example.reggie.result.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /***
     * @Description //TODO 修改员工账号状态
     * @param employee 员工id
     * @return: com.example.reggie.result.R<java.lang.String>
     **/
    @PutMapping
    public R<String> updateStatus(@RequestBody Employee employee, HttpServletRequest request){

        long id = Thread.currentThread().getId();
        log.info("当前线程：{}",id);

        Employee updateUser = (Employee)request.getSession().getAttribute("employee");

        if(null==employee){
            return R.error("空数据，错误");
        }

        Integer status = employee.getStatus();
        log.info("status-------->"+ status);


        employee.setStatus(status);
       /* employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(updateUser.getId());*/
        employeeService.updateById(employee);

        return R.success("操作成功");
    }

    /*** 
     * @Description //TODO 根据id查找员工，用于数据回显
     * @param id 
     * @return: com.example.reggie.result.R<com.example.reggie.entity.Employee>
     **/
    @GetMapping("/{id}")
    public R<Employee> findById(@PathVariable("id") Long id){
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        Employee result = employeeService.getOne(queryWrapper);
        if(null==result){
            return R.error("获取用户数据失败，数据为null");
        }
        return R.success(result);
    }
}

