package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class PrintCommand extends Command {

    final boolean showPrints = true;		

    private String mText;

    public PrintCommand(String theText) {
        mText = theText;
        addRequirements();
    }   

    @Override
    public void initialize() {
        if (showPrints) System.out.println("PrintCommand started '" + mText + "'");
    }

    @Override
    public void execute() {
        System.out.println(mText);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void end(boolean interrupted) {
    }
}







