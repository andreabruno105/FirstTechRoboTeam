package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;

import java.util.List;

@TeleOp(name = "InseguePallinaStaticoDueColoriRisucchio", group = "Vision")
public class InseguimentoDueColoriConRisucchio extends LinearOpMode {

    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private DcMotor collector;
    Servo divisor;

    private static final int GREEN=0;
    private static final int PURPLE=1;

    private static final double COLLECTOR_SPEED= 0.4;
    static final double INCREMENT   = 0.01;
    static final double GREEN_TANK  =  0.75;
    static final double PURLE_TANK  =  0.30;

    // ===== NUOVE VARIABILI SERVO =====
    double currentPosition = GREEN_TANK;
    double targetPosition = GREEN_TANK;

    double positionInit=0.5;

    private int ballsNumber=0;

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {

        ColorBlobLocatorProcessor colorLocatorGreen = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_GREEN)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)
                .setBoxFitColor(0)
                .setCircleFitColor(Color.rgb(255, 255, 0))
                .setBlurSize(5)
                .setDilateSize(15)
                .setErodeSize(15)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();

        ColorBlobLocatorProcessor colorLocatorPurple = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_PURPLE)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)
                .setBoxFitColor(0)
                .setCircleFitColor(Color.rgb(255, 255, 0))
                .setBlurSize(5)
                .setDilateSize(15)
                .setErodeSize(15)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();

        ColorBlobLocatorProcessor actualColorLocator=colorLocatorGreen;

        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorLocatorPurple)
                .addProcessor(colorLocatorGreen)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();

        portal.setProcessorEnabled(actualColorLocator, true);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        collector = hardwareMap.get(DcMotor.class, "collector");
        divisor = hardwareMap.get(Servo.class, "divisor");

        divisor.setPosition(positionInit);

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        collector.setDirection(DcMotor.Direction.REVERSE);

        divisor.setPosition(targetPosition);



        int centerx = 160;
        int centerTolerance = 5;

        boolean isCentral = false;
        boolean pallaDentro = false;

        telemetry.addLine("Premi START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            if (!isCentral) {
                isCentral = centerBlob(actualColorLocator, centerx, centerTolerance);
            }

            sleep(100);

            boolean empty = false;

            while (opModeIsActive() && isCentral && !empty) {

                List<ColorBlobLocatorProcessor.Blob> blobs = actualColorLocator.getBlobs();

                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                        2000, 7000, blobs);

                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                        0.6, 1, blobs);

                empty = blobs.isEmpty();

                if (!empty) {
                    moveRobot(0, -0.2, 0);
                }

                //updateServoGradual();   // <-- aggiornamento continuo servo
            }

            if (empty && isCentral && !pallaDentro) {
                moveRobot(0, -0.3, 0);
                collector.setPower(COLLECTOR_SPEED);
                sleep(1500);
                pallaDentro = true;
                collector.setPower(0);
                ballsNumber=(ballsNumber+1)%4;
            }

            if(ballsNumber==1){
                actualColorLocator=colorLocatorPurple;
                portal.setProcessorEnabled(actualColorLocator, true);
                targetPosition = PURLE_TANK;   // <-- cambiato
                divisor.setPosition(targetPosition);
            }
            else if(ballsNumber==3){
                actualColorLocator=colorLocatorGreen;
                portal.setProcessorEnabled(actualColorLocator, true);
                targetPosition = GREEN_TANK;   // <-- cambiato
                divisor.setPosition(targetPosition);
                ballsNumber=0;
            }

            isCentral=false;
            pallaDentro=false;

            //updateServoGradual();   // <-- aggiornamento servo
            moveRobot(0, 0, 0);
        }
    }

    private void updateServoGradual() {

        if (Math.abs(targetPosition - currentPosition) > INCREMENT) {

            if (targetPosition > currentPosition) {
                currentPosition += INCREMENT;
            } else {
                currentPosition -= INCREMENT;
            }

            divisor.setPosition(currentPosition);
        }
        else {
            currentPosition = targetPosition;
            divisor.setPosition(currentPosition);
        }
    }

    private boolean centerBlob(ColorBlobLocatorProcessor actualColorLocator, int centerx, int centerTolerance) {

        int counter = 0;
        float blobCenter = 0;

        while (opModeIsActive() &&
                (blobCenter < centerx - centerTolerance ||
                        blobCenter > centerx + centerTolerance)) {

            List<ColorBlobLocatorProcessor.Blob> blobs = actualColorLocator.getBlobs();

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                    2000, 7000, blobs);

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                    0.6, 1, blobs);

            if (!blobs.isEmpty()) {

                Circle circleFit = blobs.get(0).getCircle();
                blobCenter = circleFit.getX();

                if (blobCenter > centerx + centerTolerance) {
                    moveRobot(0, 0, -0.1);
                    if (counter % 2 == 0) counter++;
                }
                else if (blobCenter < centerx - centerTolerance) {
                    moveRobot(0, 0, 0.1);
                    if (counter % 2 == 1) counter++;
                }

                if (counter > 4) {
                    moveRobot(0, 0, 0);
                    return true;
                }
            }
            else {
                moveRobot(0, 0, -0.3);
                return false;
            }

            updateServoGradual();  // <-- anche durante centraggio
        }

        moveRobot(0, 0, 0);
        return true;
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
