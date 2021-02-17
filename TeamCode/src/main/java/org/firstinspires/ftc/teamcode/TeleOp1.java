


package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.concurrent.TimeUnit;
import java.util.Locale;


// this is the final teleop file
// essentially, don't edit this unless its a change you're sure you want
// if you want to make changes on this branch/mecanum stuff
// use the MecanumWheelDraft file

@TeleOp(name = "UltimateGoalTeleOp", group = "Opmode")
//@Disabled
public class TeleOp1 extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    GrahamHWMap robot = new GrahamHWMap();

    @Override
    public void runOpMode() {

        robot.init(hardwareMap);

        double x;
        double y;
        double r;
        double frontLeft;
        double frontRight;
        double backLeft;
        double backRight;

        double step = .1;    //was .2    //how much to update
        double interval = 25;  //was 75 // how often to update
        double lastSpeedTime = runtime.milliseconds();

        double max; //

        double launchMotorStatus = 0; // do not edit this
        double launchMotorPower = 0; //do not edit this
        double desiredLaunchPower = .75; // edit this for the power you want to motor to spin at

        double intakeMotorStatus = 0; //do not edit this
        double intakeMotorPower = 0; //do not edit this
        double desiredIntakePower = .75; //edit this for the power you want the motor to spin at



        // change the active and rest positions to change where each servo goes
        double WSactivePos1 = 0;
        double WSrestPos1 = 0;
        double WMpower = .25;  //change the motor up/down speed
        long WMsleep = 1500;    // change how long we let the claw raise/lower in milliseconds

        double LServoPos = 0;

        // change the active and rest positions to change where the servo goes
        double feederServoPosition = 0;
        double LSactivePos = 90;
        double LSrestPos = 0;

        // this should be the heading of the goals, relative to the robot start
        double GoalHeading = 90;

        Orientation targOrientMain;
        targOrientMain = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        while (opModeIsActive()) {

            Orientation angles;

            angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);


            y = gamepad1.left_stick_y;
            x = gamepad1.left_stick_x;
            r = gamepad1.right_stick_x;


            // do not let rotation dominate movement
            r = r / 2;

            // calculate the power for each wheel
            frontLeft = +y - x + r;
            backLeft = +y + x + r;

            frontRight = -y - x + r;
            backRight = -y + x + r;
            /*
            // Normalize the values so none exceeds +/- 1.0
            max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
            if (max > 1.0) {
                frontLeft = frontLeft / max;
                frontRight = frontRight / max;
                backLeft = backLeft / max;
                backRight = backRight / max;
            }

             */

            if (runtime.milliseconds() > lastSpeedTime + interval) {
                lastSpeedTime = runtime.milliseconds();

                frontLeft = getRampPower(frontLeft, robot.frontLeftMotor.getPower(), step);
                frontRight = getRampPower(-frontRight, -robot.frontRightMotor.getPower(), step);
                backLeft = getRampPower(backLeft, robot.backLeftMotor.getPower(), step);
                backRight = getRampPower(-backRight, -robot.backRightMotor.getPower(), step);

                frontRight = -frontRight;
                backRight = -backRight;




                max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
                if (max > .9) {   //was 1
                    frontLeft = frontLeft / max;
                    frontRight = frontRight / max;
                    backLeft = backLeft / max;
                    backRight = backRight / max;
                }


                robot.frontLeftMotor.setPower(frontLeft);
                robot.frontRightMotor.setPower(frontRight);
                robot.backLeftMotor.setPower(backLeft);
                robot.backRightMotor.setPower(backRight);


                // Show wheel power to driver
                telemetry.addData("front left", "%.2f", frontLeft);
                telemetry.addData("front right", "%.2f", frontRight);
                telemetry.addData("back left", "%.2f", backLeft);
                telemetry.addData("back right", "%.2f", backRight);

                //telemetry.addData("current heading", formatAngle(angles.angleUnit, angles.firstAngle));

                telemetry.addData("back distance", robot.backDistance.getDistance(DistanceUnit.MM));

                telemetry.update();



            }

            //dont delete this -- this is what you gotta pass through the heading functions to if you want to keep the heading straight
            // robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES

// gamepad 1 - 111111111111111111111111111111111111111111
            // driver - wobble



            if(gamepad1.a){


            }


            // rotate to the heading of the goals -- used to line up shots
            if(gamepad1.y){
                sleep(250);
                rotateToHeading(GoalHeading);
            }





            // pick up wobble
            if(gamepad1.left_bumper){

                sleep(250); //sleep used to debounce button

                //prep
                robot.wobbleServo.setPosition(WSrestPos1);   //open the claw
                sleep(250);  //give 1/4 second for claw to open

                //lowering
                robot.wobbleMotor.setPower(WMpower);  //lower the lift
                sleep(WMsleep);  //let the lift lower for WMsleep amount of time
                robot.wobbleMotor.setPower(0); //stop the lift from lowering

                //grabbing
                robot.wobbleServo.setPosition(WSactivePos1); //close the claw
                sleep(500); //give 1/2 second for the claw to close

                //raising
                robot.wobbleMotor.setPower(-WMpower); //raise the lift
                sleep(WMsleep); // let the lift raise for WMsleep amount of time
                robot.wobbleMotor.setPower(0);  // stop the lift at the top


                robot.wobbleServo.setPosition(WSactivePos1); // make sure the claw is closed

            }


            //put down wobble
            if(gamepad1.right_bumper){

                sleep(250);  //sleep used to debounce button

                //prep
                robot.wobbleServo.setPosition(WSactivePos1);  //make sure the claw is close
                sleep(250); //give 1/4 second to make sure claw is closed

                //lowering
                robot.wobbleMotor.setPower(WMpower);  //lower claw
                sleep(WMsleep); // let claw lower for WMsleep amount of time
                robot.wobbleMotor.setPower(0);

                //letting go
                robot.wobbleServo.setPosition(WSrestPos1); //open claw
                sleep(250); // let claw open

                //raising
                robot.wobbleMotor.setPower(-WMpower); //raise claw
                sleep(WMsleep); //raise claw for Wmsleep amount of time
                robot.wobbleMotor.setPower(0); //stop motor at top

            }

            robot.wobbleMotor.setPower(0);


            if(gamepad1.b){
                    strafeLeft(.3,robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES));
            }



            if(gamepad1.x){
                    strafeRight(.3,robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES));
            }

