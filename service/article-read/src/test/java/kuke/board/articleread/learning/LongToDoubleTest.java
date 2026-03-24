package kuke.board.articleread.learning;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class LongToDoubleTest {

    @Test
    void longToDoubleTest() {
        // long 은 64비트로 표현
        // double은 64비트로 부동소수점 표현
        long longValue = 111_111_111_111_111_111L;
        System.out.println("longValue = " + longValue);
        double doublevalue = longValue;
        System.out.println("new BigDecimal(doublevalue).toString() = " + new BigDecimal(doublevalue).toString());
        long longValue2 = (long) doublevalue;
        System.out.println("longValue2 = " + longValue2);

    }
}
