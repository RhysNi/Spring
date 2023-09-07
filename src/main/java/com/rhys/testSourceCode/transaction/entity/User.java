package com.rhys.testSourceCode.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/31 3:11 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String userId;
    private String name;
    private String age;
}
