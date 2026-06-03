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
 * 优先使用 Win11 原生 DWM 圆角，失败时回退到 GDI SetWindowRgn。
 */
public final class WindowsAcrylicUtil {

    private WindowsAcrylicUtil() {
    }

    // DWM 窗口圆角属性
    private static final int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    private static final int DWMWCP_ROUND = 2;

    // ═══════════════════════════════════════════════════════════
    // JNA 原生接口
    // ═══════════════════════════════════════════════════════════

    public interface DwmApi extends StdCallLibrary {
        DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);

        HRESULT DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
    }

    public interface User32Ext extends StdCallLibrary {
        User32Ext INSTANCE = Native.load("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

        int SetWindowRgn(HWND hWnd, WinNT.HANDLE hRgn, boolean bRedraw);

        int GetDpiForWindow(HWND hwnd);
    }

    public interface Gdi32Ext extends StdCallLibrary {
        Gdi32Ext INSTANCE = Native.load("gdi32", Gdi32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

        WinNT.HANDLE CreateRoundRectRgn(
                int nLeftRect, int nTopRect,
                int nRightRect, int nBottomRect,
                int nWidthEllipse, int nHeightEllipse);
    }

    // ═══════════════════════════════════════════════════════════
    // 公共方法
    // ═══════════════════════════════════════════════════════════

    /**
     * 将窗口裁剪为圆角矩形。
     * 优先使用 Win11 原生 DWM 圆角（自动适配 DPI），失败时回退到 SetWindowRgn。
     *
     * @param stage         已显示且尺寸已确定的 JavaFX Stage
     * @param logicalRadius 圆角半径（逻辑像素），如 12
     */
    public static void applyRoundedCorners(javafx.stage.Stage stage, int logicalRadius) {
        HWND hwnd = findWindowHwnd(stage);
        if (hwnd == null) {
            System.err.println("[Rounded] 未找到 HWND，跳过圆角裁剪");
            return;
        }

        // 方法 A: Win11 DWM 原生圆角（最优）
        try {
            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, DWMWCP_ROUND);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, mem, 4);
            if (hr.intValue() == 0) {
                System.out.println("[Rounded] DWM 原生圆角生效");
                return;
            }
            System.out.println("[Rounded] DWM 圆角不可用 (0x"
                    + Integer.toHexString(hr.intValue()) + ")，回退 SetWindowRgn");
        } catch (Exception e) {
            System.out.println("[Rounded] DWM 圆角异常: " + e);
        }

        // 方法 B: SetWindowRgn（DPI 感知）
        try {
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            if (w <= 0 || h <= 0) {
                System.err.println("[Rounded] 窗口尺寸无效: " + w + "x" + h);
                return;
            }

            int dpi = 96;
            try {
                dpi = User32Ext.INSTANCE.GetDpiForWindow(hwnd);
            } catch (Exception ignored) {
            }
            int deviceRadius = Math.round((float) logicalRadius * dpi / 96f);

            int d = deviceRadius * 2;
            WinNT.HANDLE rgn = Gdi32Ext.INSTANCE.CreateRoundRectRgn(
                    0, 0, w + 1, h + 1, d, d);
            int result = User32Ext.INSTANCE.SetWindowRgn(hwnd, rgn, true);
            System.out.println("[Rounded] SetWindowRgn (logicalR=" + logicalRadius
                    + " -> deviceR=" + deviceRadius + ", dpi=" + dpi
                    + ", size=" + w + "x" + h + ", result=" + result + ")");
        } catch (Exception e) {
            System.err.println("[Rounded] SetWindowRgn 失败: " + e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // HWND 查找
    // ═══════════════════════════════════════════════════════════

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
