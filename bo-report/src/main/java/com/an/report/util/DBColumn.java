package com.an.report.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target({ java.lang.annotation.ElementType.FIELD })  
public @interface DBColumn {
  
    /** 
     * Name of the Column 
     *  
     * @return 
     */  
    String name();

}  