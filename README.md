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
* unique configurations and how to deal with these situations
* custom groovy scripts for attributes to value mapping

There are two modes of using the `data-import` application component. The first one is an interactive UI wizard, which
will guide the user directly through the process of importing the data.

The second mode is, that the import configuration can be pre-defined by a developer / administrator of the system.
The end-user of the system can reuse this configurations and just uploads the file that should get imported.


### Import wizard

The import wizard allows the user to interactively go through the import process and configure the above mentioned settings
for the import execution. It can be found in the main menu: `Administration > Data Import > Import Wizard`

##### Step 1: Upload file
![import-wizard-step-1](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-1.png)


##### Step 2: Configure entity mapping
![import-wizard-step-2](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-2.png)

The second step in the wizard allows the user to configure which columns of the import file will be mapped to which entity
attributes. The system makes suggestions based on the similarities of the entity attribute names and the column headers
in the import file, but this can be adjusted by the user if needed.

##### Step 3: Import Configuration 
In the import configuration it is possible to define certain Format options as well as the unique configurations for this import.


##### Step 4: Import Preview
![import-wizard-step-3](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-3.png)

The last step will preview the data that was received from the import file. With "Start Import" the import process
will be triggered. Afterwards the user will see a summary of how many entities were imported correctly.