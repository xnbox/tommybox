/*
MIT License

Copyright (c) 2021 xnbox team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

HOME:   https://xnbox.github.io
E-Mail: xnbox.team@outlook.com
*/

package org.tommybox.ui.utils;

import org.tommy.common.utils.SystemProperties;

/**
 * Methods of this class based on ideas from "LWJGL - Lightweight Java Game Library 3" (BSD-3-Clause License)
 * https://github.com/LWJGL/lwjgl3 
 * org.lwjgl.system.Platform
 */
public class OsUtils {

	private static final String OS_NAME = SystemProperties.OS_NAME;
	private static final String OS_ARCH = SystemProperties.OS_ARCH;

	public enum Os {
		LINUX, //
		MACOSX, //
		WINDOWS; //
	}

	public enum Arch {
		X64, // 64 bit
		X86, // 32 bit
		ARM64, // 64 bit
		ARM32, // 32 bit
		PPC64LE // 64 bit
	}

	public static Os getOs() {
		if (OS_NAME.startsWith("Windows"))
			return Os.WINDOWS;
		else if (OS_NAME.startsWith("Linux") || //
				OS_NAME.startsWith("FreeBSD") || //
				OS_NAME.startsWith("SunOS") || //
				OS_NAME.startsWith("Unix"))
			return Os.LINUX;
		else if (OS_NAME.startsWith("Mac OS X") || //
				OS_NAME.startsWith("Darwin"))
			return Os.MACOSX;
		return Os.LINUX; // the default
	}

	public static Arch getArch() {
		if (OS_ARCH.startsWith("ppc64le"))
			return Arch.PPC64LE;

		boolean is64Bit = OS_ARCH.contains("64") || OS_ARCH.startsWith("armv8");

		return OS_ARCH.startsWith("arm") || OS_ARCH.startsWith("aarch64") //
				? (is64Bit ? Arch.ARM64 : Arch.ARM32) //
				: (is64Bit ? Arch.X64 : Arch.X86);
	}

}
