package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


/**
 * This is NOT an opmode.
 *
 * This class defines all the specific hardware for a the BACONbot robot.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 *
 * Motor channel:   Front Left drive motor:       "FL"
 * Motor channel:  Front Right  drive motor:      "FR"
 * Motor channel:   Back Left drive motor:        "BL"
 * Motor channel:  Back Right  drive motor:       "BR"
 *
 */

public class HardwareBACONbot
{
    /* Public OpMode members. */
    public DcMotor  frontLeftMotor   = null;
    public DcMotor  frontRightMotor  = null;
    public DcMotor  backLeftMotor    = null;
    public DcMotor  backRightMotor   = null;

    public DcMotor intakeMotor = null;
    public DcMotor launchMotor = null;
    public DcMotor wobbleMotor = null;

    public DistanceSensor backDistance = null;
    //public DistanceSensor frontDistance = null;

    public Servo    wobbleServo = null;
    public Servo    feederServo = null;

    //public RevBlinkinLedDriver blinkinLedDriver;
    //public RevBlinkinLedDriver.BlinkinPattern pattern;

    public BNO055IMU imu;

    /* local OpMode members. */
    private HardwareMap hwMap =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareBACONbot(){
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        frontLeftMotor  = hwMap.dcMotor.get("FL"); // H1 0 - motor port
        frontRightMotor = hwMap.dcMotor.get("FR"); // H1 1
        backLeftMotor   = hwMap.dcMotor.get("BL"); // H1 2
        backRightMotor  = hwMap.dcMotor.get("BR"); // H1 3

        launchMotor = hwMap.dcMotor.get("LM"); // Hub 2 motor port 0
        intakeMotor = hwMap.dcMotor.get("IM"); // Hub 2 motor port 1
        wobbleMotor = hwMap.dcMotor.get("WM");

        backDistance = hwMap.get(DistanceSensor.class, "bsr"); //hub2 port 2
        //frontDistance = hwMap.get(DistanceSensor.class, "fsr"); //hub2 port 2

        wobbleServo = hwMap.servo.get("WS");
        feederServo = hwMap.servo.get("FS");

        //blinkinLedDriver = hwMap.get(RevBlinkinLedDriver.class, "blinkin");


        // BACONbot uses AndyMark NeverRest Motors
        // This code assumes that the motors turns counterclockwise,
        //     looking from the back of the motor down the shaft,
        //     when positive power is applied

        /*  *****if the above assumption is incorrect uncomment these lines
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        */

        // Set all motors to zero power
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);

        launchMotor.setPower(0);
        intakeMotor.setPower(0);
        wobbleMotor.setPower(0);

        feederServo.setPosition(0);
        wobbleServo.setPosition(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.

        //Reverse direction for drive motors
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        //Setup for motors without encoders
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launchMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Setup for motors with encoders
        wobbleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wobbleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Brake drive motors
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Brake additional motors
        launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wobbleMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";


        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);



    }

}