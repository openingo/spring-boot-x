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

import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.constants.PackageConstants;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.ComponentScan;

import java.io.PrintStream;

/**
 * ExtensionScanner
 *
 * @author Qicz
 */
@ComponentScan(PackageConstants.EXTENSION_PACKAGE)
public class ExtensionScanner {

    public ExtensionScanner() {
        PrintStream out = System.out;
        out.println("  ____                   _                   ____                    _    __  __\n" +
                " / ___|   _ __    _ __  (_)  _ __     __ _  | __ )    ___     ___   | |_  \\ \\/ /\n" +
                " \\___ \\  | '_ \\  | '__| | | | '_ \\   / _` | |  _ \\   / _ \\   / _ \\  | __|  \\  / \n" +
                "  ___) | | |_) | | |    | | | | | | | (_| | | |_) | | (_) | | (_) | | |_   /  \\ \n" +
                " |____/  | .__/  |_|    |_| |_| |_|  \\__, | |____/   \\___/   \\___/   \\__| /_/\\_\\\n" +
                "         |_|                         |___/                                      ");
        out.println(String.format(" ( %s )", SpringApplicationX.springBootVersionX));
        out.print(AnsiOutput.toString(AnsiColor.GREEN, Constants.SPRING_APPLICATION_X, AnsiColor.MAGENTA, " is Running..."));
        out.println();
    }
}
