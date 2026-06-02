package com.mxster.everyphants;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Windows 亚克力 / 毛玻璃效果工具类。
 * 通过 JNA 调用 DWM（桌面窗口管理器）API 实现窗口背景模糊。
 *
 * 自动检测系统支持的最佳策略（按优先级）：
 * 1. Win11 22H2+ → SystemBackdrop (DWM_SYSTEMBACKDROP_TYPE)
 * 2. Win10+ → AccentPolicy (SetWindowCompositionAttribute)
 * 3. Win7+ → DwmEnableBlurBehindWindow
 */
public final class WindowsAcrylicUtil {

    private WindowsAcrylicUtil() {
    }

    // ═══════════════════════════════════════════════════════════
    // 常量
    // ═══════════════════════════════════════════════════════════

    // 策略 1: Win11 22H2+ SystemBackdrop
    private static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;
    private static final int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    private static final int BACKDROP_TYPE = 3; // Acrylic — 真实时模糊（唯一兼容分层窗口）
    private static final int DWMWCP_ROUND = 2; // Win11 圆角

    // 策略 2: Win10+ AccentPolicy
    private static final int ACCENT_ENABLE_BLURBEHIND = 3;
    private static final int WCA_ACCENT_POLICY = 19;

    // 策略 3: Win7+ BlurBehind
    private static final int DWM_BB_ENABLE = 0x00000001;

    // ═══════════════════════════════════════════════════════════
    // 结构体定义
    // ═══════════════════════════════════════════════════════════

    @Structure.FieldOrder({ "accentState", "accentFlags", "gradientColor", "animationId" })
    public static class AccentPolicy extends Structure {
        public int accentState;
        public int accentFlags;
        public int gradientColor;
        public int animationId;
    }

    @Structure.FieldOrder({ "attribute", "dataSize", "dataPtr" })
    public static class WindowCompositionAttributeData extends Structure {
        public int attribute;
        public int dataSize;
        public Pointer dataPtr;
    }

    @Structure.FieldOrder({ "dwFlags", "fEnable", "hRgnBlur", "fTransitionOnMaximized" })
    public static class DWMBlurBehind extends Structure {
        public int dwFlags;
        public boolean fEnable;
        public Pointer hRgnBlur;
        public boolean fTransitionOnMaximized;
    }

    @Structure.FieldOrder({ "cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight" })
    public static class MARGINS extends Structure {
        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;
    }

    // ═══════════════════════════════════════════════════════════
    // JNA 原生接口（仅声明 JNA Platform 未覆盖的函数）
    // ═══════════════════════════════════════════════════════════

    public interface DwmApi extends StdCallLibrary {
        DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);

