package com.structurizr.encryption;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.structurizr.io.WorkspaceWriterException;

import java.io.Writer;

public final class EncryptedJsonWriter {

    private boolean indentOutput = true;

    public EncryptedJsonWriter(boolean indentOutput) {
        this.indentOutput = indentOutput;
    }

    /**
     * Writes an encrypted workspace definition as a JSON string to the specified Writer object.
     *
     * @param workspace     the Workspace object to write
     * @param writer        the Writer object to write the workspace to
     * @throws WorkspaceWriterException     if something goes wrong
     */
    public void write(EncryptedWorkspace workspace, Writer writer) throws WorkspaceWriterException {
        if (workspace == null) {
            throw new IllegalArgumentException("EncryptedWorkspace cannot be null.");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (indentOutput) {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            }
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            writer.write(objectMapper.writeValueAsString(workspace));
        } catch (Exception e) {
            throw new WorkspaceWriterException("Could not write as JSON", e);
        }
    }

}
