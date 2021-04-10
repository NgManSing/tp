package seedu.duke.project;

import seedu.duke.Duke;
import seedu.duke.exception.NoProjectNameException;
import seedu.duke.exception.ProjectNotFoundException;
import seedu.duke.resource.Resource;
import seedu.duke.resource.ResourceManager;

import java.util.ArrayList;

import static seedu.duke.command.CommandHandler.printDivider;

public class ProjectManager {
    public static final String NEW_LINE = "\n";

    public static ArrayList<Project> projects;

    public static void getAllProjectsAndResourcesMatchingKeyword(String keyword, ArrayList<Project> projects) {
        int projectCount = 0;
        for (Project project : projects) {
            projectCount += 1;
            System.out.print("Project " + projectCount + ": " + project + NEW_LINE);
            ArrayList<Resource> resources = project.getResources();
            ResourceManager.printResourcesMatchingKeyword(resources, keyword);
        }
    }

    //@@author s-t-e-f
    /**
     * Search a project given the project name.
     * Return a project object if it is found.
     * Else, return null if the project name does not exist in the project list.
     * @param projName Name of the project to be searched
     * @return Project object with the specified project name
     */
    public static Project getProjByProjName(String projName) {
        projects = Duke.getProjects();
        for (Project project : projects) {
            if (project.getProjectName().equals(projName)) {
                return project;
            }
        }
        return null;
    }

    //@@author s-t-e-f
    /**
     * Delete all the resources in the project and the whole project from the project list.
     * @param proj Project to be deleted
     */
    public static void deleteWholeProject(Project proj) {
        projects = Duke.getProjects();
        proj.getResources().removeAll(proj.getResources());
        proj.getResources().removeAll(proj.getResources());
        System.out.printf("All the resources in %s has been deleted.\n", proj.getProjectName());
        projects.remove(proj);
    }

    //@@author NgManSing
    public static void newProject(String projectName, String projectUrl, String description) {
        projects.add(new Project(projectName, projectUrl, description));
    }

    //@@author NgManSing
    public static Project getProject(int projectIndex) {
        return projects.get(projectIndex);
    }

    //@@author NgManSing
    /**
     * Search a project with the provided project name within the project list. Index of the project is returned
     * if it is found.  if it is found. If no project in the list is named as the provided project name, -1 is returned.
     *
     * @param projectName Name of a Project
     * @return Index of the project or -1 if the project does not exist
     */
    public static int searchExistingProjectIndex(String projectName) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectName().equals(projectName)) {
                return i;
            }
        }
        return -1;
    }

    //@@author NgManSing
    public static void updateRecords() {
        projects = Duke.getProjects();
    }

    //@@author jovanhuang
    /**
     * This method will print the resource list for all projects.
     */
    public static void printResourceListForAllProjects() {
        int projectCount = 0;
        System.out.print("Here is the list of all project(s) and it's resource(s)!" + NEW_LINE);
        printDivider();
        for (Project project : projects) {
            projectCount += 1;
            System.out.print("Project " + projectCount + ": " + project + NEW_LINE);
            ArrayList<Resource> resources = project.getResources();
            ResourceManager.printResourceList(resources);
            printDivider();
        }
        assert true;
    }

    //@@author jovanhuang
    /**
     * This method will print the resources for a particular project.
     *
     * @throws NoProjectNameException   when user did not enter project name.
     * @throws ProjectNotFoundException when project is not found in database.
     * @param infoFragments is an string array of inputs from users
     */
    public static void printResourceListForAProject(String[] infoFragments) throws NoProjectNameException, ProjectNotFoundException {
        String projectName = processProjectName(infoFragments);
        boolean isProjectNameEmpty = checkIfProjectNameEmpty(projectName);
        if (isProjectNameEmpty) {
            throw new NoProjectNameException();
        }
        String newProjectName = projectName.substring(2);
        for (Project project : projects) {
            if (project.getProjectName().equals(newProjectName)) {
                printDivider();
                System.out.print("Project: " + newProjectName + NEW_LINE);
                ArrayList<Resource> resources = project.getResources();
                ResourceManager.printResourceList(resources);
                printDivider();
                return;
            }
        }
        throw new ProjectNotFoundException();
    }

    //@@author jovanhuang
    /**
     * This method will return the project name from userInput.
     *
     * @return Project Name is the name of the project.
     */
    public static String processProjectName(String[] infoFragments) {
        return String.join(" ", infoFragments);
    }

    //@@author jovanhuang
    /**
     * This method will check if project name is empty.
     *
     * @param projectName This string user's input for projectName.
     * @return true if empty, false if not empty.
     */
    public static boolean checkIfProjectNameEmpty(String projectName) {
        return projectName.equals("");
    }
}