        HRESULT DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);

        HRESULT DwmExtendFrameIntoClientArea(HWND hwnd, MARGINS margins);

        HRESULT DwmEnableBlurBehindWindow(HWND hwnd, DWMBlurBehind pBlurBehind);
    }

    public interface User32Ext extends StdCallLibrary {
        User32Ext INSTANCE = Native.load("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean SetWindowCompositionAttribute(HWND hwnd, WindowCompositionAttributeData data);

        /** 将窗口裁剪为指定区域，bRedraw=true 时立即重绘 */
        int SetWindowRgn(HWND hWnd, WinNT.HANDLE hRgn, boolean bRedraw);

        /** Win10 1607+：获取窗口所在显示器的 DPI */
        int GetDpiForWindow(HWND hwnd);
    }

    /** GDI32 扩展：创建圆角矩形区域 */
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
     * 对已显示的 JavaFX Stage 启用毛玻璃效果。
     * 内部带重试机制（最多 2 秒），确保原生窗口就绪后再设置模糊。
     *
     * @param stage 已经 show() 的 JavaFX Stage
     * @return 实际生效的策略名称
     */
    public static String enableAcrylic(javafx.stage.Stage stage) {
        HWND hwnd = findWindowHwnd(stage);
        if (hwnd == null) {
            System.err.println("[Acrylic]  ✗ 未能找到窗口 HWND（2秒超时）");
            return "HWND_NOT_FOUND";
        }

        long hwndVal = Pointer.nativeValue(hwnd.getPointer());
        System.out.println("[Acrylic]  ✓ 找到窗口 HWND: 0x" + Long.toHexString(hwndVal));

        // ── 策略 1: Win11 22H2+ SystemBackdrop ──
        try {
            // 关键：TRANSPARENT 分层窗口没有标题栏，
            // 必须先把 DWM 帧扩展到整个客户区，Backdrop 才会渲染到窗口可见区域。
            MARGINS margins = new MARGINS();
            margins.cxLeftWidth = -1;
            margins.cxRightWidth = -1;
            margins.cyTopHeight = -1;
            margins.cyBottomHeight = -1;
            margins.write();
            HRESULT extendHr = DwmApi.INSTANCE.DwmExtendFrameIntoClientArea(hwnd, margins);
            System.out.println("[Acrylic]  DwmExtendFrameIntoClientArea: 0x"
                    + Integer.toHexString(extendHr.intValue()));

            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, BACKDROP_TYPE);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_SYSTEMBACKDROP_TYPE, mem, 4);
            if (hr.intValue() == 0) {
                System.out.println("[Acrylic]  ✓ 策略生效: Win11_Backdrop (type=" + BACKDROP_TYPE + ")");

                // 同时尝试 Win11 原生圆角（DWM 渲染，自动适配 DPI）
                try {
                    Pointer cornerMem = new com.sun.jna.Memory(4);
                    cornerMem.setInt(0, DWMWCP_ROUND);
                    HRESULT cr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                            hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, cornerMem, 4);
                    System.out.println("[Acrylic]  圆角设置: 0x" + Integer.toHexString(cr.intValue()));
                } catch (Exception ignored) {
                }

                return "Win11_Backdrop";
            }
            System.out.println("[Acrylic]  DwmSetWindowAttribute 返回 0x"
                    + Integer.toHexString(hr.intValue()));
        } catch (Exception e) {
            System.out.println("[Acrylic]  SystemBackdrop 不可用: " + e.toString());
        }

        // ── 策略 2: Win10+ AccentPolicy（最通用、效果最好）──
        try {
            AccentPolicy accent = new AccentPolicy();
            accent.accentState = ACCENT_ENABLE_BLURBEHIND;
            accent.accentFlags = 0;
            // gradientColor: ABGR 格式。0x00000000 = 全透明纯模糊；
            // 若想要微白着色可用 0x80FFFFFF
            accent.gradientColor = 0x00000000;
            accent.animationId = 0;
            accent.write();

            WindowCompositionAttributeData data = new WindowCompositionAttributeData();
            data.attribute = WCA_ACCENT_POLICY;
            data.dataSize = accent.size();
            data.dataPtr = accent.getPointer();
            data.write();

            boolean ok = User32Ext.INSTANCE.SetWindowCompositionAttribute(hwnd, data);
            System.out.println("[Acrylic]  ✓ 策略生效: Win10_Accent (SetWindowCompositionAttribute, result=" + ok + ")");
            return "Win10_Accent";
        } catch (Exception e) {
            System.out.println("[Acrylic]  AccentPolicy 不可用: " + e.toString());
        }

        // ── 策略 3: Win7+ DwmBlurBehind ──
        try {
            DWMBlurBehind bb = new DWMBlurBehind();
            bb.dwFlags = DWM_BB_ENABLE;
            bb.fEnable = true;
            bb.hRgnBlur = null;
            bb.fTransitionOnMaximized = false;
            bb.write();

            HRESULT hr = DwmApi.INSTANCE.DwmEnableBlurBehindWindow(hwnd, bb);
            if (hr.intValue() == 0) {
                System.out.println("[Acrylic]  ✓ 策略生效: Win7_BlurBehind");
                return "Win7_BlurBehind";
            }
            System.out.println("[Acrylic]  DwmEnableBlurBehindWindow 返回 0x"
                    + Integer.toHexString(hr.intValue()));
        } catch (Exception e) {
            System.out.println("[Acrylic]  BlurBehind 不可用: " + e.toString());
        }

        System.err.println("[Acrylic]  ✗ 所有策略均失败！可能原因: DWM 未运行 / 远程桌面 / 系统版本过旧");
        return "NONE";
    }

    /**
     * 对使用 {@code StageStyle.UNDECORATED} 的 JavaFX Stage 启用 Mica 效果。
     * Mica 要求窗口为非分层窗口（不能使用 {@code StageStyle.TRANSPARENT}），
     * 通过 {@code DwmExtendFrameIntoClientArea} 让 DWM 接管整个客户区来绘制背景材质。
     *
     * @param stage 已 show() 的 JavaFX Stage（必须用 UNDECORATED 或 DECORATED）
     * @return 实际生效的策略名称
     */
    public static String enableMica(javafx.stage.Stage stage) {
        HWND hwnd = findWindowHwnd(stage);
        if (hwnd == null) {
            System.err.println("[Mica]  ✗ 未能找到窗口 HWND（2秒超时）");
            return "HWND_NOT_FOUND";
        }

        long hwndVal = Pointer.nativeValue(hwnd.getPointer());
        System.out.println("[Mica]  ✓ 找到窗口 HWND: 0x" + Long.toHexString(hwndVal));

        // ── 步骤 1: 将 DWM 框架延伸到整个客户区（值 -1 表示全窗口）──
        try {
            MARGINS margins = new MARGINS();
            margins.cxLeftWidth = -1;
            margins.cxRightWidth = -1;
            margins.cyTopHeight = -1;
            margins.cyBottomHeight = -1;
            margins.write();

            HRESULT hr = DwmApi.INSTANCE.DwmExtendFrameIntoClientArea(hwnd, margins);
            System.out.println("[Mica]  DwmExtendFrameIntoClientArea: 0x"
                    + Integer.toHexString(hr.intValue()));
        } catch (Exception e) {
            System.out.println("[Mica]  DwmExtendFrameIntoClientArea 失败: " + e.toString());
        }

        // ── 步骤 2: 设置 Mica 背景 ──
        try {
            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, BACKDROP_TYPE);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_SYSTEMBACKDROP_TYPE, mem, 4);
            if (hr.intValue() == 0) {
                System.out.println("[Mica]  ✓ 策略生效: Win11_Mica (type=" + BACKDROP_TYPE + ")");
            } else {
                System.out.println("[Mica]  DwmSetWindowAttribute 返回 0x"
                        + Integer.toHexString(hr.intValue()));
            }
        } catch (Exception e) {
            System.out.println("[Mica]  SystemBackdrop 不可用: " + e.toString());
        }

        // ── 步骤 3: Win11 圆角（可选，不影响功能）──
        try {
            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, DWMWCP_ROUND);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, mem, 4);
            System.out.println("[Mica]  圆角设置: 0x" + Integer.toHexString(hr.intValue()));
        } catch (Exception e) {
            System.out.println("[Mica]  圆角不可用: " + e.toString());
        }

        return "Win11_Mica";
    }

    /**
     * 将窗口裁剪为圆角矩形，消除 {@code StageStyle.TRANSPARENT} 下的直角外轮廓。
     * 优先使用 Win11 原生 DWM 圆角（自动适配 DPI），失败时回退到 SetWindowRgn。
     *
     * @param stage         已显示且尺寸已确定的 JavaFX Stage
     * @param logicalRadius CSS 中使用的圆角半径（逻辑像素），如 12
     */
    public static void applyRoundedCorners(javafx.stage.Stage stage, int logicalRadius) {
        HWND hwnd = findWindowHwnd(stage);
        if (hwnd == null) {
            System.err.println("[Rounded]  ✗ 未找到 HWND，跳过圆角裁剪");
            return;
        }

        // ── 方法 A: Win11 DWM 原生圆角（最优）──
        try {
            Pointer mem = new com.sun.jna.Memory(4);
            mem.setInt(0, DWMWCP_ROUND);
            HRESULT hr = DwmApi.INSTANCE.DwmSetWindowAttribute(
                    hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, mem, 4);
            if (hr.intValue() == 0) {
                System.out.println("[Rounded]  ✓ DWM 原生圆角生效");
                return; // DWM 处理圆角，不需要 SetWindowRgn
            }
            System.out.println("[Rounded]  DWM 圆角不可用 (0x"
                    + Integer.toHexString(hr.intValue()) + ")，回退 SetWindowRgn");
        } catch (Exception e) {
            System.out.println("[Rounded]  DWM 圆角异常: " + e);
        }

        // ── 方法 B: SetWindowRgn（DPI 感知）──
        try {
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(hwnd, rect);
            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            if (w <= 0 || h <= 0) {
                System.err.println("[Rounded]  ✗ 窗口尺寸无效: " + w + "x" + h);
                return;
            }

            // 获取窗口实际 DPI，将逻辑半径转为设备像素
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
            System.out.println("[Rounded]  ✓ SetWindowRgn (logicalR=" + logicalRadius
                    + " → deviceR=" + deviceRadius + ", dpi=" + dpi
                    + ", size=" + w + "x" + h + ", result=" + result + ")");
        } catch (Exception e) {
            System.err.println("[Rounded]  ✗ SetWindowRgn 失败: " + e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // HWND 查找 — PID 枚举（最可靠，无需反射 JavaFX 内部 API）
    // ═══════════════════════════════════════════════════════════

    /**
     * 通过当前进程 PID 枚举所有顶层可见窗口，找到属于本 JavaFX 进程的窗口。
     * 带重试机制：每次间隔 100ms，最多尝试 20 次（共 2 秒）。
     */
    private static HWND findWindowHwnd(javafx.stage.Stage stage) {
        long pid = ProcessHandle.current().pid();
        String stageTitle = stage.getTitle();

        for (int attempt = 0; attempt < 20; attempt++) {
            List<Long> candidates = new ArrayList<>();

            User32.INSTANCE.EnumWindows((hwnd, data) -> {
                IntByReference wndPid = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hwnd, wndPid);

                if (wndPid.getValue() != pid) {
                    return true; // 不是本进程，跳过
                }

                if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                    return true; // 不可见窗口，跳过
                }

                long h = Pointer.nativeValue(hwnd.getPointer());
                candidates.add(h);
                return true;
            }, null);

            if (!candidates.isEmpty()) {
                // EnumWindows 的枚举顺序不固定，取最后一个（通常是最近创建的）
                long hwndVal = candidates.get(candidates.size() - 1);
                System.out.println("[Acrylic]  第" + (attempt + 1) + "次尝试: 找到 " + candidates.size()
                        + " 个本进程可见窗口, 选取 0x" + Long.toHexString(hwndVal));
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
