
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="CodiceManualeDoppio", group="Linear OpMode")
//@Disabled
public class TestMovimentoOpModeDoppioController extends LinearOpMode {

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





        while (opModeIsActive()) {



            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x * 0.85;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            //MUOVO SERVO SELETTORE

            if(gamepad2.b) {
                selectorPos = MAX_POS;
            }

            if(gamepad2.a) {
                selectorPos = MIN_POS;
            }


            if(gamepad2.x) {
                collector.setPower(0.7);
            }

            if(gamepad2.y) {
                collector.setPower(0);
            }



            if(gamepad2.left_bumper){
                leftServoPos = 1;
            }

            else {
                leftServoPos = 0;
            }


            if(gamepad2.right_bumper){
                rightServoPos = 1;
            }

            else {
                rightServoPos = 0;
            }

            if(gamepad2.right_trigger_pressed){
                shootLeft.setPower(0.6);
                shootRight.setPower(0.6);
            }

            if(gamepad2.left_trigger_pressed){
                shootLeft.setPower(0);
                shootRight.setPower(0);
            }

            if(gamepad2.dpad_up){
                shootLeft.setPower(0.7);
                shootRight.setPower(0.7);
            }



            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }


            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            // MUOVO IL SERVO
            selector.setPosition(selectorPos);
            leftServo.setPosition(leftServoPos);
            rightServo.setPosition(rightServoPos);
            sleep(CYCLE_MS);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();
        }

    }

}


