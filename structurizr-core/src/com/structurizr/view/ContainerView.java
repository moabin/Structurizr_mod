package com.structurizr.view;

import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;

public class ContainerView extends StaticView {

    ContainerView() {
    }

    ContainerView(SoftwareSystem softwareSystem, String key, String description) {
        super(softwareSystem, key, description);
    }

    @Override
    public void add(SoftwareSystem softwareSystem) {
        if (softwareSystem != null && !softwareSystem.equals(getSoftwareSystem())) {
            addElement(softwareSystem, true);
        }
    }

    /**
     * Adds all containers in the software system to this view.
     */
    public void addAllContainers() {
        getSoftwareSystem().getContainers().forEach(this::add);
    }

    /**
     * Adds an individual container to this view.
     *
     * @param container     the Container to add
     */
    public void add(Container container) {
        if (container != null) {
            if (container.getParent().equals(getSoftwareSystem())) {
                addElement(container, true);
            } else {
                throw new IllegalArgumentException("Only containers belonging to " + getSoftwareSystem().getName() + " can be added to this view.");
            }
        }
    }

    /**
     * Removes an individual container from this view.
     *
     * @param container the Container to remove
     */
    public void remove(Container container) {
        removeElement(container);
    }

    @Override
    public String getName() {
        return getSoftwareSystem().getName() + " - Containers";
    }

    @Override
    public void addAllElements() {
        addAllSoftwareSystems();
        addAllPeople();
        addAllContainers();
    }

    @Override
    public void addNearestNeighbours(Element element) {
        super.addNearestNeighbours(element, SoftwareSystem.class);
        super.addNearestNeighbours(element, Person.class);
        super.addNearestNeighbours(element, Container.class);
    }

}