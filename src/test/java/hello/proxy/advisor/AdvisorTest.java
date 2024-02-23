package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {

    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(defaultPointcutAdvisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    @Test
    @DisplayName("직접 만든 포인트 컷")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
        proxyFactory.addAdvisor(defaultPointcutAdvisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    static class MyPointcut implements Pointcut{

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            MethodMatcher methodMatcher = new MethodMatcher() {
                private String matchName = "save";
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    boolean isSaveMethod = method.getName().equals(matchName);
                    log.info("call pointcut , method = {} targetClass={}", method.getName(), targetClass);
                    log.info("result of pointcut result={}", isSaveMethod);
                    return isSaveMethod;
                }

                // isRuntime이 true이면 메소드 인수값들이 포함된 matches 메소드가 호출됨. 캐싱과 관련된 문제
                @Override
                public boolean isRuntime() {
                    return false;
                }

                @Override
                public boolean matches(Method method, Class<?> targetClass, Object... args) {
                    return false;
                }
            };
            return methodMatcher;
        }
    }


    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

//        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
//        pointcut.setMappedName("save");
//        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
//        proxyFactory.addAdvisor(defaultPointcutAdvisor);

        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor(new TimeAdvice());
        advisor.setMappedName("save");

        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }
}
