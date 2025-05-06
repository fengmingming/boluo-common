package boluo.common.cache;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TestSpelExpression {

    @Test
    public void testSpel() {
        User user = new User();
        user.name = "hello world";
        SpelExpressionParser parser = new SpelExpressionParser();
        SpelExpression expression = parser.parseRaw("#id + '_' + #user.name");
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("id", 123);
        context.setVariable("user", user);
        System.out.println(expression.getValue(context, String.class));
    }

    @Setter
    @Getter
    public static class User {
        private String name;
    }

}
