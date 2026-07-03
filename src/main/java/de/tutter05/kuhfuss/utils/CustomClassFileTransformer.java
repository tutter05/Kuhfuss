package de.tutter05.kuhfuss.utils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public abstract class CustomClassFileTransformer implements ClassFileTransformer {

    private final String targetClass;
    private byte[] originalClassFileBuffer;

    public CustomClassFileTransformer(final String targetClass) {
        this.targetClass = targetClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public byte[] getOriginalClassFileBuffer() {
        return originalClassFileBuffer;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className.strip().equals(targetClass)) {
            this.originalClassFileBuffer = classfileBuffer;
            return doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
        return null;
    }

    public abstract byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;
}
