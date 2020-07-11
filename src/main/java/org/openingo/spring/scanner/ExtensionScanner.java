/*
 * MIT License
 *
 * Copyright (c) 2020 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.openingo.spring.scanner;

import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.constants.PackageConstants;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;

/**
 * ExtensionScanner
 *
 * @author Qicz
 */
@Configuration
@ComponentScan(PackageConstants.EXTENSION_PACKAGE)
public class ExtensionScanner {

    public ExtensionScanner() {
        PrintStream out = System.out;
        out.print(AnsiOutput.toString(AnsiColor.GREEN, Constants.SPRING_APPLICATION_X, AnsiColor.MAGENTA, " is Running..."));
        out.println();
    }

    @Configuration
    public static class ExtensionConfig {

        public ExtensionConfig() {
            Class<?> mainApplicationClass = this.deduceMainApplicationClass();
            if (ValidateKit.isNotNull(mainApplicationClass)) {
                SpringApplicationX.initMainApplicationInfo(mainApplicationClass);
            }
        }

        /**
         * Returns deduce MainApplication Class
         * @return in test env may be return <tt>null</tt>
         */
        private Class<?> deduceMainApplicationClass() {
            try {
                StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
                for (StackTraceElement stackTraceElement : stackTrace) {
                    String className = stackTraceElement.getClassName();
                    if ("main".equals(stackTraceElement.getMethodName())
                            && !className.contains("junit")) {
                        return Class.forName(className);
                    }
                }
            } catch (ClassNotFoundException ex) {
                // Swallow and continue
            }
            return null;
        }
    }
}
