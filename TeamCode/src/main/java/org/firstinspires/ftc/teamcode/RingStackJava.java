package org.firstinspires.ftc.teamcode;

import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

@Metadata(
        mv = {1, 1, 16},
        bv = {1, 0, 3},
        k = 1,
        xi = 2,
        d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u00152\u00020\u0001:\u0002\u0015\u0016B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0012\u0010\u0013\u001a\u00020\u00112\b\u0010\u0014\u001a\u0004\u0018\u00010\u0011H\u0016R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\f@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
        d2 = {"Lorg/firstinspires/ftc/teamcode/RingStack;", "Lorg/openftc/easyopencv/OpenCvPipeline;", "telemetry", "Lorg/firstinspires/ftc/robotcore/external/Telemetry;", "debug", "", "(Lorg/firstinspires/ftc/robotcore/external/Telemetry;Z)V", "getDebug", "()Z", "setDebug", "(Z)V", "<set-?>", "Lorg/firstinspires/ftc/teamcode/RingStack$Height;", "height", "getHeight", "()Lorg/firstinspires/ftc/teamcode/RingStack$Height;", "mat", "Lorg/opencv/core/Mat;", "ret", "processFrame", "input", "Config", "Height", "TeamCode_debug"}
)
public final class RingStackJava extends OpenCvPipeline {
    @NotNull
    private RingStack.Height height;
    private Mat mat;
    private Mat ret;
    private final Telemetry telemetry;
    private boolean debug;
    @NotNull
    private static Scalar lowerOrange = new Scalar(0.0D, 141.0D, 0.0D);
    @NotNull
    private static Scalar upperOrange = new Scalar(255.0D, 230.0D, 95.0D);
    private static int CAMERA_WIDTH = 320;
    private static int HORIZON;
    public static final double BOUND_RATIO = 0.7D;
    public static final RingStack.Config Config = new RingStack.Config((DefaultConstructorMarker)null);

