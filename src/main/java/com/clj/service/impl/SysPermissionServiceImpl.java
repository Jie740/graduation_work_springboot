package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.SysPermission;
import com.clj.domain.vo.PermissionTreeNode;
import com.clj.mapper.SysPermissionMapper;
import com.clj.service.SysPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ajie
 * @description 针对表【sys_permission(系统权限表)】的数据库操作Service实现
 * @createDate 2026-08-06 23:22:17
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionService {

    @Override
    public List<PermissionTreeNode> getPermissionTree() {
        List<SysPermission> all = this.lambdaQuery()
                .eq(SysPermission::getDeleted, 0) //获取未删除的权限
                .orderByAsc(SysPermission::getSort)
                .list();

        List<PermissionTreeNode> nodes = all.stream()
                .map(this::toNode)
                .collect(Collectors.toList());

        return buildTree(nodes, null);
    }

    @Override
    public List<PermissionTreeNode> getMenuTree() {
        List<SysPermission> all = this.lambdaQuery()
                .eq(SysPermission::getDeleted, 0)
                .eq(SysPermission::getStatus, 1)
                .eq(SysPermission::getVisible, 1)
                .in(SysPermission::getType, List.of(1, 2)) // 菜单和按钮
                .orderByAsc(SysPermission::getSort)
                .list();

        List<PermissionTreeNode> nodes = all.stream()
                .map(this::toNode)
                .collect(Collectors.toList());

        return buildTree(nodes, null);
    }

    @Override
    public void addPermission(SysPermission permission) {
        // 检查权限编码是否已存在
        Long count = this.lambdaQuery()
                .eq(SysPermission::getPermissionCode, permission.getPermissionCode())
                .eq(SysPermission::getDeleted, 0)
                .count();
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }
        if (!this.save(permission)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void updatePermission(SysPermission permission) {
        // 检查权限编码是否被其他权限使用
        if (permission.getPermissionCode() != null) {
            SysPermission existing = this.lambdaQuery()
                    .eq(SysPermission::getPermissionCode, permission.getPermissionCode())
                    .ne(SysPermission::getId, permission.getId())
                    .eq(SysPermission::getDeleted, 0)
                    .one();
            if (existing != null) {
                throw new BusinessException("权限编码已存在");
            }
        }
        if (!this.updateById(permission)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public void deletePermission(Long id) {
        // 检查是否有子节点
        Long childCount = this.lambdaQuery()
                .eq(SysPermission::getParentId, id)
                .eq(SysPermission::getDeleted, 0)
                .count();
        if (childCount > 0) {
            throw new BusinessException("该权限下有子权限，请先删除子权限");
        }
        // 软删除
        if (!this.lambdaUpdate()
                .eq(SysPermission::getId, id)
                .set(SysPermission::getDeleted, 1)
                .update()) {
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 构建权限树
     */
    private List<PermissionTreeNode> buildTree(List<PermissionTreeNode> allNodes, Long parentId) {
        List<PermissionTreeNode> tree = new ArrayList<>();
        for (PermissionTreeNode node : allNodes) {
            boolean match = (parentId == null && node.getParentId() == null)
                    || (parentId != null && parentId.equals(node.getParentId()));
            if (match) {
                List<PermissionTreeNode> children = buildTree(allNodes, node.getId());
                node.setChildren(children);
                tree.add(node);
            }
        }
        return tree;
    }

    /**
     * Entity 转 TreeNode
     */
    private PermissionTreeNode toNode(SysPermission p) {
        PermissionTreeNode node = new PermissionTreeNode();
        BeanUtils.copyProperties(p, node);
        return node;
    }
}
