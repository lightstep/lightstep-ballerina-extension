package com.lightstep.ballerina;


import com.lightstep.tracer.jre.JRETracer;
import com.lightstep.tracer.shared.Options.OptionsBuilder;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import java.io.PrintStream;
import java.util.Objects;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.util.observability.ObservabilityConstants;
import org.ballerinalang.util.tracer.OpenTracer;

/**
 * This is the open tracing extension class for {@link OpenTracer}.
 */
@JavaSPIService("org.ballerinalang.util.tracer.OpenTracer")
public class OpenTracingExtension implements OpenTracer {

  private ConfigRegistry configRegistry;
  private String componentName;
  private String hostname;
  private String token;
  private String protocol;
  private Integer port;
  private Integer verbosity;
  private Integer maxBufferedSpans;
  private Integer maxReportingIntervalMillis;
  private Boolean resetClient;
  private Boolean clockSkewCorrection;
  private Boolean disableReportingLoop;

  private static final PrintStream console = System.out;
  private static final PrintStream consoleError = System.err;

  @Override
  public void init() {
    configRegistry = ConfigRegistry.getInstance();

    componentName = getString(Constants.REPORTER_COMPONENT_NAME);
    token = getString(Constants.REPORTER_ACCESS_TOKEN);
    hostname = getString(Constants.REPORTER_HOST_NAME);

    protocol = getString(Constants.REPORTER_PROTOCOL);
    port = getInt(Constants.REPORTER_PORT);
    verbosity = getInt(Constants.REPORTER_VERBOSITY);
    maxBufferedSpans = getInt(Constants.REPORTER_MAX_BUFFERED_SPANS);
    maxReportingIntervalMillis = getInt(Constants.REPORTER_MAX_REPORTING_INTERVAL_MILLIS);

    clockSkewCorrection = getBoolean(Constants.REPORTER_CLOCK_SKEW_CORRECTION);
    disableReportingLoop = getBoolean(Constants.REPORTER_DISABLE_REPORTING_LOOP);
    resetClient = getBoolean(Constants.REPORTER_RESET_CLIENT);

    console.println("ballerina: started publishing tracers to LightStep");
  }

  private Boolean getBoolean(String key) {
    String fullKey = getFullQualifiedConfig(key);
    if (configRegistry.contains(fullKey)) {
      return configRegistry.getAsBoolean(fullKey);
    }
    return null;
  }

  private Integer getInt(String key) {
    String fullKey = getFullQualifiedConfig(key);
    if (configRegistry.contains(fullKey)) {
      return Math.toIntExact(configRegistry.getAsInt(fullKey));
    }
    return null;
  }

  private String getString(String key) {
    String fullKey = getFullQualifiedConfig(key);
    if (configRegistry.contains(fullKey)) {
      return configRegistry.getAsString(fullKey);
    }
    return null;
  }

  @Override
  public Tracer getTracer(String tracerName, String serviceName) {
    if (Objects.isNull(configRegistry)) {
      throw new IllegalStateException("Tracer not initialized with configurations");
    }

    OptionsBuilder builder = new OptionsBuilder();
    if (hostname != null) {
      builder.withCollectorHost(hostname);
    }
    if (port != null) {
      builder.withCollectorPort(port);
    }
    if (componentName != null) {
      builder.withComponentName(componentName);
    }
    if (token != null) {
      builder.withAccessToken(token);
    }
    if (protocol != null) {
      builder.withCollectorProtocol(protocol);
    }
    if (verbosity != null) {
      builder.withVerbosity(verbosity);
    }
    if (clockSkewCorrection != null) {
      builder.withClockSkewCorrection(clockSkewCorrection);
    }
    if (disableReportingLoop != null) {
      builder.withDisableReportingLoop(disableReportingLoop);
    }
    if (maxBufferedSpans != null) {
      builder.withMaxBufferedSpans(maxBufferedSpans);
    }
    if (maxReportingIntervalMillis != null) {
      builder.withMaxReportingIntervalMillis(maxReportingIntervalMillis);
    }
    if (resetClient != null) {
      builder.withResetClient(resetClient);
    }

    try {
      return new JRETracer(builder.build());
    } catch (Exception e) {
      consoleError.println("failed to build LightStep tracer: " + e.getMessage());
      e.printStackTrace(consoleError);
      return NoopTracerFactory.create();
    }
  }

  private String getFullQualifiedConfig(String configName) {
    return ObservabilityConstants.CONFIG_TABLE_TRACING + "." + Constants.TRACER_NAME + "."
        + configName;
  }

  @Override
  public String getName() {
    return Constants.TRACER_NAME;
  }
}
