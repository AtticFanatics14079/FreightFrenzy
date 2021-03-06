package org.firstinspires.ftc.teamcode.Autonomous.RRPaths;//package org.firstinspires.ftc.teamcode.Autonomous.RRBasicPaths;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.acmerobotics.roadrunner.geometry.Vector2d;
//import com.acmerobotics.roadrunner.trajectory.Trajectory;
//import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
//import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilderKt;
//import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DistanceSensor;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.teamcode.Autonomous.RoadRunner.DriveConstants;
//import org.firstinspires.ftc.teamcode.Autonomous.RoadRunner.SampleMecanumDrive;
//import org.firstinspires.ftc.teamcode.Vision.scanPipeline;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//import org.openftc.easyopencv.OpenCvPipeline;
//
//@Config
//@Autonomous
//public class AllPathsVision extends LinearOpMode {
//
//    private DriveConstraints verySlow = new DriveConstraints(
//            30.0, 20.0, 0.0,
//            Math.toRadians(120.0), Math.toRadians(120.0), 0.0
//    );
//
//    private DriveConstraints slow = new DriveConstraints(
//            40.0, 25.0, 0.0,
//            Math.toRadians(120.0), Math.toRadians(120.0), 0.0
//    );
//
//    private DriveConstraints kindaFast = new DriveConstraints(
//            50.0, 30.0, 0.0,
//            Math.toRadians(120.0), Math.toRadians(120.0), 0.0
//    );
//
//    private DriveConstraints omegaFast = new DriveConstraints(
//            60.0, 55.0, 0.0,
//            Math.toRadians(120.0), Math.toRadians(120.0), 0.0
//    );
//
//    private Pose2d startPose = new Pose2d(-63.0, -35.0, Math.toRadians(0.0)); //-18.0
//    private Pose2d midwayShoot = new Pose2d(-38.0, -18.0);
//    private Pose2d powerShotShoot = new Pose2d(-24.0, -24.0);
//    private Pose2d highGoalShoot = new Pose2d(-16.0, -50.0);
//    private Pose2d path4dropoff = new Pose2d(43.0, -52.0);
//    private Pose2d pickup4 = new Pose2d(-55, -50, 0.0);
//    private Pose2d path1dropoff = new Pose2d(15.0, -32.0);
//    private Pose2d pickup1 = new Pose2d(-56.5, -45); //Was x = -38.5, y = -40 before moving to back of tape
//    private Pose2d path0dropoff = new Pose2d(-2.0, -56.0);
//    private Pose2d pickup0 = new Pose2d(-56.5, -52.0);
//    private Pose2d endLocation = new Pose2d(5.0, -44.0, 0.0);
//
//    public static double wobbleUp = 0.17, wobbleDown = 0.65, wobbleMid = 0.45, gripperOpen = 0, gripperClosed = 1, loaded = 0.48, reload = 0.14, path5highgoalX = -36, path5highgoalY = -40, offsetDivisor = 50, pshot1 = 1, pshot2 = 4.7, pshot3 = 8.3;
//
//    public static double multiplier = 0.97, sensorSideOffset = 8.20, sensorSideAngle = 0.66, sensorStrightOffset = 8, sensorStrightAngle = 0, rightDistMult = 1.33;
//
//    private Pose2d powerShotBackShoot = new Pose2d(-39.0,-35.0, Math.toRadians(pshot1)); //Is actually 1
//    private Pose2d ingestStack = new Pose2d(-16.0, -35.0, Math.toRadians(0));
//
//    private final int rows = 640;
//    private final int cols = 480;
//    public static int sampleWidth = 30;
//    public static int sampleHeight = 3;
//    public static Point topCenter = new Point(260, 130);
//    public static Point bottomCenter = new Point(260, 60);
//    public static Point leftBar1 = new Point(442, 360), leftBar2 = new Point(451, 436), rightBar1 = new Point(198, 359), rightBar2 = new Point(207, 435);
//    public static int thresh = 130;
//    public static int wobbleThresh = 145, initThresh = 133;
//    public static int stackSize = -1;
//    public static boolean properSetup = false;
//    private static double color1, color2;
//
//    public static int extract = 1;
//    public static int row = 320;
//
//    private StageSwitchingPipeline pipeline = new StageSwitchingPipeline();
//
//    public static boolean usingCamera = true;
//
//    OpenCvCamera webCam;
//
//    SampleMecanumDrive drive;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        drive = new SampleMecanumDrive(hardwareMap);
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//
//        DistanceSensor leSense = hardwareMap.get(DistanceSensor.class, "distanceRight");
//
//        if(usingCamera) {
//            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//            webCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam1"), cameraMonitorViewId);
//            webCam.openCameraDevice();//open camera
//            webCam.setPipeline(pipeline);//different stages
//            webCam.startStreaming(rows, cols, OpenCvCameraRotation.UPRIGHT);//display on RC
//
//            pipeline.initDetect = true;
//        }
//
//        drive.loader.setPosition(reload);
//
//        drive.wobble.setPosition(wobbleUp);
//
//        while(!isStarted() && !isStopRequested()) {
//            telemetry.addData("Le stack: ", stackSize);
//            telemetry.addData("Setup: ", properSetup);
//            telemetry.addData("Distance from wall: ", leSense.getDistance(DistanceUnit.INCH));
//            telemetry.update();
//        }
//
//        if(isStopRequested()) return;
//
//        int stackSize2 = stackSize;
//
//        drive.setPoseEstimate(startPose);
//
//        drive.shooter.setVelocity(-1600);
//
//        drive.wobble.setPosition(wobbleMid);
//
//        Trajectory goToShoot = drive.trajectoryBuilder(startPose)
//                .splineTo(powerShotBackShoot.vec(), powerShotBackShoot.getHeading())
//                .build();
//        drive.followTrajectory(goToShoot);
//
//        imuTurn(Math.toRadians(pshot1));
//        //telemetry.addLine("Finished turn");
//        //telemetry.update();
//
//        //stackSize2 = -1;
//
//        drive.loader.setPosition(loaded);
//        sleep(600);
//        drive.loader.setPosition(reload);
//        drive.shooter.setVelocity(-1580);
//        imuTurn(Math.toRadians(pshot2));
//        sleep(400);
//        drive.loader.setPosition(loaded);
//        sleep(600);
//        drive.loader.setPosition(reload);
//        drive.shooter.setVelocity(-1580);
//        imuTurn(Math.toRadians(pshot3));
//        sleep(400);
//        drive.loader.setPosition(loaded);
//        sleep(600);
//        drive.shooter.setVelocity(0);
//
//        double imuHeading = drive.imu.getAngularOrientation().firstAngle;
//        Pose2d currentPose = sensorPose();
//
//        drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//        //drive.ingester.setPower(1);
//
//        switch(stackSize2) {
//            case 0: {
//
//                drive.ingester.setPower(0);
//
//                imuTurn(0);
//                drive.setPoseEstimate(sensorPose());
//                //imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                //drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                Trajectory wobble1 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .strafeTo(new Vector2d(path0dropoff.getX(), path0dropoff.getY()), slow)
//                        .build();
//                drive.followTrajectory(wobble1);
//
//                drive.loader.setPosition(reload);
//
//                //currentPose = drive.getPoseEstimate();
//                //imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                //drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//                //drive.setPoseEstimate(sensorPose());
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(1200);
//
//                Trajectory wobble2 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
//                        .lineToLinearHeading(new Pose2d(pickup0.getX() + 24, pickup0.getY() + 2, Math.toRadians(180.0)), verySlow)
//                        .build();
//                drive.followTrajectory(wobble2);
//
//                pipeline.initDetect = false;
//
//                sleep(300);
//
//                double offset = pipeline.offset;
//
//                //currentPose = drive.getPoseEstimate();
//                //imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(sensorPose());
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() - offset - 1, imuHeading));
//
//                Trajectory pickupWobble = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineToConstantHeading(new Vector2d(pickup0.getX() + 6, pickup0.getY()), Math.toRadians(180), verySlow)
//                        .splineToConstantHeading(new Vector2d(pickup0.getX(), pickup0.getY()), Math.toRadians(180), verySlow)
//                        .build();
//                drive.followTrajectory(pickupWobble);
//
//                //currentPose = drive.getPoseEstimate();
//                //imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                //drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() + offset + 1, imuHeading));
//                drive.setPoseEstimate(sensorPose()); //Should account for offset, so no need for previous line.
//
//                drive.gripper.setPosition(gripperClosed);
//                sleep(1200);
//                drive.wobble.setPosition(wobbleMid);
//
//                Trajectory dropoff = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .lineToLinearHeading(new Pose2d(path0dropoff.getX() - 7, path0dropoff.getY() + 9, 0.0), slow)
//                        .build();
//                drive.followTrajectory(dropoff);
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(1500);
//
//                drive.wobble.setPosition(wobbleUp);
//                sleep(500);
//
//                Trajectory park1 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .strafeLeft(24)
//                        .build();
//                drive.followTrajectory(park1);
//
//                Trajectory park2 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .forward(14)
//                        .build();
//                drive.followTrajectory(park2);
//            }
//            break;
//            case 1: {
//
//                drive.ingester.setPower(1);
//
//                Trajectory wobble1 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineTo(path1dropoff.vec(), 0)
//                        .build();
//                drive.followTrajectory(wobble1);
//
//                drive.loader.setPosition(reload);
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(1200);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                Trajectory wobble2 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
//                        .lineToLinearHeading(new Pose2d(pickup1.getX() + 24, pickup1.getY(), Math.toRadians(180.0)), slow)
//                        .build();
//                drive.followTrajectory(wobble2);
//
//                pipeline.initDetect = false;
//
//                sleep(300);
//
//                double offset = pipeline.offset;
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() - offset + 1, currentPose.getHeading()));
//
//                Trajectory pickupWobble = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineToConstantHeading(new Vector2d(pickup1.getX() + 6, pickup1.getY() - 1), Math.toRadians(180), verySlow)
//                        .splineToConstantHeading(new Vector2d(pickup1.getX(), pickup1.getY() - 1), Math.toRadians(180), verySlow)
//                        .build();
//                drive.followTrajectory(pickupWobble);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() + offset - 1, imuHeading));
//
//                drive.gripper.setPosition(gripperClosed);
//                sleep(1000);
//                drive.wobble.setPosition(wobbleMid);
//                drive.shooter.setVelocity(-1640);
//
//                Trajectory shoot = drive.trajectoryBuilder(drive.getPoseEstimate(), false)
//                        .lineToLinearHeading(new Pose2d(highGoalShoot.getX(), highGoalShoot.getY(), Math.toRadians(0.0)), slow)
//                        .build();
//                drive.followTrajectory(shoot);
//
//                drive.ingester.setPower(0);
//
//                drive.loader.setPosition(loaded);
//                sleep(600);
//                drive.loader.setPosition(reload);
//
//                Trajectory drop = drive.trajectoryBuilder(drive.getPoseEstimate(), false)
//                        .splineTo(new Vector2d(path1dropoff.getX(), path1dropoff.getY() + 6), Math.toRadians(0.0))
//                        .build();
//                drive.followTrajectory(drop);
//
//                drive.shooter.setVelocity(0);
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(1200);
//
//                Trajectory toPark = drive.trajectoryBuilder(drive.getPoseEstimate(), false)
//                        .lineToLinearHeading(endLocation)
//                        .build();
//                drive.followTrajectory(toPark);
//            }
//            break;
//            case 4: {
//
//                drive.ingester.setPower(1);
//
//                Trajectory wobble1 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineTo(ingestStack.vec(), ingestStack.getHeading())
//                        .splineToConstantHeading(new Vector2d(path4dropoff.getX()-48, path4dropoff.getY()-6), Math.toRadians(0))
//                        .splineToConstantHeading(new Vector2d(path4dropoff.getX(), path4dropoff.getY()), Math.toRadians(0))
//                        .build();
//                drive.followTrajectory(wobble1);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                if(imuHeading < 0) imuHeading += 2 * Math.PI;
//                System.out.println("IMU: " + imuHeading);
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(850); //Extra 500 to let it continue ingesting
//
//                Trajectory wobble2 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
//                        .lineToLinearHeading(new Pose2d(pickup4.getX() + 24, pickup4.getY() + 4, Math.toRadians(180.0)))
//                        .build();
//                drive.followTrajectory(wobble2);
//
//                pipeline.initDetect = false;
//
//                imuTurn(Math.PI);
//
//                double offset = pipeline.offset;
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                System.out.println("IMU: " + imuHeading + ", Odo: " + currentPose.getHeading());
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() - offset - 4, currentPose.getHeading()));
//
//                Trajectory pickupWobble = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineToConstantHeading(new Vector2d(pickup4.getX() + 6, pickup4.getY()), Math.toRadians(180))
//                        .splineToConstantHeading(new Vector2d(pickup4.getX(), pickup4.getY()), Math.toRadians(180))
//                        .build();
//                drive.followTrajectory(pickupWobble);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY() + offset + 4, imuHeading));
//
//                drive.gripper.setPosition(gripperClosed);
//                sleep(1200);
//                drive.wobble.setPosition(wobbleUp);
//                drive.shooter.setVelocity(-1660);
//                drive.loader.setPosition(reload);
//                sleep(200);
//
//                Trajectory shootdrop = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .lineToLinearHeading(new Pose2d(path5highgoalX, path5highgoalY, Math.toRadians(-9)))
//                        .build();
//                drive.followTrajectory(shootdrop);
//
//                drive.wobble.setPosition(wobbleMid);
//
//                sleep(500);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                System.out.println("Pose: " + drive.getPoseEstimate());
//
//                drive.loader.setPosition(loaded);
//                sleep(600);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(-1600);
//                sleep(400);
//                drive.loader.setPosition(loaded);
//                sleep(600);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(-1600);
//                sleep(400);
//                drive.loader.setPosition(loaded);
//                sleep(600);
//                drive.shooter.setVelocity(0);
//
//                Trajectory drop = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineTo(new Vector2d(path4dropoff.getX()-36, path4dropoff.getY() + 16), 0, omegaFast)
//                        .splineTo(new Vector2d(path4dropoff.getX()-24, path4dropoff.getY() + 16), 0, omegaFast)
//                        .splineTo(new Vector2d(path4dropoff.getX(), path4dropoff.getY() + 10), 0, omegaFast)
//                        .build();
//                drive.followTrajectory(drop);
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//                sleep(850);
//                drive.shooter.setVelocity(-1720);
//                drive.loader.setPosition(reload);
//
//                currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                System.out.println("Pose: " + drive.getPoseEstimate());
//
//                Trajectory toPark = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
//                        .back(10, omegaFast)
//                        .addDisplacementMarker(() -> {
//                            drive.gripper.setPosition(gripperClosed);
//                            drive.wobble.setPosition(wobbleUp);
//                        })
//                        .back(30, omegaFast)
//                        .build();
//                drive.followTrajectory(toPark);
//
//                /*currentPose = drive.getPoseEstimate();
//                imuHeading = drive.imu.getAngularOrientation().firstAngle;
//                drive.setPoseEstimate(new Pose2d(currentPose.getX(), currentPose.getY(), imuHeading));
//
//                imuTurn(0);
//
//                System.out.println("Pose: " + drive.getPoseEstimate());
//
//                drive.loader.setPosition(loaded);
//                sleep(600);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(0);
//
//                drive.wobble.setPosition(wobbleDown);
//                drive.gripper.setPosition(gripperOpen);
//
//                Trajectory park = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .forward(10)
//                        .build();
//                drive.followTrajectory(park);
//
//                 */
//            }
//            break;
//            case 69: {
//                drive.shooter.setVelocity(-1700);
//                Trajectory go = drive.trajectoryBuilder(startPose)
//                        .splineTo(new Vector2d(-1, -47), Math.toRadians(0))
//                        .build();
//                drive.followTrajectory(go);
//
//                drive.setPoseEstimate(new Pose2d(-1, -47, Math.toRadians(180)));
//                System.out.println("Pose: " + drive.getPoseEstimate());
//
//                drive.loader.setPosition(loaded);
//                sleep(800);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(-1740);
//                sleep(800);
//                drive.loader.setPosition(loaded);
//                sleep(800);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(-1720);
//                sleep(800);
//                drive.loader.setPosition(loaded);
//                sleep(800);
//                drive.loader.setPosition(reload);
//                drive.shooter.setVelocity(0);
//                drive.ingester.setPower(1);
//
//                Trajectory move = drive.trajectoryBuilder(drive.getPoseEstimate())
//                        .splineTo(new Vector2d(-1, -40), Math.toRadians(90))
//                        .build();
//                drive.followTrajectory(move);
//
//                System.out.println("Pose: " + drive.getPoseEstimate());
//            }
//        }
//
//        pipeline.initDetect = true;
//    }
//
//    public void imuTurn(double angle) {
//
//        double imuHeading = drive.imu.getAngularOrientation().firstAngle;
//
//        while((Math.abs(imuHeading - angle)>Math.toRadians(0.1)) && !isStopRequested() && opModeIsActive()){
//            drive.update();
//            imuHeading = drive.imu.getAngularOrientation().firstAngle;
//            if(imuHeading < 0) imuHeading += 2 * Math.PI;
//            //currentPose = drive.getPoseEstimate();
//            double p = 0.3, f = 0.05;
//            int invert = (angle + (2 * Math.PI - imuHeading)) % (2 * Math.PI) > Math.PI ? 1 : -1;
//            double power = invert * p * (Math.abs(imuHeading - angle) > Math.PI ? (Math.abs((imuHeading > Math.PI ? 2 * Math.PI : 0) - imuHeading) + Math.abs((angle > Math.PI ? 2 * Math.PI : 0) - angle)) : Math.abs(imuHeading - angle)); //Long line, but the gist is if you're calculating speed in the wrong direction, git gud.
//            power += (power > 0 ? f : -f);
//            drive.setMotorPowers(power, power, -power, -power);
//
//            telemetry.addData("IMU Heading: ", imuHeading);
//            telemetry.addData("Power: ", power);
//            telemetry.addData("Invert: ", invert);
//            telemetry.addData("Invert calc: ", (angle + (360 - imuHeading)));
//            telemetry.update();
//        }
//        drive.setMotorPowers(0, 0, 0, 0);
//    }
//
////    public Pose2d sensorPose() {
////        //Do not call this in a situation where distance sensors could hit the same wall
////        //NOTE: DO NOT call repeatedly (aka every loop) or the y position will not update properly (while using odo).
////
////        double imuHeading = drive.imu.getAngularOrientation().firstAngle;
////        double left = drive.left.getDistance(DistanceUnit.INCH);
////        double right = drive.right.getDistance(DistanceUnit.INCH);
////        double front = drive.back.getDistance(DistanceUnit.INCH);
////        double back = drive.backDist.getDistance(DistanceUnit.INCH);
////        System.out.println("Input : " + left);
////        System.out.println("Right input: " + right);
////        System.out.println("Front input : " + front);
////        System.out.println("Back input: " + back);
////        double correctedSideAngle = sensorSideAngle; //Accounts for X vs. Y.
////        double correctedStraightAngle = sensorStrightAngle; //Accounts for X vs. Y.
////        double correctedHeading = imuHeading > 0 ? (imuHeading + Math.PI / 4) % (Math.PI / 2) - Math.PI / 4 : -((Math.abs(imuHeading) + Math.PI / 4) % (Math.PI / 2) - Math.PI / 4); //Correct heading in each quadrant, as a new quadrant switches what wall it should be seeing.
////        left *= (left < 100) ? Math.abs(Math.cos(correctedHeading)) * multiplier : 0; //Gets distance from wall as a straight line
////        System.out.println("Distance : " + left);
////        right *= (right < 100) ? Math.abs(Math.cos(correctedHeading)) * multiplier * rightDistMult : 0;
////        front *= (front < 100) ? Math.abs(Math.cos(correctedHeading)) * multiplier : 0;
////        back *= (back < 100) ? Math.abs(Math.cos(correctedHeading)) * multiplier : 0;
////        left += (left > 0) ? sensorSideOffset * Math.abs(Math.cos(correctedSideAngle + correctedHeading)) : 200; //First quadrant, deals with small cosine values
////        System.out.println("Center Distance : " + left);
////        right += (right > 0) ? sensorSideOffset * Math.abs(Math.cos(-correctedSideAngle + correctedHeading)) : 200; //Second quadrant, deals with small cosine values
////        System.out.println("Right : " + right);
////        front += (front > 0) ? sensorStrightOffset * Math.abs(Math.cos(correctedStraightAngle + correctedHeading)) : 200; //First quadrant, deals with small cosine values
////        System.out.println("Front center : " + front);
////        back += (back > 0) ? sensorStrightOffset * Math.abs(Math.cos(-correctedStraightAngle + correctedHeading)) : 200; //Second quadrant, deals with small cosine values
////        System.out.println("Back center: " + back);
////
////        double distanceYLeft = Math.abs(imuHeading - Math.PI / 4) < Math.PI / 2 ? (Math.abs(imuHeading) < Math.PI / 4 ? left : front) : (Math.abs(imuHeading) > 3 * Math.PI / 4 ? right : back); //Switches which distance sensor input corresponds to what actual side relative to robot.
////        System.out.println("Actual dist : " + distanceYLeft);
////        double distanceYRight = Math.abs(imuHeading - Math.PI / 4) < Math.PI / 2 ? (Math.abs(imuHeading) < Math.PI / 4 ? right : back) : (Math.abs(imuHeading) > 3 * Math.PI / 4 ? left : front);
////        System.out.println("Actual right : " + distanceYRight);
////        double distanceXFront = Math.abs(imuHeading - Math.PI / 4) < Math.PI / 2 ? (Math.abs(imuHeading) < Math.PI / 4 ? front : right) : (Math.abs(imuHeading) > 3 * Math.PI / 4 ? back : left);
////        System.out.println("Actual front : " + distanceYRight);
////        double distanceXBack = Math.abs(imuHeading - Math.PI / 4) < Math.PI / 2 ? (Math.abs(imuHeading) < Math.PI / 4 ? back : left) : (Math.abs(imuHeading) > 3 * Math.PI / 4 ? front : right);
////        System.out.println("Actual back : " + distanceYRight);
////
////        Pose2d currentPose = drive.getPoseEstimate();
////        double poseX = currentPose.getX(), poseY = currentPose.getY();
////
////        poseY = (distanceYRight < distanceYLeft) ? - 87 + Math.abs(distanceYRight) : (distanceYLeft < 100) ? 9 - Math.abs(distanceYLeft) : poseY; //Sets up 0 when robot jammed against left wall, grabs smaller of two distances.
////        poseX = (distanceXBack < distanceXFront) ? - 135 + Math.abs(distanceXBack) : (distanceXFront < 100) ? 9 - Math.abs(distanceXFront) : poseX; //Sets up 0 when robot jammed against front wall
////        System.out.println("Pose Y: " + poseY);
////
////        return new Pose2d(poseX, poseY, imuHeading);
////    }
//
//    static class StageSwitchingPipeline extends OpenCvPipeline {
//
//        public double middle = -1, offset = 0;
//
//        public static boolean initDetect = true;
//
//        Mat rawMat = new Mat();
//        Mat YCRCBMat = new Mat();
//        Mat ExtractMat = new Mat();
//        Mat MediumRareMat = new Mat();
//        Mat redMat = new Mat();
//
//        enum Stage
//        {
//            RAW,
//            RED,
//            EXTRACT,
//            MEDIUMRARE
//        }
//
//        private StageSwitchingPipeline.Stage stageToRenderToViewport = StageSwitchingPipeline.Stage.RAW;
//        private StageSwitchingPipeline.Stage[] stages = StageSwitchingPipeline.Stage.values();
//
//        @Override
//        public void onViewportTapped()
//        {
//            /*
//             * Note that this method is invoked from the UI thread
//             * so whatever we do here, we must do quickly.
//             */
//
//            int currentStageNum = stageToRenderToViewport.ordinal();
//
//            int nextStageNum = currentStageNum + 1;
//
//            if(nextStageNum >= stages.length)
//            {
//                nextStageNum = 0;
//            }
//
//            stageToRenderToViewport = stages[nextStageNum];
//        }
//
//        @Override
//        public Mat processFrame(Mat input)
//        {
//            if (initDetect) {
//                rawMat = input;
//                //Imgproc.cvtColor(input, YCRCBMat, Imgproc.COLOR_BGR2YCrCb);
//                //YCRCBMat = rawMat;
//                Imgproc.cvtColor(input, YCRCBMat, Imgproc.COLOR_BGR2HSV);
//                Core.extractChannel(YCRCBMat, ExtractMat, extract);
//                Imgproc.cvtColor(ExtractMat, MediumRareMat, Imgproc.COLOR_GRAY2RGB);
//
//                Core.extractChannel(rawMat, redMat, 0);
//
//                Point topLeft1 = new Point(topCenter.x - sampleWidth,topCenter.y - sampleHeight);
//                Point bottomRight1 = new Point(topCenter.x + sampleWidth, topCenter.y + sampleHeight);
//                Point topLeft2 = new Point(bottomCenter.x - sampleWidth,bottomCenter.y - sampleHeight);
//                Point bottomRight2 = new Point(bottomCenter.x +sampleWidth, bottomCenter.y + sampleHeight);
//
//                color1 = 0;
//                color2 = 0;
//
//                for(int i = (int)(topLeft1.x); i <= (int)(bottomRight1.x); i++){
//                    for(int j = (int)topLeft1.y;  j <= (int)bottomRight1.y; j++){
//                        color1 += ExtractMat.get(j, i)[0];
//                    }
//                }
//                color1 /= (2*sampleWidth + 1)*(2*sampleHeight + 1);
//
//                for(int i = (int)(topLeft2.x); i <= (int)(bottomRight2.x); i++){
//                    for(int j = (int)(topLeft2.y);  j <= (int)(bottomRight2.y); j++){
//                        color2 += ExtractMat.get(j, i)[0];
//                    }
//                }
//                color2 /= (2*sampleWidth + 1)*(2*sampleHeight + 1);
//
//                boolean yellowness1 = color1 > thresh;
//                boolean yellowness2 = color2 > thresh;
//
//                stackSize = yellowness1 ? 4 : yellowness2 ? 1 : 0;
//
//                int numPixels = 0;
//                color1 = 0;
//                color2 = 0;
//
//                for(int i = (int)(leftBar1.x); i <= (int)(leftBar2.x); i++){
//                    for(int j = (int)leftBar1.y;  j <= (int)leftBar2.y; j++){
//                        color1 += redMat.get(j, i)[0];
//                        numPixels++;
//                    }
//                }
//                color1 /= numPixels;
//
//                numPixels = 0;
//
//                for(int i = (int)(topLeft2.x); i <= (int)(bottomRight2.x); i++){
//                    for(int j = (int)(topLeft2.y);  j <= (int)(bottomRight2.y); j++){
//                        color2 += redMat.get(j, i)[0];
//                        numPixels++;
//                    }
//                }
//                color2 /= numPixels;
//
//                properSetup = (color1 > initThresh) && (color2 > initThresh);
//
//                Imgproc.rectangle(MediumRareMat, topLeft1, bottomRight1, yellowness1 ? new Scalar(0, 255, 0) : new Scalar(255, 0, 0));
//                Imgproc.rectangle(MediumRareMat, topLeft2, bottomRight2, yellowness2 ? new Scalar(0, 255, 0) : new Scalar(255, 0, 0));
//
//                Imgproc.rectangle(MediumRareMat, leftBar1, leftBar2, properSetup ? new Scalar(255, 0, 200) : new Scalar(50, 100, 255));
//                Imgproc.rectangle(MediumRareMat, rightBar1, rightBar2, properSetup ? new Scalar(255, 0, 200) : new Scalar(50, 100, 255));
//
//                Core.flip(MediumRareMat, MediumRareMat, -1);
//                Core.flip(redMat, redMat, -1);
//            }
//            else{
//                rawMat = input;
//                //Imgproc.cvtColor(input, YCRCBMat, Imgproc.COLOR_BGR2YCrCb);
//                //YCRCBMat = rawMat;
//                Imgproc.cvtColor(input, YCRCBMat, Imgproc.COLOR_BGR2HSV);
//                Core.extractChannel(YCRCBMat, ExtractMat, extract);
//                Imgproc.cvtColor(ExtractMat, MediumRareMat, Imgproc.COLOR_GRAY2RGB);
//                Imgproc.line(MediumRareMat, new Point(0, row), new Point(640, row), new Scalar(255,0,0), 3);
//                double wobbleLeft = -1, wobbleRight = -1;
//                for(int x = 0; x < MediumRareMat.cols(); x++){
//                    int counter = 0;
//                    double[] pixel = ExtractMat.get(row,x);
//                    if(pixel[0]>wobbleThresh){
//                        Imgproc.line(MediumRareMat, new Point(x, 300), new Point(x, 340), new Scalar(0,255,0), 3);
//                        if((x<630 && x>10) && (ExtractMat.get(row, x-8)[0]>wobbleThresh) && (ExtractMat.get(row, x+8)[0]>wobbleThresh)){
//                            if(wobbleLeft == -1) wobbleLeft = x;
//                            wobbleRight = x;
//                            Imgproc.line(MediumRareMat, new Point(x, 0), new Point(x, 480), new Scalar(0,0,255), 5);
//                        }
//                    }
//                }
//                if(wobbleLeft != wobbleRight) {
//                    middle = (wobbleLeft + wobbleRight) / 2.0;
//                    offset = (320 - middle) / offsetDivisor - 2.5;
//                }
//            }
//            switch (stageToRenderToViewport){
//                case RAW:
//                {
//                    return MediumRareMat;
//                }
//                case EXTRACT:
//                {
//                    return ExtractMat;
//                }
//                case RED:
//                {
//                    return redMat;
//                }
//                default:
//                {
//                    return input;
//                }
//            }
//        }
//    }
//}