    public RingStackJava(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @NotNull
    public final RingStack.Height getHeight() {
        return this.height;
    }

    @NotNull
    public Mat processFrame(@Nullable Mat input) {
        this.ret.release();
        this.ret = new Mat();

        Telemetry var10000;
        try {
            Imgproc.cvtColor(input, this.mat, 37);
            Mat mask = new Mat(this.mat.rows(), this.mat.cols(), CvType.CV_8UC1);
            Core.inRange(this.mat, lowerOrange, upperOrange, mask);
            Core.bitwise_and(input, input, this.ret, mask);
            Imgproc.GaussianBlur(mask, mask, new Size(5.0D, 15.0D), 0.0D);
            List contours = (List)(new ArrayList());
            Mat hierarchy = new Mat();
            Imgproc.findContours(mask, contours, hierarchy, 3, 1);
            Imgproc.drawContours(this.ret, contours, -1, new Scalar(0.0D, 255.0D, 0.0D), 3);
            int maxWidth = 0;
            Rect maxRect = new Rect();
            Iterator var8 = contours.iterator();

            while(var8.hasNext()) {
                MatOfPoint c = (MatOfPoint)var8.next();
                Point[] var10002 = c.toArray();
                MatOfPoint2f copy = new MatOfPoint2f((Point[])Arrays.copyOf(var10002, var10002.length));
                Rect var15 = Imgproc.boundingRect((Mat)copy);
                Intrinsics.checkExpressionValueIsNotNull(var15, "Imgproc.boundingRect(copy)");
                Rect rect = var15;
                int w = rect.width;
                if (w > maxWidth && rect.y + rect.height > HORIZON) {
                    maxWidth = w;
                    maxRect = rect;
                }

                c.release();
                copy.release();
            }

            Imgproc.rectangle(this.ret, maxRect, new Scalar(0.0D, 0.0D, 255.0D), 2);
            Imgproc.line(this.ret, new Point(0.0D, (double)HORIZON), new Point((double)CAMERA_WIDTH, (double)HORIZON), new Scalar(255.0D, 0.0D, 255.0D));
            if (this.debug) {
                var10000 = this.telemetry;
                if (var10000 != null) {
                    var10000.addData("Vision: maxW", maxWidth);
                }
            }

            RingStack.Height var16;
            if ((double)maxWidth >= Config.getMIN_WIDTH()) {
                double aspectRatio = (double)maxRect.height / (double)maxRect.width;
                if (this.debug) {
                    Telemetry var10001 = this.telemetry;
                    if (var10001 != null) {
                        var10001.addData("Vision: Aspect Ratio", aspectRatio);
                    }
                }

                var16 = aspectRatio > 0.7D ? RingStack.Height.FOUR : RingStack.Height.ONE;
            } else {
                var16 = RingStack.Height.ZERO;
            }

            this.height = var16;
            if (this.debug) {
                var10000 = this.telemetry;
                if (var10000 != null) {
                    var10000.addData("Vision: Height", this.height);
                }
            }

            this.mat.release();
            mask.release();
            hierarchy.release();
        } catch (Exception var12) {
            var10000 = this.telemetry;
            if (var10000 != null) {
                var10000.addData("[ERROR]", var12);
            }

            if (VERSION.SDK_INT >= 24) {
                StackTraceElement[] var14 = var12.getStackTrace();
                Intrinsics.checkExpressionValueIsNotNull(var14, "e.stackTrace");
                ArraysKt.toList(var14).stream().forEach((Consumer)(new Consumer() {
                    // $FF: synthetic method
                    // $FF: bridge method
                    public void accept(Object var1) {
                        this.accept((StackTraceElement)var1);
                    }

                    public final void accept(StackTraceElement x) {
                        Telemetry var10000 = RingStackJava.this.telemetry;
                        if (var10000 != null) {
                            var10000.addLine(x.toString());
                        }

                    }
                }));
            }
        }

        var10000 = this.telemetry;
        if (var10000 != null) {
            var10000.update();
        }

        return this.ret;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean var1) {
        this.debug = var1;
    }

    public RingStackJava(@Nullable Telemetry telemetry, boolean debug) {
        this.telemetry = telemetry;
        this.debug = debug;
        this.height = RingStack.Height.ZERO;
        this.ret = new Mat();
        this.mat = new Mat();
    }

    // $FF: synthetic method
    public RingStackJava(Telemetry var1, boolean var2, int var3, DefaultConstructorMarker var4, Telemetry telemetry) {
        this.telemetry = telemetry;
        if ((var3 & 1) != 0) {
            var1 = (Telemetry)null;
        }

        if ((var3 & 2) != 0) {
            var2 = false;
        }

        this(var1, var2);
    }

    public RingStackJava() {
        this((Telemetry)null, false, 3, (DefaultConstructorMarker)null, telemetry);
    }

    static {
        HORIZON = (int)(0.3125D * (double)CAMERA_WIDTH);
    }

    @Metadata(
            mv = {1, 1, 16},
            bv = {1, 0, 3},
            k = 1,
            xi = 2,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"},
            d2 = {"Lorg/firstinspires/ftc/teamcode/RingStack$Height;", "", "(Ljava/lang/String;I)V", "ZERO", "ONE", "FOUR", "TeamCode_debug"}
    )
    public static enum Height {
        ZERO,
        ONE,
        FOUR;
    }

    @Metadata(
            mv = {1, 1, 16},
            bv = {1, 0, 3},
            k = 1,
            xi = 2,
            d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\b\"\u0004\b\r\u0010\nR\u0011\u0010\u000e\u001a\u00020\u00048F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0014\"\u0004\b\u0019\u0010\u0016¨\u0006\u001a"},
            d2 = {"Lorg/firstinspires/ftc/teamcode/RingStack$Config;", "", "()V", "BOUND_RATIO", "", "CAMERA_WIDTH", "", "getCAMERA_WIDTH", "()I", "setCAMERA_WIDTH", "(I)V", "HORIZON", "getHORIZON", "setHORIZON", "MIN_WIDTH", "getMIN_WIDTH", "()D", "lowerOrange", "Lorg/opencv/core/Scalar;", "getLowerOrange", "()Lorg/opencv/core/Scalar;", "setLowerOrange", "(Lorg/opencv/core/Scalar;)V", "upperOrange", "getUpperOrange", "setUpperOrange", "TeamCode_debug"}
    )
    public static final class Config {
        @NotNull
        public final Scalar getLowerOrange() {
            return RingStack.lowerOrange;
        }

        public final void setLowerOrange(@NotNull Scalar var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            RingStack.lowerOrange = var1;
        }

        @NotNull
        public final Scalar getUpperOrange() {
            return RingStack.upperOrange;
        }

        public final void setUpperOrange(@NotNull Scalar var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            RingStack.upperOrange = var1;
        }

        public final int getCAMERA_WIDTH() {
            return RingStack.CAMERA_WIDTH;
        }

        public final void setCAMERA_WIDTH(int var1) {
            RingStack.CAMERA_WIDTH = var1;
        }

        public final int getHORIZON() {
            return RingStack.HORIZON;
        }

        public final void setHORIZON(int var1) {
            RingStack.HORIZON = var1;
        }

        public static final double getMIN_WIDTH() {
            return 0.15625D * (double)RingStack.Config.getCAMERA_WIDTH();
        }

        private Config() {
        }

        // $FF: synthetic method
        public Config(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
