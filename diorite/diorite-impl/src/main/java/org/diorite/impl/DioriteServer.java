/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.impl;

import javax.annotation.Nullable;

import org.diorite.DioriteConfig;
import org.diorite.core.DioriteCore;
import org.diorite.inject.Inject;
import org.diorite.inject.InjectableClass;
import org.diorite.inject.Injection;

@InjectableClass
public class DioriteServer implements DioriteCore
{
    @Inject private final DioriteConfig dioriteConfig = Injection.inject();

    @Override
    public DioriteConfig getConfig()
    {
        return this.dioriteConfig;
    }

    void start()
    {

    }

    @Nullable private static DioriteServer instance;

    {
        System.out.println("ss");
        if (instance != null)
        {
            throw new RuntimeException("Server already initialized!");
        }
        instance = this;
    }

    @Nullable public static DioriteServer getInstance()
    {
        return instance;
    }
}