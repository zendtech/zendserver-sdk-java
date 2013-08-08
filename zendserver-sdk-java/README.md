# Zend SDK
Zend SDK includes a variety of tools that help you create, develop, publish and discover PHP Web applications. If you are new to the Zend SDK, please [read an overview](#install_sdk) of how to set up the environment. If you're already using the Zend SDK, you should update to the latest one.

## Samples
The "Hello, World" example below shows how to launch your first application with Zend SDK [command line](#command_line):

    # clone an example project from a git repository
    $ zend clone project -r https://ganoro@github.com/ganoro/ExampleProject.git

    # define a target environment to host your application 
    $ zend add target -d <account-name>:<account-password>

    # set working directory
    $ cd ExampleProject

    # deploy the newly created application to the defined target
    $ zend deploy application
    
# Installing Zend SDK
This page describes how to install the Zend SDK and set up your development environment for the first time.

If you encounter any problems during installation, see the [Troubleshooting](#roubleshooting) section at the bottom of this page.

Here's an overview of the steps you must follow to set up the Zend SDK:

1. Prepare your development environment and ensure it meets the system requirements.
2. Install the SDK starter package.
3. Add Zend Target environment to your SDK.
4. Explore the contents of the Zend SDK (optional).
 
## Preparing Your Development Environment
Before getting started with the Zend SDK, take a moment to confirm that your development computer meets the System Requirements. In particular, you might need to install the [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html), if you don't have it already.

## Install the SDK
The SDK starter package is not a full development environment. It includes only the core SDK Tools, which you can use to set up local and remote application servers.

If you haven't already, get the latest version of the SDK starter package from the main (SDK download page) - add a link to the package.

After you downloaded a .zip or .tgz package , unpack it to a safe location on your machine. By default, the SDK files are unpacked into a directory named zend-sdk-<ver>

Make a note of the name and location of the SDK directory on your system - you will need to refer to the SDK directory later, when using the SDK tools from the command line.

If you prefer to work from the command line, read the [Command Line Tool Manual](#command_line), otherwise continue to step 3.

## Exploring the SDK (Optional)
Once you've installed the SDK and downloaded the platforms, documentation, and add-ons that you need, we suggest that you open the SDK directory and take a look at what's inside.

The table below describes the full SDK directory contents, with components installed.

<table>
    <tr>
        <td>Name</td>
        <td>Description</td>
    </tr>
    <tr>
        <td>lib/</td>
        <td>Contains the set of libraries that helps interacting with targets, create projects, configure environment and deploy applications. You mostly don't need to use these files directly</td>
    </tr>
    <tr>
        <td>resources/</td>
        <td>Contains the set of resources that are required to validate the package descriptor</td>
    </tr>
    <tr>
        <td>tools/</td>
        <td>Contains the set of development tools that are target-independent</td>
    </tr>
    <tr>
        <td>README.txt</td>
        <td>A file that explains how to perform the initial setup of your SDK, including how to launch the zend SDK</td>
    </tr>
</table>

Optionally, you might want to add the location of the SDK's tools/ and platform-tools to your PATH environment variable, to provide easy access to the tools.

## For Command Line'rs - How to update your PATH
Adding tools/ to your PATH lets you run command line tools without needing to supply the full path to the tool directories. Depending on your operating system, you can include these directories in your PATH in the following way:
- On Windows, right-click on My Computer, and select Properties. Under the Advanced tab, hit the Environment Variables button, and in the dialog that comes up, double-click on Path (under System Variables). Add the full path to the tools/ directory to the path. Or you can use this command line in cmd:
    
    set PATH=%PATH%;</path/to/sdk>/tools

- On Linux, edit your ~/.bash_profile or ~/.bashrc file. Look for a line that sets the PATH environment variable and add the full path to the tools/ directory to it. If you don't see a line setting the path, you can add one:
    
    export PATH=$PATH:</path/to/sdk>/tools

- On a Mac OS X, look in your home directory for .bash_profile and proceed as for Linux. You can create the .bash_profile if you don't already have one.

<a name="command_line" />
# Command Line

## Introduction
zend is an important development tool that lets you:

1. Create, delete, and view Targets. See [Managing Targets](#managing_targets) from the Command Line.
2. Create and update Zend projects. See [Managing Projects](#managing_projects) from the Command Line.
3. Deploy and update applications. See [Managing Applications](#managing_applications) from the Command Line.
4. Add and remove repositories. See [Managing Repositories](#managing_repositories) from the Command Line.
5. Monitor events Operations. See [Monitoring Applications](#monitoring_applications) from the Command Line.

If you are using Eclipse, the Zend tool's features are integrated, so you should not need to use this tool directly.

<a name="managing_targets" />
## Managing Targets
A target is a reference to a container that allows you to host PHP applications. This container can be a set of targets and possibly be managed as a cluster. Developers usually start by adding their target environments such as their localhost sever or any remote server.

### Listing Targets
To generate a list of targets, use this command:

    zend list targets [-s]

The flag "-s" tells the command line tool to connect to each target and show its status.

The Zend SDK tool scans the user targets directory looking for valid targets and then generates the list.

### Adding a Target
You can add a target by passing the target's information in command line arguments to the Zend SDK tool.

To add your localhost (http://localhost) server you have a simple detection method, see more information [here](#auto_detect).

To add target, you issue the command zend add target, with options that specify an id and host name for the new target. In order to work with this target one need to specify the key and secret key as specified in the [Zend Server API Key Managment document](http://files.zend.com/help/Zend-Server-6/content/api_keys.htm). Alternatively you can specify an option file that contains all required information about your target.

Here's the command-line usage for adding a target:

    zend add target [-t <target-id>] -k <key> -s <secret-key> -h <host> 

You can use any id you want for the target, but since you are likely to be creating multiple targets, you should choose a name that lets you recognize the general characteristics offered by the target. You can also choose to not specify any id so it is assigned by the Zend SDK tool. Host must be a valid URL of Zend Server (e.g. http://localhost) or phpCloud (e.g. https://your_name.my.phpcloud.com). There is an optional ability to provide a port, e.g. http://localhost:10081. It may be necessary when server uses a custom port.

Here's an example that creates a target with name "my_target":

    zend add target -t my_target -k studio -s cc14b445ad6ed9041d936b7f363a8e5a525275d3960dbb373f35e97e2abcdab2 -h http://dev.zend.com

Alternatively you can specify a file name that lists all properties of your target. This is helpful in order to quickly add keys for several targets.

    zend add target [-t <target-id>] -h <host> -p <properties-file>

The properties in the file should have the following format:

    key=<key>
    secretkey=<sk>

Here’s an example how you can create the same target but using a properties file:

    zend add target –t my_target –h http://dev.zend.com –p my_server.properties

Where my_server.properties file has the following contents:

    key=studio
    secretkey= cc14b445ad6ed9041d936b7f363a8e5a525275d3960dbb373f35e97e2abcdab2

### Adding a Zend Developer Cloud Target
You can also add your [Zend Developer Cloud](http://www.phpcloud.com/) targets by passing the account's information in command line arguments to the Zend SDK tool.

Given the your Zend Developer Cloud credentials (same as zend.com credentials) you can detect and addall http://*.my.phpcloud.com containers.

Here's the command-line usage for adding a target:

    zend add target -d [account-name or account-email]:[account-passwword]

Using the Zend Developer Cloud API, all containers created by you will be detected automatically and are visible to the Zend SDK.

### Removing a Target
You can use the zend tool to delete a target. Here is the command usage:

    zend remove target -t <id>

When you issue the command, the zend tool looks for a target matching the specified id deletes the targets reference from the user directory. This should not in any way affect your remote server.

<a name="auto_detect" />
### Auto-Detecting the localhost Server
Zend SDK tool can help you easily detect your localhost server without requiring to fill in all arguments such as keys and hostname. During detection time two operations are executed:

1. Finding a suitable key, if doesn't exist the tools tries to generate one.
2. Adding the localhost server as a new target.

To execute the first operation one need to have admin privileges as it requires write access to the Server configuration files.

#### Under Linux/Mac:

You will need to first run the detect target to generate the key and only then actually apply it with root privileges using sudo (or any su command).

    # detect localhost and generate a key for SDK 
    $ ./zend detect target 

    # apply secret key
    $ sudo ./zend detect target -k <key> -s <secret-key>
    
#### Under Windows7:

Use the elevate tool that helps to elevate privileges (bundled as part of Zend SDK tool and is located under the same location of the batch file):

    elevate zend detect target 

<a name="managing_projects" />
## Managing Projects
The default deployable project has to have two files which are required:

- deployment.xml
- deployment.properties

First file is a deployment descriptor. For more details, see DeploymentDescriptor?.

The second file consists definition of the file mapping. For more details about using deployment.properties file, see [this](#deployment_properites).

### Creating a Project
Zend SDK allows creating new deployable project which has all required files and a default folder structure. To create new project, use this command:

    zend create project -n <name> [-d <path>] [-t <name>] [-s <script names>]

The flag "-d" can be used if the new project should be created in a different location that the current one (as an argument pass the path to the project destination).

The flag "-t" tells the command line tool to create project based on specified template name. Possible arguments are (use only one of the following):

- simple - simple project structure
- zend - Zend Application structure
- quickstart - example Guestbook project

By default (if -t is not used) Zend Application is used as a template.

The flag "-s" tells the command line tool create scripts folder (which is optional and not created by default) with scripts passed as an argument (any of the following names: all|postActivate|postDeactivate|postStage|postUnstage|preActivate|preDeactivate|preStage|preUnstage). All scripts created using this option are empty files with comments how to use them.

### Updating a Project
Zend SDK allows also to update an existing project to the proper deployable project. To update an existing project, use this command:

    zend update project [-d <path>] [-s <script names>]

By default (command without any option) command line tool treats the current directory as a project root. To specify which application should be updated, use "-d" with a path to the project as an argument (relative to the current location). The flag "-s" has the same behavior as for the project creation (arguments are also the same).

In the result of the project update the following changes are applied:

- Descriptor file is created. Project root folder is taken as a project name.
- If "-s" option was used, then scripts folder is created
- Deployment.properties file is created. By default it maps all files from the project to the appdir directory, except descriptor file, scripts directory and files from the default exclusion list (for more details about it, see DeploymentPropertiesFile).

### Clone Project
Zend SDK allows also to clone project from git repository and update it (if necessary) to the proper deployable project. To clone project from git repository, use this command:

    zend clone project -r <repository> [-d <destination>] [-b <branch>] [-u <user>] [-p <password>] [-k <key>]

By default (command without any optional parameter) command line tool clones a project from specified repository. If "-d" option is used then project is cloned to the specified destination folder. Otherwise, project root folder is created based on repository name. Clone command support two authentication method, user/password and ssh (for github access). In the case of ssh, "-p" option is applied as a passphrase for a private key. After project is cloned then it is updated (see Updating a Project).

In the result of the project clone the following changes are applied:

- Repository is cloned to specified location (or to the default one)
- Descriptor file is created (if it is not available). Project root folder is taken as a project name.
- If "-s" option was used, then scripts folder is created
- Deployment.properties file is created (if descriptor is not available). By default it maps all files from the project to the appdir directory, except descriptor file, scripts directory and files from the default exclusion list (for more details about it, see [this](#deployment_properites)).

### Add Remote
Zend SDK allows to add new remote to the existing local git repository.

To add new remote, use this command:

    zend add remote -r <repository_url> [-a </path/to/project>]

The flag -a is not required if the current location is a root application folder. New remote name is based on the repository url domain. E.g. for git@github.com:mylogin/testapp.git url new remote will be called "github" and for https://login@login.my.phpcloud.com/git/testapp.git - it will be "phpcloud".

<a name="managing_applications" />
## Managing Applications
The main operations provided by Zend SDK are related to application management. It uses Zend WebAPI to expose the following operations:

- Deploy an Application
- Update an Application
- Remove an Application
- Redeploy an Application
- List Applications

Additionally, it also allows to create deployment package.

### Deploying an Application
Zend SDK allows to deploy a new application to the server or cluster. This process is asynchronous – the initial request will wait until the application is uploaded and verified, and the initial response will show information about the application being deployed – however the staging and activation process will proceed after the response is returned. The user may continue checking the application status using the list applications command until the deployment process is complete.

There are three possible sources of the application which should be deployed. For each of them there are some dedicated options. The common options are described below this section.

#### Local project or zpk package
    zend deploy application [-p </path/to/project-or-package>] [-b <base-path>] 
          -t <target-id> [-m </path/to/properties/file>] [-n <app-name>] [-f] [-h <host-name>]

where:
- -p : Path to the project root or to the zpk package. If not provided the current directory is used

#### Git repository

    zend deploy application -r <repository> [-b <branch>] [-u <user>] [-d <password>] [-k <key>] [-b <base-path>] 
          -t <target-id> [-m </path/to/properties/file>] [-n <app-name>] [-f] [-h <host-name>]

where:

- -r : Git repository to clone from, e.g. https://ganoro@github.com/ganoro/ExampleProject.git (required).
- -b : The initial branch to check out when cloning the repository.
- -u : User name if authentication is required.
- -d : Password if authentication is required.
- -k : Path to SSH private key if SSH authentication is used.

#### Zend Repository

    zend deploy application -z <zend-repository> -i <application-id> [-b <base-path>] 
          -t <target-id> [-m </path/to/properties/file>] [-n <app-name>] [-f] [-h <host-name>]

where:

- -z : Zend Repository URL (required).
- -i : Application id in specified Zend Repository which should be deployed (required).

The following options are common for all application sources:

- -b : Base path to deploy the application to. will be concatenated to the URL hostname. If not specified, the project name is considered.
- -p : Path to the project root or to the zpk package. If not provided the current directory is used
- -t : Id of the target where application should be deployed. If not specified the default target is considered.
- -m : Path to the properties file which has values for parameters defined in the deployment descriptor.
- -n : Free text for user defined application identifier. If not specified, the baseUrl parameter will be used.
- -f : Ignore failures during staging if only some servers reported failures. If all servers report failures the operation will fail in any case. By default any failure will return an error.
- -h : Specify the virtual host which should be used. If a virtual host with the specified name does not exist, it will be created. By default if virtual host is not specified then the default one will be used (marked as <default-server> in the application url).

### Updating an Application
Zend SDK allows to update an existing application. The package or project provided must be the same application as the one with specifed id. Additionally any new parameters or new values to existing parameters must be provided. This process is asynchronous – the initial request will wait until the package is uploaded and verified, and the initial response will show information about the new version being deployed – however the staging and activation process will proceed after the response is returned. The user may continue checking the application status using the list applications command until the updating process is complete.

There are three possible sources of the application which should be updated. For each of them there are some dedicated options. The common options are described below this section.

#### Local project or zpk package

    zend update application [-p </path/to/project-or-package>] -a <app-id> [-t <target-id>] 
            [-m </path/to/properties/file>] [-n <app-name>] [-f]

where:

- -p : Path to the project root or to the zpk package. If not provided the current directory is used

#### Git repository

    zend update application -r <repository> [-b <branch>] [-u <user>] [-p <password>] [-k <key>]
            -a <app-id> [-t <target-id>] [-m </path/to/properties/file>] [-n <app-name>] [-f]

where:

- -r : Git repository to clone from, e.g. https://ganoro@github.com/ganoro/ExampleProject.git (required).
- -b : The initial branch to check out when cloning the repository.
- -u : User name if authentication is required.
- -d : Password if authentication is required.
- -k : Path to SSH private key if SSH authentication is used.

#### Zend Repository

    zend update application -z <zend-repository> -i <application-id> -a <app-id> [-t <target-id>] 
            [-m </path/to/properties/file>] [-n <app-name>] [-f]

where:

- -z : Zend Repository URL (required).
- -i : Application id in specified Zend Repository which should be deployed (required).

The following options are common for all application sources:

- -a : Id of the application which should be updated (required).
- -t : Id of the target where application should be deployed. If not specified the default target is considered.
- -m : Path to the properties file which has values for parameters defined in the deployment descriptor.
- -f : Ignore failures during staging if only some servers reported failures. If all servers report failures the operation will fail in any case. By default any failure will return an error.

### Removing an Application
Zend SDK allows to remove/undeploy an existing application. This process is asynchronous – the initial request will start the removal process and the initial response will show information about the application being removed – however the removal process will proceed after the response is returned. The user is expected to continue checking the application status using the list applications command until the removing process is complete. Once the result of list applications will not consist this applications it means that it was removed completely.

To remove application, use this command:

    zend remove application -a <app-id> [-t <target-id>]

This command line removes application with id equals to app-id from the target-id.

### Redeploying an Application
Zend SDK allows to redeploy an existing application, whether in order to fix a problem or to reset an installation. This process is asynchronous – the initial request will start the redeploy process and the initial response will show information about the application being redeployed – however the redeployment process will proceed after the response is returned. The user may continue checking the application status using the list applications command until the redeploying process is complete.

To redeploy application, use this command:

    zend redeploy application -a <app-id> [-t <target-id>] [-s <server-names>] [-i]

The following options are required:

- -a : Id of the application which should be redeployed

Additionally, there are the following optional options:

- -t : Id of the target where application should be redeployed. If not specified the default target id is considered.
- -s : List of server IDs. If specified, action will be done only on the subset of servers which are currently members of the cluster.
- -i : Ignore failures during staging if only some servers reported failures. If all servers report failures the operation will fail in any case. By default any failure will return an error.

### Listing Applications
Zend SDK allows to get the list of applications currently deployed (or staged) on the server or the cluster and information about each application. If application IDs are specified, will return information about the specified applications. If no IDs are specified, will return information about all applications in the specified target.

To redeploy application, use this command:

    zend list applications [-a <app-id>] [-t <target-id>]

The following options are available:

- -a : List of application IDs. If specified, information will be returned about these applications only. If not specified, information about all applications will be returned. Note that if a non-existing application ID is provided, this action will not fail but instead will return no information about the specific app.
- -t : Id of the target where application should be redeployed. If not specified the default target id is considered.

### Creating a Package
Zend SDK allows to create zpk application package. The default package structure consists following files:

- appdir
- scriptsdir
- deployment.xml

Where appdir and scriptsdir (optional) are directories defined in a descriptor file (for more details about descriptor file, see DeploymentDescriptor?).
Package creation process uses deployment.properites (for more details, see DeploymentPropertiesFile). If this file is not defined, then default rules are used:

- all files except files from default exclusion list (for more details, see DeploymentPropertiesFile) and descriptor file are added to appdir folder,
- if scriptsdir is defined in the descriptor and the folder with this name exists in the project root, it is used as a scriptsdir folder. If it does not exist, scriptsdir is ignored and a warning is displayed.
To create package, use this command:

    zend create package [-p </path/to/project>] [-d <destination>]

The flag -p should have as an argument path to the project root directory. -d is optional and allows to change the location where package will be created (by default it will be created in the current location).

### Push an Application
Zend SDK allows to push all local changes to remote git repository. It performs following operations:

- add all new files to the local git repository,
- commit all changes (removed and added files, modifications in existing files) to the local repository,
- push local repository changes to the phpCloud remote (called "phpcloud").

To push an application, use this command:

    zend push application [-a </path/to/project>] [-r <remote_name>] [-m <commit_message>] 
            [-a <author_name:author_email>]  [-u <user>] [-p <password>] [-k <key>]

The flag -a is not required if the current location is a root application folder. By default, remote name is "phpcloud". Commit message provided by -m option is used for all commits to local repository performed during pushing process. If it is not specified, default value is used.

<a name="managing_repositories" />
## Managing Repositories
Zend SDK provides some tools for managing Zend Repositories. Currently, following operations are supported:

- Generating Repository
- Adding Repository
- Removing Repository
- Listing Repositories

Information about repositories is stored in a local repository storage. It contains repository descriptors. It is possible to check what repositories are in it by calling 'list repositories' command (described below).

### Generating Repository
Zend SDK allows to generate new repository.

    zend generate repository -o <destination> -p <path_to_package> -t <path_to_template> [-e <existing_repository>]

This command is responsible for creating new repositories and adding application to repository. There are two possible scenarions:

- There is already existing Zend Repository ("-e" option).
- There is no Zend Repository.

In the first case new application information is added to specified repository. In the second case, new repository is created in specified destination.

### Adding Repository
Zend SDK allows to add existing repository the the local repository storage. In the result, added repository will be available for other repository dedicated commands.

    zend add repository -o <repository_url>

### Removing Repository
Zend SDK allows to remove repository.

    zend remove repository -o <repository_url>

It removes specified repository from the local repository storage.

### Listing Repository
Zend SDK allows to list available repositories which are in the local repository storage.

    zend list repository [-s <repository_url>]
