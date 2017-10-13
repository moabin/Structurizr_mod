package com.structurizr.io.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.structurizr.Workspace;
import com.structurizr.io.WorkspaceWriter;
import com.structurizr.io.WorkspaceWriterException;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes a workspace definition as a JSON string.
 */
public final class JsonWriter implements WorkspaceWriter {

    private boolean indentOutput = true;

    public JsonWriter(boolean indentOutput) {
        this.indentOutput = indentOutput;
    }

    /**
     * Writes a workspace definition as a JSON string to the specified Writer object.
     *
     * @param workspace     the Workspace object to write
     * @param writer        the Writer object to write the workspace to
     * @throws WorkspaceWriterException     if something goes wrong
     */
    public void write(Workspace workspace, Writer writer) throws WorkspaceWriterException {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null.");
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
        } catch (IOException ioe) {
            throw new WorkspaceWriterException("Could not write as JSON", ioe);
        }
    }

}
