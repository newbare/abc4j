package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class FieldDefaultLengthDefinition extends AutomataDefinition
{
    public FieldDefaultLengthDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State startingState = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(startingState,'L');
        getStartingState().addTransition(trans);
        startingState.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_DEFAULT_LENGTH, true)));
    }

}

