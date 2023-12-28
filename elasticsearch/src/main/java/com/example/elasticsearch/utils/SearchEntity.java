package com.example.elasticsearch.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsj
 * @description 查询实体
 * @date 2023/7/7
 */
@Data
public class SearchEntity  implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 搜索条件 */
    private List<Condition> conditions;

    public List<Condition> getConditions()
    {
        if (conditions == null)
        {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = "asc";


    public void setConditions(List<Condition> conditions)
    {
        this.conditions = conditions;
    }
}
