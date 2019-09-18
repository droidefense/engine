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
 * Enum for attribute types for ChunkTypes
 *
 * @author tstrazzere
 */
public enum AttributeType {

    STRING {
        @Override
        public int getIntType() {
            return 0x03000008;
        }
    },
    INT {
        @Override
        public int getIntType() {
            return 0x10000008;
        }
    },
    RESOURCE {
        @Override
        public int getIntType() {
            return 0x01000008;
        }
    },
    BOOLEAN {
        @Override
        public int getIntType() {
            return 0x12000008;
        }
    },
    ATTR {
        @Override
        public int getIntType() {
            return 0x02000008;
        }
    },
    DIMEN {
        @Override
        public int getIntType() {
            return 0x05000008;
        }
    },
    FRACTION {
        @Override
        public int getIntType() {
            return 0x06000008;
        }
    },
    FLOAT {
        @Override
        public int getIntType() {
            return 0x04000008;
        }
    },
    FLAGS {
        @Override
        public int getIntType() {
            return 0x11000008;
        }
    },
    COLOR1 {
        @Override
        public int getIntType() {
            return 0x1C000008;
        }
    },
    COLOR2 {
        @Override
        public int getIntType() {
            return 0x1D000008;
        }
    };

    public abstract int getIntType();

}
