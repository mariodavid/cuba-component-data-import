[ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/mariodavid/cuba-component-data-import.svg?branch=master)](https://travis-ci.org/mariodavid/cuba-component-data-import)
[![Coverage Status](https://coveralls.io/repos/github/mariodavid/cuba-component-data-import/badge.svg)](https://coveralls.io/github/mariodavid/cuba-component-data-import)

# CUBA Platform Component - Data import

This application component let's you easily import data into your application from various sources.


## Installation

1. Add the following maven repository `https://dl.bintray.com/mariodavid/cuba-components` to the build.gradle of your CUBA application:


        buildscript {
          repositories {
            maven {
              url  "https://dl.bintray.com/mariodavid/cuba-components"
            }
          }
       }
    

    


2. Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 6.8.x            | 0.1.x          |


The latest version is: [ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)

Add custom application component to your project:

* Artifact group: `de.diedavids.cuba.dataimport`
* Artifact name: `data-import-global`
* Version: *add-on version*

## Using the application component

The `data-import` application component helps you import data into your system from different files.

Currently the following file-types are supported: 

* Excel `.xlsx` 
* comma separated values `.csv`

In order to configure various import options, there is a UI based configuration possibility to define

* which entity will be imported
* which columns maps to which entity attribute
* format configuration for dates, boolean values etc.
* custom groovy scripts for attributes to value mapping

There are two modes of using the `data-import` application component. The first one is an interactive UI wizard, which
will guide the user directly through the process of importing the data.

The second mode is, that the import configuration can be pre-defined by a developer / administrator of the system.
The end-user of the system can reuse this configurations and just uploads the file that should get imported.


### Import wizard
 

![import-wizard-step-1](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-1.png)

![import-wizard-step-2](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-2.png)

![import-wizard-step-3](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-3.png)
