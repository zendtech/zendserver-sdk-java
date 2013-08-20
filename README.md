# Zend SDK
Zend SDK includes a variety of tools that help you create, develop, publish and discover PHP Web applications. If you are new to the Zend SDK, please [read an overview](#installing-zend-sdk) of how to set up the environment. If you're already using the Zend SDK, you should update to the latest one. To see more details about Zend SDK, please visit [our wiki](https://github.com/zendtech/zendserver-sdk-java/wiki).

## Samples
The "Hello, World" example below shows how to launch your first application with Zend SDK [command line](https://github.com/zendtech/zendserver-sdk-java/wiki/Command-Line):

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

If you prefer to work from the command line, read the [Command Line Tool Manual](https://github.com/zendtech/zendserver-sdk-java/wiki/Command-Line), otherwise continue to step 3.

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
