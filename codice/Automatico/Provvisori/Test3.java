package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;
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


@TeleOp(name = "Test3", group = "Vision")
public class Test3 extends LinearOpMode {
    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel
    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_GREEN)   // Use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)   // Show contours on the Stream Preview
                .setBoxFitColor(0)       // Disable the drawing of rectangles
                .setCircleFitColor(Color.rgb(255, 255, 0)) // Draw a circle
                .setBlurSize(5)          // Smooth the transitions between different colors in image

                // the following options have been added to fill in perimeter holes.
                .setDilateSize(15)       // Expand blobs to fill any divots on the edges
                .setErodeSize(15)        // Shrink blobs back to original size
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

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);



        int centerx=160;
        int centerTolerance= 5;
        int counter=0;
        boolean isCentral=false;
        telemetry.addLine("Premi START");
        telemetry.update();
        boolean pallaDentro=false;
        waitForStart();
        telemetry.addLine("Started");
        telemetry.update();
        while (opModeIsActive() || opModeInInit()) {
            telemetry.addData("preview on/off", "... Camera Stream\n");
            float blobCenter=0;
            while ((blobCenter < centerx - centerTolerance || blobCenter > centerx + centerTolerance)&&!isCentral) {
                // Read the current list
                List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                        50, 20000, blobs);  // filter out very small blobs.

                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                        0.6, 1, blobs);
                telemetry.addLine("Circularity Radius Center");
                telemetry.addData("BLOBS", blobs.isEmpty()) ;
                if(!blobs.isEmpty()) {
                    ColorBlobLocatorProcessor.Blob b = blobs.get(0);
                    // Display the Blob's circularity, and the size (radius) and center location of its circleFit.

                    Circle circleFit = b.getCircle();
                    telemetry.addLine(String.format("%5.3f      %3d     (%3d,%3d)",
                            b.getCircularity(), (int) circleFit.getRadius(), (int) circleFit.getX(), (int) circleFit.getY()));

                    blobCenter = circleFit.getX();

                    if (blobCenter > centerx + centerTolerance) {
                        moveRobot(0, 0, -0.1);
                        if(counter%2==0) counter++;
                    }
                    else if (blobCenter < centerx - centerTolerance) {
                        moveRobot(0, 0, 0.1);
                        if(counter%2==1) counter++;
                    }
                    if(counter>4){
                        moveRobot(0,0,0);
                        isCentral=true;
                        telemetry.addData("Centrato", " ");
                    }


                }
                else {
                    moveRobot(0, 0, -0.3);
                    counter=0;
                    break;
                }



                telemetry.update();
            }

            telemetry.addData("Central: ", isCentral);
            telemetry.update();
            sleep(100);
            blobCenter=0;
            boolean empty=false;

            while(!empty&&isCentral) {
                List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                        50, 20000, blobs);  // filter out very small blobs.

                ColorBlobLocatorProcessor.Util.filterByCriteria(
                        ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                        0.6, 1, blobs);

                if (!blobs.isEmpty()) {
                    Circle circleFit = blobs.get(0).getCircle();
                    telemetry.addLine(String.format("%5.3f      %3d     (%3d,%3d)",
                            blobs.get(0).getCircularity(), (int) circleFit.getRadius(), (int) circleFit.getX(), (int) circleFit.getY()));


                    blobCenter = circleFit.getY();
                    moveRobot(0, -0.2, 0);
                }
                empty = blobs.isEmpty();
            }

            if(empty&&isCentral&&!pallaDentro) {
                moveRobot(0, -0.3, 0);
                sleep(1500);
                pallaDentro=true;
            }
            moveRobot(0, 0, 0);




        }



    }

    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
}
