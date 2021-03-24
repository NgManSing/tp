package seedu.duke.command;

import seedu.duke.Project;
import seedu.duke.parser.CommandParser;
import seedu.duke.parser.InputParser;
import seedu.duke.resource.Resource;
import seedu.duke.ui.MainUi;

import java.util.ArrayList;

public class CommandHandler {
    private static final String ADD_COMMAND = "add";
    private static final String DELETE_COMMAND = "delete";
    private static final String EXIT_COMMAND = "exit";
    private static final String LIST_ALL_COMMAND = "list-all";
    private static final String HELP_COMMAND = "help";
    private static final String LIST_ONE_PROJECT_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String EDIT_COMMAND = "edit";
    String command;
    String[] infoFragments;
    private final ArrayList<Project> projects;

    public CommandHandler(InputParser userInput, ArrayList<Project> projects) {
        this.command = userInput.getCommand();
        this.infoFragments = userInput.getInfoFragments();
        this.projects = projects;
    }

    public boolean processCommand() {
        boolean isLoop = true;
        switch (this.command) {
        case ADD_COMMAND:
            processInputBeforeAdding();
            break;
        case DELETE_COMMAND:
            processInputBeforeDeleting();
            break;
        case EXIT_COMMAND:
            MainUi.showExitMessage();
            isLoop = false;
            break;
        case LIST_ALL_COMMAND:
            printAllProjectsAndResources();
            break;
        case LIST_ONE_PROJECT_COMMAND:
            String projectName = processProjectName();
            printProjectResources(projectName);
            break;
        case EDIT_COMMAND:
            processInputBeforeEditing();
            break;
        case FIND_COMMAND:
            processInputBeforeFinding();
            break;
        case HELP_COMMAND:
            listAllCommands();
            break;
        default:
            promptUserInvalidInput();
            break;
        }
        return isLoop;
    }

    /**
     * This method will return the project name from userInput.
     *
     * @return Project Name
     */
    private String processProjectName() {
        String[] projectNameArray = infoFragments;
        String projectName = String.join(" ", projectNameArray);
        return projectName;
    }

