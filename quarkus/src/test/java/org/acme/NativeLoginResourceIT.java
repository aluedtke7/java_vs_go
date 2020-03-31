package org.acme;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeLoginResourceIT extends LoginResourceTest {

    // Execute the same tests but in native mode.
}