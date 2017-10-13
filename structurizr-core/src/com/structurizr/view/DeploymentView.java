package com.structurizr.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.model.*;

/**
 * A deployment view, used to show the mapping of container instances to deployment nodes.
 */
public class DeploymentView extends View {

    private Model model;

    DeploymentView() {
    }

    DeploymentView(Model model, String key, String description) {
        super(null, key, description);

        this.model = model;
    }

    DeploymentView(SoftwareSystem softwareSystem, String key, String description) {
        super(softwareSystem, key, description);

        this.model = softwareSystem.getModel();
    }

    @JsonIgnore
    @Override
    public Model getModel() {
        return this.model;
    }

    void setModel(Model model) {
        this.model = model;
    }

    /**
     * Adds all of the top-level deployment nodes to this view.
     */
    public void addAllDeploymentNodes() {
        getModel().getDeploymentNodes().forEach(this::add);
    }

    /**
     * Adds a deployment node to this view.
     *
     * @param deploymentNode        the DeploymentNode to add
     */
    public void add(DeploymentNode deploymentNode) {
        if (deploymentNode != null) {
            if (addContainerInstancesAndDeploymentNodes(deploymentNode)) {
                Element parent = deploymentNode.getParent();
                while (parent != null) {
                    addElement(parent, false);
                    parent = parent.getParent();
                }
            }
        }
    }

    private boolean addContainerInstancesAndDeploymentNodes(DeploymentNode deploymentNode) {
        boolean hasContainers = false;
        for (ContainerInstance containerInstance : deploymentNode.getContainerInstances()) {
            Container container = containerInstance.getContainer();
            if (getSoftwareSystem() == null || container.getParent().equals(getSoftwareSystem())) {
                addElement(containerInstance, true);
                hasContainers = true;
            }
        }

        for (DeploymentNode child : deploymentNode.getChildren()) {
            hasContainers = hasContainers | addContainerInstancesAndDeploymentNodes(child);
        }

        if (hasContainers) {
            addElement(deploymentNode, false);
        }

        return hasContainers;
    }

    /**
     * Removes a deployment node from this view.
     *
     * @param   deploymentNode      the DeploymentNode to remove
     */
    public void remove(DeploymentNode deploymentNode) {
        removeElement(deploymentNode);
    }

    @Override
    public String getName() {
        if (getSoftwareSystem() != null) {
            return getSoftwareSystem().getName() + " - Deployment";
        } else {
            return "Deployment";
        }
    }

}