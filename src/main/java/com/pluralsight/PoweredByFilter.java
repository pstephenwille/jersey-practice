package com.pluralsight;

import java.lang.annotation.Annotation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
@PoweredBy
public class PoweredByFilter implements ContainerResponseFilter {

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext respContext) {
        for (Annotation a : respContext.getEntityAnnotations()) {
            if (a.annotationType() == PoweredBy.class) {
                System.out.println(".................."+ a);
                String value = ((PoweredBy) a).value();
                respContext.getHeaders().add("X-Powered-By", value);
            }
        }
    }
}
