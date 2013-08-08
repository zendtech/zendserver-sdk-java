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
## Command Line

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

## Listing Targets
To generate a list of targets, use this command:

    zend list targets [-s]

The flag "-s" tells the command line tool to connect to each target and show its status.

The Zend SDK tool scans the user targets directory looking for valid targets and then generates the list.

## Adding a Target
You can add a target by passing the target's information in command line arguments to the Zend SDK tool.

To add your localhost (http://localhost) server you have a simple detection method, see more information [here](#managing_targets/#auto_detect).

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

## Adding a Zend Developer Cloud Target
You can also add your [Zend Developer Cloud](http://www.phpcloud.com/) targets by passing the account's information in command line arguments to the Zend SDK tool.

Given the your Zend Developer Cloud credentials (same as zend.com credentials) you can detect and addall http://*.my.phpcloud.com containers.

Here's the command-line usage for adding a target:

    zend add target -d [account-name or account-email]:[account-passwword]

Using the Zend Developer Cloud API, all containers created by you will be detected automatically and are visible to the Zend SDK.

## Removing a Target
You can use the zend tool to delete a target. Here is the command usage:

    zend remove target -t <id>

When you issue the command, the zend tool looks for a target matching the specified id deletes the targets reference from the user directory. This should not in any way affect your remote server.

<a name="auto_detect" />
## Auto-Detecting the localhost Server
Zend SDK tool can help you easily detect your localhost server without requiring to fill in all arguments such as keys and hostname. During detection time two operations are executed:

1. Finding a suitable key, if doesn't exist the tools tries to generate one.
2. Adding the localhost server as a new target.

To execute the first operation one need to have admin privileges as it requires write access to the Server configuration files.

### Under Linux/Mac:

You will need to first run the detect target to generate the key and only then actually apply it with root privileges using sudo (or any su command).

    # detect localhost and generate a key for SDK 
    $ ./zend detect target 

    # apply secret key
    $ sudo ./zend detect target -k <key> -s <secret-key>
    
### Under Windows7:

Use the elevate tool that helps to elevate privileges (bundled as part of Zend SDK tool and is located under the same location of the batch file):

    elevate zend detect target 
