package com.example.calculator;

public interface CalculatorContract {

    interface View {
        void showResult(String Result);
        void updateCurrentExpression(String currentStringExpression);
        void showInvalidExpressionMessage();
    }
    interface Presenter{
        void onOperatorAdd(String Operator);
        void onClearExpression();
        void onCalculateResult();
        void onExpressionSignChange();
    }
}
