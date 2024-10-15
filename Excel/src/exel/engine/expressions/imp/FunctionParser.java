package exel.engine.expressions.imp;

//import com.sun.istack.NotNull;
import com.sun.istack.NotNull;
import exel.engine.expressions.api.Expression;
import exel.engine.expressions.imp.Boolean.Compare.BiggerExpression;
import exel.engine.expressions.imp.Boolean.Compare.EqualExpression;
import exel.engine.expressions.imp.Boolean.IfExpression;
import exel.engine.expressions.imp.Boolean.Logic.AndExpression;
import exel.engine.expressions.imp.Boolean.Logic.NotExpression;
import exel.engine.expressions.imp.Boolean.Logic.OrExpression;
import exel.engine.expressions.imp.Math.*;
import exel.engine.expressions.imp.String.ConcatExpression;
import exel.engine.expressions.imp.String.SubExpression;
import exel.engine.spreadsheet.cell.api.CellType;
import exel.engine.spreadsheet.coordinate.Coordinate;

import java.util.*;

public enum FunctionParser
{
    IDENTITY {
                @Override
                public Expression parse(List<String> arguments)
                {
                    // validations of the function. it should have exactly one argument
                    checkForArgumentSize(arguments.size(), 1, "IDENTITY");

                    // all is good. create the relevant function instance
                    String actualValueWithSpaces = arguments.get(0);
                    String actualValueTrimmed = actualValueWithSpaces.trim();

                    if (isBoolean(actualValueTrimmed))
                        return new IdentityExpression(Boolean.parseBoolean(actualValueTrimmed), CellType.BOOLEAN);
                    else if (isNumeric(actualValueTrimmed))
                        return new IdentityExpression(Double.parseDouble(actualValueTrimmed), CellType.NUMERIC);
                    else
                        return new IdentityExpression(actualValueWithSpaces, CellType.STRING);
                }

                private boolean isBoolean(String value)
                {
                    return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
                }

                private boolean isNumeric(String value)
                {
                    try
                    {
                        Double.parseDouble(value);
                        return true;
                    } catch (NumberFormatException e)
                    {
                        return false;
                    }
                }
            },
    REF {
        @Override
        public Expression parse(List<String> arguments)
        {
            checkForArgumentSize(arguments.size(), 1, "REF");

            String coordinate = arguments.getFirst().trim().toUpperCase();
            if (!Coordinate.isStringACellCoordinate(coordinate))
                throw new IllegalArgumentException("Invalid argument for REF function. Expected a valid cell reference, but got " + coordinate);

            return new RefExpression(coordinate);
        }
    },

