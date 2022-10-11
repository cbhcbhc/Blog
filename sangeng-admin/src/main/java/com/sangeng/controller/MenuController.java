package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.vo.MenuTreeVo;
import com.sangeng.domain.vo.MenuVo;
import com.sangeng.domain.vo.RoleMenuTreeSelectVo;
import com.sangeng.service.MenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SystemConverter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    /**
     * 5.14获取菜单列表
     */
    @GetMapping("/list")
    public ResponseResult list(Menu menu) {
        List<Menu> menus = menuService.selectMenuList(menu);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }

    /**
     * 5.15 新增菜单
     * @param menu
     * @return
     */
    @PostMapping
    public ResponseResult add(@RequestBody Menu menu){
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    /**
     * 5.16.2.1 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    public ResponseResult getInfo(@PathVariable Long menuId){
        return ResponseResult.okResult(menuService.getById(menuId));
    }

    /**
     * 5.16.2.2 更新菜单
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Menu menu) {
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(500,"修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateById(menu);
        return ResponseResult.okResult();
    }

    /**
     * 5.17 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public ResponseResult remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChild(menuId)) {
            return ResponseResult.errorResult(500,"存在子菜单不允许删除");
        }
        menuService.removeById(menuId);
        return ResponseResult.okResult();
    }

    /**
     *5.20.2.1 获取菜单树接口
     * @return
     */
    @GetMapping("/treeselect")
    public ResponseResult treeselect() {
        //复用之前的selectMenuList方法。方法需要参数，参数可以用来进行条件查询，而这个方法不需要条件，所以直接new Menu()传入
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<MenuTreeVo> options =  SystemConverter.buildMenuSelectTree(menus);
        return ResponseResult.okResult(options);
    }

    /**
     * 5.21.2.2 加载对应角色菜单列表树接口
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResponseResult roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        //menus：需要把所有的菜单权限查询出来
        List<Menu> menus = menuService.selectMenuList(new Menu());
        //checkedKeys：角色所关联的菜单权限id列表
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        //把菜单转换为树
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(menus);

        RoleMenuTreeSelectVo vo = new RoleMenuTreeSelectVo(checkedKeys,menuTreeVos);
        return ResponseResult.okResult(vo);
    }


}
