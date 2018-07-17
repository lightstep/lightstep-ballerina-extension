# Ballerina LightStep Extension

## Install guide
1. Build `lightstep-ballerina-extension` and copy the jar files found in 
`<lightstep-ballerina-extension>/target/distribution/` into the `bre/lib/` 
directory of the ballerina distribution.

1. Create a `ballerina.conf` file with following properties:
    ```text
    [b7a.observability.tracing]
    enabled=true
    name="lightstep"
    
    [b7a.observability.tracing.lightstep]
    reporter.component.name="ballerina-test"
    reporter.access.token="<your LightStep token>"   
    ```
1. Run your Ballerina service with that ballerina.conf file.
   - Either place ballerina.conf in your applications directory.
   - Or use --config path/to/ballerina.conf

1. Once everything is up and running, you can use LightStep dashboard to view traces 


## Supported configuration parameters

- reporter.access.token
- reporter.component.name
- reporter.collector.hostname
- reporter.collector.port
- reporter.collector.protocol
- reporter.verbosity (0-4)
- reporter.clock.skew.correction (true | false)
- reporter.disable.reporting.loop (true | false)  
- reporter.max.buffered.spans
- reporter.max.reporting.interval.millis
- reporter.reset.client (true | false)  

