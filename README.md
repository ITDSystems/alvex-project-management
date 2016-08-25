[![Build Status](https://travis-ci.org/ITDSystems/alvex-project-management.svg?branch=master)](https://travis-ci.org/ITDSystems/alvex-project-management)

Alvex Project Management component
========================

Features:
* Project Site aggregates the information manager needs to make right decision on knowledge-intensive project.
* Attach business processes and tasks to the project to get the list of all tasks within the project in one place.
* Project timeline and schedule
* Define project milestones and control the status of the project with it.
* Track conversations stream of the project. Add summary of your meetings, calls and email messages related to the project to this component. Integration with email server to attach messages to the project automatically is possible.
* Give your team members access to the project site. Create custom roles to give colleagues non-default permissions and unique position in the project team. Add a list of external contacts to the project site to share them with your colleagues.

Build
-----
Option 1:
Build it via [alvex-meta](https://github.com/ITDSystems/alvex-meta). It allows to build a stable version with all dependencies inside the package.

Option 2:
Build from this repo. The component may be packaged in two ways: *amp* and *jar*.
To build amp use `mvn clean package`, to build installable jar use `mvn -P make-jar clean package`.

**Note!**
Don't forget to build and install dependecies! This component depends on [alvex-common](https://github.com/ITDSystems/alvex-common) so you should install it first.

**Note**: this project requires Maven 3.3.9 at least.