    /**
     * This method will print the resources for a particular project.
     *
     * @param projectName input Project Name
     */
    private void printProjectResources(String projectName) {
        if (projectName.equals("")) {
            System.out.println("You did not key in the Project Name! Please type \"help\" for more details.");
            return;
        }
        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                System.out.print("--------------------------------------------------------" + "\n");
                System.out.println("Project: " + projectName);
                ArrayList<Resource> resources = project.getResources();
                int resourceCount = 0;
                resourceCount += 1;
                printResourcesForAProject(resourceCount, resources);
                System.out.print("--------------------------------------------------------" + "\n");
                return;
            }
        }
        System.out.println("Project not found!");
    }

    private void processInputBeforeAdding() {
        String[] keywords = {"p/", "url/", "d/"};
        int firstOptionalKeyword = 2;
        String[] projectInfo = CommandParser.decodeInfoFragments(infoFragments, keywords, firstOptionalKeyword);

        if (projectInfo != null) {
            addResource(projectInfo);
        } else {
            System.out.print("Resource is failed to be added!" + "\n");
        }

    }

    public void addResource(String[] projectInfo) {
        assert projectInfo != null;
        String projectName = projectInfo[0];
        String projectUrl = projectInfo[1];
        String descriptionOfUrl = projectInfo[2];

        int projectIndex = searchExistingProjectIndex(projectName);

        if (projectIndex == -1) {
            createNewProject(projectName, projectUrl, descriptionOfUrl);
            return;
        }

        if (isUrlAlreadyExist(projectIndex, projectUrl)) {
            overwriteResource(projectName, projectUrl, descriptionOfUrl, projectIndex);
        } else {
            addNewResource(projectName, projectUrl, descriptionOfUrl, projectIndex);
        }
    }

    private void createNewProject(String projectName, String projectUrl, String descriptionOfUrl) {
        projects.add(new Project(projectName, projectUrl, descriptionOfUrl));
        System.out.printf("The resource is added into the new project \"%s\".\n", projectName);
    }

    private void addNewResource(String projectName, String projectUrl, String descriptionOfUrl, int projectIndex) {
        projects.get(projectIndex).addResources(projectUrl, descriptionOfUrl);
        System.out.printf("The resource is added to the existing project \"%s\".\n", projectName);
    }

    private void overwriteResource(String projectName, String projectUrl, String descriptionOfUrl, int projectIndex) {
        projects.remove(projectIndex);
        projects.add(projectIndex, new Project(projectName, projectUrl, descriptionOfUrl));
        System.out.printf("The resource of the project \"%s\" is overwritten.\n", projectName);
    }

    private boolean isUrlAlreadyExist(int projectIndex, String projectUrl) {
        return projects.get(projectIndex).isUrlAlreadyExist(projectUrl);
    }

    private int searchExistingProjectIndex(String projectName) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectName().equals(projectName)) {
                return i;
            }
        }
        return -1;
    }

    private void processInputBeforeDeleting() {
        String[] keywords = {"p/", "i/"};
        int firstOptionalKeyword = 1;
        String[] projectInfo = CommandParser.decodeInfoFragments(infoFragments, keywords, firstOptionalKeyword);

        if (projectInfo == null) {
            System.out.print("Resource failed to be deleted!" + "\n");
            return;
        }
        deleteResource(projectInfo);
    }

    public void deleteResource(String[] projectInfo) {
        Project targetedProj = null;
        String projectName = projectInfo[0];
        int idx = -1;

        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                targetedProj = project;
                break;
            }
        }
        if (targetedProj == null) {
            System.out.print("Project is not found ... " + "\n");
            return;
        }

        try {
            if (projectInfo[1] != null) {
                idx = Integer.parseInt(projectInfo[1]) - 1;
                targetedProj.getResources().remove(idx);
                System.out.printf("The resource is deleted from the project \"%s\".\n", projectName);
            } else {
                // If index is not indicated, remove all resources from the specified project.
                targetedProj.getResources().removeAll(targetedProj.getResources());
                System.out.printf("All the resources in %s has been deleted.\n", projectName);
                return;
            }
        } catch (Exception e) {
            System.out.print("Resource is not found. Please enter a valid index. " + "\n");
            return;
        }
    }

    private void processInputBeforeEditing() {
        String[] keywords = {"p/", "i/", "url/", "d/"};
        int firstOptionalKeyword = 1;
        String[] projectInfo = CommandParser.decodeInfoFragments(infoFragments, keywords, firstOptionalKeyword);

        if (projectInfo == null) {
            System.out.print("Resource failed to be edited!" + "\n");
            return;
        }
        editResource(projectInfo);
    }

    public void editResource(String[] projectInfo) {
        Project targetedProj = null;
        Resource targetedResource = null;
        String projectName = projectInfo[0];
        int idx = -1;

        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                targetedProj = project;
                break;
            }
        }
        if (targetedProj == null) {
            System.out.print("Project is not found ... " + "\n");
            return;
        }

        try {
            idx = Integer.parseInt(projectInfo[1]) - 1;
            targetedResource = targetedProj.getResources().get(idx);
            if (projectInfo[2] != null) {
                targetedResource.setResourceLink(projectInfo[2]);
                System.out.printf("The resource is successfully edited to : \n");
                System.out.printf("    " + targetedResource.toString() + "\n");
            }
            // GOT ERROR HERE. -- cannot edit description without editing url
            // edit p/Jester's jokes i/1 d/test will be read as :
            // projectInfo[0] = 'Jester's jokes'
            // projectInfo[1] = '1 d/test'
            if (projectInfo[3] != null) {
                targetedResource.setResourceDescription(projectInfo[3]);
            }
            if (projectInfo[2] == null & projectInfo[3] == null) {
                System.out.println("The resource is not edited.");
            }
        } catch (Exception e) {
            System.out.printf("Resource is not found. Please enter a valid index. " + "\n");
            return;
        }


    }

    private void promptUserInvalidInput() {
        System.out.print("Invalid input! Please type \"help\" for more details." + "\n");
    }

    private void printAllProjectsAndResources() {
        int projectCount = 0;
        System.out.print("Here is the list of all project(s) and it's resource(s)!" + "\n");
        System.out.print("--------------------------------------------------------" + "\n");
        for (Project project : projects) {
            projectCount += 1;
            System.out.print("Project " + projectCount + ": " + project + "\n");
            ArrayList<Resource> resources = project.getResources();
            int resourceCount = 0;
            resourceCount += 1;
            printResourcesForAProject(resourceCount, resources);
            System.out.print("--------------------------------------------------------" + "\n");
        }
        assert true;
    }

    private static void printResourcesForAProject(int resourceCount, ArrayList<Resource> resources) {
        System.out.print("Resource(s):" + "\n");
        for (Resource resource : resources) {
            System.out.print(resourceCount + "): " + resource + "\n");
            resourceCount += 1;
        }
        assert true;
    }

    public void listAllCommands() {
        MainUi.listAllCommands();
    }

    private void processInputBeforeFinding() {
        String[] keywords = {"k/", "p/"};
        int firstOptionalKeyword = 1;
        String[] keywordInfo = CommandParser.decodeInfoFragments(infoFragments, keywords, firstOptionalKeyword);

        if (keywordInfo == null) {
            System.out.print("Resources matching keyword failed to be found!" + "\n");
            return;
        }
        findResources(keywordInfo);
    }

    private void findResources(String[] keywordInfo) {
        if (keywordInfo[1] == null) {
            String keyword = keywordInfo[0];
            printAllProjectsAndResourcesMatchingKeyword(keyword);
        } else {
            String keyword = keywordInfo[0];
            String projectName = keywordInfo[1];
            printResourcesInProjectMatchingKeyword(projectName, keyword);
        }
    }

    private void printAllProjectsAndResourcesMatchingKeyword(String keyword) {
        int projectCount = 0;
        System.out.print("Here is the list of all project(s) and its resource(s) matching the keyword!" + "\n");
        System.out.print("--------------------------------------------------------" + "\n");
        for (Project project : projects) {
            projectCount += 1;
            System.out.print("Project " + projectCount + ": " + project + "\n");
            ArrayList<Resource> resources = project.getResources();
            printResourcesMatchingKeyword(resources, keyword);
            System.out.print("--------------------------------------------------------" + "\n");
        }
    }

    private void printResourcesInProjectMatchingKeyword(String projectName, String keyword) {
        Boolean isProject = Boolean.FALSE;
        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                isProject = Boolean.TRUE;
                System.out.print("--------------------------------------------------------" + "\n");
                System.out.println("Project: " + projectName);
                ArrayList<Resource> resources = project.getResources();
                printResourcesMatchingKeyword(resources, keyword);
                System.out.print("--------------------------------------------------------" + "\n");
            }
        }
        if (!isProject) {
            System.out.println("Project cannot be found! Please enter a valid project name!");
        }
    }

    private void printResourcesMatchingKeyword(ArrayList<Resource> resources, String keyword) {
        int resourceCount = 1;
        for (Resource resource : resources) {
            if (checkKeywordMatch(resource, keyword)) {
                System.out.print(resourceCount + "): " + resource + "\n");
                resourceCount += 1;
            }
        }
        if (resourceCount == 1) {
            System.out.printf("No resources matching keyword \"%s\" found!\n", keyword);
        }
    }

    private Boolean checkKeywordMatch(Resource resource, String keyword) {
        if (resource.getResourceDescriptionOnly().toLowerCase().contains(keyword.toLowerCase())) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
