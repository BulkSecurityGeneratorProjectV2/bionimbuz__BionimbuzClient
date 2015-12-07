package br.unb.cic.bionimbuz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.model.diagram.Connection;

import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

public class WorkflowDiagram {

    // Constants
    private static final int INITIAL_X_POSITION = 1;
    private static final int X_POSITION_INCREMENT = 15;
    private static final String Y_POSITION = "15em";

    // Primefaces diagram model
    private DefaultDiagramModel workflowModel;

    // Elements of the pipeline
    private Element fromElement;
    private Element toElement;
    private StraightConnector connector;

    // X position of the element
    private int elementXPosition = INITIAL_X_POSITION;

    // List to implement the 'undo' action
    private List<Element> undoWorkflowList;

    // Index to point to the actual element 
    private int workflowIndex = 0;

    // The Pipeline object
    private Pipeline pipeline;

    /**
     * Calls the method that initializes everything...
     */
    public WorkflowDiagram(User user, String description) {
        // Initializes Pipeline
        this.pipeline = new Pipeline(user, description);
        initialize();
    }

    /**
     * Initializes variables, models, lists...
     */
    private void initialize() {
        // Initializes Workflow Model
        workflowModel = new DefaultDiagramModel();
        workflowModel.setMaxConnections(-1);
        workflowModel.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));

        // Initializes workflow list
        undoWorkflowList = new ArrayList<Element>();
        
        // Creates the Connector type
        StraightConnector connector = new StraightConnector();
        connector.setPaintStyle(DiagramStyle.CONNECTOR_STYLE);
        connector.setHoverPaintStyle(DiagramStyle.CONNECTOR_HOVER_STYLE);
        workflowModel.setDefaultConnector(connector);

        fromElement = createNewElement("Inicio", getElementXPosition(), Y_POSITION);

        workflowModel.addElement(fromElement);
    }

    /**
     * Adds a sequential program element to the workflow model
     *
     * @param program
     */
    public void addElement(ProgramInfo program, UploadedFileInfo inputFile) {
        // Creates the new Job
        JobInfo newJob = new JobInfo();
        newJob.setServiceId(program.getId());
        newJob.setTimestamp(Calendar.getInstance().getTime().toString());
        newJob.addInput(inputFile);

        // Adds it to the pipeline
        pipeline.addJobToPipeline(newJob);

        // Create new element
        toElement = createNewElement(program.getName(), getElementXPosition(), Y_POSITION);

        // Adds workflow model to workflow list and update its index 
        undoWorkflowList.add(toElement);
        workflowIndex++;

        // Add it to the model and connects it 
        workflowModel.addElement(toElement);

        // Turn the new element the new FROM element
        fromElement = toElement;
    }

    /**
     * Returns a new diagram element
     *
     * @param text
     * @return
     */
    private Element createNewElement(String text, String xPosition, String yPosition) {
        // Create new element
        Element newElement = new Element(text, xPosition, yPosition);

        // Creates Rectangle End Point
        EndPoint rectEndPoint = new RectangleEndPoint(EndPointAnchor.RIGHT);
        rectEndPoint.setSource(true);
        rectEndPoint.setStyle(DiagramStyle.RECTANGLE_STYLE);
        rectEndPoint.setHoverStyle(DiagramStyle.RECTANGLE_HOVER_STYLE);
        rectEndPoint.setSource(true);

        // Creates Dot End Point
        DotEndPoint dotEndPoint = new DotEndPoint(EndPointAnchor.LEFT);
        dotEndPoint.setTarget(true);
        dotEndPoint.setStyle(DiagramStyle.DOT_STYLE);
        dotEndPoint.setHoverStyle(DiagramStyle.DOT_HOVER_STYLE);
        dotEndPoint.setTarget(true);

        // Adds it to the new element
        newElement.addEndPoint(rectEndPoint);
        newElement.addEndPoint(dotEndPoint);

        return newElement;
    }

    /**
     * Returns X position of element (int -> String)
     *
     * @return
     */
    private String getElementXPosition() {
        String xPosition = Integer.toString(elementXPosition);
        elementXPosition += X_POSITION_INCREMENT;

        return (xPosition + "em");
    }

    /**
     * Undo an element addition and updates the references
     */
    public void undoAddition() {
        workflowModel.removeElement(undoWorkflowList.get(workflowIndex));
        fromElement = undoWorkflowList.get(workflowIndex - 1);
        undoWorkflowList.remove(workflowIndex);
        workflowIndex--;

        elementXPosition -= X_POSITION_INCREMENT;
    }

    public void endWorkflow() {
        // Create new element
        toElement = createNewElement("Fim", getElementXPosition(), Y_POSITION);

        // Adds workflow model to workflow list and update its index
        undoWorkflowList.add(toElement);
        workflowIndex++;

        // Add it to the model and connects it
        workflowModel.addElement(toElement);

        // Turn the new element the new FROM element
        fromElement = toElement;
    }

    /**
     * Resets current workflow
     */
    public void resetWorkflow() {
        elementXPosition = INITIAL_X_POSITION;
        workflowIndex = 0;

        // Reset variables
        initialize();
    }

    public class NetworkElement implements Serializable {

        private String name;
        private String image;

        public NetworkElement() {
        }

        public NetworkElement(String name, String image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public int getWorkflowIndex() {
        return this.workflowIndex;
    }

    public DefaultDiagramModel getWorkflow() {
        return this.workflowModel;
    }
}
