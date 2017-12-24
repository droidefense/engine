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

package droidefense.emulator.machine.base.struct.fake;


import droidefense.emulator.machine.base.DynamicUtils;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseField;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.emulator.machine.base.struct.model.DVMClass;
import droidefense.sdk.util.InternalConstant;

import java.util.Collection;
import java.util.HashMap;

public final class DVMTaintClass extends DVMClass {


    protected HashMap<String, IDroidefenseField> taintFields;

    protected HashMap<String, IDroidefenseMethod> taintMethods;

    public DVMTaintClass(String name) {
        super();
        this.name = name;
        taintFields = new HashMap<>();
        taintMethods = new HashMap<>();
        this.setSuperClass(InternalConstant.SUPERCLASS);
    }

    public HashMap<String, IDroidefenseField> getTaintFields() {
        return taintFields;
    }

    public void setTaintFields(HashMap<String, IDroidefenseField> taintFields) {
        this.taintFields = taintFields;
    }

    public HashMap<String, IDroidefenseMethod> getTaintMethods() {
        return taintMethods;
    }

    public void setTaintMethods(HashMap<String, IDroidefenseMethod> methods) {
        this.taintMethods = methods;
    }

    @Override
    public IDroidefenseField getStaticField(String name) {
        IDroidefenseField field = taintFields.get(name);
        if (field == null) {
            field = new DVMTaintField(name, this);
            taintFields.put(name, field);
        }
        return field;
    }

    public IDroidefenseMethod searchMethod(String name, String desc, Object[] lastMethodArgs) {
        IDroidefenseMethod method = taintMethods.get(name + desc);
        if (method == null) {
            method = new DVMTaintMethod(name, this.getName());
            method.setDescriptor(desc);
            String id = method.toString();
            taintMethods.put(id, method);
        }
        return method;
    }

    @Override
    public IDroidefenseMethod getMethod(String name, String desc, boolean fake) {
        IDroidefenseMethod method = taintMethods.get(name + desc);
        if (method == null) {
            method = new DVMTaintMethod(name, this.getName());
            method.setDescriptor(desc);
            String id = method.toString();
            taintMethods.put(id, method);
        }
        return method;
    }

    public IDroidefenseMethod getDirectMethod(String methodName, String methodDescriptor, Object[] lastMethodArgs) {
        return searchMethod(methodName, methodDescriptor, lastMethodArgs);
    }

    @Override
    public IDroidefenseMethod getDirectMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null);
    }

    @Override
    public IDroidefenseMethod[] getDirectMethods() {
        return new IDroidefenseMethod[0];
    }

    public IDroidefenseMethod getVirtualMethod(String name, String descriptor, Object[] args) {
        return searchMethod(name, descriptor, null);
    }

    @Override
    public IDroidefenseMethod[] getVirtualMethods() {
        Collection<IDroidefenseMethod> list = taintMethods.values();
        return list.toArray(new IDroidefenseMethod[list.size()]);
    }

    @Override
    public IDroidefenseField getField(String fieldName, String fieldType) {
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

    public void addDVMTaintField(IDroidefenseField field) {
        this.taintFields.put(field.getName(), field);
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public IDroidefenseMethod[] getAllMethods() {
        //tainted class must not be analyzed because they are part of sdk
        return new IDroidefenseMethod[0];
    }

    @Override
    public void addMethod(IDroidefenseMethod method) {
        this.taintMethods.put(method.getName(), method);
    }
}