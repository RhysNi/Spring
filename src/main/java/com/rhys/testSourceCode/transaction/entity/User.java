package com.rhys.testSourceCode.transaction.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/31 3:11 AM
 */
@Data
public class User implements Serializable {
    private String userId;
    private String name;
    private String age;
}
