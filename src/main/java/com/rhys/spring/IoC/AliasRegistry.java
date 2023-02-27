package com.rhys.spring.IoC;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/27 1048 PM
 */
public interface AliasRegistry {
    /**
     * 别名注册
     *
     * @param name
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    void registerAlias(String name, String alias) throws Exception;

    /**
     * 是否为别名
     *
     * @param name
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    boolean isAlias(String name) throws Exception;

    /**
     * 获取所有别名
     *
     * @param name
     * @return java.lang.String[]
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    String[] getAliases(String name);

    /**
     * 获取原名
     *
     * @param name
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    String getOriginalName(String name);

    /**
     * 别名注销
     *
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    void removeAlias(String alias);
}
