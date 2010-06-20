/**
 * Copyright 2009 - SERLI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This class permits to check
 * the state of the Rcp application.
 * 
 * @author Kevin Pollet
 */

function RcpChecker() {
	this.running = false;
	this.timer = null;
	this.listeners = [];
	
	if (typeof RcpChecker.initialized == "undefined") {

		RcpChecker.prototype.isRunning = function() {
            return this.running;
        };
		
		RcpChecker.prototype.setRunning = function(run_state){
			this.old_state = this.running;
			this.running = run_state;
			
			if (run_state != this.old_state) {
				for (var i = 0; i < this.listeners.length; i++){
					this.listeners[i].rcpStateChanged(run_state);
				}				
			}		
		};
	
		RcpChecker.prototype.addRcpListener = function(listener) { 
			var exist = false;
			
			for (var i = 0; !exist && i < this.listeners.length; i++) {
				if (this.listeners[i] == listener) {
					exist = true;
				}
			}
		
			if (!exist) {
              this.listeners.push(listener);
            }
		};
		
		RcpChecker.prototype.removeRcpListener = function(listener) {
			var index = -1;
			
			for (var i = 0; index == -1 && i < this.listeners.length; i++) { 
	
				if (this.listeners[i] == listener) {
					index = i;
                }
			}
	
			if (index != -1) {

				for (var i = index; i < (this.listeners.length-1); i++) {
					this.listeners[i] = this.listeners[i+1];
                }

				this.listeners.pop();
			}	
				
		};
		
		RcpChecker.prototype.start = function () {
			this.timer = setInterval(JSUtil.bind(this, this.check), 2000);
		};
		
		RcpChecker.prototype.stop = function() {
			clearInterval(this.timer);
		};
		
		RcpChecker.prototype.check = function() {
            var instance = this;
			var serverPort = prefs.getInt("serverport");
			var serverName = prefs.getChar("servername");
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.open("GET", "http://" + serverName + ":" + serverPort + "/test", true);
			xmlhttp.onreadystatechange = function() {
				if ((this.readyState == 4 && this.status == 0) || (this.readyState == 4 && this.status == 404)) {
                    if (instance.running != false) {
                        instance.setRunning(false);
                    }
					
				}else if (this.readyState == 4) {
                    if (instance.running != true) {
                        instance.setRunning(true);
                    }
				}
			};
			
			xmlhttp.send(null);
		};			
				
		RcpChecker.initialized = true;
	}
}





