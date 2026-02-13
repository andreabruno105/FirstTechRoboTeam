package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;

import java.util.List;

@TeleOp(name = "Auto Ball Tracker", group = "Vision")
public class TestPallina2 extends LinearOpMode {

    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private enum State {
        SEARCHING,
        CENTERING,
        APPROACHING,
        STOPPED
    }

    @Override
    public void runOpMode() {

        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_GREEN)
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-1, 1, 1, -1))
                .setDrawContours(true)
                .setCircleFitColor(Color.YELLOW)
                .setBlurSize(5)
                .setDilateSize(10)
                .setErodeSize(10)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();

        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        int centerX = 160;              // centro immagine 320px
        int tolerance = 15;
        int bottomLimit = 210;          // quasi fondo immagine

        State currentState = State.SEARCHING;

        waitForStart();

        while (opModeIsActive()) {

            List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                    100, 20000, blobs);

            ColorBlobLocatorProcessor.Util.filterByCriteria(
                    ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                    0.6, 1, blobs);

            boolean ballFound = !blobs.isEmpty();

            if (ballFound) {

                Circle circle = blobs.get(0).getCircle();
                double blobX = circle.getX();
                double blobY = circle.getY();

                switch (currentState) {

                    case SEARCHING:
                        currentState = State.CENTERING;
                        break;

                    case CENTERING:

                        if (blobX > centerX + tolerance) {
                            moveRobot(0, 0, -0.15); // ruota destra
                        }
                        else if (blobX < centerX - tolerance) {
                            moveRobot(0, 0, 0.15); // ruota sinistra
                        }
                        else {
                            moveRobot(0,0,0);
                            currentState = State.APPROACHING;
                        }
                        break;

                    case APPROACHING:

                        // Se si sposta, torna a centrare
                        if (blobX > centerX + tolerance || blobX < centerX - tolerance) {
                            currentState = State.CENTERING;
                        }
                        else if (blobY < bottomLimit) {
                            moveRobot(0.25, 0, 0); // avanti
                        }
                        else {
                            moveRobot(0,0,0);
                            currentState = State.STOPPED;
                        }
                        break;

                    case STOPPED:
                        moveRobot(0,0,0);
                        break;
                }

                telemetry.addData("State", currentState);
                telemetry.addData("X", blobX);
                telemetry.addData("Y", blobY);

            } else {
                // Se non trova palline → ricerca
                currentState = State.SEARCHING;
                moveRobot(0, 0, 0.2); // ruota su se stesso
                telemetry.addLine("Searching...");
            }

            telemetry.update();
        }
    }

    public void moveRobot(double x, double y, double yaw) {

        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

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
