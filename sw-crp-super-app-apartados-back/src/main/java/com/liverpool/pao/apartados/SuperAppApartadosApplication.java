package com.liverpool.pao.apartados;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public final class SuperAppApartadosApplication {

    private SuperAppApartadosApplication() { }

    public static void main(final String... args) {
        Quarkus.run(args);
    }
}
