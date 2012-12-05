﻿// <copyright file="FileUtilities.cs" company="WebDriver Committers">
// Copyright 2007-2011 WebDriver committers
// Copyright 2007-2011 Google Inc.
// Portions copyright 2011 Software Freedom Conservancy
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// </copyright>

using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;

namespace OpenQA.Selenium.Internal
{
    /// <summary>
    /// Encapsulates methods for working with files.
    /// </summary>
    internal static class FileUtilities
    {
        /// <summary>
        /// Recursively copies a directory.
        /// </summary>
        /// <param name="sourceDirectory">The source directory to copy.</param>
        /// <param name="destinationDirectory">The destination directory.</param>
        /// <returns><see langword="true"/> if the copy is completed; otherwise <see langword="false"/>.</returns>
        public static bool CopyDirectory(string sourceDirectory, string destinationDirectory)
        {
            bool copyComplete = false;
            DirectoryInfo sourceDirectoryInfo = new DirectoryInfo(sourceDirectory);
            DirectoryInfo destinationDirectoryInfo = new DirectoryInfo(destinationDirectory);

            if (sourceDirectoryInfo.Exists)
            {
                if (!destinationDirectoryInfo.Exists)
                {
                    destinationDirectoryInfo.Create();
                }

                foreach (FileInfo fileEntry in sourceDirectoryInfo.GetFiles())
                {
                    fileEntry.CopyTo(Path.Combine(destinationDirectoryInfo.FullName, fileEntry.Name));
                }

                foreach (DirectoryInfo directoryEntry in sourceDirectoryInfo.GetDirectories())
                {
                    if (!CopyDirectory(directoryEntry.FullName, Path.Combine(destinationDirectoryInfo.FullName, directoryEntry.Name)))
                    {
                        copyComplete = false;
                    }
                }
            }

            copyComplete = true;
            return copyComplete;
        }

        /// <summary>
        /// Recursively deletes a directory, retrying on error until a timeout.
        /// </summary>
        /// <param name="directoryToDelete">The directory to delete.</param>
        /// <remarks>This method does not throw an exception if the delete fails.</remarks>
        public static void DeleteDirectory(string directoryToDelete)
        {
            int numberOfRetries = 0;
            while (Directory.Exists(directoryToDelete) && numberOfRetries < 10)
            {
                try
                {
                    Directory.Delete(directoryToDelete, true);
                }
                catch (IOException)
                {
                    // If we hit an exception (like file still in use), wait a half second
                    // and try again. If we still hit an exception, go ahead and let it through.
                    System.Threading.Thread.Sleep(500);
                }
                catch (UnauthorizedAccessException)
                {
                    // If we hit an exception (like file still in use), wait a half second
                    // and try again. If we still hit an exception, go ahead and let it through.
                    System.Threading.Thread.Sleep(500);
                }
                finally
                {
                    numberOfRetries++;
                }
            }

            if (Directory.Exists(directoryToDelete))
            {
                Console.WriteLine("Unable to delete directory '{0}'", directoryToDelete);
            }
        }

        /// <summary>
        /// Searches for a file with the specified name.
        /// </summary>
        /// <param name="fileName">The name of the file to find.</param>
        /// <returns>The full path to the directory where the file can be found,
        /// or an empty string if the file does not exist in the locations searched.</returns>
        /// <remarks>
        /// This method looks first in the directory of the currently executing
        /// assembly. If the specified file is not there, the method then looks in
        /// each directory on the PATH environment variable, in order.
        /// </remarks>
        public static string FindFile(string fileName)
        {
            // Look first in the same directory as the executing assembly
            string currentDirectory = GetCurrentDirectory();

            // If it's not in the same directory as the executing assembly,
            // try looking in the system path.
            if (!File.Exists(Path.Combine(currentDirectory, fileName)))
            {
                string systemPath = Environment.GetEnvironmentVariable("PATH");
                string[] directories = systemPath.Split(Path.PathSeparator);
                foreach (string directory in directories)
                {
                    if (File.Exists(Path.Combine(directory, fileName)))
                    {
                        currentDirectory = directory;
                        return currentDirectory;
                    }
                }
            }

            // Note that if it wasn't found on the system path, currentDirectory is still
            // set to the same directory as the executing assembly.
            return string.Empty;
        }

        /// <summary>
        /// Gets the directory of the currently executing assembly.
        /// </summary>
        /// <returns>The directory of the currently executing assembly.</returns>
        public static string GetCurrentDirectory()
        {
            Assembly executingAssembly = Assembly.GetExecutingAssembly();
            string currentDirectory = Path.GetDirectoryName(executingAssembly.Location);

            // If we're shadow copying, get the directory from the codebase instead 
            if (AppDomain.CurrentDomain.ShadowCopyFiles)
            {
                Uri uri = new Uri(executingAssembly.CodeBase);
                currentDirectory = Path.GetDirectoryName(uri.LocalPath);
            }

            return currentDirectory;
        }
    }
}
