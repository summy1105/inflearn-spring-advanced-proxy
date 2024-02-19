package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceProxyConfig {

    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace){
        OrderControllerV1Impl targetOrderController = new OrderControllerV1Impl(orderService(logTrace));
        return new OrderControllerInterfaceProxy(targetOrderController, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl targetOrderService = new OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(targetOrderService, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl targetOrderRepository = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(targetOrderRepository, logTrace);
    }

}
