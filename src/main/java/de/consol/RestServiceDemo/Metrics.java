package de.consol.RestServiceDemo;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.OutputStreamWriter;
import java.io.Writer;

@Component
@Path("/")
public class Metrics {

	private final Logger logger = LogManager.getLogger(Metrics.class);

	private final Counter promRequestsTotal = Counter.build()
					.name("requests_total")
					.help("Total number of requests.")
					.register();
  {
    DefaultExports.initialize();
  }

  @GET()
  @Path("/hello-world")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello() {
    promRequestsTotal.inc();
    return "hello, world";
  }

  @GET()
  @Path("/metrics")
  @Produces(MediaType.TEXT_PLAIN)
  public StreamingOutput metrics() {
    logger.info("Starting service for metrics");
    return output -> {
      try (Writer writer = new OutputStreamWriter(output)) {
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
      }
    };
  }
}
