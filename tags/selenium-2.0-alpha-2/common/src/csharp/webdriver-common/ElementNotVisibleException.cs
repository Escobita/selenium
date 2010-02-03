/* Copyright notice and license
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

namespace OpenQA.Selenium
{
    /// <summary>
    /// The exception that is thrown when an element is not visible.
    /// </summary>
    [Serializable]
    public class ElementNotVisibleException : WebDriverException
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ElementNotVisibleException"/> class.
        /// </summary>
        public ElementNotVisibleException()
            : base()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ElementNotVisibleException"/> class with 
        /// a specified error message.
        /// </summary>
        /// <param name="message">The message that describes the error.</param>
        public ElementNotVisibleException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ElementNotVisibleException"/> class with
        /// a specified error message and a reference to the inner exception that is the
        /// cause of this exception.
        /// </summary>
        /// <param name="message">The error message that explains the reason for the exception.</param>
        /// <param name="innerException">The exception that is the cause of the current exception,
        /// or <see langword="null"/> if no inner exception is specified.</param>
        public ElementNotVisibleException(string message, Exception innerException)
            : base(message, innerException)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ElementNotVisibleException"/> class with serialized data.
        /// </summary>
        /// <param name="info">The <see cref="SerializationInfo"/> that holds the serialized 
        /// object data about the exception being thrown.</param>
        /// <param name="context">The <see cref="StreamingContext"/> that contains contextual 
        /// information about the source or destination.</param>
        protected ElementNotVisibleException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
