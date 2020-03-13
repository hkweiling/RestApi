package com.ikonke.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author:
 * @time: 2020/3/13 14:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfo {
    private int id;
    private String name;
    private List<Integer> devIds;
}
