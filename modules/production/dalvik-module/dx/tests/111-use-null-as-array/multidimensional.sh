#!/bin/bash
#
# Copyright (C) 2015 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

echo '
.class multidimensional
.super java/lang/Object
'

function onetype() {
local typename=$1
local stacksize=$2
local defaultvalue=$3
local descriptor=$4
local defaultload=$5
local loadstoreprefix=$6
local returnprefix=${7:-$loadstoreprefix}
echo "
; Output from some versions of javac on:
; public static $typename test_get${typename^}Array() {
;     $typename[][] array = null;
;     return array[1][1];
; }
.method public static test_get${typename^}Array()$descriptor
    .limit locals 1
    .limit stack 2

    aconst_null
    astore_0
    aload_0
    iconst_1
    aaload
    iconst_1
    ${loadstoreprefix}aload
    ${returnprefix}return
.end method

; Output from some versions of javac on:
; public static void test_set${typename^}Array() {
;     $typename[][] array = null;
;     array[1][1] = $defaultvalue;
; }
.method public static test_set${typename^}Array()V
    .limit locals 1
    .limit stack $((stacksize+2))

    aconst_null
    astore_0
    aload_0
    iconst_1
    aaload
    iconst_1
    $defaultload
    ${loadstoreprefix}astore
    return
.end method
"
}

onetype Object 1 null 'Ljava/lang/Object;' aconst_null a
onetype boolean 1 false Z iconst_0 b i
onetype byte 1 0 B iconst_0 b i
onetype char 1 0 C iconst_0 c i
onetype short 1 0 S iconst_0 s i
onetype int 1 0 I iconst_0 i
onetype long 2 0 J lconst_0 l
onetype float 1 0 F fconst_0 f
onetype double 2 0 D dconst_0 d
