package org.example.calculator.pm;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/dolphin/v1")
public class CalculatorPMResource {
    private static final Logger LOG = LoggerFactory.getLogger(DolphinServlet.class);

    @Inject
    private ServerDolphin serverDolphin;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String process(String input) {
        LOG.trace("Received JSON {}", input);
        List<Command> commands = serverDolphin.getServerConnector().getCodec().decode(input);
        List<Command> results = new ArrayList<>();
        for (Command cmd : commands) {
            LOG.trace("Processing {}", cmd);
            results.addAll(serverDolphin.getServerConnector().receive(cmd));
        }
        String output = serverDolphin.getServerConnector().getCodec().encode(results);
        LOG.trace("Sending JSON {}", output);
        return output;
    }
}
