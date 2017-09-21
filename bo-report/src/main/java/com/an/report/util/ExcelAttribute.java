package com.an.report.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target({ java.lang.annotation.ElementType.FIELD })  
public @interface ExcelAttribute {  
  
    /** 
     * Name of the Column 
     *  
     * @return 
     */  
    String name();
  
    /** 
     * Start from 0
     *  
     * @return 
     */  
    int index() default 0;
  
  
    /** 
     * Export or not
     *  
     * @return 
     */  
    boolean isExport() default true;

    /**
     * Export or not
     *
     * @return
     */
    boolean wrapContent() default false;

    int width() default 20;

}  