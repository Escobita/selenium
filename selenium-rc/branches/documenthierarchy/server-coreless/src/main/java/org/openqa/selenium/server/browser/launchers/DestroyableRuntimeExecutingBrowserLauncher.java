/*
 * Copyright 2006 ThoughtWorks, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package org.openqa.selenium.server.browser.launchers;

import java.io.IOException;

import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Runs the specified command path to start the browser, and kills the process to quit.
 * 
 * @author Paul Hammant
 */
public class DestroyableRuntimeExecutingBrowserLauncher extends
		AbstractBrowserLauncher {

	protected Process process;

	protected String commandPath;
	
	protected boolean closed = false;

	/**
	 * Specifies a command path to run
	 */
	public DestroyableRuntimeExecutingBrowserLauncher(
			SeleniumConfiguration seleniumConfiguration,
			String commandPath) {
		super(seleniumConfiguration);
		this.commandPath = commandPath;
	}

	/**
	 * Kills the process
	 */
	public boolean close() {
		if (process == null)
			return closed;
		int exitValue = AsyncExecute.killProcess(process);
		
		if (exitValue == 0) {
			closed = true;
		}
		
		return closed;
	}

	public Process getProcess() {
		return process;
	}

	protected void launch(String url) {
		exec(commandPath + " " + url);
	}

	protected void exec(String command) {

		try {
			process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new RuntimeException(
					"Error starting browser by executing command " + command
							+ ": " + e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isClosed() {
		return closed;
	}
}
