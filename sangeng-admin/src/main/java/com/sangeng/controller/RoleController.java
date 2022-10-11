package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.ChangeRoleStatusDto;
import com.sangeng.domain.entity.Role;
import com.sangeng.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    /**
     * 5.18 角色列表
     */
    @GetMapping("/list")
    public ResponseResult list(Role role, Integer pageNum, Integer pageSize){
        return roleService.selectRolePage(role, pageNum, pageSize);
    }

    /**
     * 5.18 改变角色状态
     */
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeRoleStatusDto roleStatusDto){
        Role role = new Role();
        role.setId(roleStatusDto.getRoleId());
        role.setStatus(roleStatusDto.getStatus());
        return ResponseResult.okResult(roleService.updateById(role));
    }


    /**
     * 5.20.2.2 新增角色接口
     */
    @PostMapping
    public ResponseResult add( @RequestBody Role role) {
        roleService.insertRole(role);
        return ResponseResult.okResult();
    }

    /**
     * 5.21.2.1 角色信息回显接口
     */
    @GetMapping(value = "/{roleId}")
    public ResponseResult getInfo(@PathVariable Long roleId){
        Role role = roleService.getById(roleId);
        return ResponseResult.okResult(role);
    }

    /**
     * 5.21.2.3 修改保存角色
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Role role) {
        roleService.updateRole(role);
        return ResponseResult.okResult();
    }

    /**
     * 5.22 删除角色
     * @param id
     */
    @DeleteMapping("/{id}")
    public ResponseResult remove(@PathVariable(name = "id") Long id) {
        roleService.removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 5.24.2.1 查询角色列表接口
     */
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        List<Role> roles = roleService.selectRoleAll();
        return ResponseResult.okResult(roles);
    }

}
