package expression;

import expression.exceptions.LOG;

import java.math.BigInteger;

public class Multiply extends BinaryMultiExpressionAbstract {

    public Multiply(MultiExpressionElement first, MultiExpressionElement second) {
        super(first, second);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    protected String getExpressionMark() {
        return "*";
    }

    @Override
    protected int calculate(int x, int y) {
        return x * y;
    }

    @Override
    protected BigInteger calculate(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }

    @Override
    protected boolean isRequireRightBrackets(MultiExpressionElement element) {
        if (super.isRequireRightBrackets(element))
            return true;

        return element.getPriority() == this.getPriority()
                && !(element instanceof Multiply);

 /*       if (element instanceof Multiply)
            return false;

        if(this.getPriority() - 5 == element.getPriority())
            return false;

        if (element instanceof LOG)
            return false;
*/
/*        if (super.isRequireRightBrackets(element))
            return true;
*/
    //    return super.isRequireRightBrackets(element);
   //     return (this.getPriority() == element.getPriority()) ||
                //(this.getPriority() + 5 == element.getPriority());
    }

}
