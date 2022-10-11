package com.sangeng.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 5.18 改变角色状态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleStatusDto {
    private Long roleId;
    private String status;
}

