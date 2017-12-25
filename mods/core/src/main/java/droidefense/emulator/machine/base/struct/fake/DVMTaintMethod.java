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


import droidefense.emulator.machine.base.struct.model.DVMMethod;

public final class DVMTaintMethod extends DVMMethod {

    //minimum valid value is 4
    private static final int[] NO_OP_CODES = {0x00, 0x00, 0x00, 0x00};
    private String name;

    private Object methodReturn;
    private boolean reflected;

    public DVMTaintMethod(String method, String classname) {
        super(new DVMTaintClass(classname));
        this.name = method;
        this.setDescriptor("()Tainted");
    }

    @Override
    public int[] getIndex() {
        return NO_OP_CODES;
    }

    @Override
    public int[] getOpcodes() {
        return NO_OP_CODES;
    }

    @Override
    public int[] getRegisterOpcodes() {
        return NO_OP_CODES;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getMethodReturn() {
        return methodReturn;
    }

    public void setMethodReturn(Object methodReturn) {
        this.methodReturn = methodReturn;
    }

    public boolean isReflected() {
        return reflected;
    }

    public void setReflected(boolean reflected) {
        this.reflected = reflected;
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
