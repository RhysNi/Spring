package com.rhys.spring.context;

import com.rhys.spring.IoC.BeanDefinitionRegistry;
import com.rhys.spring.context.annotation.Component;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:32 AM
 */
public class ClassPathBeanDefinitionScanner {
    private BeanDefinitionRegistry beanDefinitionRegistry;

    private int classPathAbsLength = new File(ClassPathBeanDefinitionScanner.class.getResource("/").getPath()).getAbsolutePath().length();


    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }


    public void scan(String... basePackages) {
        if (basePackages != null && basePackages.length > 0) {
            for (String basePackage : basePackages) {
                //递归扫描包目录下.class文件
                //组合包路径+class文件名 得到全限定类名
                //使用类加载器获取对应类名的Class对象
                //解析Class上的注解，获得Bean定义信息，注册Bean定义

                //递归扫描包目录下.class文件
                Set<File> files = this.doScan(basePackage);
                //得到Class对象，解析注解注册BeanDefinition
                this.readAndRegistryBeanDefinition(files);
            }
        }
    }

    private void readAndRegistryBeanDefinition(Set<File> files) {
        for (File file : files) {
            String className = getClassNameFromFile(file);
            try {
                //加载类
                Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
                Component component = clazz.getAnnotation(Component.class);
                //标注了@Component注解
                if (component != null) {

                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 根据文件路径截取类名
     * @author Rhys.Ni
     * @date 2023/4/19
     * @param file
     * @return java.lang.String
     */
    private String getClassNameFromFile(File file) {
        //获取绝对路径
        String absolutePath = file.getAbsolutePath();
        String name = absolutePath.substring(classPathAbsLength + 1, absolutePath.indexOf("."));
        return StringUtils.replace(name, File.separator, ".");
    }

    private Set<File> doScan(String basePackage) {
        //扫描包下的类，将包名转为路径名
        String basePackagePath = "/" + StringUtils.replace(basePackage, ".", "/");
        //得到对应包目录
        File dir = new File(this.getClass().getResource(basePackagePath).getPath());
        //缓存找到的class文件
        Set<File> fileSet = new HashSet<>();
        //检索class文件
        this.retrieveClassFiles(dir, fileSet);

        return fileSet;
    }

    /**
     * 检索Class文件
     * @author Rhys.Ni
     * @date 2023/4/19
     * @param dir
     * @param fileSet
     * @return void
     */
    private void retrieveClassFiles(File dir, Set<File> fileSet) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory() && file.canRead()) {
                retrieveClassFiles(file, fileSet);
            }

            if (file.getName().endsWith(".class")) {
                fileSet.add(file);
            }
        }
    }
}
