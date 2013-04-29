package org.geogit.web.api.repo;

import java.io.IOException;
import java.io.Writer;

import org.geogit.api.GeoGIT;
import org.geogit.api.ObjectId;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ServerResource;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class ParentResource extends ServerResource {
    {
        getVariants().add(new ParentRepresentation());
    }

    private class ParentRepresentation extends WriterRepresentation {
        public ParentRepresentation() {
            super(MediaType.TEXT_PLAIN);
        }

        @Override
        public void write(Writer w) throws IOException {
            Form options = getRequest().getResourceRef().getQueryAsForm();

            Optional<String> commit = Optional
                    .fromNullable(options.getFirstValue("commitId", null));

            GeoGIT ggit = (GeoGIT) getApplication().getContext().getAttributes().get("geogit");

            if (commit.isPresent()) {
                ImmutableList<ObjectId> parents = ggit.getRepository().getGraphDatabase()
                        .getParents(ObjectId.valueOf(commit.get()));
                for (ObjectId object : parents) {
                    w.write(object.toString() + "\n");
                }
            }
            w.flush();
        }
    }
}
