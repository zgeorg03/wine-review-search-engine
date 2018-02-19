package com.zgeorg03.core;

import com.zgeorg03.exceptions.QueryFormatNotValid;

import java.util.LinkedList;
import java.util.List;

public class Query {

    private final List<Operation> operationList = new LinkedList<>();

    public Query(String text) throws QueryFormatNotValid {
       parser(text);
    }
    enum State{ A,B,C,D }
    enum BoolOperation{ AND,OR,NONE }

    class Operation{
        private boolean negated;
        private String term;
        private BoolOperation operation;

        public void setTerm(String term) {
            this.term = term;
        }

        public void setNegated(boolean negated) {
            this.negated = negated;
        }

        public void setOperation(BoolOperation operation) {
            this.operation = operation;
        }

        public BoolOperation getOperation() {
            return operation;
        }

        public String getTerm() {
            return term;
        }

        public boolean isNegated() {
            return negated;
        }

        @Override
        public String toString() {
            if(negated && operation!= BoolOperation.NONE)
                return "NOT("+term+") " + operation;
            if(negated && operation == BoolOperation.NONE)
                return "NOT("+term+")";
            if(!negated && operation!= BoolOperation.NONE)
                return "("+term+") " + operation;
            if(!negated && operation == BoolOperation.NONE)
                return "("+term+")";
            return "";
        }
    }
    public boolean parser(String text) throws QueryFormatNotValid{
        char array[] = text.toCharArray();
        State currentState = State.A;

        int i = 0;
        StringBuffer buffer = new StringBuffer();
        String token="";
        BoolOperation boolOperation = BoolOperation.NONE;
        Operation operation  = new Operation();
        while(i<array.length) {
            char c = array[i];
            switch (currentState) {
                case A:
                    if(isValidStringChar(c)){
                        if(!Character.isWhitespace(c))
                            buffer.append(c);
                        i++;
                    }else if( buffer.length()!=0 && c==','){
                       currentState = State.B;
                    }else if( buffer.length()!=0 && c=='.'){
                        currentState = State.C;
                    } else if(c=='-'){
                        currentState = State.D;
                    }
                    break;
                case B:
                    token = buffer.toString();
                    buffer.setLength(0);
                    operation.setTerm(token);
                    operation.setOperation(BoolOperation.OR);
                    operationList.add(operation);
                    operation = new Operation();
                    i++;
                    if(i<array.length) {
                        char d = array[i];
                        if (d == ',' || d == '.')
                            throw new QueryFormatNotValid(text);
                    }
                    currentState = State.A;
                    break;
                case C:
                    token = buffer.toString();
                    buffer.setLength(0);
                    operation.setTerm(token);
                    operation.setOperation(BoolOperation.AND);
                    operationList.add(operation);
                    operation = new Operation();
                    i++;
                    if(i<array.length) {
                        char d = array[i];
                        if (d == ',' || d == '.')
                            throw new QueryFormatNotValid(text);
                    }
                    currentState = State.A;
                    break;
                case D:
                    operation.setNegated(true);
                    currentState= State.A;
                    i++;
                    if(i<array.length && !isValidStringChar(array[i]))
                        throw new QueryFormatNotValid(text);
                    break;
            }

        }
        if(currentState==State.A){
            if(!isValidStringChar(array[array.length-1])){
                throw new QueryFormatNotValid(text);
            }
           token = buffer.toString();
           operation.setOperation(boolOperation);
           operation.setTerm(token);
           operationList.add(operation);
        }else{
            throw new QueryFormatNotValid(text);
        }
        return true;
    }

    public boolean isValidStringChar(char c){
        return !(c=='-' || c=='.' || c==',');
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    @Override
    public String toString() {
        return operationList.toString();
    }

    public static void main(String args[]) throws QueryFormatNotValid {
        Query query = new Query("-hello-world.-test");
        System.out.println(query);

    }
}
