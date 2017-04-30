/*
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib2.immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import org.jf.dexlib2.base.reference.BaseMethodReference;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.MethodParameter;
import org.jf.util.ImmutableConverter;
import org.jf.util.ImmutableUtils;

import java.util.Set;

public class ImmutableMethod extends BaseMethodReference implements Method {
     protected final String definingClass;
     protected final String name;
     protected final ImmutableList<? extends ImmutableMethodParameter> parameters;
     protected final String returnType;
    protected final int accessFlags;
     protected final ImmutableSet<? extends ImmutableAnnotation> annotations;
    protected final ImmutableMethodImplementation methodImplementation;

    public ImmutableMethod( String definingClass,
                            String name,
                           Iterable<? extends MethodParameter> parameters,
                            String returnType,
                           int accessFlags,
                           Set<? extends Annotation> annotations,
                           MethodImplementation methodImplementation) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = ImmutableMethodParameter.immutableListOf(parameters);
        this.returnType = returnType;
        this.accessFlags = accessFlags;
        this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
        this.methodImplementation = ImmutableMethodImplementation.of(methodImplementation);
    }

    public ImmutableMethod( String definingClass,
                            String name,
                           ImmutableList<? extends ImmutableMethodParameter> parameters,
                            String returnType,
                           int accessFlags,
                           ImmutableSet<? extends ImmutableAnnotation> annotations,
                           ImmutableMethodImplementation methodImplementation) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = ImmutableUtils.nullToEmptyList(parameters);
        this.returnType = returnType;
        this.accessFlags = accessFlags;
        this.annotations = ImmutableUtils.nullToEmptySet(annotations);
        this.methodImplementation = methodImplementation;
    }

    public static ImmutableMethod of(Method method) {
        if (method instanceof ImmutableMethod) {
            return (ImmutableMethod)method;
        }
        return new ImmutableMethod(
                method.getDefiningClass(),
                method.getName(),
                method.getParameters(),
                method.getReturnType(),
                method.getAccessFlags(),
                method.getAnnotations(),
                method.getImplementation());
    }

    @Override  public String getDefiningClass() { return definingClass; }
    @Override  public String getName() { return name; }
    @Override  public ImmutableList<? extends CharSequence> getParameterTypes() { return parameters; }
    @Override  public ImmutableList<? extends ImmutableMethodParameter> getParameters() { return parameters; }
    @Override  public String getReturnType() { return returnType; }
    @Override public int getAccessFlags() { return accessFlags; }
    @Override  public ImmutableSet<? extends ImmutableAnnotation> getAnnotations() { return annotations; }
    @Override public ImmutableMethodImplementation getImplementation() { return methodImplementation; }


    public static ImmutableSortedSet<ImmutableMethod> immutableSetOf(Iterable<? extends Method> list) {
        return CONVERTER.toSortedSet(Ordering.natural(), list);
    }

    private static final ImmutableConverter<ImmutableMethod, Method> CONVERTER =
            new ImmutableConverter<ImmutableMethod, Method>() {
                @Override
                protected boolean isImmutable( Method item) {
                    return item instanceof ImmutableMethod;
                }


                @Override
                protected ImmutableMethod makeImmutable( Method item) {
                    return ImmutableMethod.of(item);
                }
            };
}
