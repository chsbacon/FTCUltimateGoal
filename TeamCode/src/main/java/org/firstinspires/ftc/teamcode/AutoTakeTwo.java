package org.firstinspires.ftc.teamcode;

// All imports here that are not commented out are likely reusable and helpful for this coming year

//These three lines I believe work with the app on the phone. Not sure if the color one is something we need to think about for this year...
import android.app.Activity;
import android.graphics.Color;
import android.view.View;


//imports related to the opmode
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

//Sensors and motors...
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


//Imports related to navigation and motion (Driving) (Vuforia)
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

// Hardware map
import org.firstinspires.ftc.teamcode.HardwareBACONbot;

import java.util.Locale;

//import javax.print.attribute.Size2DSyntax;

//import org.firstinspires.ftc.teamcode.Teleops.HardwareMap;

//Imports Abby is adding (color sensing)
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;


@Autonomous(name = "BACON: Autonomous 2021", group = "Opmode")
//@Disabled


public class AutoTakeTwo extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    HardwareBACONbot robot = new HardwareBACONbot();

    //OpenCV stuff
    OpenCvInternalCamera phoneCam;
    Auto2021.SkystoneDeterminationPipeline pipeline;


    int FRONTDIST = 860;


    // ==============================
    public void runOpMode() {
        //OpenCV stuff
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        pipeline = new Auto2021.SkystoneDeterminationPipeline();
        phoneCam.setPipeline(pipeline);
        phoneCam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                phoneCam.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }
        });


        int teamcolor = 0; // 1 = Blue 2 = Red
        int blue = 1;
        int red = 2;

        int task = 0; //1 = drop&park  2 = fullRun
        int dropPark = 1;
        int fullRun = 2;

        int side = 0; // 1 = left side start 2 = right side start

        double meetDistance = 860; //Distance from wall to the rings (CM From Wall (BackSensor))

        double lastTime = runtime.milliseconds();

        // wobbleServo and wobbleMotor states
        float grabPos = 0;
        float freePos = 1;
        float upTilt = 0;
        float downTilt = 1;

        Orientation angles;
        Acceleration gravity;

        robot.init(hardwareMap);

        // Choosing the team color
        telemetry.addData("Press X for Blue, B for Red", "");
        telemetry.update();
        //Call component setup functions here: ex. openClaw, raiseLauncher, etc.

        //It will only assign color if the buttons are pressed
        while (!gamepad1.x && !gamepad1.b) {
        }

        //This sets the strips of lights to the team color
        if (gamepad1.x) {
            teamcolor = blue;

        }

        if (gamepad1.b) {
            teamcolor = red;

        }


        telemetry.addData("teamcolor ", teamcolor);
        telemetry.update();

        // Choosing task
        telemetry.addData("Press A for drop&park, Y for fullRun", "");
        telemetry.update();
        while (!gamepad1.a && !gamepad1.y) {
        }
        if (gamepad1.a) {
            task = dropPark;
        }
        if (gamepad1.y) {
            task = fullRun;
        }
        telemetry.addData("task ", task);
        telemetry.update();

        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor) robot.backDistance;

        //Wobble grabber position
        robot.wobbleServo.setPosition(grabPos);
        //robot.wobbleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();
        runtime.reset();

        //OpenCV stuff
        telemetry.addData("Analysis", pipeline.getAnalysis());
        //telemetry.addData("Position", pipeline.position);
        telemetry.update();
        // Don't burn CPU cycles busy-looping in this sample
        sleep(500);

        // run until the end of the match (when driver presses STOP)


    }
}
