/*
 * Copyright (c) 2016 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.calculators.processors;

import me.lucko.luckperms.api.Tristate;
import me.lucko.luckperms.common.calculators.PermissionProcessor;

import java.util.Map;

public class WildcardProcessor implements PermissionProcessor {
    private Map<String, Boolean> map = null;

    @Override
    public Tristate hasPermission(String permission) {
        String node = permission;

        while (true) {
            int endIndex = node.lastIndexOf('.');
            if (endIndex == -1) {
                break;
            }

            node = node.substring(0, endIndex);
            if (!node.isEmpty()) {
                Boolean b = map.get(node + ".*");
                if (b != null) {
                    return Tristate.fromBoolean(b);
                }
            }
        }

        Boolean b = map.get("'*'");
        if (b != null) {
            return Tristate.fromBoolean(b);
        }

        b = map.get("*");
        if (b != null) {
            return Tristate.fromBoolean(b);
        }

        return Tristate.UNDEFINED;
    }

    @Override
    public void updateBacking(Map<String, Boolean> map) {
        if (this.map == null) {
            this.map = map;
        }
    }
}
