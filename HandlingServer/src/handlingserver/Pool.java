/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author 70136
 */
public class Pool {
    final static Executor executor;//thread pool
    static {//static init
        executor=Executors.newFixedThreadPool(15);
    }
    
    public static void execute(Runnable runnable){
        //use thread pool to run
        executor.execute(runnable);
    }
}
