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
 * Simple POJO for keeping the offsets and data for items inside of "pools".
 *
 * @author tstrazzere
 */
public class PoolItem {
    private int itemOffset;
    private String itemData;

    public PoolItem(int offset, String data) {
        itemOffset = offset;
        itemData = data;
    }

    public int getOffset() {
        return itemOffset;
    }

    public void setString(String data) {
        itemData = data;
    }

    public String getString() {
        return itemData;
    }
}