    PLUS {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "PLUS");
                    return new PlusExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    MINUS {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "MINUS");
                    return new MinusExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    ABS {
                @Override
                public Expression parse(List<String> arguments)
                {
                    checkForArgumentSize(arguments.size(), 1, "ABS");

                    //structure is good. parse arguments
                    Expression exp = parseExpression(arguments.get(0).trim());

                    return new AbsExpression(exp);
                }
            },
    DIVIDE {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "DIVIDE");
                    return new DivideExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    MOD {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "MOD");
                    return new ModExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    POW {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "POW");
                    return new PowExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    TIMES {
                @Override
                public Expression parse(List<String> arguments)
                {
                    AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "TIMES");
                    return new TimesExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
                }
            },
    PERCENT{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "PERCENT");
            return new PercentExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
        }
    },
    SUM{
        @Override
        public Expression parse(List<String> arguments)
        {
            checkForArgumentSize(arguments.size(), 1, "SUM");

            String rangeName = arguments.getFirst();
            return new SumExpression(rangeName);
        }
    },
    AVERAGE{
        @Override
        public Expression parse(List<String> arguments)
        {
            checkForArgumentSize(arguments.size(), 1, "AVERAGE");

            String rangeName = arguments.getFirst();
            return new AverageExpression(rangeName);
        }
    },

    CONCAT {
                @Override
                public Expression parse(List<String> arguments)
                {
                    checkForArgumentSize(arguments.size(), 2, "CONCAT");

                    //structure is good. parse arguments
                    Expression left = parseExpression(arguments.get(0));
                    Expression right = parseExpression(arguments.get(1));
                    return new ConcatExpression(left, right);
                }
            },
    SUB {
                @Override
                public Expression parse(List<String> arguments)
                {
                    // validations of the function. it should have exactly two arguments
                    checkForArgumentSize(arguments.size(), 3, "SUB");

                    //structure is good. parse arguments
                    Expression stringToCut = parseExpression(arguments.get(0));
                    Expression startIndex = parseExpression(arguments.get(1).trim());
                    Expression endIndex = parseExpression(arguments.get(2).trim());

                    CellType stringToCutType = stringToCut.getFunctionResultType();
                    CellType startIndexType = startIndex.getFunctionResultType();
                    CellType endIndexType = endIndex.getFunctionResultType();

                    return new SubExpression(stringToCut, startIndex, endIndex);
                }
            },

    BIGGER{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "BIGGER");
            return new BiggerExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
        }
    },
    LESS{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "LESS");
            return new BiggerExpression(parsedExpressions.getValue(), parsedExpressions.getKey());
        }
    },
    EQUAL{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "EQUAL");
            return new EqualExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
        }
    },

    NOT{
        @Override
        public Expression parse(List<String> arguments) {
            checkForArgumentSize(arguments.size(), 1, "NOT");

            Expression exp = parseExpression(arguments.get(0));
            return new NotExpression(exp);
        }
    },
    AND{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "AND");
            return new AndExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
        }
    },
    OR{
        @Override
        public Expression parse(List<String> arguments) {
            AbstractMap.SimpleEntry<Expression, Expression> parsedExpressions = checkArgsAndParseExpressions(arguments, "OR");
            return new OrExpression(parsedExpressions.getKey(), parsedExpressions.getValue());
        }
    },
    IF{
        @Override
        public Expression parse(List<String> arguments)
        {
            checkForArgumentSize(arguments.size(), 3, "IF");

            Expression condition = parseExpression(arguments.get(0));
            Expression thenExpression = parseExpression(arguments.get(1));
            Expression elseExpression = parseExpression(arguments.get(2));
            return new IfExpression(condition, thenExpression, elseExpression);
        }
    }
    ;

    abstract public Expression parse(List<String> arguments);

    public static Expression parseExpression(String input)
    {
        List<String> topLevelParts = new LinkedList<>();
        FunctionParser func = getFuncFromInput(input, topLevelParts);

        if (func != null)
            return func.parse(topLevelParts);
        else
            return FunctionParser.IDENTITY.parse(List.of(input));

    }

    private static FunctionParser getFuncFromInput(String input, @NotNull List<String> topLevelParts){
        String trimmedInput = input.trim();
        if (isStringAFunction(trimmedInput))
        {
            String functionContent = trimmedInput.substring(1, trimmedInput.length() - 1);
            topLevelParts.addAll(parseMainParts(functionContent));
            String functionName = topLevelParts.removeFirst().trim().toUpperCase();

            //Idea: topLevelParts now has either cells, functions, or numbers.
            //If it's a cell, it should be added to the "dependsOn" list given in the method.
            FunctionParser func;
            try
            {
                func = FunctionParser.valueOf(functionName);
            }
            //If you get an exception (probably from the enum) tell user the function does not exist
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException("\"" + functionName + "\" is not a valid function");
            }
            return func;
        }
        else
            return null;
    }

    //If the string in not a valid original value, it returns the exception (which explains why).
    //If it is valid, it returns null.
    public static Exception isStringAValidOriginalValue(String input)
    {
        try
        {
            parseExpression(input);
            return null;
        }
        catch (Exception e)
        {
            return e;
        }
    }

    private static List<String> parseMainParts(String input)
    {
        List<String> parts = new LinkedList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray())
        {
            if (c == '{')
                stack.push(c);
            else if (c == '}')
                stack.pop();

            if (c == ',' && stack.isEmpty())
            {
                // If we are at a comma and the stack is empty, it's a separator for top-level parts
                parts.add(buffer.toString());
                buffer.setLength(0); // Clear the buffer for the next part
            }
            else
                buffer.append(c);
        }

        // Add the last part
        if (buffer.length() > 0)
            parts.add(buffer.toString());

        return parts;
    }

    public static List<Coordinate> getCellCordsInOriginalValue(String input)
    {
        List<Coordinate> cellsList = new LinkedList<>();
        if (!isStringAFunction(input))
            return cellsList;

        String improvedInput = input.replaceAll(" ", "").toUpperCase();
        int refIndex = 0;

        while (refIndex < improvedInput.length())
        {
            refIndex = improvedInput.indexOf("{REF,", refIndex);

            if (refIndex == -1)
                return cellsList;
            refIndex += 5;

            int endIndex = improvedInput.indexOf('}', refIndex);
            String supposedCell = improvedInput.substring(refIndex, endIndex);
            if (Coordinate.isStringACellCoordinate(supposedCell))
                cellsList.add(new Coordinate(supposedCell));
            refIndex = endIndex + 1;
        }

        return cellsList;
    }

    public static Boolean isStringAFunction(String input)
    {
        if (input == null || input.isEmpty())
            return false;
        input = input.trim();
        return input.startsWith("{") && input.endsWith("}");
    }

    private static AbstractMap.SimpleEntry<Expression, Expression> checkArgsAndParseExpressions
            (List<String> arguments, String funcName)
    {
        checkForArgumentSize(arguments.size(), 2, funcName);

        //structure is good. parse arguments
        Expression left = parseExpression(arguments.get(0).trim());
        Expression right = parseExpression(arguments.get(1).trim());

        return new AbstractMap.SimpleEntry<>(left, right);
    }

    private static void checkForArgumentSize(int argumentsSize, int expectedSize, String funcName)
    {
        if (argumentsSize != expectedSize)
            throw new IllegalArgumentException("Invalid number of arguments for " + funcName + " function. Expected " + expectedSize + ", but got " + argumentsSize);
    }

    private static final List<FunctionParser> functionsThatUseRange = getFunctionsThatUseRange();

    private static List<FunctionParser> getFunctionsThatUseRange(){
        List<FunctionParser> functionsThatUseRange = new LinkedList<>();
        functionsThatUseRange.add(FunctionParser.SUM);
        functionsThatUseRange.add(FunctionParser.AVERAGE);
        return functionsThatUseRange;
    }

    public static String getRangeInValue(String input){
        if (isStringAValidOriginalValue(input) != null)
            return null;

        List<String> topLevelParts = new LinkedList<>();
        FunctionParser func = getFuncFromInput(input, topLevelParts);

        if (func == null || !functionsThatUseRange.contains(func))
            return null;
        return topLevelParts.getFirst();
    }
}

