package com.wingspan.platform.rs.links;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Path;

/**
 * Utility to dump out the contents of a link registry provider.
 */
public class DumpLinks {
    public static class LinkTargetAndMethod {
        public final LinkTarget linkTarget;
        public final Method method;
        public final Optional<Path> pathOpt;

        public LinkTargetAndMethod(LinkTarget linkTarget, Method method, Optional<Path> pathOpt) {
            this.linkTarget = linkTarget;
            this.method = method;
            this.pathOpt = pathOpt;
        }
    }
    public static class LinkInfo{
        public final Class<?> beanClass;
        public final LinkRef linkRef;
        public final Optional<LinkTargetAndMethod> linkTargetAndMethodOpt;
        public final Optional<Throwable> errorOpt;

        public LinkInfo(Class<?> beanClass, LinkRef linkRef, Optional<LinkTargetAndMethod> linkTargetAndMethodOpt, Optional<Throwable> errorOpt) {
            this.beanClass = beanClass;
            this.linkRef = linkRef;
            this.linkTargetAndMethodOpt = linkTargetAndMethodOpt;
            this.errorOpt = errorOpt;
        }
    }


    public static Optional<LinkTargetAndMethod> findLinkTarget(LinkRef linkRef) {
        List<LinkTargetAndMethod> matches = new ArrayList<>();
        for(Method m : linkRef.getResource().getMethods()) {
            LinkTarget linkTarget = m.getAnnotation(LinkTarget.class);
            if (null != linkTarget && linkRef.getName().equals(linkTarget.name())) {
                matches.add(new LinkTargetAndMethod(linkTarget, m,
                                                    Optional.ofNullable(m.getAnnotation(Path.class))));
            }
        }

        if(matches.size() > 1){
            throw new RuntimeException("found multiple methods for linkRef: " + linkRef.getName() + " on " + linkRef.getResource().getName());
        }
        if(matches.size() == 1){
            return Optional.of(matches.get(0));
        }
        return Optional.empty();
    }

    public static LinkInfo getLinkInfo(Class<?> beanClass, LinkRef linkRef) {
        Optional<Throwable> errorOpt = null;
        Optional<LinkTargetAndMethod> ltOpt = Optional.empty();
        try {
            ltOpt = findLinkTarget(linkRef);
        } catch (Throwable t){
            errorOpt = Optional.of(t);
        }
        return new LinkInfo(beanClass, linkRef, ltOpt, errorOpt);
    }

    public static void logLinkInfo(Appendable a, LinkInfo li) {
        try{
        a.append("  ").append(li.linkRef.getName()).append("\n");
        a.append("    resource:         ").append(li.linkRef.getResource().getName()).append("\n");
        a.append("    locatorMethod:    ").append(li.linkRef.getLocatorMethod()).append("\n");
        li.linkTargetAndMethodOpt.ifPresent(l -> {
            try{
                a.append("    method:           ").append(l.method.getDeclaringClass().getName()).append("::").append(l.method.getName()).append("\n");
                a.append("    path:             ").append(l.pathOpt.map(m -> m.value()).orElse("")).append("\n");
                a.append("    templateParams:   ").append(Arrays.stream(l.linkTarget.templateParams()).collect(Collectors.joining(", "))).append("\n");
                a.append("    condition:        ").append(String.valueOf(l.linkTarget.condition())).append("\n");
                a.append("    linkProc(Target): ").append(Arrays.stream(l.linkTarget.linkProcessors()).map(p -> p.getName()).collect(Collectors.joining(", "))).append("\n");
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        });
        a.append("    linkProc(Ref):    ").append(li.linkRef.getAdditionalLinkProcessors().stream().map(p -> p.getName()).collect(Collectors.joining(", "))).append("\n");
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void dumpRegistry(Appendable a, Class<?> beanClass, LinkRegistry reg){
        try{
            a.append(beanClass.getName()).append("\n");
            reg.getLinks().stream()
                .map(linkRef -> getLinkInfo(beanClass, linkRef))
                .forEach(li -> logLinkInfo(a, li));
            a.append("\n\n");
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    public static void dumpRegistryProvider(Appendable a, LinkRegistryProvider<?> lrp){
        lrp.getLinkRegistryMap().entrySet().forEach(entry -> dumpRegistry(a, entry.getKey(), entry.getValue()));
    }


    public static void main(String[] args) {
        try{
            if(args.length != 1 || null == args[0]|| args[0].length() == 0){
                System.out.println("one LinkRegistryProvider class name must be provided");
                System.exit(1);
            }
            String className = args[0];
            System.out.println("Dumping Links Registry Provider Class: '" + className + "'");

            LinkRegistryProvider<?> lrp = (LinkRegistryProvider<?>)Class.forName(className).newInstance();
            dumpRegistryProvider(System.out, lrp);


            System.exit(0);
        } catch (Throwable t){
            t.printStackTrace();
            System.exit(1);
        }
    }
}
