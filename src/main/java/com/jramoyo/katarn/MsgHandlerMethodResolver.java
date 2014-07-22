package com.jramoyo.katarn;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jramoyo.katarn.annotation.Destination;

public class MsgHandlerMethodResolver {
    private final String prefix;

    @VisibleForTesting
    Map<String, MethodContext> mapping = Maps.newHashMap();

    public MsgHandlerMethodResolver(String prefix) {
        this.prefix = prefix;
    }

    public void init(String... packages) throws MsgHandlingException {
        checkArgument(packages != null, "List of packages to scan cannot be null.");
        checkArgument(packages.length > 0, "List of packages to scan cannot be empty.");

        Set<String> classNames = getClassNames(packages);
        boolean hasMapping = false;
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(Destination.class)) {
                        Destination annotation = method.getAnnotation(Destination.class);
                        String value = annotation.value();
                        if (mapping.containsKey(value)) {
                            throw new MsgHandlingException(format("Duplicate message mapping for value='%s'", value));
                        }
                        mapping.put(value, new MethodContext(method, clazz));
                        hasMapping = true;
                    }
                }
            }
            if (!hasMapping) {
                throw new MsgHandlingException("No message mapping found.");
            }
        } catch (ClassNotFoundException ex) {
            throw new MsgHandlingException(ex);
        }
    }

    private Set<String> getClassNames(String... packages) {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(true);

        Set<BeanDefinition> beanDefinitions = Sets.newHashSet();
        for (String path : packages) {
            beanDefinitions.addAll(componentProvider.findCandidateComponents(path));
        }

        Set<String> candidateNames = Sets.newHashSet();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            candidateNames.add(beanDefinition.getBeanClassName());
        }

        return candidateNames;
    }

    public MethodContext resolve(String destination) throws MsgHandlingException {
        checkArgument(!Strings.isNullOrEmpty(destination), "Destination cannot be null or empty.");

        String value = StringUtils.delete(destination, prefix);
        if (mapping.containsKey(value)) {
            return mapping.get(value);
        }

        throw new MsgHandlingException(format("Unknown message mapping for value='%s'", value));
    }
}
