/*
 * Copyright 2015 Red Naga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.content.res.chunk;

/**
 * Enum for ChunkTypes - the different types of Chunks available to create
 *
 * @author tstrazzere
 */
public enum ChunkType {
    BUFFER {
        // This is a faked type
        @Override
        public int getIntType() {
            return 0;
        }
    },
    ATTRIBUTE {
        // This is a faked type
        // XXX : Unneeded?
        @Override
        public int getIntType() {
            return 0;
        }
    },
    AXML_HEADER {
        @Override
        public int getIntType() {
            return 0x00080003;
        }
    },
    STRING_SECTION {
        @Override
        public int getIntType() {
            return 0x001C0001;
        }
    },
    RESOURCE_SECTION {
        @Override
        public int getIntType() {
            return 0x00080180;
        }
    },
    START_NAMESPACE {
        @Override
        public int getIntType() {
            return 0x00100100;
        }
    },
    END_NAMESPACE {
        @Override
        public int getIntType() {
            return 0x00100101;
        }
    },
    START_TAG {
        @Override
        public int getIntType() {
            return 0x00100102;
        }
    },
    END_TAG {
        @Override
        public int getIntType() {
            return 0x00100103;
        }
    },
    TEXT_TAG {
        @Override
        public int getIntType() {
            return 0x00100104;
        }
    };

    public abstract int getIntType();
}