// gamepad 2 - 22222222222222222222222222222222222222
            // shooter collector

            //intake motor
            if(gamepad2.a){
                sleep(250);
                if(intakeMotorStatus == 0){ //if motor is off
                    intakeMotorPower = desiredIntakePower; //turn motor on
                    intakeMotorStatus = 1; //motor is on
                }
                else if (intakeMotorStatus == 1){ //if motor is on
                    intakeMotorPower = 0;   //turn motor off
                    intakeMotorStatus = 0; //motor is off
                }
            }

            if((gamepad2.a) && (gamepad2.left_bumper)){
                robot.intakeMotor.setPower(-desiredIntakePower);
            }

            robot.intakeMotor.setPower(intakeMotorPower);


            //launcher motor
            // turns the launcher motor on or off
            if(gamepad2.x){
                sleep(250);
                if (launchMotorStatus == 0){ //if motor off
                    launchMotorPower = desiredLaunchPower;  //turn motor on
                    launchMotorStatus = 1;  // motor is on
                }
                else if (launchMotorStatus == 1){ // if motor on
                    launchMotorPower = 0;       // turn motor off
                    launchMotorStatus = 0;      // motor is off
                }
            }
            robot.launchMotor.setPower(launchMotorPower);


            if(gamepad2.b){
                robot.feederServo.setPosition(LSrestPos);
                sleep(250);
            }
            if(gamepad2.y){
                robot.feederServo.setPosition(LSactivePos);
                sleep(250);
            }






        }

    }

    //used for ramp up driving
    double getRampPower(double t, double a, double step) {
        double delta;
        double returnPower = 0;

        delta = t - a;
        if (delta > 0) {  // speeding up
            returnPower = a + step;
            if (returnPower > t) {
                returnPower = t;
            }
        }
        if (delta < 0) {  //slowing down
            returnPower = a - (step);
            if (returnPower < t)
                returnPower = t;
        }
        if (delta == 0) {
            returnPower = a;
        }
        return returnPower;
    }

    //rotates to the given heading
    void rotateToHeading(double heading){

        Orientation currentOrient;
        currentOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double currentAngle = currentOrient.angleUnit.DEGREES.normalize(currentOrient.firstAngle);

        for (double i = .3; i > .1; i = i-.1) {

            blindRotateRight(i);
            while ((currentOrient.angleUnit.DEGREES.normalize(currentOrient.firstAngle) > heading) && opModeIsActive()) {

                currentOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                telemetry.addData("current heading", formatAngle(currentOrient.angleUnit, currentOrient.firstAngle));
                telemetry.addData("target heading", heading);
                telemetry.update();
            }
            stopDriving();

            blindRotateLeft(i);
            while ((currentOrient.angleUnit.DEGREES.normalize(currentOrient.firstAngle) < heading) && opModeIsActive()) {

                currentOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                telemetry.addData("current heading", formatAngle(currentOrient.angleUnit, currentOrient.firstAngle));
                telemetry.addData("target heading", heading);
                telemetry.update();
            }
            stopDriving();
        }



        blindRotateRight(.175);
        while ((currentOrient.angleUnit.DEGREES.normalize(currentOrient.firstAngle) > heading) && opModeIsActive()) {

            currentOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading", formatAngle(currentOrient.angleUnit, currentOrient.firstAngle));
            telemetry.addData("target heading", heading);
            telemetry.update();
        }
        stopDriving();




        blindRotateLeft(.175);
        while ((currentOrient.angleUnit.DEGREES.normalize(currentOrient.firstAngle) < heading) && opModeIsActive()) {
            currentOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("current heading", formatAngle(currentOrient.angleUnit, currentOrient.firstAngle));
            telemetry.addData("target heading", heading);
            telemetry.update();
        }
        stopDriving();
    }





    //just rotates to the right
    void blindRotateRight(double pwr){
        pwr = -pwr; // -pwr on all wheels turns right
        // Set power on each wheel
        robot.frontLeftMotor.setPower(pwr);
        robot.frontRightMotor.setPower(pwr);
        robot.backLeftMotor.setPower(pwr);
        robot.backRightMotor.setPower(pwr);

    }

    //just rotates to the left
    void blindRotateLeft(double pwr){

        robot.frontLeftMotor.setPower(pwr);
        robot.frontRightMotor.setPower(pwr);
        robot.backLeftMotor.setPower(pwr);
        robot.backRightMotor.setPower(pwr);

    }



    //strafes left at the heading it was called at
    void strafeLeft(double pwr, Orientation target) {

        //orients
        Orientation targetOrient;
        Orientation currOrient;

        //converts the target heading to a double to use in error calculation
        targetOrient = target;
        double targAng = targetOrient.angleUnit.DEGREES.normalize(target.firstAngle);;  // target.angleUnit.DEGREES.normalize(target.firstAngle);

        //rChanger changes the sensitivity of the R value
        double rChanger = 10;
        double frontLeft, frontRight, backLeft, backRight, max;


        while((opModeIsActive()) && (gamepad1.x)){

            //gamepad.x is here as that is the button I've been pressing to test this function
            //if you want to have this run properly, you'll need to replace gamepad.x with some other qualifier t
            // that will stop the while loop at some point, some way


            currOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currAng = currOrient.angleUnit.DEGREES.normalize(currOrient.firstAngle);

            double error = targAng - currAng;


            double r = (-error / 180) / (rChanger * pwr);
            //double r = (-error/180);
            //r = 0;
            //r=-r;

            if ((r < .07) && (r > 0)) {
                r = .07;
            } else if ((r > -.07) && (r < 0)) {
                r = -.07;
            }


            // Normalize the values so none exceeds +/- 1.0
            frontLeft = pwr + r ;
            backLeft = -pwr + r ;
            backRight = -pwr + r ;
            frontRight = pwr + r ;

            //original
            // +    +
            // -    +
            // -    +
            // +    +


            //strafe right
            // -    +
            // +    +
            // +    +
            // -    +

            max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
            if (max > 1.0) {
                frontLeft = frontLeft / max;
                frontRight = frontRight / max;
                backLeft = backLeft / max;
                backRight = backRight / max;
            }



            telemetry.addData("front left", "%.2f", frontLeft);
            telemetry.addData("front right", "%.2f", frontRight);
            telemetry.addData("back left", "%.2f", backLeft);
            telemetry.addData("back right", "%.2f", backRight);

            telemetry.addData("error", error);

            telemetry.addData("current heading", formatAngle(currOrient.angleUnit, currOrient.firstAngle));
            telemetry.addData("target heading", formatAngle(targetOrient.angleUnit, targetOrient.firstAngle));

            telemetry.update();

            //send the power to the motors
            robot.frontLeftMotor.setPower(frontLeft);
            robot.backLeftMotor.setPower(backLeft);
            robot.backRightMotor.setPower(backRight);
            robot.frontRightMotor.setPower(frontRight);


        }


    }



    //strafes left at the heading it was called at
    void strafeRight(double pwr, Orientation target) {

        //orients
        Orientation targetOrient;
        Orientation currOrient;

        //converts the target heading to a double to use in error calculation
        targetOrient = target;
        double targAng = targetOrient.angleUnit.DEGREES.normalize(target.firstAngle);;  // target.angleUnit.DEGREES.normalize(target.firstAngle);

        //rChanger changes the sensitivity of the R value
        double rChanger = 10;
        double frontLeft, frontRight, backLeft, backRight, max;


        while((opModeIsActive()) && (gamepad1.x)){

            //gamepad.x is here as that is the button I've been pressing to test this function
            //if you want to have this run properly, you'll need to replace gamepad.x with some other qualifier t
            // that will stop the while loop at some point, some way


            currOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currAng = currOrient.angleUnit.DEGREES.normalize(currOrient.firstAngle);

            double error = targAng - currAng;


            double r = (-error / 180) / (rChanger * pwr);
            //double r = (-error/180);
            //r = 0;
            //r=-r;

            if ((r < .07) && (r > 0)) {
                r = .07;
            } else if ((r > -.07) && (r < 0)) {
                r = -.07;
            }


            // Normalize the values so none exceeds +/- 1.0
            frontLeft = -pwr + r ;
            backLeft = pwr + r ;
            backRight = pwr + r ;
            frontRight = -pwr + r ;

            //original
            // +    +
            // -    +
            // -    +
            // +    +


            //strafe right
            // -    +
            // +    +
            // +    +
            // -    +

            max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
            if (max > 1.0) {
                frontLeft = frontLeft / max;
                frontRight = frontRight / max;
                backLeft = backLeft / max;
                backRight = backRight / max;
            }



            telemetry.addData("front left", "%.2f", frontLeft);
            telemetry.addData("front right", "%.2f", frontRight);
            telemetry.addData("back left", "%.2f", backLeft);
            telemetry.addData("back right", "%.2f", backRight);

            telemetry.addData("error", error);

            telemetry.addData("current heading", formatAngle(currOrient.angleUnit, currOrient.firstAngle));
            telemetry.addData("target heading", formatAngle(targetOrient.angleUnit, targetOrient.firstAngle));

            telemetry.update();

            //send the power to the motors
            robot.frontLeftMotor.setPower(frontLeft);
            robot.backLeftMotor.setPower(backLeft);
            robot.backRightMotor.setPower(backRight);
            robot.frontRightMotor.setPower(frontRight);


        }


    }


    //kills power ot all wheels
    void stopDriving(){
        robot.frontLeftMotor.setPower(0);
        robot.frontRightMotor.setPower(0);
        robot.backLeftMotor.setPower(0);
        robot.backRightMotor.setPower(0);
    }

    //drives forward at the heading it was called at
    // for example, calling this when the robot is at 60 heading, it will go to that heading, even if it gets knocked off course
    void driveStraightTime(double pwr, Orientation target, double desiredTime){

        //orients
        Orientation targetOrient;
        Orientation currOrient;


        double lastTime = runtime.milliseconds();

        //converts the target heading to a double to use in error calculation
        targetOrient = target;
        double targAng = targetOrient.angleUnit.DEGREES.normalize(target.firstAngle);;  // target.angleUnit.DEGREES.normalize(target.firstAngle);

        //rChanger changes the sensitivity of the R value
        double rChanger = 10;
        double frontLeft, frontRight, backLeft, backRight, max;

        while(((runtime.milliseconds() < lastTime + desiredTime) && (opModeIsActive()))){



            currOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currAng = currOrient.angleUnit.DEGREES.normalize(currOrient.firstAngle);

            double error = targAng - currAng;


            double r = (-error / 180) / (pwr /* * rChanger*/);
            //r = 0;

            // Normalize the values so none exceeds +/- 1.0
            frontLeft = pwr + r ;
            backLeft = pwr + r ;
            backRight = pwr - r ;
            frontRight = pwr - r ;

            frontLeft = -frontLeft;
            backLeft = -backLeft;

            max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
            if (max > 1.0) {
                frontLeft = frontLeft / max;
                frontRight = frontRight / max;
                backLeft = backLeft / max;
                backRight = backRight / max;
            }



            telemetry.addData("front left", "%.2f", frontLeft);
            telemetry.addData("front right", "%.2f", frontRight);
            telemetry.addData("back left", "%.2f", backLeft);
            telemetry.addData("back right", "%.2f", backRight);

            telemetry.addData("current heading", formatAngle(currOrient.angleUnit, currOrient.firstAngle));
            telemetry.addData("target heading", formatAngle(targetOrient.angleUnit, targetOrient.firstAngle));

            telemetry.update();

            //send the power to the motors
            robot.frontLeftMotor.setPower(frontLeft);
            robot.backLeftMotor.setPower(backLeft);
            robot.backRightMotor.setPower(backRight);
            robot.frontRightMotor.setPower(frontRight);



        }

    }

    //drives straight for a desired distanced based off of the back distance sensor
    void driveStraightDistance(double pwr, Orientation target, double desiredDistance){

        //orients
        Orientation targetOrient;
        Orientation currOrient;


        //converts the target heading to a double to use in error calculation
        targetOrient = target;
        double targAng = targetOrient.angleUnit.DEGREES.normalize(target.firstAngle);;  // target.angleUnit.DEGREES.normalize(target.firstAngle);

        //rChanger changes the sensitivity of the R value
        double rChanger = 10;
        double frontLeft, frontRight, backLeft, backRight, max;

        while(((robot.backDistance.getDistance(DistanceUnit.MM) < desiredDistance) && (opModeIsActive()))){

            currOrient = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            double currAng = currOrient.angleUnit.DEGREES.normalize(currOrient.firstAngle);

            double error = targAng - currAng;


            double r = (-error / 180) / (pwr /* * rChanger*/);
            //r = 0;

            // Normalize the values so none exceeds +/- 1.0
            frontLeft = pwr + r ;
            backLeft = pwr + r ;
            backRight = pwr - r ;
            frontRight = pwr - r ;

            frontLeft = -frontLeft;
            backLeft = -backLeft;

            max = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(frontRight), Math.abs(frontRight)));
            if (max > 1.0) {
                frontLeft = frontLeft / max;
                frontRight = frontRight / max;
                backLeft = backLeft / max;
                backRight = backRight / max;
            }



            telemetry.addData("front left", "%.2f", frontLeft);
            telemetry.addData("front right", "%.2f", frontRight);
            telemetry.addData("back left", "%.2f", backLeft);
            telemetry.addData("back right", "%.2f", backRight);

            telemetry.addData("current heading", formatAngle(currOrient.angleUnit, currOrient.firstAngle));
            telemetry.addData("target heading", formatAngle(targetOrient.angleUnit, targetOrient.firstAngle));

            telemetry.update();

            //send the power to the motors
            robot.frontLeftMotor.setPower(frontLeft);
            robot.backLeftMotor.setPower(backLeft);
            robot.backRightMotor.setPower(backRight);
            robot.frontRightMotor.setPower(frontRight);



        }

    }




    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }






}
