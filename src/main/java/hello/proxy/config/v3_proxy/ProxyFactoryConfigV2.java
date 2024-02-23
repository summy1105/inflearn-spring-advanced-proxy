package hello.proxy.config.v3_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v3_proxy.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV2 {

    @Bean
    OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 orderControllerV2 = new OrderControllerV2(orderServiceV2(logTrace));
        OrderControllerV2 proxy = (OrderControllerV2) getProxy(orderControllerV2, logTrace);
        return proxy;
    }

    @Bean
    OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 orderServiceV2 = new OrderServiceV2(orderRepositoryV2(logTrace));
        OrderServiceV2 proxy = (OrderServiceV2) getProxy(orderServiceV2, logTrace);
        return proxy;
    }

    @Bean
    OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 orderRepositoryV2 = new OrderRepositoryV2();
        OrderRepositoryV2 proxy = (OrderRepositoryV2) getProxy(orderRepositoryV2, logTrace);
        return proxy;
    }

    private Object getProxy(Object target, LogTrace logTrace) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new LogTraceAdvice(logTrace));
        proxyFactory.addAdvisor(advisor);
        Object proxy = proxyFactory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), target.getClass());
        return proxy;
    }
}
