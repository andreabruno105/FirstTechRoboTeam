package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "InseguePallinaDinamico", group = "Vision")
public class InseguePallinaDinamico extends LinearOpMode {

    private DcMotor collectorMotor;

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {

        // ===== MOTORI =====
        collectorMotor = hardwareMap.get(DcMotor.class, "collector");

        collectorMotor.setDirection(DcMotor.Direction.REVERSE);



        telemetry.addLine("Premi START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            collectorMotor.setPower(0.4);
            telemetry.addLine("Muovo il collector");
            telemetry.update();

        }



    }

}
