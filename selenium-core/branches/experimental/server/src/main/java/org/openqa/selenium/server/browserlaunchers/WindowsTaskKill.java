/*
 * Created on Mar 4, 2006
 *
 */
package org.openqa.selenium.server.browserlaunchers;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.w3c.dom.*;

public class WindowsTaskKill {

    private static final boolean THIS_IS_WINDOWS = File.pathSeparator.equals(";");
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Kills Windows processes by matching their command lines");
            System.out.println("usage: " + WindowsTaskKill.class.getName() + " command arg1 arg2 ...");
        }
        kill(args);

    }

    public static void kill(String[] cmdarray) throws Exception {
        StringBuffer pattern = new StringBuffer();
        File executable = new File(cmdarray[0]);
        /* For the first argument, the executable, Windows may modify
         * the start path in any number of ways.  Ignore a starting quote
         * if any (\"?), non-greedily look for anything up until the last
         * backslash (.*?\\\\), then look for the executable's filename,
         * then finally ignore a final quote (\"?)
         */
        // TODO We should be careful, in case Windows has ~1-ified the executable name as well
        pattern.append("\"?.*?\\\\");
        pattern.append(executable.getName());
        pattern.append("\"?");
        for (int i = 1; i < cmdarray.length; i++) {
            /* There may be a space, but maybe not (\\s?), may be a quote or maybe not (\"?),
             * but then turn on block quotation (as if *everything* had a regex backslash in front of it)
             * with \Q.  Then look for the next argument (which may have ?s, \s, "s, who knows),
             * turning off block quotation.  Now ignore a final quote if any (\"?)
             */
            pattern.append("\\s?\"?\\Q");
            String arg = cmdarray[i];
            pattern.append(arg);
            pattern.append("\\E\"?");
        }
        pattern.append("\\s*");
        Pattern cmd = Pattern.compile(pattern.toString(), Pattern.CASE_INSENSITIVE);
        Map procMap = procMap();
        boolean killedOne = false;
        for (Iterator i = procMap.keySet().iterator(); i.hasNext();) {
            String commandLine = (String) i.next();
            if (commandLine == null) continue;
            Matcher m = cmd.matcher(commandLine);
            if (m.matches()) {
                String processID = (String) procMap.get(commandLine);
                System.out.print("Killing PID ");
                System.out.print(processID);
                System.out.print(": ");
                System.out.println(commandLine);
                Runtime.getRuntime().exec(new String[] {"taskkill", "/pid", processID});
                System.out.println("Killed");
                killedOne = true;
            }
        }
        System.err.print("Didn't find any matches for");
        for (int i = 0; i < cmdarray.length; i++) {
            System.err.print(" '");
            System.err.print(cmdarray[i]);
            System.err.print('\'');
        }
        System.err.println("");
    }
    
    public static Map procMap() throws Exception {
        Project p = new Project();
        Property propTask = new Property();
        propTask.setProject(p);
        propTask.setEnvironment("env");
        propTask.execute();
        ExecTask exec = new ExecTask();
        exec.setProject(p);
        exec.setExecutable("wmic");
        exec.createArg().setValue("process");
        exec.createArg().setValue("list");
        exec.createArg().setValue("full");
        exec.createArg().setValue("/format:rawxml.xsl");
        exec.setOutputproperty("proclist");
        System.out.println("Reading Windows Process List...");
        exec.execute();
        System.out.println("Done, searching for processes to kill...");
        String output = p.getProperty("proclist");
        // TODO This would be faster if it used SAX instead of DOM
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(output.getBytes()));
        NodeList procList = doc.getElementsByTagName("INSTANCE");
        Map processes = new HashMap();
        for (int i = 0; i < procList.getLength(); i++) {
            Element process = (Element) procList.item(i);
            NodeList propList = process.getElementsByTagName("PROPERTY");
            Map procProps = new HashMap();
            for (int j = 0; j < propList.getLength(); j++) {
                Element property = (Element) propList.item(j);
                String propName = property.getAttribute("NAME");
                NodeList valList = property.getElementsByTagName("VALUE");
                String value = null;
                if (valList.getLength() != 0) {
                    Element valueElement = (Element) valList.item(0);
                    Text valueNode = (Text) valueElement.getFirstChild();
                    value = valueNode.getData();
                }
                procProps.put(propName, value);
            }
            String processID = (String) procProps.get("ProcessId");
            String commandLine = (String) procProps.get("CommandLine");
            processes.put(commandLine, processID);
        }
        return processes;
    }
    
    public static boolean thisIsWindows() {
        return THIS_IS_WINDOWS;
    }
}
