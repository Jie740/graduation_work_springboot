package com.clj.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限树节点 VO
 */
@Data
public class PermissionTreeNode {

    private Long id;

    private Long parentId;

    private String permissionName;

    private String permissionCode;

    private Integer type;

    private String path;

    private String component;

    private String apiUrl;

    private String method;

    private String icon;

    private Integer sort;

    private Integer visible;

    private Integer status;

    private List<PermissionTreeNode> children = new ArrayList<>();
}
