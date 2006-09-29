package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;
/** **/
public class ExtraTextDefinition extends AutomataDefinition
{
  private static char[] chars = {
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
    'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public ExtraTextDefinition()
    {   //===================== FIELD
        State stateTEXT_CHAR = new State(AbcTokenType.TEXT, true);
        Transition trans = new Transition(stateTEXT_CHAR, chars);
        getStartingState().addTransition(trans);
        stateTEXT_CHAR.addTransition(new Transition(stateTEXT_CHAR, chars));
    }

}

