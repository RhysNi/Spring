// package com.rhys.testSourceCode.config.base;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
// /**
//  * @author Rhys.Ni
//  * @version 1.0
//  * @date 2023/8/8 2:10 AM
//  */
//
// @Component
// public class BeanA {
//     private BeanB beanB;
//
//     @Autowired
//     public BeanA(BeanB beanB) {
//         this.beanB = beanB;
//     }
// }
//
// @Component
// class BeanB {
//
//     private BeanC beanC;
//
//     @Autowired
//     public BeanB(BeanC beanC) {
//         this.beanC = beanC;
//     }
// }
//
// @Component
// class BeanC {
//     private BeanB beanB;
//
//     @Autowired
//     public BeanC(BeanB beanB) {
//         this.beanB = beanB;
//     }
// }
