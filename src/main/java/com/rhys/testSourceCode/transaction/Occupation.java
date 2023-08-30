package com.rhys.testSourceCode.transaction;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/31 3:19 AM
 */
@Data
public class Occupation implements Serializable {
    private String uName;
    private String occupation;
}
