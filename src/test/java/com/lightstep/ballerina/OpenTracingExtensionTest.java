package com.lightstep.ballerina;

import static org.junit.Assert.assertFalse;

import com.lightstep.tracer.jre.JRETracer;
import java.util.HashMap;
import org.ballerinalang.config.ConfigRegistry;
import org.junit.Test;

public class OpenTracingExtensionTest {

  @Test
  public void testGetTracer() throws Exception {
    ConfigRegistry.getInstance()
        .initRegistry(new HashMap<>(), "src/test/resources/ballerina.conf", null);
    OpenTracingExtension extension = new OpenTracingExtension();
    extension.init();

    JRETracer tracer = (JRETracer) extension.getTracer("lightstep", "echo");
    assertFalse(tracer.isDisabled());
  }
}