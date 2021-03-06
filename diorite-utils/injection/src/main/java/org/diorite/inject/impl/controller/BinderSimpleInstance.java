/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
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

package org.diorite.inject.impl.controller;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.diorite.inject.binder.Binder;
import org.diorite.inject.binder.BinderInstance;
import org.diorite.inject.binder.DynamicProvider;
import org.diorite.inject.binder.qualifier.QualifierPattern;

import net.bytebuddy.description.type.TypeDescription.Generic;

final class BinderSimpleInstance<T> implements BinderInstance<T>, Binder<T>
{
    private final Controller diController;
    private final Collection<QualifierPattern> patterns = new HashSet<>(2);
    private final Predicate<Generic> typePredicate;
    private       DynamicProvider<T> provider;

    BinderSimpleInstance(Controller diController, Predicate<Generic> typePredicate)
    {
        this.diController = diController;
        this.typePredicate = typePredicate;
    }

    @Override
    public BinderInstance<T> with()
    {
        return this;
    }

    @Override
    public BinderInstance<T> with(QualifierPattern a)
    {
        this.patterns.add(a);
        return this;
    }

    @Override
    public void dynamic(DynamicProvider<T> dynamic)
    {
        this.provider = dynamic;
        this.bind();
    }

    @Override
    public void toType(Class<? extends T> type)
    {
        for (Constructor<?> constructor : type.getDeclaredConstructors())
        {
            if (this.diController.isInjectElement(constructor))
            {
                this.dynamic(new BinderToConstructor<>(this.diController, type, constructor));
                return;
            }
        }
        for (Constructor<?> constructor : type.getDeclaredConstructors())
        {
            if (constructor.getParameterCount() == 0)
            {
                this.toProvider(new BinderToClassProvider<>(type));
                return;
            }
        }
        throw new IllegalArgumentException("Can't bind to that type, can't find suitable constructor.");
    }

    @Override
    public void toType(Class<? extends T> type, Class<?>... constructorParams)
    {
        int length = constructorParams.length;
        if (length == 0)
        {
            this.toType(type);
            return;
        }
        List<Class<?>> params = Arrays.asList(constructorParams);
        for (Constructor<?> constructor : type.getConstructors())
        {
            if (constructor.getParameterCount() != length)
            {
                continue;
            }
            if (params.containsAll(Arrays.asList(constructor.getParameterTypes())))
            {
                this.dynamic(new BinderToConstructor<>(this.diController, type, constructor));
                return;
            }
        }
        throw new IllegalArgumentException("Can't bind to that type, can't find suitable constructor.");
    }

    @Override
    public void toProvider(Supplier<? extends T> provider)
    {
        this.provider = (ignored1, ignored2) -> (T) provider.get();
        this.bind();
    }

    private void bind()
    {
        Predicate<Generic> typePredicate =
                type -> type.asErasure().equals(Controller.PROVIDER) ?
                        this.typePredicate.test(type.getTypeArguments().get(0)) :
                        this.typePredicate.test(type);
        BinderValueData valueData = BinderValueData.dynamic(typePredicate, this.patterns, this.provider);
        this.diController.bindValues.add(valueData);
    }
}
