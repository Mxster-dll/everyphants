package com.mxster.everyphants.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Windows 窗口圆角裁剪工具。
 */
public final class WindowsAcrylicUtil {

    private WindowsAcrylicUtil() {
    }

    private static final int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    private static final int DWMWCP_ROUND = 2;

    public interface DwmApi extends StdCallLibrary {
        DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);

        HRESULT DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
    }

    public static void applyRoundedCorners(javafx.stage.Stage stage, int logicalRadius) {
        HWND hwnd = findWindowHwnd(stage);
        if (hwnd == null) {
            System.err.println("[Rounded] 未找到 HWND，跳过圆角裁剪");
            return;
        }

        try {
            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, DWMWCP_ROUND);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, mem, 4);
            if (hr.intValue() == 0) {
                System.out.println("[Rounded] DWM 原生圆角生效");
            } else {
                System.out.println("[Rounded] DWM 圆角不可用 (0x"
                        + Integer.toHexString(hr.intValue()) + ")，放弃裁剪");
            }
        } catch (Exception e) {
            System.out.println("[Rounded] DWM 圆角异常: " + e + "，放弃裁剪");
        }
    }

    private static HWND findWindowHwnd(javafx.stage.Stage stage) {
        long pid = ProcessHandle.current().pid();

        for (int attempt = 0; attempt < 20; attempt++) {
            List<Long> candidates = new ArrayList<>();

            User32.INSTANCE.EnumWindows((hwnd, data) -> {
                IntByReference wndPid = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hwnd, wndPid);

                if (wndPid.getValue() != pid) {
                    return true;
                }

                if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                    return true;
                }

                long h = Pointer.nativeValue(hwnd.getPointer());
                candidates.add(h);
                return true;
            }, null);

            if (!candidates.isEmpty()) {
                long hwndVal = candidates.get(candidates.size() - 1);
                System.out.println("[Rounded] 第" + (attempt + 1) + "次尝试: 找到 "
                        + candidates.size() + " 个窗口, 选取 0x" + Long.toHexString(hwndVal));
                return new HWND(Pointer.createConstant(hwndVal));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return null;
    }
}
