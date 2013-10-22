/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */
package org.geogit.test.integration;

import org.geogit.api.InjectorBuilder;
import org.geogit.api.MemoryModule;
import org.geogit.api.Platform;
import org.geogit.di.GeogitModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class TestInjectorBuilder extends InjectorBuilder {

    Platform platform;

    public TestInjectorBuilder(Platform platform) {
        this.platform = platform;
    }

    @Override
    public Injector build() {
        return Guice.createInjector(Modules.override(new GeogitModule()).with(
                new MemoryModule(platform)));
    }

    @Override
    public Injector buildWithOverrides(Module... overrides) {
        return Guice.createInjector(Modules.override(
                Modules.override(new GeogitModule()).with(new MemoryModule(platform))).with(
                overrides));
    }

}
