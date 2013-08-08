# Zend SDK
Zend SDK includes a variety of tools that help you create, develop, publish and discover PHP Web applications. If you are new to the Zend SDK, please [read an overview](#install_sdk) of how to set up the environment. If you're already using the Zend SDK, you should update to the latest one.

## Samples
The "Hello, World" example below shows how to launch your first application with Zend SDK [command line](#command_line):

`# clone an example project from a git repository
$ zend clone project -r https://ganoro@github.com/ganoro/ExampleProject.git`

`# define a target environment to host your application
$ zend add target -d <account-name>:<account-password>`

`# set working directory
$ cd ExampleProject`

`# deploy the newly created application to the defined target
$ zend deploy application`
