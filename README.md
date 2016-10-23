Redmine plugin
==================

[![Build Status](https://buildhive.cloudbees.com/job/jenkinsci/job/redmine-plugin/badge/icon)](https://buildhive.cloudbees.com/job/jenkinsci/job/redmine-plugin/)

What's this?
------------

https://wiki.jenkins-ci.org/display/JENKINS/Redmine+Plugin

Does this plugin require custom Redmine configuration?
------------

This plugin uses Redmine REST API - to enable it, login into your Redmine,
navigate to Administration -> Settings -> Authentication, check "Enable REST web service" and Save.

To use "Aggregate Redmine ticket metrics" you should provide API key assigned to existing Redmine user.
Login as desired user, visit http://redmine/my/account, copy key from "API access key" and paste it into job on Jenkins.

Contribute
------------

Fork and send a pull request (or create an issue on github)
