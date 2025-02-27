// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.server.PathPlannerServer;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Commands.Autos.Autos;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Subsystems.Arm;
import frc.robot.Subsystems.CowCatcher;
import frc.robot.Subsystems.Drive;
import frc.robot.Subsystems.Vision;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.Logger;

public class RobotContainer implements Loggable {

  public CommandXboxController controller;
  Vision visionSub;
  Drive driveSub;
  Arm arm;
  CowCatcher cowCatcher;
  private boolean isKeyboard = false;

  public RobotContainer() {
    PathPlannerServer.startServer(5811);
    System.out.println("Robot Start");

    controller = new CommandXboxController(OperatorConstants.Driver_Controller_ID);
    visionSub = new Vision();
    driveSub = new Drive(visionSub);
    cowCatcher = new CowCatcher();
    arm  = new Arm();

    Logger.configureLoggingAndConfig(this, false);
    configureBindings();
  }

  private void configureBindings() {
    //moves cowcatcher
    controller.x().onTrue(cowCatcher.toggle_Full_Command());
    controller.y().onTrue(cowCatcher.toggle_Half_Command());
    controller.a().onTrue(cowCatcher.moveToHalf());
    //shifts gears
    controller.rightBumper().onTrue(driveSub.Shift());

    // sets weather to use keyboard or controller
    if (Robot.isReal() || !isKeyboard) {
      driveSub.setDefaultCommand(driveSub.ArcadeDrive(() -> controller.getRightTriggerAxis() - controller.getLeftTriggerAxis(), controller::getLeftX));
    } else if (isKeyboard) {
      driveSub.setDefaultCommand(driveSub.ArcadeDrive(controller::getLeftY, controller::getLeftX));
     }
  }

  public Command getAutonomousCommand() {
    return Autos.DevPath(driveSub, "TestPath");
  }
}
