package com.example.calculator;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/** code by saini
   22/06/2021 3:30AM
*/

public class CalculatorPresenter implements CalculatorContract.Presenter {

    private static final String STRING_COMMA = ".";
    public static final String PERCENTAGE = "%";
    public static final String SCIENTIFIC_NOTATION_CHAR = "E";
    public static final String INFINITY = "Infinity";
    private List<Character> validOperators = Arrays.asList('+','-','*','/');

    private boolean isNumberPositive = true;

    private final CalculatorContract.View view;
    private String mCurrentStringExpression;

    public CalculatorPresenter(CalculatorContract.View view) {
        this.view = view;
        mCurrentStringExpression = "";
    }


    @Override
    public void onOperatorAdd(String value) {

        if (mCurrentStringExpression.isEmpty() && value.equals(STRING_COMMA)){
            view.showInvalidExpressionMessage();
        }
        else {
            boolean isCommaAddedToExpression = false;

            try {
                if ((isValueAnOperator(value) || value.equals(PERCENTAGE)) && mCurrentStringExpression.length() > 0) {
                    char lastCharacterOfExpression = mCurrentStringExpression.charAt(mCurrentStringExpression.length() - 1);


                    if (isValueAnOperator(String.valueOf(lastCharacterOfExpression))) {
                        clearLastCharofExpression();
                    }

                }


            else if (value.equals(STRING_COMMA)) {
                char[] expressionArray = mCurrentStringExpression.toCharArray();
                for (char c : expressionArray) {
                    if (c == STRING_COMMA.toCharArray()[0]) {
                        isCommaAddedToExpression = true;
                    }
                    if (validOperators.contains(c)) {
                        isCommaAddedToExpression = false;
                    }
                }
                // If last character of expression is either a number or an operator, do not add the comma to the expression.
                char lastCharacterOfExpression = mCurrentStringExpression.charAt(mCurrentStringExpression.length() - 1);
                if (validOperators.contains(lastCharacterOfExpression)) {
                    isCommaAddedToExpression = true;
                }
            }
        }catch (NumberFormatException e) {
                return;
            }
            if (!isCommaAddedToExpression){
                mCurrentStringExpression += value;
                view.updateCurrentExpression(mCurrentStringExpression);
            }
        }

    }

    @Override
    public void onClearExpression() {
        mCurrentStringExpression="";
        view.updateCurrentExpression(mCurrentStringExpression);
        view.showResult("");

    }

    @Override
    public void onCalculateResult() {
        if (mCurrentStringExpression.isEmpty() || mCurrentStringExpression.contains(INFINITY)){
            view.showInvalidExpressionMessage();
        }
        else {
            clearLastValueIfItIsAnOperator();

            mCurrentStringExpression = mCurrentStringExpression.replaceAll(PERCENTAGE,"/100");

            Expression expression = new Expression(mCurrentStringExpression);

            BigDecimal bigDecimalResult = expression.eval();

            double doubleResult = bigDecimalResult.doubleValue();

            String stringResult;

            if (isValueIneger(doubleResult) && !isScientificNotation(Double.toString(doubleResult))){
                int roundValue = (int) Math.round(doubleResult);
                stringResult = String.valueOf(roundValue);
            }else {
                stringResult = Double.toString(doubleResult);
            }
            view.showResult(stringResult);
            mCurrentStringExpression = stringResult;

        }

    }

    @Override
    public void onExpressionSignChange() {
        mCurrentStringExpression = isNumberPositive ? "-" + mCurrentStringExpression:
                mCurrentStringExpression.substring(1,mCurrentStringExpression.length());
        isNumberPositive = !isNumberPositive;
        view.updateCurrentExpression(mCurrentStringExpression);

    }

    private boolean isValueAnOperator(String value){
        return validOperators.contains(value.toCharArray()[0]);
    }

    private void clearLastCharofExpression(){
        mCurrentStringExpression = mCurrentStringExpression.substring(0,mCurrentStringExpression.length()-1);
    }

    private void clearLastValueIfItIsAnOperator(){
        if(isValueAnOperator(String.valueOf(getLastCharOfExpression()))){
            clearLastCharofExpression();
            view.updateCurrentExpression(mCurrentStringExpression);
        }
    }

    private char getLastCharOfExpression(){
        int currentExpressionLastValuePosition = mCurrentStringExpression.length()-1;
        return mCurrentStringExpression.charAt(currentExpressionLastValuePosition);
    }

    private boolean isValueIneger(double number){
        int roundValue = (int)Math.round(number);
        return number % roundValue == 0;
    }

    private boolean isScientificNotation(String numberString){
        try {
            new BigDecimal(numberString);
        }catch (NumberFormatException e){
            return false;
        }
        return numberString.toUpperCase().contains(SCIENTIFIC_NOTATION_CHAR);
    }
}
