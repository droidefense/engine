/*
 * Developed by Koji Hisano <koji.hisano@eflow.jp>
 *
 * Copyright (C) 2009 eflow Inc. <http://www.eflow.jp/en/>
 *
 * This file is a part of Android Dalvik VM on Java.
 * http://code.google.com/p/android-dalvik-vm-on-java/
 *
 * This project is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.fake;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DynamicUtils;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.model.DVMClass;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

import java.util.Collection;
import java.util.HashMap;

public final class DVMTaintClass extends DVMClass {

    @JsonIgnore
    protected HashMap<String, IAtomField> taintFields;
    @JsonIgnore
    protected HashMap<String, IAtomMethod> taintMethods;

    public DVMTaintClass(String name) {
        super();
        this.name = name;
        taintFields = new HashMap<>();
        taintMethods = new HashMap<>();
        this.setSuperClass(ApkrConstants.SUPERCLASS);
    }

    public HashMap<String, IAtomField> getTaintFields() {
        return taintFields;
    }

    public void setTaintFields(HashMap<String, IAtomField> taintFields) {
        this.taintFields = taintFields;
    }

    public HashMap<String, IAtomMethod> getTaintMethods() {
        return taintMethods;
    }

    public void setTaintMethods(HashMap<String, IAtomMethod> methods) {
        this.taintMethods = methods;
    }

    @Override
    public IAtomField getStaticField(String name) {
        IAtomField field = taintFields.get(name);
        if (field == null) {
            field = new DVMTaintField(name, this);
            taintFields.put(name, field);
        }
        return field;
    }

    public IAtomMethod searchMethod(String name, String desc, Object[] lastMethodArgs) {
        IAtomMethod method = taintMethods.get(name + desc);
        if (method == null) {
            method = new DVMTaintMethod(name, this.getName());
            method.setDescriptor(desc);
            String id = method.toString();
            taintMethods.put(id, method);
        }
        return method;
    }

    @Override
    public IAtomMethod getMethod(String name, String desc, boolean fake) {
        IAtomMethod method = taintMethods.get(name + desc);
        if (method == null) {
            method = new DVMTaintMethod(name, this.getName());
            method.setDescriptor(desc);
            String id = method.toString();
            taintMethods.put(id, method);
        }
        return method;
    }

    public IAtomMethod getDirectMethod(String methodName, String methodDescriptor, Object[] lastMethodArgs) {
        return searchMethod(methodName, methodDescriptor, lastMethodArgs);
    }

    @Override
    public IAtomMethod getDirectMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null);
    }

    @Override
    public IAtomMethod[] getDirectMethods() {
        return new IAtomMethod[0];
    }

    public IAtomMethod getVirtualMethod(String name, String descriptor, Object[] args) {
        return searchMethod(name, descriptor, null);
    }

    @Override
    public IAtomMethod[] getVirtualMethods() {
        Collection<IAtomMethod> list = taintMethods.values();
        return list.toArray(new IAtomMethod[list.size()]);
    }

    @Override
    public IAtomField getField(String fieldName, String fieldType) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return DynamicUtils.capitalizeString(this.name);
    }

    public void addDVMTaintField(IAtomField field) {
        this.taintFields.put(field.getName(), field);
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public IAtomMethod[] getAllMethods() {
        //tainted class must not be analyzed because they are part of sdk
        return new IAtomMethod[0];
    }

    @Override
    public void addMethod(IAtomMethod method) {
        this.taintMethods.put(method.getName(), method);
    }
}