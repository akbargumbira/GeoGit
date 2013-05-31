package org.geogit.web.api.commands;

import java.util.Iterator;
import java.util.List;

import org.geogit.api.CommandLocator;
import org.geogit.api.ObjectId;
import org.geogit.api.plumbing.diff.DiffEntry;
import org.geogit.api.porcelain.DiffOp;
import org.geogit.api.porcelain.PullOp;
import org.geogit.api.porcelain.PullResult;
import org.geogit.api.porcelain.SynchronizationException;
import org.geogit.web.api.AbstractWebAPICommand;
import org.geogit.web.api.CommandContext;
import org.geogit.web.api.CommandResponse;
import org.geogit.web.api.ResponseWriter;

import com.google.common.collect.Lists;

public class PullWebOp extends AbstractWebAPICommand {

    private String remoteName;

    private boolean fetchAll;

    private List<String> refSpecs = Lists.newArrayList();

    /**
     * Mutator for the remoteName variable
     * 
     * @param remoteName - the name of the remote to add or remove
     */
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    /**
     * Mutator for the fetchAll variable
     * 
     * @param fetchAll - true to fetch all
     */
    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    /**
     * Mutator for the refSpecs variable
     * 
     * @param refSpecs - a list of all the refs to pull
     */
    public void setRefSpecs(List<String> refSpecs) {
        this.refSpecs = refSpecs;
    }

    /**
     * Adds a ref to the list of refs to pull
     * 
     * @param refSpec - a ref to pull
     */
    public void addRefSpec(String refSpec) {
        this.refSpecs.add(refSpec);
    }

    @Override
    public void run(CommandContext context) {
        final CommandLocator geogit = this.getCommandLocator(context);

        PullOp command = geogit.command(PullOp.class);

        for (String refSpec : refSpecs) {
            command.addRefSpec(refSpec);
        }
        try {
            final PullResult result = command.setRemote(remoteName).setAll(fetchAll).call();
            final Iterator<DiffEntry> iter;
            if (result.getOldRef() != null && result.getNewRef() != null
                    && result.getOldRef().equals(result.getNewRef())) {
                iter = null;
            } else {
                if (result.getOldRef() == null) {
                    iter = geogit.command(DiffOp.class)
                            .setNewVersion(result.getNewRef().getObjectId())
                            .setOldVersion(ObjectId.NULL).call();
                } else {
                    iter = geogit.command(DiffOp.class)
                            .setNewVersion(result.getNewRef().getObjectId())
                            .setOldVersion(result.getOldRef().getObjectId()).call();
                }
            }

            context.setResponseContent(new CommandResponse() {
                @Override
                public void write(ResponseWriter out) throws Exception {
                    out.start();
                    out.writePullResponse(result, iter);
                    out.finish();
                }
            });
        } catch (SynchronizationException e) {
            switch (e.statusCode) {
            case HISTORY_TOO_SHALLOW:
            default:
                context.setResponseContent(CommandResponse
                        .error("Unable to pull, the remote history is shallow."));
            }
        }
    }

}