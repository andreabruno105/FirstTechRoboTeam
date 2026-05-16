
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="Low Left Start", group="Linear OpMode")
//@Disabled
public class LowLeft extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor collector = null;

    private DcMotor shootLeft = null;
    private DcMotor shootRight = null;

    //INIZIALIZZAZIONE SERVO SELETTORE

    final int    CYCLE_MS    =   50;     // period of each cycle
    final double MAX_POS     =  0.65;     // Maximum rotational position
    final double MIN_POS     =  0.37;     // Minimum rotational position




    // Define class members
    Servo selector;
    Servo leftServo;
    Servo rightServo;

    double  selectorPos = 0.5; // Start at halfway position

    double leftServoPos = 0;
    double rightServoPos = 0;

    @Override
    public void runOpMode() {


        //DICHIARAZIONE RUOTE CHE RACCOLGONO LE PALLE
        collector = hardwareMap.get(DcMotor.class, "collector");
        shootLeft = hardwareMap.get(DcMotor.class, "left_shoot");
        shootRight = hardwareMap.get(DcMotor.class, "right_shoot");



        //DICHIARAZIONE CONTROLLER
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        shootLeft.setDirection(DcMotor.Direction.REVERSE);
        shootRight.setDirection(DcMotor.Direction.FORWARD);

        collector.setDirection(DcMotor.Direction.REVERSE);


        //ATTIVAZIONE SERVO
        selector = hardwareMap.get(Servo.class, "servo_divisore");
        leftServo = hardwareMap.get(Servo.class, "left_servo");
        rightServo = hardwareMap.get(Servo.class, "right_servo");

        leftServo.setDirection(Servo.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();





        if(opModeIsActive()) {
            /*moveRobot(0, -0.5, 0);
            shootLeft.setPower(0.5);
            shootRight.setPower(0.5);
            leftServo.setPosition(0);
            rightServo.setPosition(0);
            sleep(3000);
            moveRobot(0, 0, 0.3);

            sleep(500);
            moveRobot(0,0.5,0);
            sleep(200);
            moveRobot(0,0,0);
            leftServo.setPosition(1);
            sleep(500);
            collector.setPower(0.7);
            leftServo.setPosition(0);
            sleep(500);
            leftServo.setPosition(1);
            sleep(500);
            leftServo.setPosition(0);
            sleep(500);
            rightServo.setPosition(1);
            sleep(500);
            rightServo.setPosition(0);
            sleep(500);*/

            shootLeft.setPower(0.75);
            shootRight.setPower(0.75);
            leftServo.setPosition(0);
            rightServo.setPosition(0);
            sleep(5000);
            collector.setPower(0.8);
            sleep(1000);
            leftServo.setPosition(1);
            rightServo.setPosition(1);
            sleep(1000);
            leftServo.setPosition(0);
            rightServo.setPosition(0);
            sleep(1000);
            leftServo.setPosition(1);
            rightServo.setPosition(1);
            shootLeft.setPower(0);
            shootRight.setPower(0);
            selector.setPosition(0.75);
            moveRobot(0, -0.5, 0);
            sleep(2000);
            moveRobot(0, 0, 0.5);
            sleep(1000);
            moveRobot(0, -0.5, 0);
            sleep(500);
            moveRobot(0, 0, 0);
            selector.setPosition(0.3);
            sleep(500);
            moveRobot(0, -0.5, 0);


        }

    }

    public void moveRobot(double x, double y, double yaw) {

        double frontLeftPower  = x - y - yaw;
        double frontRightPower = x + y + yaw;
        double backLeftPower   = x + y - yaw;
        double backRightPower  = x - y + yaw;

        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }


}


