// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.AftershockXboxController;
import java.util.Arrays;
import frc.robot.commands.DelayCommand;
import frc.robot.commands.PrintCommand;

public class RobotContainer {

  //private final REVDigitBoard revDigitBoard = new REVDigitBoard();
  private final REVDigitBoardController revDigitBoardController = new REVDigitBoardController();
  private final AftershockXboxController mControllerPrimary = new AftershockXboxController(0);
  private Command commandTable[][][] = new Command[2][10][100];  // Indices: Color, Position, Scenario. 

  public RobotContainer() {
    System.out.println("RobotContainer constructor");
    configureBindings();
  }

  public void initialize() {
    revDigitBoardController.clear();
    revDigitBoardController.display("DEAD");
    //Arrays.fill(commandTable, Commands.none());
    
    System.out.println("Ordinal: Red: " + REVDigitBoardController.RobotColorEnum.eRed.ordinal() + 
      "  Blue: " + REVDigitBoardController.RobotColorEnum.eBlue.ordinal());

    setAllSequenceNames();

    commandTable[0][0][0] = Commands.none();  // Being explicit: Reserve 0,0,0 for No Command, particularly for unexpected reboot keep robot from runnng inccorect sequence.
    commandTable[REVDigitBoardController.RobotColorEnum.eRed.ordinal()][1][1] = new PrintCommand("A PrintCommand (Red, 1, 1)"); // Change this example to a useful sequence.
    commandTable[REVDigitBoardController.RobotColorEnum.eRed.ordinal()][1][2] = sequenceExample1; // Change this example to a useful sequence.
    commandTable[REVDigitBoardController.RobotColorEnum.eBlue.ordinal()][1][1] = sequenceExample2;  // Change this example to a useful sequence.
  }

  private void configureBindings() {

    System.out.println("RobotContainer.configureBindings()");

    // Note: Bindings do not trigger unless RoboRIO is enabled via Driver Station.

    Trigger turboButton = new Trigger(()-> mControllerPrimary.getRawButton(1)); // XBoxController A button
    turboButton.onTrue(new InstantCommand(()->{
      System.out.println("XboxController 1 Pressed");
    })).onFalse(new InstantCommand(()->{
      System.out.println("XboxController 1 Released");
    }));


  }

  public void processRevDigitBoardController() {
    revDigitBoardController.processRevDigitBoardController();
  }

  public void logRevDigitBoardControllerState() {
    revDigitBoardController.logRevDigitBoardControllerState();
  }

  /////////////////// Trigger objects only do work when Drive Station is ENABLE,
  //  which fails to serve our purpose to make setting changes while the robot is disabled (before match start).
  //  Left working tested code here for reference, but can't think of a use-case at real events.  This is why
  //  switched to direct polling of the pot and buttons in the RevDigiBoardController class.
  //
  //   Trigger TriggerA = new Trigger(() -> revDigitBoardController.getButtonA());
  //   TriggerA.onTrue(new InstantCommand(()->{
  //     revDigitBoardController.setRobotColor(RobotColorEnum.eRed);
  //     revDigitBoardController.m_RobotColor = RobotColorEnum.eRed;
  //     System.out.println("A Pressed");
  //   }))
  //   .onFalse(new InstantCommand(()->{
  //     System.out.println("A NOT PRESSED");
  //   }))
  //   ;
  //
  //   Trigger TriggerB = new Trigger(() -> revDigitBoardController.getButtonB());
  //   TriggerB.onTrue(new InstantCommand(()->{
  //     revDigitBoardController.setRobotColor(RobotColorEnum.eBlue);
  //     revDigitBoardController.m_RobotColor = RobotColorEnum.eBlue;
  //     System.out.println("B Pressed");
  //   }))
  //   .onFalse(new InstantCommand(()->{
  //     System.out.println("B NOT PRESSED");
  //   }))
  //   ;
  // }

  public Command getAutonomousCommand() {
    //return Commands.print("No autonomous command configured");

    int color = revDigitBoardController.getRobotColor().ordinal();
    int pos = revDigitBoardController.getRobotStartPositionNumber();
    int scene = revDigitBoardController.getScenarioNumberForPosition();

    Command theCommand = commandTable
              [color]
              [pos]
              [scene];

      return theCommand;
  }

  private void setAllSequenceNames() {
    // Need to do this or else getName retuns SequentialCommandGroup instead of the identifier name.
    sequenceExample1.setName("sequenceExample1");
    sequenceExample2.setName("sequenceExample2");
  }

  private Command sequenceExample1 = new SequentialCommandGroup(
    (new DelayCommand(0.1)).andThen(new PrintCommand("Example1: (Red, 1, 2)"))
  ); 

  private Command sequenceExample2 = new SequentialCommandGroup(
    (new DelayCommand(0.1)).andThen(new PrintCommand("Example2 (Blue, 1, 1)"))
  ); 



}
