package com.structurizr.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A dynamic view, used to describe behaviour between static elements at runtime.
 */
public class DynamicView extends View {

    private Model model;

    private Element element;
    private String elementId;

    private SequenceNumber sequenceNumber = new SequenceNumber();

    DynamicView() {
    }

    DynamicView(Model model, String key, String description) {
        super(null, key, description);

        this.model = model;
        this.element = null;
    }

    DynamicView(SoftwareSystem softwareSystem, String key, String description) {
        super(softwareSystem, key, description);

        this.model = softwareSystem.getModel();
        this.element = softwareSystem;
    }

    DynamicView(Container container, String key, String description) {
        super(container.getSoftwareSystem(), key, description);

        this.model = container.getModel();
        this.element = container;
    }

    @JsonIgnore
    @Override
    public Model getModel() {
        return this.model;
    }

    void setModel(Model model) {
        this.model = model;
    }

    @Override
    @JsonIgnore
    public String getSoftwareSystemId() {
        return super.getSoftwareSystemId();
    }

    /**
     * Gets the ID of the container associated with this view.
     *
     * @return the ID, as a String
     */
    public String getElementId() {
        if (this.element != null) {
            return element.getId();
        } else {
            return this.elementId;
        }
    }

    void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @JsonIgnore
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public RelationshipView add(Element source, Element destination) {
        return add(source, "", destination);
    }

    public RelationshipView add(Element source, String description, Element destination) {
        if (source != null && destination != null) {
            checkElement(source);
            checkElement(destination);

            // check that the relationship is in the model before adding it
            Relationship relationship = source.getEfferentRelationshipWith(destination);
            if (relationship != null) {
                addElement(source, false);
                addElement(destination, false);
                RelationshipView relationshipView = addRelationship(relationship, description, sequenceNumber.getNext());
                return relationshipView;
            } else {
                throw new IllegalArgumentException("Relationship does not exist in model");
            }
        } else {
            throw new IllegalArgumentException("Source and destination must not be null");
        }
    }

    /**
     * This checks that only appropriate elements can be added to the view.
     */
    private void checkElement(Element e) {
        // people can always be added
        if (e instanceof Person) {
            return;
        }

        // if the scope of this dynamic is a software system, we only want:
        //  - containers inside that software system
        //  - other software systems
        if (element instanceof SoftwareSystem) {
            if (e.equals(element)) {
                throw new IllegalArgumentException(e.getName() + " is already the scope of this view and cannot be added to it.");
            }
            if (e instanceof Container && !e.getParent().equals(element)) {
                throw new IllegalArgumentException("Only containers that reside inside " + element.getName() + " can be added to this view.");
            }
            if (e instanceof Component) {
                throw new IllegalArgumentException("Components can't be added to a dynamic view when the scope is a software system.");
            }
        }

        // if the scope of this dynamic view is a container, we only want other containers inside the same software system
        // and other components inside the container
        if (element instanceof Container) {
            if (e.equals(element) || e.equals(element.getParent())) {
                throw new IllegalArgumentException(e.getName() + " is already the scope of this view and cannot be added to it.");
            }
            if (e instanceof Container && !e.getParent().equals(element.getParent())) {
                throw new IllegalArgumentException("Only containers that reside inside " + element.getParent().getName() + " can be added to this view.");
            }

            if (e instanceof Component && !e.getParent().equals(element)) {
                throw new IllegalArgumentException("Only components that reside inside " + element.getName() + " can be added to this view.");
            }
        }
    }

    @Override
    public RelationshipView add(Relationship relationship) {
        // when adding a relationship to a DynamicView we suppose the user really wants to also see both elements
        addElement(relationship.getSource(), false);
        addElement(relationship.getDestination(), false);
        return super.add(relationship);
    }

    @Override
    protected RelationshipView findRelationshipView(RelationshipView sourceRelationshipView) {
        for (RelationshipView relationshipView : getRelationships()) {
            if (relationshipView.getRelationship().equals(sourceRelationshipView.getRelationship())) {
                if ((relationshipView.getDescription() != null && relationshipView.getDescription().equals(sourceRelationshipView.getDescription())) &&
                        relationshipView.getOrder().equals(sourceRelationshipView.getOrder())) {
                    return relationshipView;
                }
            }
        }

        return null;
    }

    @Override
    public String getName() {
        if (element != null) {
            return element.getName() + " - Dynamic";
        } else {
            return "Dynamic";
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        List<RelationshipView> list = new ArrayList<>(getRelationships());
        Collections.sort(list, (rv1, rv2) -> rv1.getOrder().compareTo(rv2.getOrder()));
        list.forEach(rv -> buf.append(rv.toString() + "\n"));

        return buf.toString();
    }

    public void startParallelSequence() {
        sequenceNumber.startParallelSequence();
    }

    public void endParallelSequence() {
        sequenceNumber.endParallelSequence();
    }

}