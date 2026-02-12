package org.firstinspires.ftc.robotcontroller.external.samples.externalhardware;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Test Base", group="Test")
public class Test extends LinearOpMode {

    @Override
    public void runOpMode() {

        telemetry.addLine("Programma minimale avviato correttamente!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Il robot funziona!");
            telemetry.update();
            sleep(500); // pausa per non saturare la CPU
        }
    }
}
