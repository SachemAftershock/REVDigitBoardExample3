package frc.robot;

public class REVDigitBoardController extends REVDigitBoard {

    // Alliance Color
    public enum RobotColorEnum { eRed, eBlue };
    public RobotColorEnum m_RobotColor = RobotColorEnum.eBlue;   //  restrict to R or B (red or blue)
    public RobotColorEnum getRobotColor() { return m_RobotColor; }
    public String getRobotColorString() { return ((m_RobotColor == RobotColorEnum.eRed) ? "R" : "B"); }
    public void setRobotColor(RobotColorEnum InColor) {
        m_RobotColor = InColor;
    }
    public void toggleRobotColor() {
        m_RobotColor = (m_RobotColor == RobotColorEnum.eRed) ? RobotColorEnum.eBlue : RobotColorEnum.eRed;
    }

    // Robot starting position
    public final int kFirstPosition = 0;
    public final int kLastPosition = 9;
    public int m_StartPositionNumber = kFirstPosition;  // Restrict to characters 1,2,3
    public int getRobotStartPositionNumber() { return m_StartPositionNumber; }
    public String getRobotStartPositionNumberString() { return String.valueOf(m_StartPositionNumber); }
    public void setRobotStartPositionNumber(int startPositionNumber) {
        m_StartPositionNumber = startPositionNumber;
    }
    public void incrementRobotStartPosition() { 
        m_StartPositionNumber++; 
        if (m_StartPositionNumber > kLastPosition) m_StartPositionNumber = kFirstPosition;  // single digit, wrap around
    }  
    public void decrementRobotStartPosition() { 
        m_StartPositionNumber--;  
        if (m_StartPositionNumber < kFirstPosition) m_StartPositionNumber = kLastPosition; 
    }

    // The autonomous scenario to be executed for the selected starting position of the selected alliance color.
    public final int kFirstScenario = 1;
    public final int kLastScenario = 99;
    public int m_ScenarioNumberForPosition = kFirstScenario;  // Restrict to characters 1,2,3,... for each Start Position.
    public int getScenarioNumberForPosition() { return m_ScenarioNumberForPosition; }
    public String getScenarioNumberForPositionString() { return String.valueOf(m_ScenarioNumberForPosition); }
    public void setScenarioNumberForPosition(int startScenarioNumber) {
        m_ScenarioNumberForPosition = startScenarioNumber;
    }
    public void incrementScenarioNumberForPosition() { 
        m_ScenarioNumberForPosition++; 
        if (m_ScenarioNumberForPosition > kLastScenario) m_ScenarioNumberForPosition = kFirstScenario; // two digits, wrap around
    } 
    public void decrementScenarioNumberForPosition() { 
        m_ScenarioNumberForPosition--;  
        if (m_ScenarioNumberForPosition < kFirstScenario) m_ScenarioNumberForPosition = kLastScenario; 
    }

    private String FourAlphaNumberics = "DEAD";

    // Update the digit board's display
    public void updateDigitBoardAlphaNumericLEDs() {
        switch (mEditState) {
            case eNothing:
                if (m_ScenarioNumberForPosition < 10)
                    FourAlphaNumberics = getRobotColorString() + m_StartPositionNumber + "0" + m_ScenarioNumberForPosition;
                else 
                    FourAlphaNumberics = getRobotColorString() + m_StartPositionNumber + m_ScenarioNumberForPosition;
                break;
            case eColor:
                FourAlphaNumberics = getRobotColorString() +"   ";
                break;
            case ePosition:
                FourAlphaNumberics = " " + m_StartPositionNumber + "  ";
                break;
            case eScenario:
                if (m_ScenarioNumberForPosition < 10)
                    FourAlphaNumberics = "   " + m_ScenarioNumberForPosition;
                else 
                    FourAlphaNumberics = "  " + m_ScenarioNumberForPosition;
                break;
        }
        display(FourAlphaNumberics);
    }

    private boolean debugTelemetryOn = false;
    private boolean debugEditModeyOn = false;
    
    private enum EditStateEnum { eNothing, eColor, ePosition, eScenario }
    private EditStateEnum mEditState = EditStateEnum.eNothing; 
    private int m_intervalCount = 0;
    private int m_secondsElapsed = 0;

    public void processRevDigitBoardController() {
                
        m_intervalCount++;
        
        // Telemetry:
        // each iteration is 20ms, then 50x = 1000ms = 1 second intervals.
        if ((debugTelemetryOn) && (m_intervalCount % 50) == 0 ) {  
            m_secondsElapsed++;
            if (debugTelemetryOn) System.out.printf("%4d | ",m_secondsElapsed);
            logRevDigitBoardControllerState();
        }

        // Use input update polling:
        // each iteration is 20ms, then 12x = 240ms = ~1/4th second intervals.  
        // Result in good increment/decrement responsivity to user, and minimize CPU usage.
        if ((m_intervalCount % 12) == 0 ) {  
    
            // Divide the 5V range evenly into 4 regions, 5/4=1.25
            if (getAdjustPotentiometerVoltage() > 3.75) {
                if (debugEditModeyOn) System.out.println("Edit Scenario");
                // Edit Scenario
                mEditState = EditStateEnum.eScenario; 
                setBlink2Hz();  // blink rapidly to indicate edit mode on.
                if (getButtonA()) {
                    incrementScenarioNumberForPosition();
                } else if (getButtonB()) {
                    decrementScenarioNumberForPosition();
                }
            } else if (getAdjustPotentiometerVoltage() > 2.5) {
                if (debugEditModeyOn) System.out.println("Edit Start Position");
                // Edit Start Position
                mEditState = EditStateEnum.ePosition; 
                setBlink2Hz(); // blink rapidly to indicate edit mode on.
                if (getButtonA()) {
                    incrementRobotStartPosition();
                } else if (getButtonB()) {
                    decrementRobotStartPosition();
                }
            } else if (getAdjustPotentiometerVoltage() > 1.25) {
                if (debugEditModeyOn) System.out.println("Edit Color");
                // Edit Color
                mEditState = EditStateEnum.eColor; 
                setBlink2Hz(); // blink rapidly to indicate edit mode on.
                if (getButtonA()) {
                    toggleRobotColor();
                } else if (getButtonB()) {
                    toggleRobotColor();
                }
            } else {
                if (debugEditModeyOn) System.out.println("Edit Nothing");
                // Nominal Mode (no editing)
                mEditState = EditStateEnum.eNothing; 
                setBlinkOff(); // do not blink when not in edit mode.
            }
    
            updateDigitBoardAlphaNumericLEDs();
        
        }

    }
  
  
    // Telemetry
    public void logRevDigitBoardControllerState() {
        System.out.println("DigitBoard>" +  
            "  Display: " + FourAlphaNumberics +
            "  Color: " + getRobotColorString() + 
            "  Position: " + m_StartPositionNumber + 
            "  Scenario: " + m_ScenarioNumberForPosition +
            "  ButtonA: " + getButtonA() + 
            "  ButtonB: " + getButtonB() + 
            "  Pot: " + String.format("%.2f",getAdjustPotentiometerVoltage()) +
            "  Edit: " + mEditState.name());

    }
  
}
