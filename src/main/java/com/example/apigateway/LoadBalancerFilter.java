package com.example.apigateway;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class LoadBalancerFilter extends AbstractGatewayFilterFactory<LoadBalancerFilter.Config> {

    public LoadBalancerFilter() {
        super(Config.class);
    }

    @LoadBalanced
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {


            int randomWeight = Math.random() > 0.5 ? config.service1Weight : config.service2Weight;

            String serviceUri = randomWeight > 50 ? "http://localhost:8085" : "http://localhost:8086";
            exchange.getAttributes().put("serviceUri", serviceUri);

            return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(serviceUri)).build()).build());
        };
    }



    public static class Config {
        private int service1Weight = 50;
        private int service2Weight = 50;

        public int getService1Weight() {
            return service1Weight;
        }

        public void setService1Weight(int service1Weight) {
            this.service1Weight = service1Weight;
        }

        public int getService2Weight() {
            return service2Weight;
        }

        public void setService2Weight(int service2Weight) {
            this.service2Weight = service2Weight;
        }
    }

}
