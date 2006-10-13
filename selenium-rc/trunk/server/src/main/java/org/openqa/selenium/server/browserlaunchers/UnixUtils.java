/*
 * Created on Oct 13, 2006
 *
 */
package org.openqa.selenium.server.browserlaunchers;

import java.lang.reflect.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

/** Handy utilities for managing Unix/Linux processes */
public class UnixUtils {
    /** retrieves the pid */
    public static int getProcessId(Process p) {
        if (WindowsUtils.thisIsWindows()) {
            throw new IllegalStateException("UnixUtils may not be used on Windows");
        }
        try {
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            Integer pid = (Integer) f.get(p);
            return pid;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't detect pid", e);
        }
    }
    
    /** runs "kill -9" on the specified pid */
    public static void kill9(Integer pid) {
        System.out.println("kill -9 " + pid);
        Project p = new Project();
        ExecTask exec = new ExecTask();
        exec.setProject(p);
        exec.setExecutable("kill");
        exec.setTaskType("kill");
        exec.setFailonerror(false);
        exec.createArg().setValue("-9");
        exec.createArg().setValue(pid.toString());
        exec.setResultProperty("result");
        exec.setOutputproperty("output");
        exec.execute();
        String result = p.getProperty("result");
        String output = p.getProperty("output");
        System.out.println(output);
        if (!"0".equals(result)) {
            throw new RuntimeException("exec return code " + result + ": " + output);
        }
    }
    
    /** runs "kill -9" on the specified process */
    public static void kill9(Process p) {
        kill9(getProcessId(p));
    }
}
